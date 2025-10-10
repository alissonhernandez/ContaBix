package com.contabix.contabix.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "asientos")
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String descripcion;

    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "asiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleAsiento> detalles = new ArrayList<>();

    public Asiento() {
        this.fecha = LocalDate.now();
    }

    public Asiento(String descripcion, Usuario usuario) {
        this.descripcion = descripcion;
        this.usuario = usuario;
        this.fecha = LocalDate.now();
    }

    // Getters y setters manuales
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<DetalleAsiento> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleAsiento> detalles) { this.detalles = detalles; }
}
