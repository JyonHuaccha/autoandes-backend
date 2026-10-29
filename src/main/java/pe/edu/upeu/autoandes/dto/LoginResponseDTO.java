package pe.edu.upeu.autoandes.dto;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.List;
public record LoginResponseDTO(String token,String tipo,long expiraEnSegundos,String username,String nombreCompleto,List<String> roles,List<String> permisos) {}
