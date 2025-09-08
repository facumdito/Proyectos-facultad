package com.iso9001.enums;

public enum TipoProceso {
    ESTRATEGICO("Estratégico", "Procesos de dirección y planificación"),
    OPERATIVO("Operativo", "Procesos que generan valor directo al cliente"),
    APOYO("Apoyo", "Procesos que soportan los procesos operativos"),
    MEJORA("Mejora", "Procesos de mejora continua y calidad");

    private final String nombre;
    private final String descripcion;

    TipoProceso(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() { return nombre; }
}