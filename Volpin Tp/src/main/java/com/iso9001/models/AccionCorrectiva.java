package com.iso9001.models;

import com.iso9001.enums.Prioridad;
import java.time.LocalDate;

public class AccionCorrectiva {
    private String id;
    private String descripcion;
    private String responsable;
    private LocalDate fechaInicio;
    private LocalDate fechaLimite;
    private LocalDate fechaCompletada;
    private boolean completada;
    private String noConformidadId;
    private String resultadoObtenido;
    private Prioridad prioridad;
    private double costoEstimado;
    private String observaciones;

    // Constructor
    public AccionCorrectiva(String id, String descripcion, String responsable,
                            LocalDate fechaLimite, String noConformidadId) {
        this.id = id;
        this.descripcion = descripcion;
        this.responsable = responsable;
        this.fechaLimite = fechaLimite;
        this.noConformidadId = noConformidadId;
        this.fechaInicio = LocalDate.now();
        this.completada = false;
        this.prioridad = Prioridad.MEDIA;
    }

    // Métodos de negocio
    public void marcarComoCompletada(String resultado) {
        this.completada = true;
        this.fechaCompletada = LocalDate.now();
        this.resultadoObtenido = resultado;
    }

    public boolean estaVencida() {
        return !completada && LocalDate.now().isAfter(fechaLimite);
    }

    public long diasParaVencimiento() {
        if (completada) return 0;
        return LocalDate.now().until(fechaLimite).getDays();
    }

    public int calcularPorcentajeAvance() {
        if (completada) return 100;

        long diasTotales = fechaInicio.until(fechaLimite).getDays();
        long diasTranscurridos = fechaInicio.until(LocalDate.now()).getDays();

        if (diasTotales <= 0) return 0;

        int porcentaje = (int) ((diasTranscurridos * 100) / diasTotales);
        return Math.min(porcentaje, 99); // Máximo 99% si no está completada
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }

    public LocalDate getFechaCompletada() { return fechaCompletada; }
    public void setFechaCompletada(LocalDate fechaCompletada) { this.fechaCompletada = fechaCompletada; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }

    public String getNoConformidadId() { return noConformidadId; }
    public void setNoConformidadId(String noConformidadId) { this.noConformidadId = noConformidadId; }

    public String getResultadoObtenido() { return resultadoObtenido; }
    public void setResultadoObtenido(String resultadoObtenido) { this.resultadoObtenido = resultadoObtenido; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public double getCostoEstimado() { return costoEstimado; }
    public void setCostoEstimado(double costoEstimado) { this.costoEstimado = costoEstimado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
        return String.format("AccionCorrectiva{id='%s', descripcion='%s', completada=%s}",
                id, descripcion, completada);
    }
}
