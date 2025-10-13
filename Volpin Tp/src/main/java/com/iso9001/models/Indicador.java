package com.iso9001.models;

import com.iso9001.enums.TipoIndicador;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Indicador {
    private String id;
    private String nombre;
    private String descripcion;
    private TipoIndicador tipo;
    private String unidadMedida;
    private double valorObjetivo;
    private double valorActual;
    private double valorAnterior;
    private LocalDate fechaUltimaMedicion;
    private String formulaCalculo;
    private String procesoId;
    private List<Double> historialValores;
    private List<LocalDate> historialFechas;

    public Indicador(String id, String nombre, String descripcion,
                     TipoIndicador tipo, String unidadMedida,
                     String procesoId, double valorObjetivo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.unidadMedida = unidadMedida;
        this.procesoId = procesoId;
        this.valorObjetivo = valorObjetivo;
        this.valorActual = 0.0;
        this.valorAnterior = 0.0;
        this.fechaUltimaMedicion = LocalDate.now();
        this.historialValores = new ArrayList<>();
        this.historialFechas = new ArrayList<>();
    }

    public Indicador(String trim, String trim1, String trim2, TipoIndicador tipo, String trim3, double v, String trim4) {
    }

    public void registrarMedicion(double valor) {
        this.valorAnterior = this.valorActual;
        this.valorActual = valor;
        this.fechaUltimaMedicion = LocalDate.now();

        historialValores.add(valor);
        historialFechas.add(LocalDate.now());

        if (historialValores.size() > 12) {
            historialValores.removeFirst();
            historialFechas.removeFirst();
        }
    }

    public double calcularTendencia() {
        if (historialValores.size() < 2) return 0.0;

        double valorInicial = historialValores.getFirst();
        double valorFinal = historialValores.getLast();

        if (valorInicial == 0) return 0.0;
        return ((valorFinal - valorInicial) / valorInicial) * 100;
    }

    public double calcularPromedio() {
        if (historialValores.isEmpty()) return 0.0;

        return historialValores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    public boolean estaDentroObjetivo() {
        return Math.abs(valorActual - valorObjetivo) <= (valorObjetivo * 0.1);
    }

    public String getEstadoSemaforo() {
        double diferencia = Math.abs(valorActual - valorObjetivo) / valorObjetivo;

        if (diferencia <= 0.05) return "VERDE";
        else if (diferencia <= 0.15) return "AMARILLO";
        else return "ROJO";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoIndicador getTipo() { return tipo; }
    public void setTipo(TipoIndicador tipo) { this.tipo = tipo; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public double getValorObjetivo() { return valorObjetivo; }
    public void setValorObjetivo(double valorObjetivo) { this.valorObjetivo = valorObjetivo; }

    public double getValorActual() { return valorActual; }
    public void setValorActual(double valorActual) { this.valorActual = valorActual; }

    public double getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(double valorAnterior) { this.valorAnterior = valorAnterior; }

    public LocalDate getFechaUltimaMedicion() { return fechaUltimaMedicion; }
    public void setFechaUltimaMedicion(LocalDate fechaUltimaMedicion) {
        this.fechaUltimaMedicion = fechaUltimaMedicion;
    }

    public String getFormulaCalculo() { return formulaCalculo; }
    public void setFormulaCalculo(String formulaCalculo) { this.formulaCalculo = formulaCalculo; }

    public String getProcesoId() { return procesoId; }
    public void setProcesoId(String procesoId) { this.procesoId = procesoId; }

    public List<Double> getHistorialValores() { return new ArrayList<>(historialValores); }
    public List<LocalDate> getHistorialFechas() { return new ArrayList<>(historialFechas); }

    @Override
    public String toString() {
        return String.format("Indicador{id='%s', nombre='%s', actual=%.2f, objetivo=%.2f}",
                id, nombre, valorActual, valorObjetivo);
    }
}