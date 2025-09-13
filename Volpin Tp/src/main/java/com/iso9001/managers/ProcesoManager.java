package com.iso9001.managers;

import com.iso9001.models.Proceso;
import com.iso9001.models.Indicador;
import com.iso9001.enums.TipoProceso;
import com.iso9001.utils.DatabaseHelper;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ProcesoManager {
    private List<Proceso> procesos;
    private IndicadorManager indicadorManager;

    public ProcesoManager() {
        this.procesos = new ArrayList<>();
        cargarProcesos();
    }

    public ProcesoManager(IndicadorManager indicadorManager) {
        this();
        this.indicadorManager = indicadorManager;
    }

    // Operaciones CRUD
    public void agregarProceso(Proceso proceso) {
        if (proceso != null && !existeProceso(proceso.getId())) {
            procesos.add(proceso);
            guardarCambios();
            System.out.println("Proceso agregado: " + proceso.getNombre());
        } else {
            System.err.println("Error: Proceso nulo o ID ya existe");
        }
    }

    public Proceso obtenerProceso(String id) {
        return procesos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Proceso> obtenerTodosProcesos() {
        return new ArrayList<>(procesos);
    }

    public List<Proceso> obtenerProcesosActivos() {
        return procesos.stream()
                .filter(Proceso::isActivo)
                .collect(Collectors.toList());
    }

    public void actualizarProceso(Proceso proceso) {
        if (proceso != null) {
            for (int i = 0; i < procesos.size(); i++) {
                if (procesos.get(i).getId().equals(proceso.getId())) {
                    proceso.actualizarFechaRevision();
                    procesos.set(i, proceso);
                    guardarCambios();
                    System.out.println("Proceso actualizado: " + proceso.getNombre());
                    return;
                }
            }
            System.err.println("Proceso no encontrado para actualizar");
        }
    }

    public boolean eliminarProceso(String id) {
        Proceso proceso = obtenerProceso(id);
        if (proceso != null) {
            // En lugar de eliminar, desactivar
            proceso.setActivo(false);
            actualizarProceso(proceso);
            System.out.println("Proceso desactivado: " + proceso.getNombre());
            return true;
        }
        return false;
    }

    // Búsquedas y filtros
    public List<Proceso> buscarPorNombre(String nombre) {
        return procesos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Proceso> obtenerPorTipo(TipoProceso tipo) {
        return procesos.stream()
                .filter(p -> p.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    public List<Proceso> obtenerPorResponsable(String responsable) {
        return procesos.stream()
                .filter(p -> p.getResponsable().toLowerCase().contains(responsable.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Análisis y métricas
    public Map<TipoProceso, Long> obtenerEstadisticasPorTipo() {
        return procesos.stream()
                .filter(Proceso::isActivo)
                .collect(Collectors.groupingBy(Proceso::getTipo, Collectors.counting()));
    }

    public List<Proceso> obtenerProcesosConIndicadoresCriticos() {
        if (indicadorManager == null) return new ArrayList<>();

        return procesos.stream()
                .filter(p -> {
                    List<Indicador> indicadoresProceso = indicadorManager.obtenerIndicadoresPorProceso(p.getId());
                    return indicadoresProceso.stream()
                            .anyMatch(i -> "ROJO".equals(i.getEstadoSemaforo()));
                })
                .collect(Collectors.toList());
    }

    public double calcularEficienciaPromedio() {
        return procesos.stream()
                .filter(Proceso::isActivo)
                .mapToDouble(Proceso::calcularEficienciaGeneral)
                .average()
                .orElse(0.0);
    }

    public List<Proceso> obtenerProcesosSinRevision(int diasLimite) {
        LocalDate fechaLimite = LocalDate.now().minusDays(diasLimite);
        return procesos.stream()
                .filter(p -> p.isActivo() && p.getFechaUltimaRevision().isBefore(fechaLimite))
                .collect(Collectors.toList());
    }

    // Validaciones
    public boolean existeProceso(String id) {
        return procesos.stream().anyMatch(p -> p.getId().equals(id));
    }

    public boolean validarProceso(Proceso proceso) {
        if (proceso == null) return false;
        if (proceso.getId() == null || proceso.getId().trim().isEmpty()) return false;
        if (proceso.getNombre() == null || proceso.getNombre().trim().isEmpty()) return false;
        if (proceso.getTipo() == null) return false;
        return proceso.getResponsable() != null && !proceso.getResponsable().trim().isEmpty();
    }

    // Reportes
    public String generarReporteProcesos() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE PROCESOS\n");
        reporte.append("=".repeat(30)).append("\n");
        reporte.append("Total procesos: ").append(procesos.size()).append("\n");
        reporte.append("Procesos activos: ").append(obtenerProcesosActivos().size()).append("\n\n");

        Map<TipoProceso, Long> estadisticas = obtenerEstadisticasPorTipo();
        reporte.append("Por tipo de proceso:\n");
        for (Map.Entry<TipoProceso, Long> entry : estadisticas.entrySet()) {
            reporte.append("- ").append(entry.getKey().getNombre())
                    .append(": ").append(entry.getValue()).append("\n");
        }

        return reporte.toString();
    }

    // Métodos de persistencia
    private void cargarProcesos() {
        this.procesos = DatabaseHelper.cargarProcesos();
        System.out.println("Procesos cargados: " + procesos.size());
    }

    private void guardarCambios() {
        DatabaseHelper.guardarProcesos(procesos);
    }

    // Getters
    public int getTotalProcesos() {
        return procesos.size();
    }

    public int getProcesosActivos() {
        return (int) procesos.stream().filter(Proceso::isActivo).count();
    }
}