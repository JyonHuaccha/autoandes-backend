package pe.edu.upeu.autoandes.config;
import org.springframework.stereotype.Component; import org.springframework.boot.CommandLineRunner; import org.springframework.transaction.annotation.Transactional; import org.springframework.security.crypto.password.PasswordEncoder; import lombok.RequiredArgsConstructor; import pe.edu.upeu.autoandes.repository.UsuarioRepository;
@Component @RequiredArgsConstructor public class SemillaPasswords implements CommandLineRunner {private final UsuarioRepository usuarios;private final PasswordEncoder encoder;
@Override @Transactional public void run(String... args){usuarios.findAll().stream().filter(u->u.getPassword()==null||u.getPassword().isBlank()).forEach(u->u.setPassword(encoder.encode("Auto2026*")));}}
