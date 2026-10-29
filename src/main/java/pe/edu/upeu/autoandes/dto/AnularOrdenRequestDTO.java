package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AnularOrdenRequestDTO {
    @NotBlank(message = "El motivo de anulación es obligatorio")
    @Size(min = 10, max = 200, message = "El motivo debe tener entre 10 y 200 caracteres")
    private String motivo;
}
