package com.iso9001.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Empleado {
    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private String cargo;
    private String departamento;
    private LocalDate fechaIngreso;
    private boolean activo;
    private List<String> certificacionesISO;
    private LocalDate ultimaCapacitacion;
    private double evaluacionDesempeno;
    private List<String> responsabilidadesProcesos;
    private String jefe;
    private boolean auditorInterno;

    // Constructor
    public Empleado(String id, String nombre, String apellido, String email,
                    String cargo, String departamento) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.cargo = cargo;
        this.departamento = departamento;
        this.fechaIngreso = LocalDate.now();
        this.activo = true;
        this.certificacionesISO = new ArrayList<>();
        this.responsabilidadesProcesos = new ArrayList<>();
        this.auditorInterno = false;
    }

    // Métodos de negocio
    public void agregarCertificacionISO(String certificacion) {
        if (!certificacionesISO.contains(certificacion)) {
            certificacionesISO.add(certificacion);
        }
    }

    public void agregarResponsabilidadProceso(String procesoId) {
        if (!responsabilidadesProcesos.contains(procesoId)) {
            responsabilidadesProcesos.add(procesoId);
        }
    }

    public void removerResponsabilidadProceso(String procesoId) {
        responsabilidadesProcesos.remove(procesoId);
    }

    public void registrarCapacitacion() {
        this.ultimaCapacitacion = LocalDate.now();
    }

    public boolean necesitaCapacitacion() {
        if (ultimaCapacitacion == null) return true;
        return ultimaCapacitacion.plusMonths(12).isBefore(LocalDate.now());
    }

    public long diasSinCapacitacion() {
        if (ultimaCapacitacion == null) return Long.MAX_VALUE;
        return ultimaCapacitacion.until(LocalDate.now()).getDays();
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public int cantidadCertificaciones() {
        return certificacionesISO.size();
    }

    public int cantidadProcesosResponsable() {
        return responsabilidadesProcesos.size();
    }

    public boolean estaCalificadoParaAuditorias() {
        return auditorInterno &&
                certificacionesISO.contains("ISO 9001") &&
                !necesitaCapacitacion();
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public List<String> getCertificacionesISO() { return new ArrayList<>(certificacionesISO); }
    public void setCertificacionesISO(List<String> certificacionesISO) { this.certificacionesISO = new ArrayList<>(certificacionesISO); }

    public LocalDate getUltimaCapacitacion() { return ultimaCapacitacion; }
    public void setUltimaCapacitacion(LocalDate ultimaCapacitacion) { this.ultimaCapacitacion = ultimaCapacitacion; }

    public double getEvaluacionDesempeno() { return evaluacionDesempeno; }
    public void setEvaluacionDesempeno(double evaluacionDesempeno) { this.evaluacionDesempeno = evaluacionDesempeno; }

    public List<String> getResponsabilidadesProcesos() { return new ArrayList<>(responsabilidadesProcesos); }
    public void setResponsabilidadesProcesos(List<String> responsabilidadesProcesos) { this.responsabilidadesProcesos = new ArrayList<>(responsabilidadesProcesos); }

    public String getJefe() { return jefe; }
    public void setJefe(String jefe) { this.jefe = jefe; }

    public boolean isAuditorInterno() { return auditorInterno; }
    public void setAuditorInterno(boolean auditorInterno) { this.auditorInterno = auditorInterno; }

    @Override
    public String toString() {
        return String.format("Empleado{id='%s', nombre='%s %s', cargo='%s', departamento='%s'}",
                id, nombre, apellido, cargo, departamento);
    }
}