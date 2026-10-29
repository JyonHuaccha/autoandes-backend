package pe.edu.upeu.autoandes.repository;
import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*; import pe.edu.upeu.autoandes.entity.*;
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden,Long> { boolean existsByServicioId(Long servicioId);
@Query("select s.codigo as codigo,s.nombre as servicio,sum(d.cantidad) as cantidadTotal,sum(d.subtotal) as importeTotal from DetalleOrden d join d.orden o join d.servicio s where o.estado='REGISTRADA' and (:desde is null or o.fecha>=:desde) and (:hasta is null or o.fecha<:hasta) group by s.codigo,s.nombre order by sum(d.subtotal) desc,s.codigo asc")
List<pe.edu.upeu.autoandes.dto.IngresoPorServicioDTO> ingresos(@Param("desde") java.time.LocalDateTime desde,@Param("hasta") java.time.LocalDateTime hasta); }
