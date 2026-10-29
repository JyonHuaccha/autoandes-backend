package pe.edu.upeu.autoandes.entity;
import jakarta.persistence.*; import lombok.Getter; import lombok.Setter;
 @Entity @Table(name="orden_servicio") @Getter @Setter
 public class OrdenServicio { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String numero; private java.time.LocalDateTime fecha;
@ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="vehiculo_id") private Vehiculo vehiculo;
private Integer kilometraje; private String estado; private java.math.BigDecimal total;
@Column(name="usuario_registro") private String usuarioRegistro;
@Column(name="motivo_anulacion") private String motivoAnulacion;
@OneToMany(mappedBy="orden",cascade=CascadeType.ALL,orphanRemoval=true) private java.util.List<DetalleOrden> detalles=new java.util.ArrayList<>(); }
