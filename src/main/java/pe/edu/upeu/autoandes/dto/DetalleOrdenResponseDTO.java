package pe.edu.upeu.autoandes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DetalleOrdenResponseDTO {

    private Long id;

    private Long servicioId;

    private String servicioCodigo;

    private String servicioNombre;

    private Integer cantidad;

    private BigDecimal precioUnitario;

    private BigDecimal subtotal;
}
