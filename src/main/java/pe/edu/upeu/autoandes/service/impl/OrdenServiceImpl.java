package pe.edu.upeu.autoandes.service.impl;

import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import java.util.*; import java.time.*; import java.math.BigDecimal;
import pe.edu.upeu.autoandes.repository.*; import pe.edu.upeu.autoandes.mapper.*; import pe.edu.upeu.autoandes.service.*; import pe.edu.upeu.autoandes.dto.*; import pe.edu.upeu.autoandes.entity.*; import pe.edu.upeu.autoandes.exception.*;
@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly=true) public class OrdenServiceImpl implements OrdenService {
private final OrdenServicioRepository repository;private final VehiculoRepository vehiculos;private final ServicioRepository servicios;private final AutoandesMapper mapper;
private OrdenServicio buscar(Long id){return repository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Orden no encontrada"));}
@Transactional public OrdenResponseDTO registrar(OrdenRequestDTO dto,String usuario){
 if(dto.detalles()==null||dto.detalles().isEmpty()||dto.detalles().size()>10)throw new SolicitudInvalidaException("La orden debe contener entre 1 y 10 detalles");
 var repetidos=new HashSet<Long>();for(var d:dto.detalles()){if(d==null||d.servicioId()==null||d.cantidad()==null||d.cantidad()<1||d.cantidad()>5)throw new SolicitudInvalidaException("Cada detalle exige un servicio y cantidad de 1 a 5");if(!repetidos.add(d.servicioId()))throw new SolicitudInvalidaException("No se puede repetir el mismo servicio en dos líneas");}
 // Serializa el kilometraje del mismo vehículo incluso con peticiones concurrentes.
 var vehiculo=vehiculos.bloquear(dto.vehiculoId()).orElseThrow(()->new RecursoNoEncontradoException("Vehículo no encontrado"));
 var anterior=repository.ultimoKilometraje(vehiculo.getId());
 if(anterior!=null&&dto.kilometraje()<anterior)throw new ReglaNegocioException("El kilometraje no puede ser menor al último registrado: "+anterior);
 var o=new OrdenServicio();o.setNumero("OS-%06d".formatted(repository.siguienteNumero()));o.setFecha(LocalDateTime.now());o.setVehiculo(vehiculo);o.setKilometraje(dto.kilometraje());o.setEstado("REGISTRADA");o.setUsuarioRegistro(usuario);var total=BigDecimal.ZERO;
 for(var item:dto.detalles()){
 var servicio=servicios.findById(item.servicioId()).orElseThrow(()->new RecursoNoEncontradoException("Servicio no encontrado: "+item.servicioId()));
 if(!Boolean.TRUE.equals(servicio.getActivo()))throw new ReglaNegocioException("El servicio "+servicio.getCodigo()+" está inactivo");
 var d=new DetalleOrden();d.setOrden(o);d.setServicio(servicio);d.setCantidad(item.cantidad());d.setPrecioUnitario(servicio.getPrecio());d.setSubtotal(servicio.getPrecio().multiply(BigDecimal.valueOf(item.cantidad())));o.getDetalles().add(d);total=total.add(d.getSubtotal());}
 o.setTotal(total);repository.saveAndFlush(o);log.info("Orden registrada numero={} total={} usuario={}",o.getNumero(),total,usuario);return mapper.orden(o);
}
public List<OrdenResponseDTO> consultar(String placa,String estado,LocalDate desde,LocalDate hasta){validarFechas(desde,hasta);String e=estado==null||estado.isBlank()?null:estado.toUpperCase(Locale.ROOT);if(e!=null&&!Set.of("REGISTRADA","ANULADA").contains(e))throw new SolicitudInvalidaException("Estado inválido: use REGISTRADA o ANULADA");return repository.consultar(placa==null||placa.isBlank()?null:placa.trim(),e,desde==null?null:desde.atStartOfDay(),hasta==null?null:hasta.plusDays(1).atStartOfDay()).stream().map(mapper::orden).toList();}
public OrdenResponseDTO obtener(Long id){return mapper.orden(buscar(id));}
@Transactional public OrdenResponseDTO anular(Long id,String motivo){
 if(motivo==null||motivo.trim().length()<10||motivo.trim().length()>200)throw new SolicitudInvalidaException("El motivo debe tener entre 10 y 200 caracteres");
 var o=repository.bloquear(id).orElseThrow(()->new RecursoNoEncontradoException("Orden no encontrada"));
 if(!"REGISTRADA".equals(o.getEstado()))throw new ReglaNegocioException("Solo se puede anular una orden REGISTRADA");
 if(!o.getFecha().toLocalDate().equals(LocalDate.now()))throw new ReglaNegocioException("La orden solo se puede anular el mismo día de su registro");
 o.setEstado("ANULADA");o.setMotivoAnulacion(motivo.trim());repository.flush();log.info("Orden anulada numero={}",o.getNumero());return mapper.orden(o);
}
private void validarFechas(LocalDate desde,LocalDate hasta){if(desde!=null&&hasta!=null&&desde.isAfter(hasta))throw new SolicitudInvalidaException("Desde no puede ser posterior a hasta");}
}
