package pe.edu.upeu.autoandes.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.upeu.autoandes.dto.reporte.IngresoPorServicioDTO;
import pe.edu.upeu.autoandes.service.service.ReporteService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {
    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    @GetMapping("/ingresos-por-servicio")
    @PreAuthorize("hasAuthority('REPORTE_VER')")
    public List<IngresoPorServicioDTO> ingresos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate hasta) {
        return service.ingresos(desde, hasta);
    }
}
