package pe.edu.upeu.autoandes.service.service;

import pe.edu.upeu.autoandes.dto.VehiculoRequestDTO;
import pe.edu.upeu.autoandes.dto.VehiculoResponseDTO;
import pe.edu.upeu.autoandes.service.generic.CrudService;

import java.util.List;

public interface VehiculoService
        extends CrudService<VehiculoRequestDTO, VehiculoResponseDTO, Long> {
    List<VehiculoResponseDTO> listar(Long clienteId);
}
