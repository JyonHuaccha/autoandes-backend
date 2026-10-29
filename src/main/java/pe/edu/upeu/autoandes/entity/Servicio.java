package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "servicio")
@Getter
@Setter
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private String nombre;
    private java.math.BigDecimal precio;

    @Column(name = "duracion_min")
    private Integer duracionMin;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(columnDefinition = "NUMBER(1)")
    private Boolean activo;
}
