package pe.edu.upeu.autoandes.service.impl;

import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j; import java.util.List;
import pe.edu.upeu.autoandes.repository.*; import pe.edu.upeu.autoandes.mapper.*; import pe.edu.upeu.autoandes.service.*; import pe.edu.upeu.autoandes.dto.*; import pe.edu.upeu.autoandes.entity.*; import pe.edu.upeu.autoandes.exception.*;
@Service @RequiredArgsConstructor @Slf4j @Transactional(readOnly=true) public class ServicioServiceImpl implements ServicioService {
private final ServicioRepository repository; private final DetalleOrdenRepository detalles; private final AutoandesMapper mapper;
private Servicio buscar(Long id){return repository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Servicio no encontrado"));}
public List<ServicioResponseDTO> listar(){return repository.findAll(org.springframework.data.domain.Sort.by("codigo")).stream().map(mapper::servicio).toList();}
public ServicioResponseDTO obtener(Long id){return mapper.servicio(buscar(id));}
private void asignar(Servicio s,ServicioRequestDTO dto){s.setCodigo(dto.codigo().trim());s.setNombre(dto.nombre().trim());s.setPrecio(dto.precio());s.setDuracionMin(dto.duracionMin());}
@Transactional public ServicioResponseDTO crear(ServicioRequestDTO dto){if(repository.existsByCodigoIgnoreCase(dto.codigo().trim()))throw new ReglaNegocioException("El código de servicio ya existe"); var s=new Servicio();asignar(s,dto);s.setActivo(true);repository.saveAndFlush(s);log.info("Servicio creado id={}",s.getId());return mapper.servicio(s);}
@Transactional public ServicioResponseDTO actualizar(Long id,ServicioRequestDTO dto){var s=buscar(id);if(repository.existsByCodigoIgnoreCaseAndIdNot(dto.codigo().trim(),id))throw new ReglaNegocioException("El código de servicio ya existe");asignar(s,dto);repository.flush();log.info("Servicio actualizado id={}",id);return mapper.servicio(s);}
@Transactional public void eliminar(Long id){var s=buscar(id);if(detalles.existsByServicioId(id))throw new ReglaNegocioException("No se puede eliminar: tiene órdenes asociadas. Use Dar de baja para cambiar su estado");repository.delete(s);repository.flush();log.info("Servicio eliminado id={}",id);}
@Transactional public ServicioResponseDTO estado(Long id,boolean activo){var s=buscar(id);s.setActivo(activo);repository.flush();log.info("Estado de servicio id={} activo={}",id,activo);return mapper.servicio(s);}
}
