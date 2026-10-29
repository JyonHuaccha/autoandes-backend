package pe.edu.upeu.autoandes.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.upeu.autoandes.dto.EstadoRequestDTO;
import pe.edu.upeu.autoandes.dto.ServicioRequestDTO;
import pe.edu.upeu.autoandes.dto.ServicioResponseDTO;
import pe.edu.upeu.autoandes.service.service.ServicioService;

@RestController
@RequestMapping("/api/v1/servicios")
public class ServicioController {
    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    public ResponseEntity<Iterable<ServicioResponseDTO>> findAll() {
        return ResponseEntity.ok(servicioService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(servicioService.read(id));
    }

    @PostMapping
    public ResponseEntity<ServicioResponseDTO> create(
            @Valid @RequestBody ServicioRequestDTO requestDTO) {
        ServicioResponseDTO response = servicioService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> update(
            @PathVariable Long id, @Valid @RequestBody ServicioRequestDTO requestDTO) {
        ServicioResponseDTO response = servicioService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        servicioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ServicioResponseDTO> cambiarEstado(
            @PathVariable Long id, @Valid @RequestBody EstadoRequestDTO requestDTO) {
        return ResponseEntity.ok(servicioService.estado(id, requestDTO.getActivo()));
    }
}
