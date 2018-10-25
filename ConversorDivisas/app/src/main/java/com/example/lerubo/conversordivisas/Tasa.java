package com.example.lerubo.conversordivisas;

/**
 * Created by lerubo on 09/11/2017.
 */

public class Tasa {
    private String simbolo;
    private double valor;

    public Tasa() {
    }

    public Tasa(String simbolo, double valor) {
        this.simbolo = simbolo;
        this.valor = valor;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = Double.parseDouble(valor);
    }
}
