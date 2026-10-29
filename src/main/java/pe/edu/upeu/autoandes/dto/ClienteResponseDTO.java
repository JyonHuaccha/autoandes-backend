package pe.edu.upeu.autoandes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClienteResponseDTO {

    private Long id;

    private String documento;

    private String nombres;

    private String telefono;
}
