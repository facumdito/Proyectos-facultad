package com.iso9001.models;

import com.iso9001.enums.TipoProceso;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Proceso {
    private String id;
    private String nombre;
    private String descripcion;
    private TipoProceso tipo;
    private String responsable;
    private LocalDate fechaCreacion;
    private LocalDate fechaUltimaRevision;
    private boolean activo;
    private List<Indicador> indicadores;
    private String objetivo;
    private String alcance;

    // Constructor
    public Proceso(String id, String nombre, String descripcion, TipoProceso tipo, String responsable) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.responsable = responsable;
        this.fechaCreacion = LocalDate.now();
        this.fechaUltimaRevision = LocalDate.now();
        this.activo = true;
        this.indicadores = new ArrayList<>();
    }

    // Constructor para cargar desde archivo
    public Proceso(String id, String nombre, String descripcion, TipoProceso tipo,
                   String responsable, String objetivo, String alcance) {
        this(id, nombre, descripcion, tipo, responsable);
        this.objetivo = objetivo;
        this.alcance = alcance;
    }

    // Métodos de negocio
    public void agregarIndicador(Indicador indicador) {
        if (!indicadores.contains(indicador)) {
            indicadores.add(indicador);
        }
    }

    public void removerIndicador(Indicador indicador) {
        indicadores.remove(indicador);
    }

    public double calcularEficienciaGeneral() {
        if (indicadores.isEmpty()) return 0.0;

        return indicadores.stream()
                .mapToDouble(Indicador::getValorActual)
                .average()
                .orElse(0.0);
    }

    public void actualizarFechaRevision() {
        this.fechaUltimaRevision = LocalDate.now();
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoProceso getTipo() { return tipo; }
    public void setTipo(TipoProceso tipo) { this.tipo = tipo; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDate getFechaUltimaRevision() { return fechaUltimaRevision; }
    public void setFechaUltimaRevision(LocalDate fechaUltimaRevision) { this.fechaUltimaRevision = fechaUltimaRevision; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public List<Indicador> getIndicadores() { return new ArrayList<>(indicadores); }
    public void setIndicadores(List<Indicador> indicadores) { this.indicadores = new ArrayList<>(indicadores); }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public String getAlcance() { return alcance; }
    public void setAlcance(String alcance) { this.alcance = alcance; }

    @Override
    public String toString() {
        return String.format("Proceso{id='%s', nombre='%s', tipo=%s, responsable='%s'}",
                id, nombre, tipo, responsable);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Proceso proceso = (Proceso) obj;
        return id.equals(proceso.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}