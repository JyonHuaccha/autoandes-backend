package pe.edu.upeu.autoandes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.edu.upeu.autoandes.dto.reporte.IngresoPorServicioDTO;
import pe.edu.upeu.autoandes.entity.DetalleOrden;

import java.time.LocalDateTime;
import java.util.List;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
    boolean existsByServicioId(Long servicioId);

    @Query(
            """
            SELECT new pe.edu.upeu.autoandes.dto.reporte.IngresoPorServicioDTO(
                s.codigo, s.nombre, SUM(d.cantidad), SUM(d.subtotal))
            FROM DetalleOrden d
            JOIN d.orden o
            JOIN d.servicio s
            WHERE o.estado = pe.edu.upeu.autoandes.enums.EstadoOrden.REGISTRADA
              AND (:desde IS NULL OR o.fecha >= :desde)
              AND (:hasta IS NULL OR o.fecha < :hasta)
            GROUP BY s.codigo, s.nombre
            ORDER BY SUM(d.subtotal) DESC, s.codigo ASC
            """)
    List<IngresoPorServicioDTO> ingresos(
            @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
