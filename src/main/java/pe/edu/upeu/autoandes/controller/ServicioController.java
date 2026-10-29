package pe.edu.upeu.autoandes.controller;
import org.springframework.web.bind.annotation.*; import org.springframework.http.*; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import java.util.List; import java.net.URI; import pe.edu.upeu.autoandes.service.*; import pe.edu.upeu.autoandes.dto.*;
@RestController @RequestMapping("/api/v1/servicios") @RequiredArgsConstructor public class ServicioController {private final ServicioService service;
@GetMapping public List<ServicioResponseDTO> listar(){return service.listar();}
@GetMapping("/{id}") public ServicioResponseDTO obtener(@PathVariable Long id){return service.obtener(id);}
@PostMapping public ResponseEntity<ServicioResponseDTO> crear(@Valid @RequestBody ServicioRequestDTO dto){var r=service.crear(dto);return ResponseEntity.created(URI.create("/api/v1/servicios/"+r.id())).body(r);}
@PutMapping("/{id}") public ServicioResponseDTO actualizar(@PathVariable Long id,@Valid @RequestBody ServicioRequestDTO dto){return service.actualizar(id,dto);}
@DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){service.eliminar(id);return ResponseEntity.noContent().build();} @PatchMapping("/{id}/estado") public ServicioResponseDTO estado(@PathVariable Long id,@Valid @RequestBody EstadoRequestDTO dto){return service.estado(id,dto.activo());} }
