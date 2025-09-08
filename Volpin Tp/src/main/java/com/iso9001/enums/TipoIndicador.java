package com.iso9001.enums;

public enum TipoIndicador {
    EFICIENCIA("Eficiencia", "Mide la relación entre recursos utilizados y resultados"),
    EFICACIA("Eficacia", "Mide el grado de cumplimiento de objetivos"),
    CALIDAD("Calidad", "Mide la conformidad con especificaciones"),
    SATISFACCION("Satisfacción", "Mide la satisfacción del cliente"),
    TIEMPO("Tiempo", "Mide los tiempos de ciclo y respuesta"),
    COSTO("Costo", "Mide los costos asociados a los procesos");

    private final String nombre;
    private final String descripcion;

    TipoIndicador(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() { return nombre; }
}
