package com.contabix.contabix.model;

import jakarta.persistence.*;

// Representa la entidad "Usuario" en la base de datos
@Entity
// Define el nombre de la tabla asociada en la base de datos
@Table(name = "usuarios")
public class Usuario {

    // Clave primaria autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del usuario
    private String nombre;

    // Apellido del usuario
    private String apellido;

    // Nombre de usuario utilizado para iniciar sesión
    private String usuario;

    // Correo electrónico (único para evitar duplicados)
    @Column(unique = true)
    private String correo;

    // Contraseña del usuario (se debe almacenar cifrada)
    private String contrasena;

    // Relación muchos a uno: varios usuarios pueden tener un mismo rol
    @ManyToOne
    @JoinColumn(name = "rol_id") // Llave foránea que enlaza con la tabla 'roles'
    private Rol rol;

    // ===== Getters y Setters =====

    // Devuelve el ID del usuario
    public Long getId() { return id; }

    // Asigna un ID al usuario
    public void setId(Long id) { this.id = id; }

    // Devuelve el apellido del usuario
    public String getApellido() { return apellido; }

    // Asigna un apellido al usuario
    public void setApellido(String apellido) { this.apellido = apellido; }

    // Devuelve el nombre del usuario
    public String getNombre() { return nombre; }

    // Asigna un nombre al usuario
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Devuelve el correo del usuario
    public String getCorreo() { return correo; }

    // Asigna un correo al usuario
    public void setCorreo(String correo) { this.correo = correo; }

    // Devuelve el nombre de usuario
    public String getUsuario() { return usuario; }

    // Asigna un nombre de usuario
    public void setUsuario(String usuario) { this.usuario = usuario; }

    // Devuelve la contraseña
    public String getContrasena() { return contrasena; }

    // Asigna la contraseña
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    // Devuelve el rol asociado al usuario
    public Rol getRol() { return rol; }

    // Asigna un rol al usuario
    public void setRol(Rol rol) { this.rol = rol; }
}
