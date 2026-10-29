package pe.edu.upeu.autoandes.entity;
import jakarta.persistence.*; import lombok.Getter; import lombok.Setter;
 @Entity @Table(name="usuarios") @Getter @Setter
 public class Usuario { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String username; private String password; @Column(name="nombre_completo") private String nombreCompleto; private Boolean activo;
@ManyToMany(fetch=FetchType.LAZY) @JoinTable(name="usuario_roles",joinColumns=@JoinColumn(name="usuario_id"),inverseJoinColumns=@JoinColumn(name="rol_id")) private java.util.Set<Rol> roles=new java.util.HashSet<>(); }
