package com.iso9001.utils;

import com.iso9001.models.*;
import com.iso9001.enums.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReporteGenerator {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Generar reporte de estado general del sistema
    public static String generarReporteEstadoGeneral(List<Proceso> procesos,
                                                     List<Indicador> indicadores,
                                                     List<NoConformidad> noConformidades) {
        StringBuilder reporte = new StringBuilder();

        reporte.append("=".repeat(60)).append("\n");
        reporte.append("REPORTE DE ESTADO GENERAL - SISTEMA ISO 9001\n");
        reporte.append("Fecha: ").append(LocalDate.now().format(FORMATO_FECHA)).append("\n");
        reporte.append("=".repeat(60)).append("\n\n");

        // Resumen ejecutivo
        reporte.append("RESUMEN EJECUTIVO\n");
        reporte.append("-".repeat(30)).append("\n");
        reporte.append("Total Procesos: ").append(procesos.size()).append("\n");
        reporte.append("Procesos Activos: ").append(procesos.stream().mapToInt(p -> p.isActivo() ? 1 : 0).sum()).append("\n");
        reporte.append("Total Indicadores: ").append(indicadores.size()).append("\n");
        reporte.append("Indicadores en Verde: ").append(contarIndicadoresPorColor(indicadores, "VERDE")).append("\n");
        reporte.append("Indicadores en Amarillo: ").append(contarIndicadoresPorColor(indicadores, "AMARILLO")).append("\n");
        reporte.append("Indicadores en Rojo: ").append(contarIndicadoresPorColor(indicadores, "ROJO")).append("\n");
        reporte.append("No Conformidades Abiertas: ").append(contarNoConformidadesPorEstado(noConformidades, EstadoNoConformidad.ABIERTA)).append("\n");
        reporte.append("No Conformidades Cerradas: ").append(contarNoConformidadesPorEstado(noConformidades, EstadoNoConformidad.CERRADA)).append("\n\n");

        // Análisis por tipo de proceso
        reporte.append("ANÁLISIS POR TIPO DE PROCESO\n");
        reporte.append("-".repeat(30)).append("\n");
        for (TipoProceso tipo : TipoProceso.values()) {
            long cantidad = procesos.stream().filter(p -> p.getTipo() == tipo).count();
            reporte.append(tipo.getNombre()).append(": ").append(cantidad).append(" procesos\n");
        }
        reporte.append("\n");

        // Top 5 indicadores críticos
        reporte.append("TOP 5 INDICADORES CRÍTICOS\n");
        reporte.append("-".repeat(30)).append("\n");
        indicadores.stream()
                .filter(i -> "ROJO".equals(i.getEstadoSemaforo()))
                .limit(5)
                .forEach(i -> reporte.append("- ").append(i.getNombre())
                        .append(" (Actual: ").append(String.format("%.2f", i.getValorActual()))
                        .append(", Objetivo: ").append(String.format("%.2f", i.getValorObjetivo()))
                        .append(")\n"));

        return reporte.toString();
    }

    // Generar reporte de indicadores
    public static String generarReporteIndicadores(List<Indicador> indicadores) {
        StringBuilder reporte = new StringBuilder();

        reporte.append("REPORTE DETALLADO DE INDICADORES\n");
        reporte.append("=".repeat(50)).append("\n");
        reporte.append("Fecha: ").append(LocalDate.now().format(FORMATO_FECHA)).append("\n\n");

        // Agrupar por tipo
        Map<TipoIndicador, List<Indicador>> indicadoresPorTipo = indicadores.stream()
                .collect(Collectors.groupingBy(Indicador::getTipo));

        for (Map.Entry<TipoIndicador, List<Indicador>> entry : indicadoresPorTipo.entrySet()) {
            reporte.append(entry.getKey().getNombre().toUpperCase()).append("\n");
            reporte.append("-".repeat(20)).append("\n");

            for (Indicador indicador : entry.getValue()) {
                reporte.append("• ").append(indicador.getNombre()).append("\n");
                reporte.append("  Valor Actual: ").append(String.format("%.2f", indicador.getValorActual()))
                        .append(" ").append(indicador.getUnidadMedida()).append("\n");
                reporte.append("  Valor Objetivo: ").append(String.format("%.2f", indicador.getValorObjetivo()))
                        .append(" ").append(indicador.getUnidadMedida()).append("\n");
                reporte.append("  Estado: ").append(indicador.getEstadoSemaforo()).append("\n");
                reporte.append("  Tendencia: ").append(String.format("%.2f%%", indicador.calcularTendencia())).append("\n\n");
            }
        }

        return reporte.toString();
    }

    // Generar reporte de no conformidades
    public static String generarReporteNoConformidades(List<NoConformidad> noConformidades) {
        StringBuilder reporte = new StringBuilder();

        reporte.append("REPORTE DE NO CONFORMIDADES\n");
        reporte.append("=".repeat(40)).append("\n");
        reporte.append("Fecha: ").append(LocalDate.now().format(FORMATO_FECHA)).append("\n\n");

        // Estadísticas generales
        reporte.append("ESTADÍSTICAS GENERALES\n");
        reporte.append("-".repeat(25)).append("\n");
        for (EstadoNoConformidad estado : EstadoNoConformidad.values()) {
            long cantidad = contarNoConformidadesPorEstado(noConformidades, estado);
            reporte.append(estado.getNombre()).append(": ").append(cantidad).append("\n");
        }
        reporte.append("\n");

        // No conformidades por prioridad
        reporte.append("POR PRIORIDAD\n");
        reporte.append("-".repeat(15)).append("\n");
        for (Prioridad prioridad : Prioridad.values()) {
            long cantidad = noConformidades.stream().filter(nc -> nc.getPrioridad() == prioridad).count();
            reporte.append(prioridad.getNombre()).append(": ").append(cantidad).append("\n");
        }
        reporte.append("\n");

        // No conformidades vencidas
        List<NoConformidad> vencidas = noConformidades.stream()
                .filter(NoConformidad::estaVencida)
                .collect(Collectors.toList());

        if (!vencidas.isEmpty()) {
            reporte.append("NO CONFORMIDADES VENCIDAS (").append(vencidas.size()).append(")\n");
            reporte.append("-".repeat(30)).append("\n");
            for (NoConformidad nc : vencidas) {
                reporte.append("• ").append(nc.getTitulo()).append("\n");
                reporte.append("  Prioridad: ").append(nc.getPrioridad().getNombre()).append("\n");
                reporte.append("  Proceso: ").append(nc.getProcesoAfectado()).append("\n");
                reporte.append("  Días vencida: ").append(-nc.diasParaVencimiento()).append("\n\n");
            }
        }

        return reporte.toString();
    }

    // Exportar reporte a archivo
    public static void exportarReporteAArchivo(String contenido, String nombreArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("reports/" + nombreArchivo))) {
            writer.print(contenido);
            System.out.println("Reporte exportado: " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al exportar reporte: " + e.getMessage());
        }
    }

    // Generar reporte de auditoría
    public static String generarReporteAuditoria(List<Proceso> procesos,
                                                 List<Indicador> indicadores,
                                                 List<NoConformidad> noConformidades) {
        StringBuilder reporte = new StringBuilder();

        reporte.append("REPORTE DE PREPARACIÓN PARA AUDITORÍA ISO 9001\n");
        reporte.append("=".repeat(55)).append("\n");
        reporte.append("Fecha: ").append(LocalDate.now().format(FORMATO_FECHA)).append("\n\n");

        // Estado de cumplimiento
        reporte.append("ESTADO DE CUMPLIMIENTO\n");
        reporte.append("-".repeat(25)).append("\n");

        double porcentajeCumplimiento = calcularPorcentajeCumplimiento(indicadores);
        reporte.append("Cumplimiento General: ").append(String.format("%.1f%%", porcentajeCumplimiento)).append("\n");

        if (porcentajeCumplimiento >= 90) {
            reporte.append("Estado: LISTO PARA AUDITORÍA ✓\n");
        } else if (porcentajeCumplimiento >= 75) {
            reporte.append("Estado: REQUIERE MEJORAS MENORES\n");
        } else {
            reporte.append("Estado: REQUIERE MEJORAS IMPORTANTES\n");
        }
        reporte.append("\n");

        // Hallazgos críticos
        List<NoConformidad> criticas = noConformidades.stream()
                .filter(nc -> nc.getPrioridad() == Prioridad.CRITICA && nc.getEstado() != EstadoNoConformidad.CERRADA)
                .collect(Collectors.toList());

        if (!criticas.isEmpty()) {
            reporte.append("NO CONFORMIDADES CRÍTICAS PENDIENTES\n");
            reporte.append("-".repeat(35)).append("\n");
            for (NoConformidad nc : criticas) {
                reporte.append("⚠ ").append(nc.getTitulo()).append("\n");
                reporte.append("  Proceso: ").append(nc.getProcesoAfectado()).append("\n");
                reporte.append("  Estado: ").append(nc.getEstado().getNombre()).append("\n\n");
            }
        }

        return reporte.toString();
    }

    // Métodos auxiliares
    private static long contarIndicadoresPorColor(List<Indicador> indicadores, String color) {
        return indicadores.stream().filter(i -> color.equals(i.getEstadoSemaforo())).count();
    }

    private static long contarNoConformidadesPorEstado(List<NoConformidad> noConformidades, EstadoNoConformidad estado) {
        return noConformidades.stream().filter(nc -> nc.getEstado() == estado).count();
    }

    private static double calcularPorcentajeCumplimiento(List<Indicador> indicadores) {
        if (indicadores.isEmpty()) return 0.0;

        long enObjetivo = indicadores.stream().filter(Indicador::estaDentroObjetivo).count();
        return (enObjetivo * 100.0) / indicadores.size();
    }
}