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
public class ClienteRequestDTO {
    @NotBlank
    @Size(max = 11)
    private String documento;

    @NotBlank
    @Size(max = 120)
    private String nombres;

    @Size(max = 15)
    private String telefono;
}
