package pe.edu.upeu.autoandes.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;

import pe.edu.upeu.autoandes.exception.dto.ErrorResponseDTO;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

public final class RespuestaErrorSeguridad {
    private RespuestaErrorSeguridad() {}

    public static void escribir(
            HttpServletRequest req,
            HttpServletResponse res,
            int status,
            String error,
            String mensaje)
            throws IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter()
                .write(
                        new ObjectMapper()
                                .writeValueAsString(
                                        new ErrorResponseDTO(
                                                LocalDateTime.now(),
                                                status,
                                                error,
                                                mensaje,
                                                req.getRequestURI(),
                                                null)));
    }
}
