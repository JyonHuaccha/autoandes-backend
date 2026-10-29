package pe.edu.upeu.autoandes.dto;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.List;
public record ServicioResponseDTO(Long id,String codigo,String nombre,BigDecimal precio,Integer duracionMin,Boolean activo) {}
