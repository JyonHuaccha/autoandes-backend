package pe.edu.upeu.autoandes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrdenRequestDTO {
    @NotNull(message = "Debe seleccionar un vehículo")
    @Positive(message = "El vehículo debe tener un identificador válido")
    private Long vehiculoId;

    @NotNull(message = "El kilometraje es obligatorio")
    @Min(value = 0, message = "El kilometraje no puede ser negativo")
    @Max(value = 9999999, message = "El kilometraje admite hasta 7 dígitos")
    private Integer kilometraje;

    @NotNull(message = "El detalle de la orden es obligatorio")
    @Size(min = 1, max = 10, message = "La orden debe contener entre 1 y 10 detalles")
    private List<@NotNull(message = "El detalle no puede ser nulo") @Valid DetalleOrdenRequestDTO>
            detalles;
}
