package pe.edu.upeu.autoandes.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import pe.edu.upeu.autoandes.exception.dto.ErrorResponseDTO;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ErrorResponseDTO> error(
            HttpStatus status, String mensaje, HttpServletRequest req, Map<String, String> campos) {
        if (status.is4xxClientError())
            log.warn("Solicitud rechazada {} {}: {}", status.value(), req.getRequestURI(), mensaje);
        return ResponseEntity.status(status)
                .body(
                        new ErrorResponseDTO(
                                LocalDateTime.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                mensaje,
                                req.getRequestURI(),
                                campos));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponseDTO> validacion(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        var campos = new LinkedHashMap<String, String>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(e -> campos.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Datos inválidos", req, campos);
    }

    @ExceptionHandler(RecursosNoEncontradoException.class)
    ResponseEntity<ErrorResponseDTO> noExiste(RuntimeException ex, HttpServletRequest r) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), r, null);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    ResponseEntity<ErrorResponseDTO> regla(RuntimeException ex, HttpServletRequest r) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), r, null);
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    ResponseEntity<ErrorResponseDTO> solicitud(RuntimeException ex, HttpServletRequest r) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), r, null);
    }

    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
        org.springframework.web.bind.MissingServletRequestParameterException.class
    })
    ResponseEntity<ErrorResponseDTO> formato(Exception ex, HttpServletRequest r) {
        return error(HttpStatus.BAD_REQUEST, "Formato de solicitud inválido", r, null);
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ErrorResponseDTO> auth(Exception ex, HttpServletRequest r) {
        return error(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", r, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorResponseDTO> permiso(Exception ex, HttpServletRequest r) {
        return error(HttpStatus.FORBIDDEN, "No tiene permisos para esta operación", r, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErrorResponseDTO> integridad(Exception ex, HttpServletRequest r) {
        return error(
                HttpStatus.CONFLICT,
                "El registro está duplicado o tiene registros asociados",
                r,
                null);
    }

    @ExceptionHandler({
        org.springframework.web.servlet.resource.NoResourceFoundException.class,
        org.springframework.web.servlet.NoHandlerFoundException.class
    })
    ResponseEntity<ErrorResponseDTO> ruta(Exception ex, HttpServletRequest r) {
        return error(HttpStatus.NOT_FOUND, "Recurso no encontrado", r, null);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponseDTO> inesperado(Exception ex, HttpServletRequest r) {
        log.error("Error inesperado en {}", r.getRequestURI(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado", r, null);
    }

    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> metodoNoPermitido(
            Exception ex, HttpServletRequest request) {
        return error(
                HttpStatus.METHOD_NOT_ALLOWED, "Método no permitido para esta ruta", request, null);
    }

    @ExceptionHandler(org.springframework.dao.ConcurrencyFailureException.class)
    public ResponseEntity<ErrorResponseDTO> conflictoConcurrente(
            Exception ex, HttpServletRequest request) {
        return error(
                HttpStatus.CONFLICT,
                "El registro está siendo modificado. Actualice e inténtelo nuevamente",
                request,
                null);
    }
}
