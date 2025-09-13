package com.iso9001.managers;

import com.iso9001.models.Indicador;
import com.iso9001.enums.TipoIndicador;
import com.iso9001.utils.DatabaseHelper;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class IndicadorManager {
    private List<Indicador> indicadores;

    public IndicadorManager() {
        this.indicadores = new ArrayList<>();
        cargarIndicadores();
    }

    // Operaciones CRUD
    public void agregarIndicador(Indicador indicador) {
        if (indicador != null && !existeIndicador(indicador.getId())) {
            indicadores.add(indicador);
            guardarCambios();
            System.out.println("Indicador agregado: " + indicador.getNombre());
        } else {
            System.err.println("Error: Indicador nulo o ID ya existe");
        }
    }

    public Indicador obtenerIndicador(String id) {
        return indicadores.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Indicador> obtenerTodosIndicadores() {
        return new ArrayList<>(indicadores);
    }

    public void actualizarIndicador(Indicador indicador) {
        if (indicador != null) {
            for (int i = 0; i < indicadores.size(); i++) {
                if (indicadores.get(i).getId().equals(indicador.getId())) {
                    indicadores.set(i, indicador);
                    guardarCambios();
                    System.out.println("Indicador actualizado: " + indicador.getNombre());
                    return;
                }
            }
            System.err.println("Indicador no encontrado para actualizar");
        }
    }

    public void eliminarIndicador(String id) {
        boolean eliminado = indicadores.removeIf(i -> i.getId().equals(id));
        if (eliminado) {
            guardarCambios();
            System.out.println("Indicador eliminado: " + id);
        }
    }

    // Búsquedas y filtros
    public List<Indicador> buscarPorNombre(String nombre) {
        return indicadores.stream()
                .filter(i -> i.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Indicador> obtenerPorTipo(TipoIndicador tipo) {
        return indicadores.stream()
                .filter(i -> i.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    public List<Indicador> obtenerIndicadoresPorProceso(String procesoId) {
        return indicadores.stream()
                .filter(i -> i.getProcesoId().equals(procesoId))
                .collect(Collectors.toList());
    }

    public List<Indicador> obtenerIndicadoresPorEstado(String estado) {
        return indicadores.stream()
                .filter(i -> estado.equals(i.getEstadoSemaforo()))
                .collect(Collectors.toList());
    }

    // Análisis y métricas
    public Map<String, Long> obtenerEstadisticasPorEstado() {
        Map<String, Long> estadisticas = new HashMap<>();
        estadisticas.put("VERDE", indicadores.stream().filter(i -> "VERDE".equals(i.getEstadoSemaforo())).count());
        estadisticas.put("AMARILLO", indicadores.stream().filter(i -> "AMARILLO".equals(i.getEstadoSemaforo())).count());
        estadisticas.put("ROJO", indicadores.stream().filter(i -> "ROJO".equals(i.getEstadoSemaforo())).count());
        return estadisticas;
    }

    public Map<TipoIndicador, Long> obtenerEstadisticasPorTipo() {
        return indicadores.stream()
                .collect(Collectors.groupingBy(Indicador::getTipo, Collectors.counting()));
    }

    public double calcularPorcentajeCumplimientoGeneral() {
        if (indicadores.isEmpty()) return 0.0;

        long indicadoresEnObjetivo = indicadores.stream()
                .filter(Indicador::estaDentroObjetivo)
                .count();

        return (indicadoresEnObjetivo * 100.0) / indicadores.size();
    }

    public List<Indicador> obtenerIndicadoresCriticos() {
        return indicadores.stream()
                .filter(i -> "ROJO".equals(i.getEstadoSemaforo()))
                .sorted((i1, i2) -> Double.compare(
                        Math.abs(i2.getValorActual() - i2.getValorObjetivo()) / i2.getValorObjetivo(),
                        Math.abs(i1.getValorActual() - i1.getValorObjetivo()) / i1.getValorObjetivo()
                ))
                .collect(Collectors.toList());
    }

    public List<Indicador> obtenerIndicadoresConMejorTendencia() {
        return indicadores.stream()
                .filter(i -> i.calcularTendencia() > 0)
                .sorted((i1, i2) -> Double.compare(i2.calcularTendencia(), i1.calcularTendencia()))
                .limit(5)
                .collect(Collectors.toList());
    }

    // Operaciones de medición
    public void registrarMedicion(String indicadorId, double valor) {
        Indicador indicador = obtenerIndicador(indicadorId);
        if (indicador != null) {
            indicador.registrarMedicion(valor);
            actualizarIndicador(indicador);
            System.out.println("Medición registrada para: " + indicador.getNombre());
        } else {
            System.err.println("Indicador no encontrado: " + indicadorId);
        }
    }

    public void actualizarObjetivo(String indicadorId, double nuevoObjetivo) {
        Indicador indicador = obtenerIndicador(indicadorId);
        if (indicador != null) {
            indicador.setValorObjetivo(nuevoObjetivo);
            actualizarIndicador(indicador);
            System.out.println("Objetivo actualizado para: " + indicador.getNombre());
        }
    }

    // Alertas y notificaciones
    public List<Indicador> obtenerIndicadoresConAlertas() {
        return indicadores.stream()
                .filter(i -> !"VERDE".equals(i.getEstadoSemaforo()))
                .collect(Collectors.toList());
    }

    public List<Indicador> obtenerIndicadoresSinMedicionReciente(int diasLimite) {
        LocalDate fechaLimite = LocalDate.now().minusDays(diasLimite);
        return indicadores.stream()
                .filter(i -> i.getFechaUltimaMedicion().isBefore(fechaLimite))
                .collect(Collectors.toList());
    }

    // Validaciones
    public boolean existeIndicador(String id) {
        return indicadores.stream().anyMatch(i -> i.getId().equals(id));
    }

    public boolean validarIndicador(Indicador indicador) {
        if (indicador == null) return false;
        if (indicador.getId() == null || indicador.getId().trim().isEmpty()) return false;
        if (indicador.getNombre() == null || indicador.getNombre().trim().isEmpty()) return false;
        if (indicador.getTipo() == null) return false;
        if (indicador.getValorObjetivo() < 0) return false;
        return indicador.getProcesoId() != null && !indicador.getProcesoId().trim().isEmpty();
    }

    // Reportes
    public String generarReporteIndicadores() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE INDICADORES\n");
        reporte.append("=".repeat(30)).append("\n");
        reporte.append("Total indicadores: ").append(indicadores.size()).append("\n");
        reporte.append("Cumplimiento general: ").append(String.format("%.1f%%", calcularPorcentajeCumplimientoGeneral())).append("\n\n");

        Map<String, Long> estadisticas = obtenerEstadisticasPorEstado();
        reporte.append("Por estado:\n");
        reporte.append("- Verde: ").append(estadisticas.get("VERDE")).append("\n");
        reporte.append("- Amarillo: ").append(estadisticas.get("AMARILLO")).append("\n");
        reporte.append("- Rojo: ").append(estadisticas.get("ROJO")).append("\n\n");

        List<Indicador> criticos = obtenerIndicadoresCriticos();
        if (!criticos.isEmpty()) {
            reporte.append("Indicadores críticos:\n");
            criticos.stream().limit(5).forEach(i ->
                    reporte.append("- ").append(i.getNombre())
                            .append(" (").append(String.format("%.2f", i.getValorActual()))
                            .append("/").append(String.format("%.2f", i.getValorObjetivo()))
                            .append(")\n")
            );
        }

        return reporte.toString();
    }

    // Métodos de persistencia
    private void cargarIndicadores() {
        this.indicadores = DatabaseHelper.cargarIndicadores();
        System.out.println("Indicadores cargados: " + indicadores.size());
    }

    private void guardarCambios() {
        DatabaseHelper.guardarIndicadores(indicadores);
    }

    // Getters para estadísticas
    public int getTotalIndicadores() {
        return indicadores.size();
    }

    public int getIndicadoresVerde() {
        return (int) indicadores.stream().filter(i -> "VERDE".equals(i.getEstadoSemaforo())).count();
    }

    public int getIndicadoresAmarillo() {
        return (int) indicadores.stream().filter(i -> "AMARILLO".equals(i.getEstadoSemaforo())).count();
    }

    public int getIndicadoresRojo() {
        return (int) indicadores.stream().filter(i -> "ROJO".equals(i.getEstadoSemaforo())).count();
    }
}