package pe.edu.upeu.autoandes.dto;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.List;
public record DetalleOrdenResponseDTO(Long id,Long servicioId,String servicioCodigo,String servicioNombre,Integer cantidad,BigDecimal precioUnitario,BigDecimal subtotal) {}
