package pe.edu.upeu.autoandes;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.dto.auth.LoginRequestDTO;
import pe.edu.upeu.autoandes.dto.reporte.IngresoPorServicioDTO;
import pe.edu.upeu.autoandes.exception.*;
import pe.edu.upeu.autoandes.repository.*;
import pe.edu.upeu.autoandes.security.*;
import pe.edu.upeu.autoandes.service.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Todas las escrituras de estas pruebas se revierten al terminar cada caso.
@org.springframework.test.context.ActiveProfiles({"dev", "test"})
@SpringBootTest
@Transactional
class AutoandesIntegracionTest {
    @Autowired WebApplicationContext context;
    @Autowired AuthService auth;
    @Autowired ServicioService servicios;
    @Autowired VehiculoService vehiculos;
    @Autowired OrdenService ordenes;
    @Autowired ReporteService reportes;
    @Autowired OrdenServicioRepository ordenRepository;
    @Autowired DetalleOrdenRepository detalleRepository;
    @Autowired ServicioRepository servicioRepository;
    @Autowired JwtService jwt;

    @Value("${app.jwt.secret}")
    String secret;

    MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    private String token(String username) {
        return auth.login(new LoginRequestDTO(username, "Auto2026*")).getToken();
    }

    private OrdenRequestDTO orden(int kilometraje, DetalleOrdenRequestDTO... detalles) {
        return new OrdenRequestDTO(1L, kilometraje, List.of(detalles));
    }

