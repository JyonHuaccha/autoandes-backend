package pe.edu.upeu.autoandes.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErrorResponseDTO {

    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String mensaje;

    private String ruta;

    private Map<String, String> campos;
}
