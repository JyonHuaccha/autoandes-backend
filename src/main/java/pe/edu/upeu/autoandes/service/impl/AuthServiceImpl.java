package pe.edu.upeu.autoandes.service.impl;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.authentication.BadCredentialsException; import lombok.RequiredArgsConstructor; import pe.edu.upeu.autoandes.repository.UsuarioRepository; import pe.edu.upeu.autoandes.security.*; import pe.edu.upeu.autoandes.config.JwtService; import pe.edu.upeu.autoandes.service.AuthService; import pe.edu.upeu.autoandes.dto.*;
@Service @RequiredArgsConstructor @Transactional(readOnly=true) public class AuthServiceImpl implements AuthService {
private final UsuarioRepository usuarios;private final PasswordEncoder encoder;private final UsuarioDetailsService details;private final JwtService jwt;
public LoginResponseDTO login(LoginRequestDTO dto){var u=usuarios.findByUsernameIgnoreCase(dto.username().trim());
// Siempre se comprueba BCrypt, incluso si no existe el usuario, para no revelar cuentas.
String hash=u.map(x->x.getPassword()).orElse("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");boolean coincide=encoder.matches(dto.password(),hash);
if(u.isEmpty()||!coincide||!Boolean.TRUE.equals(u.get().getActivo()))throw new BadCredentialsException("Credenciales inválidas");
var perfil=actual(u.get().getUsername());return new LoginResponseDTO(jwt.generar(details.loadUserByUsername(perfil.username())),"Bearer",jwt.expiraEnSegundos(),perfil.username(),perfil.nombreCompleto(),perfil.roles(),perfil.permisos());}
public UsuarioResponseDTO actual(String username){var u=usuarios.findByUsernameIgnoreCase(username).filter(x->Boolean.TRUE.equals(x.getActivo())).orElseThrow(()->new BadCredentialsException("Credenciales inválidas"));var roles=u.getRoles().stream().map(r->r.getNombre()).sorted().toList();return new UsuarioResponseDTO(u.getUsername(),u.getNombreCompleto(),roles,PermisosPorRol.permisos(roles));}}
