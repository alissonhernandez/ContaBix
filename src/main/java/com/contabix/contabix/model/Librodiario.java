package com.contabix.contabix.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "detalle_asiento")
public class Librodiario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate fecha;

    private String descripcion;

    private Double debe;

    private Double haber;

    @ManyToOne
    @JoinColumn(name = "cuenta_contable_id", nullable = false)
    private CuentaContable cuentaContable;

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getDebe() { return debe; }
    public void setDebe(Double debe) { this.debe = debe; }

    public Double getHaber() { return haber; }
    public void setHaber(Double haber) { this.haber = haber; }

    public CuentaContable getCuentaContable() { return cuentaContable; }
    public void setCuentaContable(CuentaContable cuentaContable) { this.cuentaContable = cuentaContable; }
}
