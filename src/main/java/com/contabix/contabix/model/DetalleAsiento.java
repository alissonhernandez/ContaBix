package com.contabix.contabix.model;

import jakarta.persistence.*;

@Entity
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

    // Getters y setters manuales
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Double getDebe() { return debe; }
    public void setDebe(Double debe) { this.debe = debe; }

    public Double getHaber() { return haber; }
    public void setHaber(Double haber) { this.haber = haber; }

    public Asiento getAsiento() { return asiento; }
    public void setAsiento(Asiento asiento) { this.asiento = asiento; }

    public CuentaContable getCuenta() { return cuenta; }
    public void setCuenta(CuentaContable cuenta) { this.cuenta = cuenta; }
}
