package pe.edu.upeu.autoandes.entity;
import jakarta.persistence.*; import lombok.Getter; import lombok.Setter;
 @Entity @Table(name="vehiculo") @Getter @Setter
 public class Vehiculo { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String placa; private String marca; private String modelo; private Integer anio; @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="cliente_id") private Cliente cliente; }
