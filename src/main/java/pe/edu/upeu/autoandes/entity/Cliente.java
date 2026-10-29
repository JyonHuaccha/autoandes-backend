package pe.edu.upeu.autoandes.entity;
import jakarta.persistence.*; import lombok.Getter; import lombok.Setter;
 @Entity @Table(name="cliente") @Getter @Setter
 public class Cliente { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String documento; private String nombres; private String telefono; }
