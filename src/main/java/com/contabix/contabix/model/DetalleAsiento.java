package com.contabix.contabix.model;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "detalle_asiento")
public class DetalleAsiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double debe;
    private Double haber;

    @ManyToOne
    @JoinColumn(name = "asiento_id")
    private Asiento asiento;

    @ManyToOne
    @JoinColumn(name = "cuenta_id")
    private CuentaContable cuenta;
}
