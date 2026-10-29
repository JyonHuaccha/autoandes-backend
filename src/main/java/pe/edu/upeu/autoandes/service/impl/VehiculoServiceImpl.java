package pe.edu.upeu.autoandes.service.impl;

import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j; import java.util.List;
import pe.edu.upeu.autoandes.repository.*; import pe.edu.upeu.autoandes.mapper.*; import pe.edu.upeu.autoandes.service.*; import pe.edu.upeu.autoandes.dto.*; import pe.edu.upeu.autoandes.entity.*; import pe.edu.upeu.autoandes.exception.*;
@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly=true) public class VehiculoServiceImpl implements VehiculoService {
private final VehiculoRepository repository;private final ClienteRepository clientes;private final OrdenServicioRepository ordenes;private final AutoandesMapper mapper;
private Vehiculo buscar(Long id){return repository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Vehículo no encontrado"));}
public List<VehiculoResponseDTO> listar(Long clienteId){if(clienteId!=null&&!clientes.existsById(clienteId))throw new RecursoNoEncontradoException("Cliente no encontrado");return repository.listar(clienteId).stream().map(mapper::vehiculo).toList();}
public VehiculoResponseDTO obtener(Long id){return mapper.vehiculo(buscar(id));}
private void asignar(Vehiculo v,VehiculoRequestDTO dto){v.setCliente(clientes.findById(dto.clienteId()).orElseThrow(()->new RecursoNoEncontradoException("Cliente no encontrado")));v.setPlaca(dto.placa().trim());v.setMarca(dto.marca().trim());v.setModelo(dto.modelo().trim());v.setAnio(dto.anio());}
@Transactional public VehiculoResponseDTO crear(VehiculoRequestDTO dto){if(repository.existsByPlacaIgnoreCase(dto.placa().trim()))throw new ReglaNegocioException("La placa ya existe");var v=new Vehiculo();asignar(v,dto);repository.saveAndFlush(v);log.info("Vehículo creado id={} placa={}",v.getId(),v.getPlaca());return mapper.vehiculo(v);}
@Transactional public VehiculoResponseDTO actualizar(Long id,VehiculoRequestDTO dto){var v=buscar(id);if(repository.existsByPlacaIgnoreCaseAndIdNot(dto.placa().trim(),id))throw new ReglaNegocioException("La placa ya existe");asignar(v,dto);repository.flush();log.info("Vehículo actualizado id={}",id);return mapper.vehiculo(v);}
@Transactional public void eliminar(Long id){var v=buscar(id);if(ordenes.existsByVehiculoId(id))throw new ReglaNegocioException("No se puede eliminar el vehículo porque tiene órdenes asociadas");repository.delete(v);repository.flush();log.info("Vehículo eliminado id={}",id);}
}
