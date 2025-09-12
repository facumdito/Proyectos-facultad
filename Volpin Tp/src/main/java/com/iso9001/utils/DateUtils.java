package com.iso9001.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Utilidades para manejo de fechas en el Sistema ISO 9001
 *
 * Proporciona formatters y métodos auxiliares para el manejo
 * consistente de fechas en toda la aplicación.
 *
 * @author Sistema ISO 9001
 * @version 1.0
 */
public class DateUtils {

    // Formatters de fecha usando el constructor recomendado para Locale
    public static final DateTimeFormatter FORMATO_FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final DateTimeFormatter FORMATO_FECHA_LARGA =
            DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));

    public static final DateTimeFormatter FORMATO_FECHA_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Convierte una fecha a formato corto (dd/MM/yyyy)
     * @param fecha La fecha a formatear
     * @return String con la fecha formateada
     */
    public static String formatearFechaCorta(LocalDate fecha) {
        if (fecha == null) return "";
        return fecha.format(FORMATO_FECHA_CORTA);
    }

    /**
     * Convierte una fecha a formato largo en español
     * @param fecha La fecha a formatear
     * @return String con la fecha formateada
     */
    public static String formatearFechaLarga(LocalDate fecha) {
        if (fecha == null) return "";
        return fecha.format(FORMATO_FECHA_LARGA);
    }

    /**
     * Convierte una fecha a formato ISO (yyyy-MM-dd)
     * @param fecha La fecha a formatear
     * @return String con la fecha formateada
     */
    public static String formatearFechaISO(LocalDate fecha) {
        if (fecha == null) return "";
        return fecha.format(FORMATO_FECHA_ISO);
    }

    /**
     * Convierte una fecha y hora a formato estándar
     * @param fechaHora La fecha y hora a formatear
     * @return String con la fecha y hora formateada
     */
    public static String formatearFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) return "";
        return fechaHora.format(FORMATO_FECHA_HORA);
    }

    /**
     * Parsea una fecha desde formato corto (dd/MM/yyyy)
     * @param fechaStr La fecha como string
     * @return LocalDate parseada
     * @throws DateTimeParseException si el formato es incorrecto
     */
    public static LocalDate parsearFechaCorta(String fechaStr) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(fechaStr, FORMATO_FECHA_CORTA);
    }

    /**
     * Parsea una fecha desde formato ISO (yyyy-MM-dd)
     * @param fechaStr La fecha como string
     * @return LocalDate parseada
     * @throws DateTimeParseException si el formato es incorrecto
     */
    public static LocalDate parsearFechaISO(String fechaStr) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(fechaStr, FORMATO_FECHA_ISO);
    }

    /**
     * Calcula la diferencia en días entre dos fechas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Número de días de diferencia
     */
    public static long diferenciaDias(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }

    /**
     * Verifica si una fecha está dentro de un rango
     * @param fecha Fecha a verificar
     * @param inicio Fecha de inicio del rango
     * @param fin Fecha de fin del rango
     * @return true si está dentro del rango
     */
    public static boolean estaEnRango(LocalDate fecha, LocalDate inicio, LocalDate fin) {
        if (fecha == null || inicio == null || fin == null) {
            return false;
        }
        return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
    }

    /**
     * Obtiene la fecha actual formateada para mostrar
     * @return String con la fecha actual
     */
    public static String fechaActualFormateada() {
        return formatearFechaCorta(LocalDate.now());
    }

    /**
     * Obtiene la fecha y hora actual formateada
     * @return String con la fecha y hora actual
     */
    public static String fechaHoraActualFormateada() {
        return formatearFechaHora(LocalDateTime.now());
    }

    /**
     * Verifica si una fecha es hoy
     * @param fecha Fecha a verificar
     * @return true si la fecha es hoy
     */
    public static boolean esHoy(LocalDate fecha) {
        if (fecha == null) return false;
        return fecha.equals(LocalDate.now());
    }

    /**
     * Verifica si una fecha es del pasado
     * @param fecha Fecha a verificar
     * @return true si la fecha es anterior a hoy
     */
    public static boolean esPasado(LocalDate fecha) {
        if (fecha == null) return false;
        return fecha.isBefore(LocalDate.now());
    }

    /**
     * Verifica si una fecha es del futuro
     * @param fecha Fecha a verificar
     * @return true si la fecha es posterior a hoy
     */
    public static boolean esFuturo(LocalDate fecha) {
        if (fecha == null) return false;
        return fecha.isAfter(LocalDate.now());
    }

    /**
     * Calcula una fecha agregando días
     * @param fecha Fecha base
     * @param dias Días a agregar (puede ser negativo)
     * @return Nueva fecha
     */
    public static LocalDate agregarDias(LocalDate fecha, long dias) {
        if (fecha == null) return null;
        return fecha.plusDays(dias);
    }

    /**
     * Calcula una fecha agregando meses
     * @param fecha Fecha base
     * @param meses Meses a agregar (puede ser negativo)
     * @return Nueva fecha
     */
    public static LocalDate agregarMeses(LocalDate fecha, long meses) {
        if (fecha == null) return null;
        return fecha.plusMonths(meses);
    }

    /**
     * Obtiene el nombre del mes en español
     * @param fecha Fecha de la cual obtener el mes
     * @return Nombre del mes en español
     */
    public static String obtenerNombreMes(LocalDate fecha) {
        if (fecha == null) return "";
        return fecha.format(DateTimeFormatter.ofPattern("MMMM", Locale.forLanguageTag("es-ES")));
    }

    /**
     * Valida si un string tiene formato de fecha válido
     * @param fechaStr String a validar
     * @param formato Formato esperado
     * @return true si es válida
     */
    public static boolean esFechaValida(String fechaStr, DateTimeFormatter formato) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate.parse(fechaStr, formato);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Valida si un string tiene formato de fecha corta válido
     * @param fechaStr String a validar
     * @return true si es válida
     */
    public static boolean esFechaCortaValida(String fechaStr) {
        return esFechaValida(fechaStr, FORMATO_FECHA_CORTA);
    }
}