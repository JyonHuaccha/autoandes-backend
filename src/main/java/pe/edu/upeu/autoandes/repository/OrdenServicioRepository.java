package pe.edu.upeu.autoandes.repository;
import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*; import pe.edu.upeu.autoandes.entity.*;
public interface OrdenServicioRepository extends JpaRepository<OrdenServicio,Long> { boolean existsByVehiculoId(Long vehiculoId);
@Query("select max(o.kilometraje) from OrdenServicio o where o.vehiculo.id=:id and o.estado='REGISTRADA'") Integer ultimoKilometraje(@Param("id") Long id);
@Query(value="select orden_numero_seq.nextval from dual",nativeQuery=true) Long siguienteNumero();
@Query("select o from OrdenServicio o join fetch o.vehiculo v join fetch v.cliente where (:placa is null or upper(v.placa) like concat('%',concat(upper(:placa),'%'))) and (:estado is null or o.estado=:estado) and (:desde is null or o.fecha>=:desde) and (:hasta is null or o.fecha<:hasta) order by o.fecha desc,o.id desc")
List<OrdenServicio> consultar(@Param("placa") String placa,@Param("estado") String estado,@Param("desde") java.time.LocalDateTime desde,@Param("hasta") java.time.LocalDateTime hasta);
@Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) @Query("select o from OrdenServicio o where o.id=:id") Optional<OrdenServicio> bloquear(@Param("id") Long id); }
