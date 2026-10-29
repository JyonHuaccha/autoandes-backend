package pe.edu.upeu.autoandes.mapper;

import org.springframework.stereotype.Component;

import pe.edu.upeu.autoandes.dto.auth.UsuarioResponseDTO;
import pe.edu.upeu.autoandes.entity.Rol;
import pe.edu.upeu.autoandes.entity.Usuario;
import pe.edu.upeu.autoandes.security.PermisosPorRol;

import java.util.Collection;
import java.util.List;

@Component
public class UsuarioMapper {
    public UsuarioResponseDTO toUsuarioResponse(Usuario usuario) {
        List<String> roles = nombresRoles(usuario.getRoles());
        return new UsuarioResponseDTO(
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                roles,
                PermisosPorRol.permisos(usuario.getRoles()));
    }

    public List<String> nombresRoles(Collection<Rol> roles) {
        return roles.stream().map(Rol::getNombre).sorted().toList();
    }
}
