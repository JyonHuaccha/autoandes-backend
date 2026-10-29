package pe.edu.upeu.autoandes.controller;
import org.springframework.web.bind.annotation.*; import lombok.RequiredArgsConstructor; import java.util.List; import pe.edu.upeu.autoandes.service.*; import pe.edu.upeu.autoandes.dto.*;
@RestController @RequestMapping("/api/v1/clientes") @RequiredArgsConstructor public class ClienteController {private final ClienteService service; @GetMapping public List<ClienteResponseDTO> listar(){return service.listar();}}
