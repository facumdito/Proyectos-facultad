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
    private String procesoId; // ID del proceso al que pertenece
    private List<Double> historialValores;
    private List<LocalDate> historialFechas;

    // Constructor
    public Indicador(String id, String nombre, String descripcion, TipoIndicador tipo,
                     String unidadMedida, double valorObjetivo, String procesoId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.unidadMedida = unidadMedida;
        this.valorObjetivo = valorObjetivo;
        this.procesoId = procesoId;
        this.historialValores = new ArrayList<>();
        this.historialFechas = new ArrayList<>();
        this.fechaUltimaMedicion = LocalDate.now();
    }

    // Métodos de negocio
    public void registrarMedicion(double valor) {
        this.valorAnterior = this.valorActual;
        this.valorActual = valor;
        this.fechaUltimaMedicion = LocalDate.now();

        // Agregar al historial
        historialValores.add(valor);
        historialFechas.add(LocalDate.now());

        // Mantener solo los últimos 12 registros
        if (historialValores.size() > 12) {
            historialValores.remove(0);
            historialFechas.remove(0);
        }
    }

    public double calcularTendencia() {
        if (historialValores.size() < 2) return 0.0;

        // Cálculo simple de tendencia (diferencia porcentual)
        double valorInicial = historialValores.get(0);
        double valorFinal = historialValores.get(historialValores.size() - 1);

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
        return Math.abs(valorActual - valorObjetivo) <= (valorObjetivo * 0.1); // 10% de tolerancia
    }

    public String getEstadoSemaforo() {
        double diferencia = Math.abs(valorActual - valorObjetivo) / valorObjetivo;

        if (diferencia <= 0.05) return "VERDE";      // Dentro del 5%
        else if (diferencia <= 0.15) return "AMARILLO"; // Dentro del 15%
        else return "ROJO";                              // Fuera del 15%
    }

    // Getters y Setters
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
    public void setFechaUltimaMedicion(LocalDate fechaUltimaMedicion) { this.fechaUltimaMedicion = fechaUltimaMedicion; }

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