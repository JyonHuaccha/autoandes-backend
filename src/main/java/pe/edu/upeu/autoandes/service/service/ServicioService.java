package pe.edu.upeu.autoandes.service.service;

import pe.edu.upeu.autoandes.dto.ServicioRequestDTO;
import pe.edu.upeu.autoandes.dto.ServicioResponseDTO;
import pe.edu.upeu.autoandes.service.generic.CrudService;

public interface ServicioService
        extends CrudService<ServicioRequestDTO, ServicioResponseDTO, Long> {
    ServicioResponseDTO estado(Long id, boolean activo);
}
