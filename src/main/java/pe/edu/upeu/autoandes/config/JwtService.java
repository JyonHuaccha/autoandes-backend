package pe.edu.upeu.autoandes.config;
import org.springframework.stereotype.Service; import org.springframework.beans.factory.annotation.Value; import io.jsonwebtoken.*; import io.jsonwebtoken.io.Decoders; import io.jsonwebtoken.security.Keys; import javax.crypto.SecretKey; import java.time.Instant; import java.util.*; import org.springframework.security.core.userdetails.UserDetails;
@Service public class JwtService {private final SecretKey key;private final long segundos;
public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiracion-minutos:60}") long minutos){key=Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));segundos=minutos*60;}
public String generar(UserDetails user){var ahora=Instant.now();return Jwts.builder().subject(user.getUsername()).claim("authorities",user.getAuthorities().stream().map(a->a.getAuthority()).toList()).issuedAt(Date.from(ahora)).expiration(Date.from(ahora.plusSeconds(segundos))).signWith(key).compact();}
public String username(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();}
public long expiraEnSegundos(){return segundos;}}
