package pe.edu.upeu.autoandes.service.impl;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upeu.autoandes.dto.ClienteRequestDTO;
import pe.edu.upeu.autoandes.dto.ClienteResponseDTO;
import pe.edu.upeu.autoandes.entity.Cliente;
import pe.edu.upeu.autoandes.exception.RecursosNoEncontradoException;
import pe.edu.upeu.autoandes.exception.ReglaNegocioException;
import pe.edu.upeu.autoandes.repository.ClienteRepository;
import pe.edu.upeu.autoandes.service.service.ClienteService;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<ClienteResponseDTO> readAll() {
        return clienteRepository.findAll(Sort.by("nombres")).stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    public ClienteResponseDTO read(Long id) {
        Cliente cliente =
                clienteRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RecursosNoEncontradoException("Cliente no encontrado"));
        return convertirResponse(cliente);
    }

    // Se conserva el contrato genérico de Pharma, pero los clientes del examen son semilla de solo
    // lectura.
    @Override
    public ClienteResponseDTO create(ClienteRequestDTO request) {
        throw new ReglaNegocioException("Los clientes son de solo lectura en AutoAndes");
    }

    @Override
    public ClienteResponseDTO update(Long id, ClienteRequestDTO request) {
        throw new ReglaNegocioException("Los clientes son de solo lectura en AutoAndes");
    }

    @Override
    public void delete(Long id) {
        throw new ReglaNegocioException("Los clientes son de solo lectura en AutoAndes");
    }

    private ClienteResponseDTO convertirResponse(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getDocumento(),
                cliente.getNombres(),
                cliente.getTelefono());
    }
}
