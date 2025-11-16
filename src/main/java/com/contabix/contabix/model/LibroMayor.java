package com.contabix.contabix.model;

import jakarta.persistence.*;

@Entity
@Table(name = "libro_mayor")
public class LibroMayor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "cuenta_contable_id", nullable = false)
    private CuentaContable cuentaContable;

    @Column(name = "total_debe")
    private Double totalDebe;

    @Column(name = "total_haber")
    private Double totalHaber;

    private Double saldo;

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public CuentaContable getCuentaContable() { return cuentaContable; }
    public void setCuentaContable(CuentaContable cuentaContable) { this.cuentaContable = cuentaContable; }

    public Double getTotalDebe() { return totalDebe; }
    public void setTotalDebe(Double totalDebe) { this.totalDebe = totalDebe; }

    public Double getTotalHaber() { return totalHaber; }
    public void setTotalHaber(Double totalHaber) { this.totalHaber = totalHaber; }

    public Double getSaldo() { return saldo; }
    public void setSaldo(Double saldo) { this.saldo = saldo; }
}
