package pe.edu.upeu.autoandes.repository;
import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*; import pe.edu.upeu.autoandes.entity.*;
public interface VehiculoRepository extends JpaRepository<Vehiculo,Long> { boolean existsByPlacaIgnoreCase(String placa); boolean existsByPlacaIgnoreCaseAndIdNot(String placa,Long id);
@EntityGraph(attributePaths="cliente") @Query("select v from Vehiculo v where (:clienteId is null or v.cliente.id=:clienteId) order by v.placa") List<Vehiculo> listar(@Param("clienteId") Long clienteId);
@Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) @Query("select v from Vehiculo v where v.id=:id") Optional<Vehiculo> bloquear(@Param("id") Long id); }
