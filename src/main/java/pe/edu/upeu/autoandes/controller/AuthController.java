package pe.edu.upeu.autoandes.controller;
import org.springframework.web.bind.annotation.*; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import java.security.Principal; import pe.edu.upeu.autoandes.service.AuthService; import pe.edu.upeu.autoandes.dto.*;
@RestController @RequestMapping("/api/v1/auth") @RequiredArgsConstructor public class AuthController {private final AuthService service;
@PostMapping("/login") public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO dto){return service.login(dto);}
@GetMapping("/me") public UsuarioResponseDTO actual(Principal principal){return service.actual(principal.getName());}}
