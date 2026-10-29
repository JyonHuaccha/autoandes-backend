package pe.edu.upeu.autoandes.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import pe.edu.upeu.autoandes.entity.Rol;
import pe.edu.upeu.autoandes.enums.Permiso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class PermisosPorRol {
    private static final Map<String, Set<Permiso>> PERMISOS =
            Map.of(
                    "ADMIN", Collections.unmodifiableSet(EnumSet.allOf(Permiso.class)),
                    "ASESOR",
                            Collections.unmodifiableSet(
                                    EnumSet.of(
                                            Permiso.SERVICIO_LEER,
                                            Permiso.VEHICULO_LEER,
                                            Permiso.VEHICULO_ESCRIBIR,
                                            Permiso.ORDEN_LEER,
                                            Permiso.ORDEN_REGISTRAR)),
                    "JEFE_TALLER",
                            Collections.unmodifiableSet(
                                    EnumSet.of(
                                            Permiso.SERVICIO_LEER,
                                            Permiso.SERVICIO_ESCRIBIR,
                                            Permiso.VEHICULO_LEER,
                                            Permiso.ORDEN_LEER,
                                            Permiso.ORDEN_ANULAR,
                                            Permiso.REPORTE_VER)));

    private PermisosPorRol() {}

    public static Set<Permiso> de(String nombreRol) {
        return PERMISOS.getOrDefault(nombreRol, Set.of());
    }

    /* Union de los permisos de todos los roles, ordenada alfabeticamente. */
    public static List<String> permisos(Collection<Rol> roles) {
        Set<String> nombres = new TreeSet<>();
        roles.forEach(rol -> de(rol.getNombre()).forEach(permiso -> nombres.add(permiso.name())));
        return new ArrayList<>(nombres);
    }

    /*
     * Authorities para Spring Security: un "ROLE_<rol>" por cada rol (para
     * hasRole) mas un authority por cada permiso (para hasAuthority).
     */
    public static List<GrantedAuthority> authorities(Collection<Rol> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.stream()
                .map(Rol::getNombre)
                .sorted()
                .forEach(nombre -> authorities.add(new SimpleGrantedAuthority("ROLE_" + nombre)));
        permisos(roles).forEach(permiso -> authorities.add(new SimpleGrantedAuthority(permiso)));
        return authorities;
    }
}