    private DetalleOrdenRequestDTO detalle(long id, int cantidad) {
        return new DetalleOrdenRequestDTO(id, cantidad);
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "asesor", "jefe"})
    void loginDeLosTresUsuariosConDuracionUnaHora(String usuario) throws Exception {
        mvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"username\":\""
                                                + usuario
                                                + "\",\"password\":\"Auto2026*\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.expiraEnSegundos").value(3600))
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(strings = {"desconocido", "inactivo"})
    void LoginRechazadoNoRevelaSiLaCuentaExiste(String usuario) throws Exception {
        mvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"username\":\""
                                                + usuario
                                                + "\",\"password\":\"Auto2026*\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"))
                .andExpect(jsonPath("$.ruta").value("/api/v1/auth/login"))
                .andExpect(jsonPath("$.campos").doesNotExist());
    }

    @Test
    void claveIncorrectaTieneElMismoMensaje() throws Exception {
        mvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"admin\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "clientes",
                "servicios",
                "vehiculos",
                "ordenes",
                "auth/me",
                "reportes/ingresos-por-servicio"
            })
    void todasLasConsultasExigenToken(String ruta) throws Exception {
        mvc.perform(get("/api/v1/" + ruta))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.mensaje").exists())
                .andExpect(jsonPath("$.ruta").value("/api/v1/" + ruta))
                .andExpect(jsonPath("$.campos").doesNotExist());
    }

    @Test
    void tokenAlteradoEs401() throws Exception {
        String t = token("asesor");
        String[] partes = t.split("\\.");
        partes[1] = "eyJzdWIiOiJhZG1pbiJ9";
        mvc.perform(
                        get("/api/v1/servicios")
                                .header("Authorization", "Bearer " + String.join(".", partes)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenVencidoEs401() throws Exception {
        var vencido =
                new JwtService(secret, -1)
                        .generarToken(
                                User.withUsername("admin")
                                        .password("x")
                                        .authorities("SERVICIO_LEER")
                                        .build());
        mvc.perform(get("/api/v1/servicios").header("Authorization", "Bearer " + vencido))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void asesorNoPuedeAnularNiVerReportes() throws Exception {
        String t = token("asesor");
        mvc.perform(
                        patch("/api/v1/ordenes/1/anular")
                                .header("Authorization", "Bearer " + t)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"motivo\":\"Solicitud del cliente\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.ruta").value("/api/v1/ordenes/1/anular"));
        mvc.perform(
                        get("/api/v1/reportes/ingresos-por-servicio")
                                .header("Authorization", "Bearer " + t))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + t))
                .andExpect(status().isOk());
    }

    @Test
    void jefeNoPuedeRegistrarOrdenesNiEscribirVehiculos() throws Exception {
        var t = token("jefe");
        mvc.perform(
                        post("/api/v1/ordenes")
                                .header("Authorization", "Bearer " + t)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"vehiculoId\":1,\"kilometraje\":45000,\"detalles\":[{\"servicioId\":1,\"cantidad\":1}]}"))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/v1/vehiculos/8").header("Authorization", "Bearer " + t))
                .andExpect(status().isForbidden());
    }

    @Test
    void asesorNoPuedeEscribirServicios() throws Exception {
        mvc.perform(
                        patch("/api/v1/servicios/1/estado")
                                .header("Authorization", "Bearer " + token("asesor"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"activo\":false}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void placaInvalidaEs400ConCampos() throws Exception {
        mvc.perform(
                        post("/api/v1/vehiculos")
                                .header("Authorization", "Bearer " + token("admin"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"placa\":\"MAL\",\"marca\":\"Toyota\",\"modelo\":\"Yaris\",\"anio\":1980,\"clienteId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Datos inválidos"))
                .andExpect(jsonPath("$.campos.placa").exists())
                .andExpect(jsonPath("$.campos.anio").exists());
    }

    @Test
    void detalleInvalidoEs400ConCampoAnidado() throws Exception {
        mvc.perform(
                        post("/api/v1/ordenes")
                                .header("Authorization", "Bearer " + token("asesor"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"vehiculoId\":1,\"kilometraje\":45000,\"detalles\":[{\"servicioId\":1,\"cantidad\":6}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos['detalles[0].cantidad']").exists());
    }

    @Test
    void faltanteEs404ConFormatoComun() throws Exception {
        mvc.perform(
                        get("/api/v1/vehiculos/999999")
                                .header("Authorization", "Bearer " + token("asesor")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Vehículo no encontrado"));
    }

    @Test
    void placaRepetidaEs409() throws Exception {
        mvc.perform(
                        post("/api/v1/vehiculos")
                                .header("Authorization", "Bearer " + token("asesor"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"placa\":\"AAA-111\",\"marca\":\"Toyota\",\"modelo\":\"Yaris\",\"anio\":2020,\"clienteId\":1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("La placa ya existe"));
    }

    @Test
    void crudServicioYBajaPorEstado() {
        var creado =
                servicios.create(
                        new ServicioRequestDTO(
                                "SRV-901", "Servicio prueba", new BigDecimal("35.50"), 30));
        var cambio =
                servicios.update(
                        creado.getId(),
                        new ServicioRequestDTO(
                                "SRV-901", "Servicio editado", new BigDecimal("45.50"), 45));
        assertThat(cambio.getNombre()).isEqualTo("Servicio editado");
        assertThat(servicios.estado(creado.getId(), false).getActivo()).isFalse();
        servicios.delete(creado.getId());
        assertThatThrownBy(() -> servicios.read(creado.getId()))
                .isInstanceOf(RecursosNoEncontradoException.class);
    }

    @Test
    void servicioUsadoNoSeEliminaPeroSeDaDeBaja() {
        assertThatThrownBy(() -> servicios.delete(1L)).isInstanceOf(ReglaNegocioException.class);
        assertThat(servicios.estado(1L, false).getActivo()).isFalse();
        assertThat(servicioRepository.existsById(1L)).isTrue();
    }

    @Test
    void codigoDuplicadoSeRechaza() {
        assertThatThrownBy(
                        () ->
                                servicios.create(
                                        new ServicioRequestDTO(
                                                "SRV-001", "Duplicado", BigDecimal.TEN, 30)))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void crudVehiculoRelacionado() {
        var v = vehiculos.create(new VehiculoRequestDTO("ZXY-987", "Toyota", "Yaris", 2024, 1L));
        assertThat(v.getClienteNombre()).isNotBlank();
        var e =
                vehiculos.update(
                        v.getId(),
                        new VehiculoRequestDTO("ZXY-987", "Toyota", "Corolla", 2025, 2L));
        assertThat(e.getClienteId()).isEqualTo(2L);
        assertThat(e.getModelo()).isEqualTo("Corolla");
        vehiculos.delete(v.getId());
        assertThatThrownBy(() -> vehiculos.read(v.getId()))
                .isInstanceOf(RecursosNoEncontradoException.class);
    }

    @Test
    void clienteInexistenteEs404() {
        assertThatThrownBy(
                        () ->
                                vehiculos.create(
                                        new VehiculoRequestDTO(
                                                "ZXY-987", "Toyota", "Yaris", 2024, 999999L)))
                .isInstanceOf(RecursosNoEncontradoException.class);
    }

    @Test
    void vehiculoConOrdenesNoSeElimina() {
        assertThatThrownBy(() -> vehiculos.delete(1L)).isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void calculaPreciosSubtotalesYTotalEnServidor() {
        var r = ordenes.registrar(orden(45001, detalle(1, 2), detalle(2, 3)), "asesor");
        assertThat(r.getTotal()).isEqualByComparingTo("480.00");
        assertThat(r.getDetalles()).hasSize(2);
        assertThat(r.getDetalles().get(0).getPrecioUnitario()).isEqualByComparingTo("120.00");
        assertThat(r.getNumero()).matches("OS-[0-9]{6}");
        assertThat(r.getEstado()).isEqualTo("REGISTRADA");
        assertThat(ordenRepository.findById(r.getId())).isPresent();
    }

    @Test
    void tercerDetalleInactivoNoDejaCabeceraNiDetalles() {
        long cabeceras = ordenRepository.count(), detalles = detalleRepository.count();
        assertThatThrownBy(
                        () ->
                                ordenes.registrar(
                                        orden(45000, detalle(1, 1), detalle(2, 1), detalle(8, 1)),
                                        "asesor"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("SRV-008");
        assertThat(ordenRepository.count()).isEqualTo(cabeceras);
        assertThat(detalleRepository.count()).isEqualTo(detalles);
    }

    @Test
    void servicioRepetidoEs400() {
        assertThatThrownBy(
                        () ->
                                ordenes.registrar(
                                        orden(45000, detalle(1, 1), detalle(1, 2)), "asesor"))
                .isInstanceOf(SolicitudInvalidaException.class);
    }

    @Test
    void kilometrajeMenorEs409ConUltimoValor() {
        assertThatThrownBy(() -> ordenes.registrar(orden(44999, detalle(1, 1)), "asesor"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("45000");
    }

    @Test
    void noAdmiteOrdenVaciaNiCantidadFueraDelRango() {
        assertThatThrownBy(() -> ordenes.registrar(orden(45000), "asesor"))
                .isInstanceOf(SolicitudInvalidaException.class);
        assertThatThrownBy(() -> ordenes.registrar(orden(45000, detalle(1, 6)), "asesor"))
                .isInstanceOf(SolicitudInvalidaException.class);
    }

    @Test
    void anulaSoloLaRegistradaDelDia() {
        var o = ordenes.registrar(orden(45001, detalle(1, 1)), "asesor");
        var anulada = ordenes.anular(o.getId(), "Solicitud de anulación del cliente");
        assertThat(anulada.getEstado()).isEqualTo("ANULADA");
        assertThatThrownBy(() -> ordenes.anular(o.getId(), "Solicitud repetida del cliente"))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void noAnulaLaOrdenDeOtroDia() {
        assertThatThrownBy(() -> ordenes.anular(2L, "Solicitud de anulación del cliente"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("mismo día");
    }

    @Test
    void motivoObligatorioDeDiezCaracteres() {
        assertThatThrownBy(() -> ordenes.anular(1L, "corto"))
                .isInstanceOf(SolicitudInvalidaException.class);
    }

    @Test
    void filtrosCombinadosYListaVacia() {
        assertThat(ordenes.consultar("AAA-111", "REGISTRADA", null, null))
                .allSatisfy(
                        o -> {
                            assertThat(o.getPlaca()).isEqualTo("AAA-111");
                            assertThat(o.getEstado()).isEqualTo("REGISTRADA");
                        });
        assertThat(ordenes.consultar("ZZZ-999", null, null, null)).isEmpty();
    }

    @Test
    void rangoInvertidoSeRechaza() {
        assertThatThrownBy(
                        () ->
                                ordenes.consultar(
                                        null, null, LocalDate.now(), LocalDate.now().minusDays(1)))
                .isInstanceOf(SolicitudInvalidaException.class);
    }

    @Test
    void reporteAgrupadoExcluyeAnuladas() {
        var r = reportes.ingresos(null, null);
        assertThat(r).extracting(IngresoPorServicioDTO::codigo).doesNotContain("SRV-004");
        assertThat(
                        r.stream()
                                .map(IngresoPorServicioDTO::importeTotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                .isEqualByComparingTo("470.00");
        assertThat(r.get(0).importeTotal()).isEqualByComparingTo("240.00");
    }

    @Test
    void corsPermiteAuthorizationDesdeAngular() throws Exception {
        mvc.perform(
                        options("/api/v1/ordenes")
                                .header("Origin", "http://localhost:4200")
                                .header("Access-Control-Request-Method", "POST")
                                .header(
                                        "Access-Control-Request-Headers",
                                        "Authorization,Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }
}
