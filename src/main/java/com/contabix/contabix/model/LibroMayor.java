package com.contabix.contabix.model;

// Importación de las anotaciones de JPA y clases necesarias
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Clase que representa la entidad "LibroMayor" en la base de datos.
 * Esta entidad almacena los registros contables del libro mayor,
 * con los movimientos de debe, haber y el saldo correspondiente.
 */
@Entity
@Table(name = "libro_mayor") // Especifica el nombre de la tabla en la base de datos
public class LibroMayor {

    @Id // Indica que este campo es la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genera el ID automáticamente (auto-incremental)
    private Long id;

    // Fecha del registro contable
    private LocalDate fecha;

    // Descripción o detalle del movimiento
    private String descripcion;

    // Importe cargado en el debe (entrada)
    private Double debe;

    // Importe abonado en el haber (salida)
    private Double haber;

    // Saldo resultante después del movimiento
    private Double saldo;

    // Nombre o código de la cuenta contable asociada
    private String cuenta;

    // ----- Getters y Setters -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getDebe() {
        return debe;
    }

    public void setDebe(Double debe) {
        this.debe = debe;
    }

    public Double getHaber() {
        return haber;
    }

    public void setHaber(Double haber) {
        this.haber = haber;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }
}
