package pe.edu.upeu.autoandes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upeu.autoandes.dto.DetalleOrdenResponseDTO;
import pe.edu.upeu.autoandes.dto.OrdenRequestDTO;
import pe.edu.upeu.autoandes.dto.OrdenResponseDTO;
import pe.edu.upeu.autoandes.entity.DetalleOrden;
import pe.edu.upeu.autoandes.entity.OrdenServicio;
import pe.edu.upeu.autoandes.enums.EstadoOrden;
import pe.edu.upeu.autoandes.exception.RecursosNoEncontradoException;
import pe.edu.upeu.autoandes.exception.ReglaNegocioException;
import pe.edu.upeu.autoandes.exception.SolicitudInvalidaException;
import pe.edu.upeu.autoandes.repository.OrdenServicioRepository;
import pe.edu.upeu.autoandes.repository.ServicioRepository;
import pe.edu.upeu.autoandes.repository.VehiculoRepository;
import pe.edu.upeu.autoandes.service.service.OrdenService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class OrdenServiceImpl implements OrdenService {
    private static final Logger log = LoggerFactory.getLogger(OrdenServiceImpl.class);
    private final OrdenServicioRepository repository;
    private final VehiculoRepository vehiculos;
    private final ServicioRepository servicios;

    public OrdenServiceImpl(
            OrdenServicioRepository repository,
            VehiculoRepository vehiculos,
            ServicioRepository servicios) {
        this.repository = repository;
        this.vehiculos = vehiculos;
        this.servicios = servicios;
    }

    private OrdenServicio buscar(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Orden no encontrada"));
    }

    @Transactional
    public OrdenResponseDTO registrar(OrdenRequestDTO dto, String usuario) {
        if (dto.getDetalles() == null
                || dto.getDetalles().isEmpty()
                || dto.getDetalles().size() > 10)
            throw new SolicitudInvalidaException("La orden debe contener entre 1 y 10 detalles");
        var repetidos = new HashSet<Long>();
        for (var d : dto.getDetalles()) {
            if (d == null
                    || d.getServicioId() == null
                    || d.getCantidad() == null
                    || d.getCantidad() < 1
                    || d.getCantidad() > 5)
                throw new SolicitudInvalidaException(
                        "Cada detalle exige un servicio y cantidad de 1 a 5");
            if (!repetidos.add(d.getServicioId()))
                throw new SolicitudInvalidaException(
                        "No se puede repetir el mismo servicio en dos líneas");
        }
        // Serializa el kilometraje del mismo vehículo incluso con peticiones concurrentes.
        var vehiculo =
                vehiculos
                        .bloquear(dto.getVehiculoId())
                        .orElseThrow(
                                () -> new RecursosNoEncontradoException("Vehículo no encontrado"));
        var anterior = repository.ultimoKilometraje(vehiculo.getId());
        if (anterior != null && dto.getKilometraje() < anterior)
            throw new ReglaNegocioException(
                    "El kilometraje no puede ser menor al último registrado: " + anterior);
        var o = new OrdenServicio();
        o.setNumero("OS-%06d".formatted(repository.siguienteNumero()));
        o.setFecha(LocalDateTime.now());
        o.setVehiculo(vehiculo);
        o.setKilometraje(dto.getKilometraje());
        o.setEstado(EstadoOrden.REGISTRADA);
        o.setUsuarioRegistro(usuario);
        var total = BigDecimal.ZERO;
        for (var item : dto.getDetalles()) {
            var servicio =
                    servicios
                            .findById(item.getServicioId())
                            .orElseThrow(
                                    () ->
                                            new RecursosNoEncontradoException(
                                                    "Servicio no encontrado: "
                                                            + item.getServicioId()));
            if (!Boolean.TRUE.equals(servicio.getActivo()))
                throw new ReglaNegocioException(
                        "El servicio " + servicio.getCodigo() + " está inactivo");
            var d = new DetalleOrden();
            d.setOrden(o);
            d.setServicio(servicio);
            d.setCantidad(item.getCantidad());
            d.setPrecioUnitario(servicio.getPrecio());
            d.setSubtotal(servicio.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
            o.agregarDetalle(d);
            total = total.add(d.getSubtotal());
        }
        if (total.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new SolicitudInvalidaException(
                    "El total de la orden supera el límite de S/ 99999999.99");
        }
        o.setTotal(total);
        repository.saveAndFlush(o);
        log.info("Orden registrada numero={} total={} usuario={}", o.getNumero(), total, usuario);
        return convertirResponse(o);
    }

    public List<OrdenResponseDTO> consultar(
            String placa, String estado, LocalDate desde, LocalDate hasta) {
        validarFechas(desde, hasta);
        String e = estado == null || estado.isBlank() ? null : estado.toUpperCase(Locale.ROOT);
        if (e != null && !Set.of("REGISTRADA", "ANULADA").contains(e))
            throw new SolicitudInvalidaException("Estado inválido: use REGISTRADA o ANULADA");
        return repository
                .consultar(
                        placa == null || placa.isBlank() ? null : placa.trim(),
                        e == null ? null : EstadoOrden.valueOf(e),
                        desde == null ? null : desde.atStartOfDay(),
                        hasta == null ? null : hasta.plusDays(1).atStartOfDay())
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    public OrdenResponseDTO obtener(Long id) {
        return convertirResponse(buscar(id));
    }

    @Transactional
    public OrdenResponseDTO anular(Long id, String motivo) {
        if (motivo == null || motivo.trim().length() < 10 || motivo.trim().length() > 200)
            throw new SolicitudInvalidaException("El motivo debe tener entre 10 y 200 caracteres");
        var o =
                repository
                        .bloquear(id)
                        .orElseThrow(
                                () -> new RecursosNoEncontradoException("Orden no encontrada"));
        if (o.getEstado() != EstadoOrden.REGISTRADA)
            throw new ReglaNegocioException("Solo se puede anular una orden REGISTRADA");
        if (!o.getFecha().toLocalDate().equals(LocalDate.now()))
            throw new ReglaNegocioException(
                    "La orden solo se puede anular el mismo día de su registro");
        o.setEstado(EstadoOrden.ANULADA);
        o.setMotivoAnulacion(motivo.trim());
        repository.flush();
        log.info("Orden anulada numero={}", o.getNumero());
        return convertirResponse(o);
    }

    private void validarFechas(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta))
            throw new SolicitudInvalidaException("Desde no puede ser posterior a hasta");
    }

    private DetalleOrdenResponseDTO convertirDetalleResponse(DetalleOrden d) {
        return new DetalleOrdenResponseDTO(
                d.getId(),
                d.getServicio().getId(),
                d.getServicio().getCodigo(),
                d.getServicio().getNombre(),
                d.getCantidad(),
                d.getPrecioUnitario(),
                d.getSubtotal());
    }

    private OrdenResponseDTO convertirResponse(OrdenServicio o) {
        return new OrdenResponseDTO(
                o.getId(),
                o.getNumero(),
                o.getFecha(),
                o.getVehiculo().getId(),
                o.getVehiculo().getPlaca(),
                o.getVehiculo().getCliente().getNombres(),
                o.getKilometraje(),
                o.getEstado().name(),
                o.getTotal(),
                o.getUsuarioRegistro(),
                o.getMotivoAnulacion(),
                o.getDetalles().stream().map(this::convertirDetalleResponse).toList());
    }
}
