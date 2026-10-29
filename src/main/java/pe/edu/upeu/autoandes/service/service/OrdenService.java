package pe.edu.upeu.autoandes.service.service;

import pe.edu.upeu.autoandes.dto.OrdenRequestDTO;
import pe.edu.upeu.autoandes.dto.OrdenResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface OrdenService {
    OrdenResponseDTO registrar(OrdenRequestDTO dto, String usuario);

    List<OrdenResponseDTO> consultar(String placa, String estado, LocalDate desde, LocalDate hasta);

    OrdenResponseDTO obtener(Long id);

    OrdenResponseDTO anular(Long id, String motivo);
}
