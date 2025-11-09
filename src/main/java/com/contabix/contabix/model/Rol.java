package com.contabix.contabix.model;

import jakarta.persistence.*;

// Indica que esta clase representa una entidad de base de datos
@Entity
// Define el nombre de la tabla asociada en la base de datos
@Table(name = "roles")
public class Rol {

    // Clave primaria de la tabla 'roles'
    @Id
    // Genera el ID automáticamente (autoincremental)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del rol (único para evitar duplicados)
    @Column(unique = true)
    private String nombre;

    // ===== Getters y Setters =====
    // Devuelve el ID del rol
    public Long getId() { return id; }

    // Asigna un ID al rol
    public void setId(Long id) { this.id = id; }

    // Devuelve el nombre del rol (ej: "ADMIN", "USUARIO")
    public String getNombre() { return nombre; }

    // Asigna un nombre al rol
    public void setNombre(String nombre) { this.nombre = nombre; }
}
