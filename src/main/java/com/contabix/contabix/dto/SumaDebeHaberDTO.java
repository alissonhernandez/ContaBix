package com.contabix.contabix.dto;

public class SumaDebeHaberDTO {
    private Double debe;
    private Double haber;

    public SumaDebeHaberDTO(Double debe, Double haber) {
        this.debe = debe;
        this.haber = haber;
    }

    public Double getDebe() { return debe; }
    public Double getHaber() { return haber; }
}


