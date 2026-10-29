package pe.edu.upeu.autoandes.controller;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.upeu.autoandes.dto.AnularOrdenRequestDTO;
import pe.edu.upeu.autoandes.dto.OrdenRequestDTO;
import pe.edu.upeu.autoandes.dto.OrdenResponseDTO;
import pe.edu.upeu.autoandes.service.service.OrdenService;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
public class OrdenController {
    private final OrdenService service;

    public OrdenController(OrdenService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrdenResponseDTO> registrar(
            @Valid @RequestBody OrdenRequestDTO dto, Principal principal) {
        var r = service.registrar(dto, principal.getName());
        return ResponseEntity.created(URI.create("/api/v1/ordenes/" + r.getId())).body(r);
    }

    @GetMapping
    public List<OrdenResponseDTO> consultar(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate hasta) {
        return service.consultar(placa, estado, desde, hasta);
    }

    @GetMapping("/{id}")
    public OrdenResponseDTO obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PatchMapping("/{id}/anular")
    public OrdenResponseDTO anular(
            @PathVariable Long id, @Valid @RequestBody AnularOrdenRequestDTO dto) {
        return service.anular(id, dto.getMotivo());
    }
}
