package com.iso9001.models;

import com.iso9001.enums.EstadoNoConformidad;
import com.iso9001.enums.Prioridad;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NoConformidad {
    private String id;
    private String titulo;
    private String descripcion;
    private EstadoNoConformidad estado;
    private Prioridad prioridad;
    private LocalDate fechaDeteccion;
    private LocalDate fechaLimiteCorreccion;
    private LocalDate fechaCierre;
    private String procesoAfectado;
    private String responsableDeteccion;
    private String responsableCorreccion;
    private String causaRaiz;
    private List<AccionCorrectiva> accionesCorrectivas;
    private String observaciones;
    private double impactoEconomico;

    // Constructor
    public NoConformidad(String id, String titulo, String descripcion,
                         Prioridad prioridad, String procesoAfectado, String responsableDeteccion) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.procesoAfectado = procesoAfectado;
        this.responsableDeteccion = responsableDeteccion;
        this.estado = EstadoNoConformidad.ABIERTA;
        this.fechaDeteccion = LocalDate.now();
        this.accionesCorrectivas = new ArrayList<>();

        // Calcular fecha límite basada en prioridad
        calcularFechaLimite();
    }

    // Métodos de negocio
    private void calcularFechaLimite() {
        switch (prioridad) {
            case CRITICA:
                this.fechaLimiteCorreccion = fechaDeteccion.plusDays(7);
                break;
            case ALTA:
                this.fechaLimiteCorreccion = fechaDeteccion.plusDays(15);
                break;
            case MEDIA:
                this.fechaLimiteCorreccion = fechaDeteccion.plusDays(30);
                break;
            case BAJA:
                this.fechaLimiteCorreccion = fechaDeteccion.plusDays(60);
                break;
        }
    }

    public void agregarAccionCorrectiva(AccionCorrectiva accion) {
        if (!accionesCorrectivas.contains(accion)) {
            accionesCorrectivas.add(accion);
        }
    }

    public void cambiarEstado(EstadoNoConformidad nuevoEstado) {
        this.estado = nuevoEstado;
        if (nuevoEstado == EstadoNoConformidad.CERRADA) {
            this.fechaCierre = LocalDate.now();
        }
    }

    public boolean estaVencida() {
        return LocalDate.now().isAfter(fechaLimiteCorreccion) &&
                estado != EstadoNoConformidad.CERRADA;
    }

    public long diasParaVencimiento() {
        if (estado == EstadoNoConformidad.CERRADA) return 0;
        return LocalDate.now().until(fechaLimiteCorreccion).getDays();
    }

    public int calcularPorcentajeCompletitud() {
        if (accionesCorrectivas.isEmpty()) return 0;

        long accionesCompletadas = accionesCorrectivas.stream()
                .mapToLong(accion -> accion.isCompletada() ? 1 : 0)
                .sum();

        return (int) ((accionesCompletadas * 100) / accionesCorrectivas.size());
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public EstadoNoConformidad getEstado() { return estado; }
    public void setEstado(EstadoNoConformidad estado) { this.estado = estado; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
        calcularFechaLimite(); // Recalcular fecha límite
    }

    public LocalDate getFechaDeteccion() { return fechaDeteccion; }
    public void setFechaDeteccion(LocalDate fechaDeteccion) { this.fechaDeteccion = fechaDeteccion; }

    public LocalDate getFechaLimiteCorreccion() { return fechaLimiteCorreccion; }
    public void setFechaLimiteCorreccion(LocalDate fechaLimiteCorreccion) { this.fechaLimiteCorreccion = fechaLimiteCorreccion; }

    public LocalDate getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDate fechaCierre) { this.fechaCierre = fechaCierre; }

    public String getProcesoAfectado() { return procesoAfectado; }
    public void setProcesoAfectado(String procesoAfectado) { this.procesoAfectado = procesoAfectado; }

    public String getResponsableDeteccion() { return responsableDeteccion; }
    public void setResponsableDeteccion(String responsableDeteccion) { this.responsableDeteccion = responsableDeteccion; }

    public String getResponsableCorreccion() { return responsableCorreccion; }
    public void setResponsableCorreccion(String responsableCorreccion) { this.responsableCorreccion = responsableCorreccion; }

    public String getCausaRaiz() { return causaRaiz; }
    public void setCausaRaiz(String causaRaiz) { this.causaRaiz = causaRaiz; }

    public List<AccionCorrectiva> getAccionesCorrectivas() { return new ArrayList<>(accionesCorrectivas); }
    public void setAccionesCorrectivas(List<AccionCorrectiva> accionesCorrectivas) { this.accionesCorrectivas = new ArrayList<>(accionesCorrectivas); }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public double getImpactoEconomico() { return impactoEconomico; }
    public void setImpactoEconomico(double impactoEconomico) { this.impactoEconomico = impactoEconomico; }

    @Override
    public String toString() {
        return String.format("NoConformidad{id='%s', titulo='%s', estado=%s, prioridad=%s}",
                id, titulo, estado, prioridad);
    }
}