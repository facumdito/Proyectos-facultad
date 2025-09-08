package com.iso9001.enums;

public enum EstadoNoConformidad {
    ABIERTA("Abierta", "No conformidad detectada y registrada"),
    EN_ANALISIS("En Análisis", "Se está investigando la causa raíz"),
    EN_CORRECCION("En Corrección", "Se están implementando las acciones"),
    CERRADA("Cerrada", "No conformidad resuelta y verificada"),
    RECHAZADA("Rechazada", "No se considera no conformidad válida");

    private final String nombre;
    private final String descripcion;

    EstadoNoConformidad(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() { return nombre; }
}