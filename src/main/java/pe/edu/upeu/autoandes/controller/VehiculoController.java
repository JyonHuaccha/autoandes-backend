package pe.edu.upeu.autoandes.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.upeu.autoandes.dto.VehiculoRequestDTO;
import pe.edu.upeu.autoandes.dto.VehiculoResponseDTO;
import pe.edu.upeu.autoandes.service.service.VehiculoService;

@RestController
@RequestMapping("/api/v1/vehiculos")
public class VehiculoController {
    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public ResponseEntity<Iterable<VehiculoResponseDTO>> findAll(
            @RequestParam(required = false) Long clienteId) {
        return ResponseEntity.ok(vehiculoService.listar(clienteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculoService.read(id));
    }

    @PostMapping
    public ResponseEntity<VehiculoResponseDTO> create(
            @Valid @RequestBody VehiculoRequestDTO requestDTO) {
        VehiculoResponseDTO response = vehiculoService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> update(
            @PathVariable Long id, @Valid @RequestBody VehiculoRequestDTO requestDTO) {
        VehiculoResponseDTO response = vehiculoService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehiculoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
