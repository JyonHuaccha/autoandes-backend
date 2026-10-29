package pe.edu.upeu.autoandes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upeu.autoandes.dto.ServicioRequestDTO;
import pe.edu.upeu.autoandes.dto.ServicioResponseDTO;
import pe.edu.upeu.autoandes.entity.Servicio;
import pe.edu.upeu.autoandes.exception.RecursosNoEncontradoException;
import pe.edu.upeu.autoandes.exception.ReglaNegocioException;
import pe.edu.upeu.autoandes.exception.SolicitudInvalidaException;
import pe.edu.upeu.autoandes.repository.DetalleOrdenRepository;
import pe.edu.upeu.autoandes.repository.ServicioRepository;
import pe.edu.upeu.autoandes.service.service.ServicioService;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ServicioServiceImpl implements ServicioService {
    private static final Logger log = LoggerFactory.getLogger(ServicioServiceImpl.class);
    private final ServicioRepository repository;
    private final DetalleOrdenRepository detalles;

    public ServicioServiceImpl(ServicioRepository repository, DetalleOrdenRepository detalles) {
        this.repository = repository;
        this.detalles = detalles;
    }

    private Servicio buscar(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Servicio no encontrado"));
    }

    @Override
    public List<ServicioResponseDTO> readAll() {
        return repository.findAll(org.springframework.data.domain.Sort.by("codigo")).stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    public ServicioResponseDTO read(Long id) {
        return convertirResponse(buscar(id));
    }

    private void asignar(Servicio s, ServicioRequestDTO dto) {
        s.setCodigo(dto.getCodigo().trim());
        if (dto.getNombre().trim().length() < 3) {
            throw new SolicitudInvalidaException(
                    "El nombre debe tener entre 3 y 80 caracteres, sin contar espacios externos");
        }
        s.setNombre(dto.getNombre().trim());
        s.setPrecio(dto.getPrecio());
        s.setDuracionMin(dto.getDuracionMin());
    }

    @Transactional
    @Override
    public ServicioResponseDTO create(ServicioRequestDTO dto) {
        if (repository.existsByCodigoIgnoreCase(dto.getCodigo().trim()))
            throw new ReglaNegocioException("El código de servicio ya existe");
        var s = new Servicio();
        asignar(s, dto);
        s.setActivo(true);
        repository.saveAndFlush(s);
        log.info("Servicio creado id={}", s.getId());
        return convertirResponse(s);
    }

    @Transactional
    @Override
    public ServicioResponseDTO update(Long id, ServicioRequestDTO dto) {
        var s = buscar(id);
        if (repository.existsByCodigoIgnoreCaseAndIdNot(dto.getCodigo().trim(), id))
            throw new ReglaNegocioException("El código de servicio ya existe");
        asignar(s, dto);
        repository.flush();
        log.info("Servicio actualizado id={}", id);
        return convertirResponse(s);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        var s = buscar(id);
        if (detalles.existsByServicioId(id))
            throw new ReglaNegocioException(
                    "No se puede eliminar: tiene órdenes asociadas. Use Dar de baja para cambiar su"
                            + " estado");
        repository.delete(s);
        repository.flush();
        log.info("Servicio eliminado id={}", id);
    }

    @Transactional
    @Override
    public ServicioResponseDTO estado(Long id, boolean activo) {
        var s = buscar(id);
        s.setActivo(activo);
        repository.flush();
        log.info("Estado de servicio id={} activo={}", id, activo);
        return convertirResponse(s);
    }

    private ServicioResponseDTO convertirResponse(Servicio s) {
        return new ServicioResponseDTO(
                s.getId(),
                s.getCodigo(),
                s.getNombre(),
                s.getPrecio(),
                s.getDuracionMin(),
                s.getActivo());
    }
}
