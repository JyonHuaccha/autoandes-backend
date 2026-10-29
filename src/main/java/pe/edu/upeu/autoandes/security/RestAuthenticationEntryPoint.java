package pe.edu.upeu.autoandes.security;
import org.springframework.stereotype.Component; import org.springframework.security.web.AuthenticationEntryPoint; import org.springframework.security.core.AuthenticationException; import jakarta.servlet.http.*; import java.io.IOException;
@Component public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {public void commence(HttpServletRequest req,HttpServletResponse res,AuthenticationException ex)throws IOException {RespuestaErrorSeguridad.escribir(req,res,401,"Unauthorized","Se requiere un token válido y vigente");}}
