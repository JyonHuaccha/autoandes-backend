package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VehiculoRequestDTO {
    @NotBlank(message = "La placa es obligatoria")
    @Pattern(regexp = "[A-Z]{3}-[0-9]{3}", message = "La placa debe tener el formato ABC-123")
    private String placa;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 40, message = "La marca admite hasta 40 caracteres")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 40, message = "El modelo admite hasta 40 caracteres")
    private String modelo;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1990, message = "El año mínimo es 1990")
    @Max(value = 2027, message = "El año máximo es 2027")
    private Integer anio;

    @NotNull(message = "Debe seleccionar un cliente")
    @Positive(message = "El cliente debe tener un identificador válido")
    private Long clienteId;
}
