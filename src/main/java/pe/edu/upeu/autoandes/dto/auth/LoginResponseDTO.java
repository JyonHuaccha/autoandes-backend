package pe.edu.upeu.autoandes.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginResponseDTO {

    private String token;

    private String tipo;

    private long expiraEnSegundos;

    private String username;

    private String nombreCompleto;

    private List<String> roles;

    private List<String> permisos;
}
