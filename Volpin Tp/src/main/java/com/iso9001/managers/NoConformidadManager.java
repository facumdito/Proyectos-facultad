package com.iso9001.managers;

import com.iso9001.models.NoConformidad;
import com.iso9001.models.AccionCorrectiva;
import com.iso9001.enums.EstadoNoConformidad;
import com.iso9001.enums.Prioridad;
import com.iso9001.utils.DatabaseHelper;

import java.util.*;
import java.util.stream.Collectors;

public class NoConformidadManager {
    private List<NoConformidad> noConformidades;
    private final List<AccionCorrectiva> accionesCorrectivas;

    public NoConformidadManager() {
        this.noConformidades = new ArrayList<>();
        this.accionesCorrectivas = new ArrayList<>();
        cargarNoConformidades();
    }

    public void agregarNoConformidad(NoConformidad noConformidad) {
        if (noConformidad != null && !existeNoConformidad(noConformidad.getId())) {
            noConformidades.add(noConformidad);
            guardarCambios();
            System.out.println("No conformidad agregada: " + noConformidad.getTitulo());
        } else {
            System.err.println("Error: No conformidad nula o ID ya existe");
        }
    }

    public NoConformidad obtenerNoConformidad(String id) {
        return noConformidades.stream()
                .filter(nc -> nc.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<NoConformidad> obtenerTodasNoConformidades() {
        return new ArrayList<>(noConformidades);
    }

    public void actualizarNoConformidad(NoConformidad noConformidad) {
        if (noConformidad != null) {
            for (int i = 0; i < noConformidades.size(); i++) {
                if (noConformidades.get(i).getId().equals(noConformidad.getId())) {
                    noConformidades.set(i, noConformidad);
                    guardarCambios();
                    System.out.println("No conformidad actualizada: " + noConformidad.getTitulo());
                    return;
                }
            }
            System.err.println("No conformidad no encontrada para actualizar");
        }
    }

    public boolean eliminarNoConformidad(String id) {
        boolean eliminada = noConformidades.removeIf(nc -> nc.getId().equals(id));
        if (eliminada) {
            accionesCorrectivas.removeIf(ac -> ac.getNoConformidadId().equals(id));
            guardarCambios();
            System.out.println("No conformidad eliminada: " + id);
        }
        return eliminada;
    }

    public List<NoConformidad> buscarPorTitulo(String titulo) {
        return noConformidades.stream()
                .filter(nc -> nc.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<NoConformidad> obtenerPorEstado(EstadoNoConformidad estado) {
        return noConformidades.stream()
                .filter(nc -> nc.getEstado() == estado)
                .collect(Collectors.toList());
    }

    public List<NoConformidad> obtenerPorPrioridad(Prioridad prioridad) {
        return noConformidades.stream()
                .filter(nc -> nc.getPrioridad() == prioridad)
                .collect(Collectors.toList());
    }

    public List<NoConformidad> obtenerPorProceso(String procesoId) {
        return noConformidades.stream()
                .filter(nc -> nc.getProcesoAfectado().equals(procesoId))
                .collect(Collectors.toList());
    }

    public List<NoConformidad> obtenerPorResponsable(String responsable) {
        return noConformidades.stream()
                .filter(nc -> nc.getResponsableDeteccion().toLowerCase().contains(responsable.toLowerCase()) ||
                        (nc.getResponsableCorreccion() != null &&
                                nc.getResponsableCorreccion().toLowerCase().contains(responsable.toLowerCase())))
                .collect(Collectors.toList());
    }

    public Map<EstadoNoConformidad, Long> obtenerEstadisticasPorEstado() {
        return noConformidades.stream()
                .collect(Collectors.groupingBy(NoConformidad::getEstado, Collectors.counting()));
    }

    public Map<Prioridad, Long> obtenerEstadisticasPorPrioridad() {
        return noConformidades.stream()
                .collect(Collectors.groupingBy(NoConformidad::getPrioridad, Collectors.counting()));
    }

    public List<NoConformidad> obtenerNoConformidadesVencidas() {
        return noConformidades.stream()
                .filter(NoConformidad::estaVencida)
                .sorted(Comparator.comparingLong(NoConformidad::diasParaVencimiento))
                .collect(Collectors.toList());
    }

    public List<NoConformidad> obtenerNoConformidadesPorVencer(int diasAnticipacion) {
        return noConformidades.stream()
                .filter(nc -> !nc.estaVencida() && nc.diasParaVencimiento() <= diasAnticipacion)
                .sorted(Comparator.comparingLong(NoConformidad::diasParaVencimiento))
                .collect(Collectors.toList());
    }

    public double calcularTiempoPromedioCierre() {
        List<NoConformidad> cerradas = obtenerPorEstado(EstadoNoConformidad.CERRADA);
        if (cerradas.isEmpty()) return 0.0;

        return cerradas.stream()
                .filter(nc -> nc.getFechaCierre() != null)
                .mapToLong(nc -> nc.getFechaDeteccion().until(nc.getFechaCierre()).getDays())
                .average()
                .orElse(0.0);
    }

    public double calcularPorcentajeCompletitudGeneral() {
        if (noConformidades.isEmpty()) return 0.0;

        return noConformidades.stream()
                .mapToInt(NoConformidad::calcularPorcentajeCompletitud)
                .average()
                .orElse(0.0);
    }

    public void cambiarEstado(String noConformidadId, EstadoNoConformidad nuevoEstado) {
        NoConformidad nc = obtenerNoConformidad(noConformidadId);
        if (nc != null) {
            nc.cambiarEstado(nuevoEstado);
            actualizarNoConformidad(nc);
            System.out.println("Estado cambiado para NC " + noConformidadId + ": " + nuevoEstado.getNombre());
        }
    }

    public void cerrarNoConformidad(String noConformidadId, String observacionesCierre) {
        NoConformidad nc = obtenerNoConformidad(noConformidadId);
        if (nc != null) {
            nc.cambiarEstado(EstadoNoConformidad.CERRADA);
            nc.setObservaciones(observacionesCierre);
            actualizarNoConformidad(nc);
            System.out.println("No conformidad cerrada: " + nc.getTitulo());
        }
    }

    public void agregarAccionCorrectiva(AccionCorrectiva accion) {
        if (accion != null) {
            accionesCorrectivas.add(accion);

            NoConformidad nc = obtenerNoConformidad(accion.getNoConformidadId());
            if (nc != null) {
                nc.agregarAccionCorrectiva(accion);
                actualizarNoConformidad(nc);
            }

            System.out.println("Acción correctiva agregada: " + accion.getDescripcion());
        }
    }

    public List<AccionCorrectiva> obtenerAccionesCorrectivas(String noConformidadId) {
        return accionesCorrectivas.stream()
                .filter(ac -> ac.getNoConformidadId().equals(noConformidadId))
                .collect(Collectors.toList());
    }

    public List<AccionCorrectiva> obtenerAccionesVencidas() {
        return accionesCorrectivas.stream()
                .filter(AccionCorrectiva::estaVencida)
                .collect(Collectors.toList());
    }

    public void completarAccionCorrectiva(String accionId, String resultado) {
        AccionCorrectiva accion = accionesCorrectivas.stream()
                .filter(ac -> ac.getId().equals(accionId))
                .findFirst()
                .orElse(null);

        if (accion != null) {
            accion.marcarComoCompletada();
            System.out.println("Acción correctiva completada: " + accion.getDescripcion());

            String ncId = accion.getNoConformidadId();
            List<AccionCorrectiva> acciones = obtenerAccionesCorrectivas(ncId);
            boolean todasCompletadas = acciones.stream().allMatch(AccionCorrectiva::isCompletada);

            if (todasCompletadas) {
                cambiarEstado(ncId, EstadoNoConformidad.EN_CORRECCION);
                System.out.println("Todas las acciones completadas para NC: " + ncId);
            }
        }
    }

    public List<String> generarAlertas() {
        List<String> alertas = new ArrayList<>();

        List<NoConformidad> vencidas = obtenerNoConformidadesVencidas();
        if (!vencidas.isEmpty()) {
            alertas.add("¡ALERTA! " + vencidas.size() + " no conformidades vencidas");
        }

        List<NoConformidad> porVencer = obtenerNoConformidadesPorVencer(7);
        if (!porVencer.isEmpty()) {
            alertas.add("ATENCIÓN: " + porVencer.size() + " no conformidades vencen en 7 días");
        }

        long criticas = obtenerPorPrioridad(Prioridad.CRITICA).stream()
                .filter(nc -> nc.getEstado() != EstadoNoConformidad.CERRADA)
                .count();
        if (criticas > 0) {
            alertas.add("URGENTE: " + criticas + " no conformidades críticas abiertas");
        }

        return alertas;
    }

    public boolean existeNoConformidad(String id) {
        return noConformidades.stream().anyMatch(nc -> nc.getId().equals(id));
    }

    public boolean validarNoConformidad(NoConformidad noConformidad) {
        if (noConformidad == null) return false;
        if (noConformidad.getId() == null || noConformidad.getId().trim().isEmpty()) return false;
        if (noConformidad.getTitulo() == null || noConformidad.getTitulo().trim().isEmpty()) return false;
        if (noConformidad.getDescripcion() == null || noConformidad.getDescripcion().trim().isEmpty()) return false;
        if (noConformidad.getPrioridad() == null) return false;
        if (noConformidad.getProcesoAfectado() == null || noConformidad.getProcesoAfectado().trim().isEmpty()) return false;
        return noConformidad.getResponsableDeteccion() != null && !noConformidad.getResponsableDeteccion().trim().isEmpty();
    }

    public String generarReporteNoConformidades() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE NO CONFORMIDADES\n");
        reporte.append("=".repeat(40)).append("\n");
        reporte.append("Total: ").append(noConformidades.size()).append("\n");

        Map<EstadoNoConformidad, Long> estadisticas = obtenerEstadisticasPorEstado();
        reporte.append("\nPor estado:\n");
        for (Map.Entry<EstadoNoConformidad, Long> entry : estadisticas.entrySet()) {
            reporte.append("- ").append(entry.getKey().getNombre())
                    .append(": ").append(entry.getValue()).append("\n");
        }

        List<NoConformidad> vencidas = obtenerNoConformidadesVencidas();
        if (!vencidas.isEmpty()) {
            reporte.append("\nVencidas (").append(vencidas.size()).append("):\n");
            vencidas.stream().limit(5).forEach(nc ->
                    reporte.append("- ").append(nc.getTitulo())
                            .append(" (").append(-nc.diasParaVencimiento()).append(" días)\n")
            );
        }

        return reporte.toString();
    }

    private void cargarNoConformidades() {
        this.noConformidades = DatabaseHelper.cargarNoConformidades();
        System.out.println("No conformidades cargadas: " + noConformidades.size());
    }

    private void guardarCambios() {
        DatabaseHelper.guardarNoConformidades(noConformidades);
    }

    public int getTotalNoConformidades() {
        return noConformidades.size();
    }

    public int getNoConformidadesAbiertas() {
        return obtenerPorEstado(EstadoNoConformidad.ABIERTA).size();
    }

    public int getNoConformidadesCerradas() {
        return obtenerPorEstado(EstadoNoConformidad.CERRADA).size();
    }

    public int getNoConformidadesVencidas() {
        return obtenerNoConformidadesVencidas().size();
    }
}