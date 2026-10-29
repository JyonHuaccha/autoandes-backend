package pe.edu.upeu.autoandes.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upeu.autoandes.repository.UsuarioRepository;

@Component
public class SemillaPasswords implements CommandLineRunner {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public SemillaPasswords(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        usuarios.findAll().stream()
                .filter(u -> u.getPassword() == null || u.getPassword().isBlank())
                .forEach(u -> u.setPassword(encoder.encode("Auto2026*")));
    }
}
