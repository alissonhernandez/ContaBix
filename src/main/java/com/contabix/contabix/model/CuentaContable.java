package com.contabix.contabix.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cuentas_contables")
public class CuentaContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String codigo;   // AGREGAR ESTE CAMPO
    private String nombre;

    private String tipo; // ingreso, gasto, pasivo, patrimonio

    private String categoria; // costo, operativo, etc.

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}

