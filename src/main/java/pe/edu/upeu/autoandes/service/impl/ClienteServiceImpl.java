package pe.edu.upeu.autoandes.service.impl;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import lombok.RequiredArgsConstructor; import java.util.List;
import pe.edu.upeu.autoandes.repository.*; import pe.edu.upeu.autoandes.mapper.*; import pe.edu.upeu.autoandes.service.*; import pe.edu.upeu.autoandes.dto.*;
@Service @RequiredArgsConstructor @Transactional(readOnly=true) public class ClienteServiceImpl implements ClienteService {
private final ClienteRepository repository; private final AutoandesMapper mapper;
public List<ClienteResponseDTO> listar(){return repository.findAll(org.springframework.data.domain.Sort.by("nombres")).stream().map(mapper::cliente).toList();}}
