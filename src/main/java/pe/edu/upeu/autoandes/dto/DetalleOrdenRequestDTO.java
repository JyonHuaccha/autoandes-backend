package pe.edu.upeu.autoandes.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DetalleOrdenRequestDTO {
    @NotNull(message = "Debe seleccionar un servicio")
    @Positive(message = "El servicio debe tener un identificador válido")
    private Long servicioId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    @Max(value = 5, message = "La cantidad máxima es 5")
    private Integer cantidad;
}
