package com.iso9001.enums;

public enum Prioridad {
    BAJA("Baja", 1, "#4CAF50"),
    MEDIA("Media", 2, "#FF9800"),
    ALTA("Alta", 3, "#F44336"),
    CRITICA("Crítica", 4, "#9C27B0");

    private final String nombre;
    private final int nivel;
    private final String color;

    Prioridad(String nombre, int nivel, String color) {
        this.nombre = nombre;
        this.nivel = nivel;
        this.color = color;
    }

    public String getNombre() { return nombre; }
    public int getNivel() { return nivel; }
    public String getColor() { return color; }

    @Override
    public String toString() { return nombre; }
}
