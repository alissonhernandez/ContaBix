package com.contabix.contabix.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "periodos_contables")
public class PeriodoContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private boolean cerrado; // true = el periodo ya está cerrado

    public Integer getId() { return id; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public boolean isCerrado() { return cerrado; }

    public void setId(Integer id) { this.id = id; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public void setCerrado(boolean cerrado) { this.cerrado = cerrado; }
}

