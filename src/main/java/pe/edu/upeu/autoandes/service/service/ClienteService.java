package pe.edu.upeu.autoandes.service.service;

import pe.edu.upeu.autoandes.dto.ClienteRequestDTO;
import pe.edu.upeu.autoandes.dto.ClienteResponseDTO;
import pe.edu.upeu.autoandes.service.generic.CrudService;

public interface ClienteService extends CrudService<ClienteRequestDTO, ClienteResponseDTO, Long> {}
