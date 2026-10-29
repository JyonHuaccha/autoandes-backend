package pe.edu.upeu.autoandes.repository;
import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*; import pe.edu.upeu.autoandes.entity.*;
public interface RolRepository extends JpaRepository<Rol,Long> {  }
