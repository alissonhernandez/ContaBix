package com.contabix.contabix.dto;

public class BalanceGeneralDTO {

    private String codigo;
    private String nombre;
    private String tipo;       // activo, pasivo, patrimonio
    private String categoria;  // corriente, no corriente
    private Double saldo;

    public BalanceGeneralDTO(String codigo, String nombre,
                             String tipo, String categoria, Double saldo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.categoria = categoria;
        this.saldo = saldo;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getCategoria() { return categoria; }
    public Double getSaldo() { return saldo; }
}
