package pe.edu.upeu.autoandes.mapper;
import org.springframework.stereotype.Component; import pe.edu.upeu.autoandes.entity.*; import pe.edu.upeu.autoandes.dto.*;
@Component public class AutoandesMapper {
public ClienteResponseDTO cliente(Cliente c){return new ClienteResponseDTO(c.getId(),c.getDocumento(),c.getNombres(),c.getTelefono());}
public ServicioResponseDTO servicio(Servicio s){return new ServicioResponseDTO(s.getId(),s.getCodigo(),s.getNombre(),s.getPrecio(),s.getDuracionMin(),s.getActivo());}
public VehiculoResponseDTO vehiculo(Vehiculo v){return new VehiculoResponseDTO(v.getId(),v.getPlaca(),v.getMarca(),v.getModelo(),v.getAnio(),v.getCliente().getId(),v.getCliente().getNombres());}
public DetalleOrdenResponseDTO detalle(DetalleOrden d){return new DetalleOrdenResponseDTO(d.getId(),d.getServicio().getId(),d.getServicio().getCodigo(),d.getServicio().getNombre(),d.getCantidad(),d.getPrecioUnitario(),d.getSubtotal());}
public OrdenResponseDTO orden(OrdenServicio o){return new OrdenResponseDTO(o.getId(),o.getNumero(),o.getFecha(),o.getVehiculo().getId(),o.getVehiculo().getPlaca(),o.getVehiculo().getCliente().getNombres(),o.getKilometraje(),o.getEstado(),o.getTotal(),o.getUsuarioRegistro(),o.getMotivoAnulacion(),o.getDetalles().stream().map(this::detalle).toList());}
}
