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
public class ServicioResponseDTO {

    private Long id;

    private String codigo;

    private String nombre;

    private BigDecimal precio;

    private Integer duracionMin;

    private Boolean activo;
}
