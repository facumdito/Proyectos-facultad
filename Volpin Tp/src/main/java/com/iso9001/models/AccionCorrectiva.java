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

    public AccionCorrectiva(String id, String descripcion, String responsable,
                            LocalDate fechaLimite, String noConformidadId) {
        this.id = id.trim();
        this.descripcion = descripcion.trim();
        this.responsable = responsable;
        this.fechaLimite = fechaLimite;
        this.noConformidadId = noConformidadId.trim();
        this.fechaInicio = LocalDate.now();
        this.completada = false;
        this.prioridad = Prioridad.MEDIA;
        this.costoEstimado = 0.0;
        this.observaciones = "";
    }

    public AccionCorrectiva(String trim, String trim1, LocalDate fechaLimite) {
    }

    public void marcarComoCompletada() {
        this.completada = true;
        this.fechaCompletada = LocalDate.now();
        this.resultadoObtenido = "";
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
        return Math.min(porcentaje, 99);
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public void setFechaCompletada(LocalDate fechaCompletada) {
        this.fechaCompletada = fechaCompletada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public void setNoConformidadId(String noConformidadId) {
        this.noConformidadId = noConformidadId;
    }

    public void setResultadoObtenido(String resultadoObtenido) {
        this.resultadoObtenido = resultadoObtenido;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public void setCostoEstimado(double costoEstimado) {
        this.costoEstimado = costoEstimado;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getResponsable() {
        return responsable;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public LocalDate getFechaCompletada() {
        return fechaCompletada;
    }

    public boolean isCompletada() {
        return completada;
    }

    public String getNoConformidadId() {
        return noConformidadId;
    }

    public String getResultadoObtenido() {
        return resultadoObtenido;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public double getCostoEstimado() {
        return costoEstimado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String generarResumen() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("ACCIÓN CORRECTIVA\n");
        resumen.append("ID: ").append(id).append("\n");
        resumen.append("Descripción: ").append(descripcion).append("\n");
        resumen.append("Responsable: ").append(responsable).append("\n");
        resumen.append("Prioridad: ").append(prioridad.getNombre()).append("\n");
        resumen.append("Fecha Inicio: ").append(fechaInicio).append("\n");
        resumen.append("Fecha Límite: ").append(fechaLimite).append("\n");

        if (completada) {
            resumen.append("Estado: COMPLETADA\n");
            resumen.append("Fecha Completada: ").append(fechaCompletada).append("\n");
            resumen.append("Resultado: ").append(resultadoObtenido).append("\n");
        } else {
            resumen.append("Estado: PENDIENTE\n");
            resumen.append("Días para vencimiento: ").append(diasParaVencimiento()).append("\n");
            resumen.append("Avance: ").append(calcularPorcentajeAvance()).append("%\n");
        }

        if (costoEstimado > 0) {
            resumen.append("Costo Estimado: $").append(String.format("%.2f", costoEstimado)).append("\n");
        }

        if (observaciones != null && !observaciones.isEmpty()) {
            resumen.append("Observaciones: ").append(observaciones).append("\n");
        }

        return resumen.toString();
    }

    public boolean requiereAtencionUrgente() {
        return !completada && diasParaVencimiento() <= 3 &&
                (prioridad == Prioridad.ALTA || prioridad == Prioridad.CRITICA);
    }

    public String getEstadoDetallado() {
        if (completada) {
            return "COMPLETADA";
        } else if (estaVencida()) {
            return "VENCIDA";
        } else if (diasParaVencimiento() <= 3) {
            return "URGENTE";
        } else if (diasParaVencimiento() <= 7) {
            return "PRÓXIMA A VENCER";
        } else {
            return "EN PROCESO";
        }
    }

    @Override
    public String toString() {
        return String.format("AccionCorrectiva{id='%s', descripcion='%s', completada=%s, prioridad=%s}",
                id, descripcion, completada, prioridad.getNombre());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AccionCorrectiva that = (AccionCorrectiva) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}