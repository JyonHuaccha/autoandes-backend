package pe.edu.upeu.autoandes.security;
import org.springframework.stereotype.Component; import org.springframework.security.web.access.AccessDeniedHandler; import org.springframework.security.access.AccessDeniedException; import jakarta.servlet.http.*; import java.io.IOException;
@Component public class RestAccessDeniedHandler implements AccessDeniedHandler {public void handle(HttpServletRequest req,HttpServletResponse res,AccessDeniedException ex)throws IOException {RespuestaErrorSeguridad.escribir(req,res,403,"Forbidden","No tiene permisos para esta operación");}}
