package com.contabix.contabix.dto;

public class BalanceComprobacionDTO {

    private String codigo;
    private String nombre;

    private double sumDebe;
    private double sumHaber;

    private double saldoDeudor;
    private double saldoAcreedor;

    public BalanceComprobacionDTO(String codigo, String nombre,
                                  double sumDebe, double sumHaber) {

        this.codigo = codigo;
        this.nombre = nombre;
        this.sumDebe = sumDebe;
        this.sumHaber = sumHaber;

        double saldo = sumDebe - sumHaber;

        if (saldo >= 0) {
            this.saldoDeudor = saldo;
            this.saldoAcreedor = 0;
        } else {
            this.saldoDeudor = 0;
            this.saldoAcreedor = Math.abs(saldo);
        }
    }

    // getters
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getSumDebe() { return sumDebe; }
    public double getSumHaber() { return sumHaber; }
    public double getSaldoDeudor() { return saldoDeudor; }
    public double getSaldoAcreedor() { return saldoAcreedor; }
}
