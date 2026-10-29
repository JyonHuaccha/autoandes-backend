package pe.edu.upeu.autoandes.dto.reporte;

import java.math.BigDecimal;

public record IngresoPorServicioDTO(
        String codigo, String servicio, Long cantidadTotal, BigDecimal importeTotal) {}
