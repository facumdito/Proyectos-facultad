package com.iso9001.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Auditoria {
    private String id;
    private String titulo;
    private String tipo; // "Interna" o "Externa"
    private LocalDate fechaProgramada;
    private LocalDate fechaRealizada;
    private String auditorLider;
    private List<String> auditores;
    private List<String> procesosAuditados;
    private String alcance;
    private String objetivos;
    private String metodologia;
    private List<String> hallazgos;
    private List<String> noConformidadesDetectadas;
    private String conclusion;
    private double calificacionGeneral;
    private boolean completada;
    private String observaciones;

    // Constructor
    public Auditoria(String id, String titulo, String tipo, LocalDate fechaProgramada, String auditorLider) {
        this.id = id;
        this.titulo = titulo;
        this.tipo = tipo;
        this.fechaProgramada = fechaProgramada;
        this.auditorLider = auditorLider;
        this.auditores = new ArrayList<>();
        this.procesosAuditados = new ArrayList<>();
        this.hallazgos = new ArrayList<>();
        this.noConformidadesDetectadas = new ArrayList<>();
        this.completada = false;
    }

    // Métodos de negocio
    public void agregarAuditor(String auditor) {
        if (!auditores.contains(auditor)) {
            auditores.add(auditor);
        }
    }

    public void agregarProcesoAuditado(String procesoId) {
        if (!procesosAuditados.contains(procesoId)) {
            procesosAuditados.add(procesoId);
        }
    }

    public void agregarHallazgo(String hallazgo) {
        hallazgos.add(hallazgo);
    }

    public void agregarNoConformidad(String noConformidadId) {
        if (!noConformidadesDetectadas.contains(noConformidadId)) {
            noConformidadesDetectadas.add(noConformidadId);
        }
    }

    public void completarAuditoria(LocalDate fechaReal, String conclusion, double calificacion) {
        this.fechaRealizada = fechaReal;
        this.conclusion = conclusion;
        this.calificacionGeneral = calificacion;
        this.completada = true;
    }

    public boolean estaVencida() {
        return !completada && LocalDate.now().isAfter(fechaProgramada);
    }

    public String getEstadoAuditoria() {
        if (completada) return "COMPLETADA";
        if (estaVencida()) return "VENCIDA";
        if (LocalDate.now().isEqual(fechaProgramada)) return "EN_CURSO";
        return "PROGRAMADA";
    }

    public int cantidadNoConformidades() {
        return noConformidadesDetectadas.size();
    }

    public int cantidadHallazgos() {
        return hallazgos.size();
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDate fechaProgramada) { this.fechaProgramada = fechaProgramada; }

    public LocalDate getFechaRealizada() { return fechaRealizada; }
    public void setFechaRealizada(LocalDate fechaRealizada) { this.fechaRealizada = fechaRealizada; }

    public String getAuditorLider() { return auditorLider; }
    public void setAuditorLider(String auditorLider) { this.auditorLider = auditorLider; }

    public List<String> getAuditores() { return new ArrayList<>(auditores); }
    public void setAuditores(List<String> auditores) { this.auditores = new ArrayList<>(auditores); }

    public List<String> getProcesosAuditados() { return new ArrayList<>(procesosAuditados); }
    public void setProcesosAuditados(List<String> procesosAuditados) { this.procesosAuditados = new ArrayList<>(procesosAuditados); }

    public String getAlcance() { return alcance; }
    public void setAlcance(String alcance) { this.alcance = alcance; }

    public String getObjetivos() { return objetivos; }
    public void setObjetivos(String objetivos) { this.objetivos = objetivos; }

    public String getMetodologia() { return metodologia; }
    public void setMetodologia(String metodologia) { this.metodologia = metodologia; }

    public List<String> getHallazgos() { return new ArrayList<>(hallazgos); }
    public void setHallazgos(List<String> hallazgos) { this.hallazgos = new ArrayList<>(hallazgos); }

    public List<String> getNoConformidadesDetectadas() { return new ArrayList<>(noConformidadesDetectadas); }
    public void setNoConformidadesDetectadas(List<String> noConformidadesDetectadas) { this.noConformidadesDetectadas = new ArrayList<>(noConformidadesDetectadas); }

    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }

    public double getCalificacionGeneral() { return calificacionGeneral; }
    public void setCalificacionGeneral(double calificacionGeneral) { this.calificacionGeneral = calificacionGeneral; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
        return String.format("Auditoria{id='%s', titulo='%s', tipo='%s', completada=%s}",
                id, titulo, tipo, completada);
    }
}