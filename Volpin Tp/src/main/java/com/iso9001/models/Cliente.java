package com.iso9001.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private String id;
    private String nombre;
    private String empresa;
    private String email;
    private String telefono;
    private String sector;
    private LocalDate fechaRegistro;
    private boolean activo;
    private List<Double> evaluacionesSatisfaccion;
    private List<String> quejas;
    private List<String> sugerencias;
    private double satisfaccionPromedio;
    private int numeroQuejas;
    private String representanteAsignado;

    // Constructor
    public Cliente(String id, String nombre, String empresa, String email, String sector) {
        this.id = id;
        this.nombre = nombre;
        this.empresa = empresa;
        this.email = email;
        this.sector = sector;
        this.fechaRegistro = LocalDate.now();
        this.activo = true;
        this.evaluacionesSatisfaccion = new ArrayList<>();
        this.quejas = new ArrayList<>();
        this.sugerencias = new ArrayList<>();
        this.numeroQuejas = 0;
    }

    // Métodos de negocio
    public void registrarEvaluacionSatisfaccion(double puntuacion) {
        if (puntuacion >= 1.0 && puntuacion <= 5.0) {
            evaluacionesSatisfaccion.add(puntuacion);
            calcularSatisfaccionPromedio();
        }
    }

    public void registrarQueja(String queja) {
        quejas.add(queja);
        numeroQuejas++;
    }

    public void registrarSugerencia(String sugerencia) {
        sugerencias.add(sugerencia);
    }

    private void calcularSatisfaccionPromedio() {
        if (evaluacionesSatisfaccion.isEmpty()) {
            satisfaccionPromedio = 0.0;
            return;
        }

        satisfaccionPromedio = evaluacionesSatisfaccion.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    public String getNivelSatisfaccion() {
        if (satisfaccionPromedio >= 4.5) return "MUY_SATISFECHO";
        if (satisfaccionPromedio >= 3.5) return "SATISFECHO";
        if (satisfaccionPromedio >= 2.5) return "NEUTRAL";
        if (satisfaccionPromedio >= 1.5) return "INSATISFECHO";
        return "MUY_INSATISFECHO";
    }

    public boolean esClienteRiesgo() {
        return numeroQuejas > 3 || satisfaccionPromedio < 2.5;
    }

    public double getTendenciaSatisfaccion() {
        if (evaluacionesSatisfaccion.size() < 2) return 0.0;

        int ultimas = Math.min(3, evaluacionesSatisfaccion.size());
        List<Double> ultimasEvaluaciones = evaluacionesSatisfaccion.subList(
                evaluacionesSatisfaccion.size() - ultimas,
                evaluacionesSatisfaccion.size()
        );

        if (ultimasEvaluaciones.size() < 2) return 0.0;

        double primera = ultimasEvaluaciones.get(0);
        double ultima = ultimasEvaluaciones.get(ultimasEvaluaciones.size() - 1);

        return ultima - primera;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public List<Double> getEvaluacionesSatisfaccion() { return new ArrayList<>(evaluacionesSatisfaccion); }
    public void setEvaluacionesSatisfaccion(List<Double> evaluacionesSatisfaccion) {
        this.evaluacionesSatisfaccion = new ArrayList<>(evaluacionesSatisfaccion);
        calcularSatisfaccionPromedio();
    }

    public List<String> getQuejas() { return new ArrayList<>(quejas); }
    public void setQuejas(List<String> quejas) {
        this.quejas = new ArrayList<>(quejas);
        this.numeroQuejas = quejas.size();
    }

    public List<String> getSugerencias() { return new ArrayList<>(sugerencias); }
    public void setSugerencias(List<String> sugerencias) { this.sugerencias = new ArrayList<>(sugerencias); }

    public double getSatisfaccionPromedio() { return satisfaccionPromedio; }

    public int getNumeroQuejas() { return numeroQuejas; }

    public String getRepresentanteAsignado() { return representanteAsignado; }
    public void setRepresentanteAsignado(String representanteAsignado) { this.representanteAsignado = representanteAsignado; }

    @Override
    public String toString() {
        return String.format("Cliente{id='%s', nombre='%s', empresa='%s', satisfaccion=%.2f}",
                id, nombre, empresa, satisfaccionPromedio);
    }
}
