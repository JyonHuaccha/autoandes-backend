package pe.edu.upeu.autoandes.repository;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.edu.upeu.autoandes.entity.OrdenServicio;
import pe.edu.upeu.autoandes.enums.EstadoOrden;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdenServicioRepository extends JpaRepository<OrdenServicio, Long> {
    boolean existsByVehiculoId(Long vehiculoId);

    Optional<OrdenServicio> findFirstByVehiculoIdAndEstadoOrderByFechaDescIdDesc(
            Long vehiculoId, EstadoOrden estado);

    default Integer ultimoKilometraje(Long id) {
        return findFirstByVehiculoIdAndEstadoOrderByFechaDescIdDesc(id, EstadoOrden.REGISTRADA)
                .map(OrdenServicio::getKilometraje)
                .orElse(null);
    }

    @Query(value = "SELECT orden_numero_seq.NEXTVAL FROM dual", nativeQuery = true)
    Long siguienteNumero();

    @Query(
            """
            SELECT o FROM OrdenServicio o
            JOIN FETCH o.vehiculo v
            JOIN FETCH v.cliente
            WHERE (:placa IS NULL OR UPPER(v.placa) LIKE CONCAT('%',CONCAT(UPPER(:placa),'%')))
              AND (:estado IS NULL OR o.estado = :estado)
              AND (:desde IS NULL OR o.fecha >= :desde)
              AND (:hasta IS NULL OR o.fecha < :hasta)
            ORDER BY o.fecha DESC,o.id DESC
            """)
    List<OrdenServicio> consultar(
            @Param("placa") String placa,
            @Param("estado") EstadoOrden estado,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrdenServicio o WHERE o.id=:id")
    Optional<OrdenServicio> bloquear(@Param("id") Long id);
}
