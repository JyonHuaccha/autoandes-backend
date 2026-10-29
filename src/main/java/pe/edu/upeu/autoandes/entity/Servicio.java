package pe.edu.upeu.autoandes.entity;
import jakarta.persistence.*; import lombok.Getter; import lombok.Setter;
 @Entity @Table(name="servicio") @Getter @Setter
 public class Servicio { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String codigo; private String nombre; private java.math.BigDecimal precio; @Column(name="duracion_min") private Integer duracionMin; private Boolean activo; }
