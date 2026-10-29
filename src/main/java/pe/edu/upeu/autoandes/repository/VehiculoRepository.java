package pe.edu.upeu.autoandes.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.edu.upeu.autoandes.entity.Vehiculo;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    boolean existsByPlacaIgnoreCase(String placa);

    boolean existsByPlacaIgnoreCaseAndIdNot(String placa, Long id);

    @EntityGraph(attributePaths = "cliente")
    @Query(
            "select v from Vehiculo v where (:clienteId is null or v.cliente.id=:clienteId) order"
                    + " by v.placa")
    List<Vehiculo> listar(@Param("clienteId") Long clienteId);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Vehiculo v where v.id=:id")
    Optional<Vehiculo> bloquear(@Param("id") Long id);
}
