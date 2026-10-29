package pe.edu.upeu.autoandes;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import pe.edu.upeu.autoandes.dto.AnularOrdenRequestDTO;
import pe.edu.upeu.autoandes.dto.DetalleOrdenRequestDTO;
import pe.edu.upeu.autoandes.dto.OrdenRequestDTO;
import pe.edu.upeu.autoandes.dto.ServicioRequestDTO;
import pe.edu.upeu.autoandes.dto.VehiculoRequestDTO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class ValidacionesDTOTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    static Stream<Arguments> datosInvalidos() {
        return Stream.of(
                Arguments.of(
                        new ServicioRequestDTO("ABC-001", "Aceite", BigDecimal.TEN, 30), "codigo"),
                Arguments.of(new ServicioRequestDTO("SRV-001", "ab", BigDecimal.TEN, 30), "nombre"),
                Arguments.of(new ServicioRequestDTO("SRV-001", " ", BigDecimal.TEN, 30), "nombre"),
                Arguments.of(
                        new ServicioRequestDTO("SRV-001", "Aceite", BigDecimal.ZERO, 30), "precio"),
                Arguments.of(
                        new ServicioRequestDTO("SRV-001", "Aceite", new BigDecimal("-1"), 30),
                        "precio"),
                Arguments.of(
                        new ServicioRequestDTO("SRV-001", "Aceite", new BigDecimal("1.999"), 30),
                        "precio"),
                Arguments.of(
                        new ServicioRequestDTO(
                                "SRV-001", "Aceite", new BigDecimal("100000000"), 30),
                        "precio"),
                Arguments.of(new ServicioRequestDTO("SRV-001", "Aceite", null, 30), "precio"),
                Arguments.of(
                        new ServicioRequestDTO("SRV-001", "Aceite", BigDecimal.TEN, 14),
                        "duracionMin"),
                Arguments.of(
                        new ServicioRequestDTO("SRV-001", "Aceite", BigDecimal.TEN, 481),
                        "duracionMin"),
                Arguments.of(
                        new VehiculoRequestDTO("AA-111", "Toyota", "Yaris", 2024, 1L), "placa"),
                Arguments.of(new VehiculoRequestDTO("AAA-111", " ", "Yaris", 2024, 1L), "marca"),
                Arguments.of(new VehiculoRequestDTO("AAA-111", "Toyota", " ", 2024, 1L), "modelo"),
                Arguments.of(
                        new VehiculoRequestDTO("AAA-111", "Toyota", "Yaris", 1989, 1L), "anio"),
                Arguments.of(
                        new VehiculoRequestDTO("AAA-111", "Toyota", "Yaris", 2028, 1L), "anio"),
                Arguments.of(
                        new VehiculoRequestDTO("AAA-111", "Toyota", "Yaris", 2024, null),
                        "clienteId"),
                Arguments.of(
                        new OrdenRequestDTO(
                                null, 45000, List.of(new DetalleOrdenRequestDTO(1L, 1))),
                        "vehiculoId"),
                Arguments.of(
                        new OrdenRequestDTO(1L, -1, List.of(new DetalleOrdenRequestDTO(1L, 1))),
                        "kilometraje"),
                Arguments.of(
                        new OrdenRequestDTO(
                                1L, 10000000, List.of(new DetalleOrdenRequestDTO(1L, 1))),
                        "kilometraje"),
                Arguments.of(new OrdenRequestDTO(1L, 45000, List.of()), "detalles"),
                Arguments.of(new OrdenRequestDTO(1L, 45000, null), "detalles"),
                Arguments.of(
                        new OrdenRequestDTO(
                                1L,
                                45000,
                                Collections.nCopies(11, new DetalleOrdenRequestDTO(1L, 1))),
                        "detalles"),
                Arguments.of(new DetalleOrdenRequestDTO(1L, 0), "cantidad"),
                Arguments.of(new DetalleOrdenRequestDTO(1L, 6), "cantidad"),
                Arguments.of(new DetalleOrdenRequestDTO(null, 1), "servicioId"),
                Arguments.of(new AnularOrdenRequestDTO("corto"), "motivo"),
                Arguments.of(new AnularOrdenRequestDTO("a".repeat(201)), "motivo"));
    }

    @ParameterizedTest
    @MethodSource("datosInvalidos")
    void validaLosLimitesDelContrato(Object request, String campo) {
        assertThat(validator.validate(request))
                .anyMatch(error -> error.getPropertyPath().toString().equals(campo));
    }
}
