package pe.edu.upeu.autoandes.controller;
import org.springframework.web.bind.annotation.*; import org.springframework.http.*; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import java.util.List; import java.net.URI; import pe.edu.upeu.autoandes.service.*; import pe.edu.upeu.autoandes.dto.*;
@RestController @RequestMapping("/api/v1/vehiculos") @RequiredArgsConstructor public class VehiculoController {private final VehiculoService service;
@GetMapping public List<VehiculoResponseDTO> listar(@RequestParam(required=false) Long clienteId){return service.listar(clienteId);}
@GetMapping("/{id}") public VehiculoResponseDTO obtener(@PathVariable Long id){return service.obtener(id);}
@PostMapping public ResponseEntity<VehiculoResponseDTO> crear(@Valid @RequestBody VehiculoRequestDTO dto){var r=service.crear(dto);return ResponseEntity.created(URI.create("/api/v1/vehiculos/"+r.id())).body(r);}
@PutMapping("/{id}") public VehiculoResponseDTO actualizar(@PathVariable Long id,@Valid @RequestBody VehiculoRequestDTO dto){return service.actualizar(id,dto);}
@DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){service.eliminar(id);return ResponseEntity.noContent().build();}  }
