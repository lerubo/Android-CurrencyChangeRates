package com.example.lerubo.conversordivisas;

public class Moneda {
    private String nombre;
    private double cantidad;
    private int imagen;
    private long id;

    public Moneda(String nombre, double cantidad, int imagen, long id) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.imagen = imagen;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getCantidad() {
        return cantidad;
    }

    public int getImagen() {
        return imagen;
    }

    public long getId() {
        return id;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
}
