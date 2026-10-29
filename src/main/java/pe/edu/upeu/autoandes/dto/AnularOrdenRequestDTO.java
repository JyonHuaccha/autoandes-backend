package pe.edu.upeu.autoandes.dto;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.List;
public record AnularOrdenRequestDTO(@NotBlank @Size(min=10,max=200) String motivo) {}
