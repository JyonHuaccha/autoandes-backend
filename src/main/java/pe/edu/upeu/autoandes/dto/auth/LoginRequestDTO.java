package pe.edu.upeu.autoandes.dto.auth;

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
public class LoginRequestDTO {
    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 30, message = "El usuario admite hasta 30 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 72, message = "La contraseña admite hasta 72 caracteres")
    private String password;
}
