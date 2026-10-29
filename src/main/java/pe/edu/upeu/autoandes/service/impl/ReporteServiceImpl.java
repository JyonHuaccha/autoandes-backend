package pe.edu.upeu.autoandes.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upeu.autoandes.dto.reporte.IngresoPorServicioDTO;
import pe.edu.upeu.autoandes.exception.SolicitudInvalidaException;
import pe.edu.upeu.autoandes.repository.DetalleOrdenRepository;
import pe.edu.upeu.autoandes.service.service.ReporteService;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {
    private final DetalleOrdenRepository repository;

    public ReporteServiceImpl(DetalleOrdenRepository repository) {
        this.repository = repository;
    }

    public List<IngresoPorServicioDTO> ingresos(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta))
            throw new SolicitudInvalidaException("Desde no puede ser posterior a hasta");
        return repository.ingresos(
                desde == null ? null : desde.atStartOfDay(),
                hasta == null ? null : hasta.plusDays(1).atStartOfDay());
    }
}
