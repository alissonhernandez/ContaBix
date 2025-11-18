package com.contabix.contabix.dto;

public class EstadoResultadosDTO {

    private double ingresos;
    private double costos;
    private double gastos;
    private double otros;
    private double utilidad;

    public EstadoResultadosDTO(double ingresos, double costos, double gastos, double otros, double utilidad) {
        this.ingresos = ingresos;
        this.costos = costos;
        this.gastos = gastos;
        this.otros = otros;
        this.utilidad = utilidad;
    }

    public double getIngresos() { return ingresos; }
    public double getCostos() { return costos; }
    public double getGastos() { return gastos; }
    public double getOtros() { return otros; }
    public double getUtilidad() { return utilidad; }
}

