package pe.edu.upeu.autoandes.service;
import java.util.List; import java.time.LocalDate; import pe.edu.upeu.autoandes.dto.*; public interface ReporteService { List<IngresoPorServicioDTO> ingresos(LocalDate desde,LocalDate hasta); }
