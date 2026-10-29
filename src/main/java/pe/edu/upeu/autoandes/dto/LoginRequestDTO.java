package pe.edu.upeu.autoandes.dto;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.List;
public record LoginRequestDTO(@NotBlank @Size(max=30) String username,@NotBlank @Size(max=100) String password) {}
