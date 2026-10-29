package pe.edu.upeu.autoandes.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upeu.autoandes.entity.Usuario;
import pe.edu.upeu.autoandes.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Usuario usuario =
                usuarioRepository
                        .findByUsernameIgnoreCase(username.trim())
                        .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(PermisosPorRol.authorities(usuario.getRoles()))
                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .build();
    }
}
