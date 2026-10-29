package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orden_servicio")
@Getter
@Setter
public class OrdenServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;
    private java.time.LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehiculo_id")
    private Vehiculo vehiculo;

    private Integer kilometraje;

    @Enumerated(EnumType.STRING)
    private pe.edu.upeu.autoandes.enums.EstadoOrden estado;

    private java.math.BigDecimal total;

    @Column(name = "usuario_registro")
    private String usuarioRegistro;

    @Column(name = "motivo_anulacion")
    private String motivoAnulacion;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<DetalleOrden> detalles = new java.util.ArrayList<>();

    public void agregarDetalle(DetalleOrden detalle) {
        detalles.add(detalle);
        detalle.setOrden(this);
    }
}
