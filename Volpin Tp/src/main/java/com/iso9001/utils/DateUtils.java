package com.iso9001.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class DateUtils {
    public static final DateTimeFormatter FORMATO_FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FORMATO_FECHA_LARGA = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
    public static final DateTimeFormatter FORMATO_FECHA_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Convierte una fecha a formato de texto legible
     */
    public static String formatearFecha(LocalDate fecha) {
        if (fecha == null) return "No definida";
        return fecha.format(FORMATO_FECHA_CORTA);
    }

    /**
     * Convierte una fecha a formato largo en español
     */
    public static String formatearFechaLarga(LocalDate fecha) {
        if (fecha == null) return "No definida";
        return fecha.format(FORMATO_FECHA_LARGA);
    }

    /**
     * Calcula los días entre dos fechas
     */
    public static long diasEntre(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) return 0;
        return ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }

    /**
     * Calcula los días desde una fecha hasta hoy
     */
    public static long diasDesde(LocalDate fecha) {
        if (fecha == null) return 0;
        return ChronoUnit.DAYS.between(fecha, LocalDate.now());
    }

    /**
     * Calcula los días desde hoy hasta una fecha futura
     */
    public static long diasHasta(LocalDate fecha) {
        if (fecha == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), fecha);
    }

    /**
     * Verifica si una fecha está vencida
     */
    public static boolean estaVencida(LocalDate fecha) {
        if (fecha == null) return false;
        return LocalDate.now().isAfter(fecha);
    }

    /**
     * Verifica si una fecha está próxima a vencer (dentro de los próximos N días)
     */
    public static boolean proximaAVencer(LocalDate fecha, int diasAnticipacion) {
        if (fecha == null) return false;
        LocalDate fechaLimite = LocalDate.now().plusDays(diasAnticipacion);
        return !fecha.isAfter(fechaLimite) && !fecha.isBefore(LocalDate.now());
    }

    /**
     * Obtiene el primer día del mes actual
     */
    public static LocalDate primerDiaDelMes() {
        return LocalDate.now().withDayOfMonth(1);
    }

    /**
     * Obtiene el último día del mes actual
     */
    public static LocalDate ultimoDiaDelMes() {
        LocalDate hoy = LocalDate.now();
        return hoy.withDayOfMonth(hoy.lengthOfMonth());
    }

    /**
     * Obtiene el primer día del año actual
     */
    public static LocalDate primerDiaDelAno() {
        return LocalDate.now().withDayOfYear(1);
    }

    /**
     * Convierte string en formato ISO a LocalDate
     */
    public static LocalDate parsearFechaISO(String fechaString) {
        try {
            return LocalDate.parse(fechaString, FORMATO_FECHA_ISO);
        } catch (Exception e) {
            System.err.println("Error al parsear fecha: " + fechaString);
            return LocalDate.now();
        }
    }

    /**
     * Convierte string en formato corto a LocalDate
     */
    public static LocalDate parsearFechaCorta(String fechaString) {
        try {
            return LocalDate.parse(fechaString, FORMATO_FECHA_CORTA);
        } catch (Exception e) {
            System.err.println("Error al parsear fecha: " + fechaString);
            return LocalDate.now();
        }
    }

    /**
     * Obtiene un texto descriptivo del estado temporal
     */
    public static String obtenerEstadoTemporal(LocalDate fecha) {
        if (fecha == null) return "Sin fecha";

        long dias = diasHasta(fecha);

        if (dias < 0) {
            return "Vencida hace " + Math.abs(dias) + " días";
        } else if (dias == 0) {
            return "Vence hoy";
        } else if (dias == 1) {
            return "Vence mañana";
        } else if (dias <= 7) {
            return "Vence en " + dias + " días";
        } else if (dias <= 30) {
            return "Vence en " + (dias / 7) + " semanas";
        } else {
            return "Vence en " + (dias / 30) + " meses";
        }
    }

    /**
     * Genera un rango de fechas para reportes
     */
    public static String generarRangoFechas(LocalDate inicio, LocalDate fin) {
        if (inicio == null && fin == null) return "Sin rango definido";
        if (inicio == null) return "Hasta " + formatearFecha(fin);
        if (fin == null) return "Desde " + formatearFecha(inicio);

        return formatearFecha(inicio) + " - " + formatearFecha(fin);
    }
}