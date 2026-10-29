package pe.edu.upeu.autoandes.security;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PermisosPorRolTest {
    @Test
    void asesorSoloTieneSusPermisos() {
        assertThat(permisos("ASESOR"))
                .contains("ORDEN_REGISTRAR", "VEHICULO_ESCRIBIR")
                .doesNotContain("ORDEN_ANULAR", "REPORTE_VER", "SERVICIO_ESCRIBIR");
    }

    @Test
    void jefeSoloTieneSusPermisos() {
        assertThat(permisos("JEFE_TALLER"))
                .contains("ORDEN_ANULAR", "REPORTE_VER", "SERVICIO_ESCRIBIR")
                .doesNotContain("ORDEN_REGISTRAR", "VEHICULO_ESCRIBIR");
    }

    @Test
    void adminTieneTodos() {
        assertThat(permisos("ADMIN")).hasSize(8);
    }

    @Test
    void variosRolesRecibenLaUnion() {
        assertThat(permisos("ASESOR", "JEFE_TALLER")).hasSize(8).doesNotHaveDuplicates();
    }

    @Test
    void rolDesconocidoNoConcedePermisos() {
        assertThat(permisos("OTRO")).isEmpty();
    }

    private java.util.List<String> permisos(String... nombres) {
        var roles =
                java.util.Arrays.stream(nombres)
                        .map(
                                n -> {
                                    var r = new pe.edu.upeu.autoandes.entity.Rol();
                                    r.setNombre(n);
                                    return r;
                                })
                        .toList();
        return PermisosPorRol.permisos(roles);
    }
}
