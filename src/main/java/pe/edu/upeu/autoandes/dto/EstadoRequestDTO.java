package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EstadoRequestDTO {
    @NotNull(message = "Debe indicar el estado activo")
    private Boolean activo;
}
