package pe.edu.upeu.autoandes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upeu.autoandes.dto.VehiculoRequestDTO;
import pe.edu.upeu.autoandes.dto.VehiculoResponseDTO;
import pe.edu.upeu.autoandes.entity.Vehiculo;
import pe.edu.upeu.autoandes.exception.RecursosNoEncontradoException;
import pe.edu.upeu.autoandes.exception.ReglaNegocioException;
import pe.edu.upeu.autoandes.repository.ClienteRepository;
import pe.edu.upeu.autoandes.repository.OrdenServicioRepository;
import pe.edu.upeu.autoandes.repository.VehiculoRepository;
import pe.edu.upeu.autoandes.service.service.VehiculoService;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class VehiculoServiceImpl implements VehiculoService {
    private static final Logger log = LoggerFactory.getLogger(VehiculoServiceImpl.class);
    private final VehiculoRepository repository;
    private final ClienteRepository clientes;
    private final OrdenServicioRepository ordenes;

    public VehiculoServiceImpl(
            VehiculoRepository repository,
            ClienteRepository clientes,
            OrdenServicioRepository ordenes) {
        this.repository = repository;
        this.clientes = clientes;
        this.ordenes = ordenes;
    }

    @Override
    public List<VehiculoResponseDTO> readAll() {
        return listar(null);
    }

    private Vehiculo buscar(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Vehículo no encontrado"));
    }

    @Override
    public List<VehiculoResponseDTO> listar(Long clienteId) {
        if (clienteId != null && !clientes.existsById(clienteId))
            throw new RecursosNoEncontradoException("Cliente no encontrado");
        return repository.listar(clienteId).stream().map(this::convertirResponse).toList();
    }

    @Override
    public VehiculoResponseDTO read(Long id) {
        return convertirResponse(buscar(id));
    }

    private void asignar(Vehiculo v, VehiculoRequestDTO dto) {
        v.setCliente(
                clientes.findById(dto.getClienteId())
                        .orElseThrow(
                                () -> new RecursosNoEncontradoException("Cliente no encontrado")));
        v.setPlaca(dto.getPlaca().trim());
        v.setMarca(dto.getMarca().trim());
        v.setModelo(dto.getModelo().trim());
        v.setAnio(dto.getAnio());
    }

    @Transactional
    @Override
    public VehiculoResponseDTO create(VehiculoRequestDTO dto) {
        if (repository.existsByPlacaIgnoreCase(dto.getPlaca().trim()))
            throw new ReglaNegocioException("La placa ya existe");
        var v = new Vehiculo();
        asignar(v, dto);
        repository.saveAndFlush(v);
        log.info("Vehículo creado id={} placa={}", v.getId(), v.getPlaca());
        return convertirResponse(v);
    }

    @Transactional
    @Override
    public VehiculoResponseDTO update(Long id, VehiculoRequestDTO dto) {
        var v = buscar(id);
        if (repository.existsByPlacaIgnoreCaseAndIdNot(dto.getPlaca().trim(), id))
            throw new ReglaNegocioException("La placa ya existe");
        asignar(v, dto);
        repository.flush();
        log.info("Vehículo actualizado id={}", id);
        return convertirResponse(v);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        var v = buscar(id);
        if (ordenes.existsByVehiculoId(id))
            throw new ReglaNegocioException(
                    "No se puede eliminar el vehículo porque tiene órdenes asociadas");
        repository.delete(v);
        repository.flush();
        log.info("Vehículo eliminado id={}", id);
    }

    private VehiculoResponseDTO convertirResponse(Vehiculo v) {
        return new VehiculoResponseDTO(
                v.getId(),
                v.getPlaca(),
                v.getMarca(),
                v.getModelo(),
                v.getAnio(),
                v.getCliente().getId(),
                v.getCliente().getNombres());
    }
}
