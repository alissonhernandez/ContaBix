package com.contabix.contabix.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "libro_mayor")
public class LibroMayor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private String descripcion;
    private Double debe;
    private Double haber;
    private Double saldo;
    private String cuenta;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getDebe() { return debe; }
    public void setDebe(Double debe) { this.debe = debe; }

    public Double getHaber() { return haber; }
    public void setHaber(Double haber) { this.haber = haber; }

    public Double getSaldo() { return saldo; }
    public void setSaldo(Double saldo) { this.saldo = saldo; }

    public String getCuenta() { return cuenta; }
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }
}