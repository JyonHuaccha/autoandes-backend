package pe.edu.upeu.autoandes.service.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upeu.autoandes.dto.auth.LoginRequestDTO;
import pe.edu.upeu.autoandes.dto.auth.LoginResponseDTO;
import pe.edu.upeu.autoandes.dto.auth.UsuarioResponseDTO;
import pe.edu.upeu.autoandes.mapper.UsuarioMapper;
import pe.edu.upeu.autoandes.repository.UsuarioRepository;
import pe.edu.upeu.autoandes.security.JwtService;
import pe.edu.upeu.autoandes.security.UsuarioDetailsService;
import pe.edu.upeu.autoandes.service.service.AuthService;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final UsuarioMapper mapper;
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final UsuarioDetailsService details;
    private final JwtService jwt;

    public AuthServiceImpl(
            UsuarioMapper mapper,
            UsuarioRepository usuarios,
            PasswordEncoder encoder,
            UsuarioDetailsService details,
            JwtService jwt) {
        this.mapper = mapper;
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.details = details;
        this.jwt = jwt;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        if (dto.getPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        var u = usuarios.findByUsernameIgnoreCase(dto.getUsername().trim());
        // Siempre se comprueba BCrypt, incluso si no existe el usuario, para no revelar cuentas.
        String hash =
                u.map(x -> x.getPassword())
                        .orElse("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        boolean coincide = encoder.matches(dto.getPassword(), hash);
        if (u.isEmpty() || !coincide || !Boolean.TRUE.equals(u.get().getActivo()))
            throw new BadCredentialsException("Credenciales inválidas");
        var perfil = actual(u.get().getUsername());
        return new LoginResponseDTO(
                jwt.generarToken(details.loadUserByUsername(perfil.getUsername())),
                "Bearer",
                jwt.getExpiracionSegundos(),
                perfil.getUsername(),
                perfil.getNombreCompleto(),
                perfil.getRoles(),
                perfil.getPermisos());
    }

    public UsuarioResponseDTO actual(String username) {
        var u =
                usuarios.findByUsernameIgnoreCase(username)
                        .filter(x -> Boolean.TRUE.equals(x.getActivo()))
                        .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        return mapper.toUsuarioResponse(u);
    }
}
