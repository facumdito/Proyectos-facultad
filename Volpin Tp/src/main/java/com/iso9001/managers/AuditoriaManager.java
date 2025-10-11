package com.iso9001.managers;

import com.iso9001.models.Auditoria;
import com.iso9001.models.Empleado;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AuditoriaManager {
    private final List<Auditoria> auditorias;
    private final List<Empleado> auditoresDisponibles;

    public AuditoriaManager() {
        this.auditorias = new ArrayList<>();
        this.auditoresDisponibles = new ArrayList<>();
        inicializarAuditoresDisponibles();
        crearAuditoriasEjemplo();
    }

    public void agregarAuditoria(Auditoria auditoria) {
        if (auditoria != null && !existeAuditoria(auditoria.getId())) {
            auditorias.add(auditoria);
            System.out.println("Auditoría agregada: " + auditoria.getTitulo());
        } else {
            System.err.println("Error: Auditoría nula o ID ya existe");
        }
    }

    public Auditoria obtenerAuditoria(String id) {
        return auditorias.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Auditoria> obtenerTodasAuditorias() {
        return new ArrayList<>(auditorias);
    }

    public void actualizarAuditoria(Auditoria auditoria) {
        if (auditoria != null) {
            for (int i = 0; i < auditorias.size(); i++) {
                if (auditorias.get(i).getId().equals(auditoria.getId())) {
                    auditorias.set(i, auditoria);
                    System.out.println("Auditoría actualizada: " + auditoria.getTitulo());
                    return;
                }
            }
            System.err.println("Auditoría no encontrada para actualizar");
        }
    }

    public boolean eliminarAuditoria(String id) {
        boolean eliminada = auditorias.removeIf(a -> a.getId().equals(id));
        if (eliminada) {
            System.out.println("Auditoría eliminada: " + id);
        }
        return eliminada;
    }

    public List<Auditoria> buscarPorTitulo(String titulo) {
        return auditorias.stream()
                .filter(a -> a.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Auditoria> obtenerPorTipo(String tipo) {
        return auditorias.stream()
                .filter(a -> a.getTipo().equals(tipo))
                .collect(Collectors.toList());
    }

    public List<Auditoria> obtenerPorAuditor(String auditor) {
        return auditorias.stream()
                .filter(a -> a.getAuditorLider().equals(auditor) || a.getAuditores().contains(auditor))
                .collect(Collectors.toList());
    }

    public List<Auditoria> obtenerPorEstado(String estado) {
        return auditorias.stream()
                .filter(a -> a.getEstadoAuditoria().equals(estado))
                .collect(Collectors.toList());
    }

    public List<Auditoria> obtenerPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        return auditorias.stream()
                .filter(a -> !a.getFechaProgramada().isBefore(fechaInicio) &&
                        !a.getFechaProgramada().isAfter(fechaFin))
                .collect(Collectors.toList());
    }

    public void programarAuditoria(String titulo, String tipo, LocalDate fecha, String auditorLider) {
        String id = "AUD" + String.format("%03d", auditorias.size() + 1);
        Auditoria auditoria = new Auditoria(id, titulo, tipo, fecha, auditorLider);
        agregarAuditoria(auditoria);
    }

    public List<Auditoria> obtenerAuditoriasProgramadas() {
        return auditorias.stream()
                .filter(a -> "PROGRAMADA".equals(a.getEstadoAuditoria()))
                .sorted(Comparator.comparing(Auditoria::getFechaProgramada))
                .collect(Collectors.toList());
    }

    public List<Auditoria> obtenerAuditoriasVencidas() {
        return auditorias.stream()
                .filter(Auditoria::estaVencida)
                .collect(Collectors.toList());
    }

    public List<Auditoria> obtenerAuditoriasPorVencer(int diasAnticipacion) {
        LocalDate fechaLimite = LocalDate.now().plusDays(diasAnticipacion);
        return auditorias.stream()
                .filter(a -> !a.isCompletada() &&
                        !a.getFechaProgramada().isAfter(fechaLimite) &&
                        !a.getFechaProgramada().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public List<Empleado> obtenerAuditoresDisponibles() {
        return auditoresDisponibles.stream()
                .filter(Empleado::estaCalificadoParaAuditorias)
                .collect(Collectors.toList());
    }

    public void asignarAuditor(String auditoriaId, String auditor) {
        Auditoria auditoria = obtenerAuditoria(auditoriaId);
        if (auditoria != null) {
            auditoria.agregarAuditor(auditor);
            actualizarAuditoria(auditoria);
            System.out.println("Auditor asignado: " + auditor + " a auditoría " + auditoriaId);
        }
    }

    public boolean auditorDisponible(String auditor, LocalDate fecha) {
        return auditorias.stream()
                .noneMatch(a -> (a.getAuditorLider().equals(auditor) || a.getAuditores().contains(auditor)) &&
                        a.getFechaProgramada().equals(fecha) &&
                        !a.isCompletada());
    }

    public void iniciarAuditoria(String auditoriaId) {
        Auditoria auditoria = obtenerAuditoria(auditoriaId);
        if (auditoria != null && "PROGRAMADA".equals(auditoria.getEstadoAuditoria())) {
            System.out.println("Auditoría iniciada: " + auditoria.getTitulo());
        }
    }

    public void agregarHallazgo(String auditoriaId, String hallazgo) {
        Auditoria auditoria = obtenerAuditoria(auditoriaId);
        if (auditoria != null) {
            auditoria.agregarHallazgo(hallazgo);
            actualizarAuditoria(auditoria);
            System.out.println("Hallazgo agregado a auditoría " + auditoriaId);
        }
    }

    public void registrarNoConformidad(String auditoriaId, String noConformidadId) {
        Auditoria auditoria = obtenerAuditoria(auditoriaId);
        if (auditoria != null) {
            auditoria.agregarNoConformidad(noConformidadId);
            actualizarAuditoria(auditoria);
            System.out.println("No conformidad registrada en auditoría " + auditoriaId);
        }
    }

    public void completarAuditoria(String auditoriaId, String conclusion, double calificacion) {
        Auditoria auditoria = obtenerAuditoria(auditoriaId);
        if (auditoria != null) {
            auditoria.completarAuditoria(LocalDate.now(), conclusion, calificacion);
            actualizarAuditoria(auditoria);
            System.out.println("Auditoría completada: " + auditoria.getTitulo());
        }
    }

    public Map<String, Long> obtenerEstadisticasPorEstado() {
        Map<String, Long> estadisticas = new HashMap<>();
        estadisticas.put("PROGRAMADA", auditorias.stream().filter(a -> "PROGRAMADA".equals(a.getEstadoAuditoria())).count());
        estadisticas.put("EN_CURSO", auditorias.stream().filter(a -> "EN_CURSO".equals(a.getEstadoAuditoria())).count());
        estadisticas.put("COMPLETADA", auditorias.stream().filter(a -> "COMPLETADA".equals(a.getEstadoAuditoria())).count());
        estadisticas.put("VENCIDA", auditorias.stream().filter(a -> "VENCIDA".equals(a.getEstadoAuditoria())).count());
        return estadisticas;
    }

    public Map<String, Long> obtenerEstadisticasPorTipo() {
        return auditorias.stream()
                .collect(Collectors.groupingBy(Auditoria::getTipo, Collectors.counting()));
    }

    public double calcularCalificacionPromedio() {
        return auditorias.stream()
                .filter(Auditoria::isCompletada)
                .mapToDouble(Auditoria::getCalificacionGeneral)
                .average()
                .orElse(0.0);
    }

    public Map<String, Integer> obtenerEstadisticasHallazgos() {
        Map<String, Integer> estadisticas = new HashMap<>();
        estadisticas.put("totalHallazgos", auditorias.stream().mapToInt(Auditoria::cantidadHallazgos).sum());
        estadisticas.put("totalNoConformidades", auditorias.stream().mapToInt(Auditoria::cantidadNoConformidades).sum());
        estadisticas.put("promedioHallazgosPorAuditoria",
                (int) auditorias.stream().filter(Auditoria::isCompletada)
                        .mapToInt(Auditoria::cantidadHallazgos)
                        .average().orElse(0.0));
        return estadisticas;
    }

    public List<String> generarAlertas() {
        List<String> alertas = new ArrayList<>();

        List<Auditoria> vencidas = obtenerAuditoriasVencidas();
        if (!vencidas.isEmpty()) {
            alertas.add("¡ALERTA! " + vencidas.size() + " auditorías vencidas");
        }

        List<Auditoria> proximas = obtenerAuditoriasPorVencer(7);
        if (!proximas.isEmpty()) {
            alertas.add("RECORDATORIO: " + proximas.size() + " auditorías programadas en 7 días");
        }

        long sinAuditores = auditorias.stream()
                .filter(a -> "PROGRAMADA".equals(a.getEstadoAuditoria()) && a.getAuditores().isEmpty())
                .count();
        if (sinAuditores > 0) {
            alertas.add("ATENCIÓN: " + sinAuditores + " auditorías sin auditores asignados");
        }

        return alertas;
    }

    public List<LocalDate> sugerirFechasAuditoria(String auditor, int proximosMeses) {
        List<LocalDate> fechasDisponibles = new ArrayList<>();
        LocalDate fechaInicio = LocalDate.now().plusDays(7);
        LocalDate fechaFin = fechaInicio.plusMonths(proximosMeses);

        LocalDate fechaActual = fechaInicio;
        while (!fechaActual.isAfter(fechaFin)) {
            if (fechaActual.getDayOfWeek().getValue() <= 5 &&
                    auditorDisponible(auditor, fechaActual)) {
                fechasDisponibles.add(fechaActual);
            }
            fechaActual = fechaActual.plusDays(1);
        }

        return fechasDisponibles.stream().limit(10).collect(Collectors.toList());
    }

    public boolean existeAuditoria(String id) {
        return auditorias.stream().anyMatch(a -> a.getId().equals(id));
    }

    public boolean validarAuditoria(Auditoria auditoria) {
        if (auditoria == null) return false;
        if (auditoria.getId() == null || auditoria.getId().trim().isEmpty()) return false;
        if (auditoria.getTitulo() == null || auditoria.getTitulo().trim().isEmpty()) return false;
        if (auditoria.getTipo() == null || auditoria.getTipo().trim().isEmpty()) return false;
        if (auditoria.getFechaProgramada() == null) return false;
        return auditoria.getAuditorLider() != null && !auditoria.getAuditorLider().trim().isEmpty();
    }

    public String generarReporteAuditorias() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE AUDITORÍAS\n");
        reporte.append("=".repeat(30)).append("\n");
        reporte.append("Total auditorías: ").append(auditorias.size()).append("\n");
        reporte.append("Calificación promedio: ").append(String.format("%.1f", calcularCalificacionPromedio())).append("\n\n");

        Map<String, Long> estadisticas = obtenerEstadisticasPorEstado();
        reporte.append("Por estado:\n");
        for (Map.Entry<String, Long> entry : estadisticas.entrySet()) {
            reporte.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        Map<String, Integer> hallazgos = obtenerEstadisticasHallazgos();
        reporte.append("\nHallazgos:\n");
        reporte.append("- Total: ").append(hallazgos.get("totalHallazgos")).append("\n");
        reporte.append("- No conformidades: ").append(hallazgos.get("totalNoConformidades")).append("\n");
        reporte.append("- Promedio por auditoría: ").append(hallazgos.get("promedioHallazgosPorAuditoria")).append("\n");

        return reporte.toString();
    }

    public String generarPlanAuditorias(int proximosMeses) {
        StringBuilder plan = new StringBuilder();
        plan.append("PLAN DE AUDITORÍAS - PRÓXIMOS ").append(proximosMeses).append(" MESES\n");
        plan.append("=".repeat(50)).append("\n");

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = fechaInicio.plusMonths(proximosMeses);

        List<Auditoria> auditoriasPlaneadas = obtenerPorFecha(fechaInicio, fechaFin);
        auditoriasPlaneadas.sort(Comparator.comparing(Auditoria::getFechaProgramada));

        for (Auditoria auditoria : auditoriasPlaneadas) {
            plan.append(auditoria.getFechaProgramada()).append(" - ")
                    .append(auditoria.getTitulo()).append(" (")
                    .append(auditoria.getTipo()).append(")\n");
            plan.append("  Auditor: ").append(auditoria.getAuditorLider()).append("\n");
            plan.append("  Estado: ").append(auditoria.getEstadoAuditoria()).append("\n\n");
        }

        return plan.toString();
    }

    private void inicializarAuditoresDisponibles() {
        Empleado emp1 = new Empleado("EMP001", "Carlos", "Mendoza", "carlos.mendoza@empresa.com",
                "Auditor Senior", "Calidad");
        emp1.setAuditorInterno(true);
        emp1.agregarCertificacionISO("ISO 9001");
        auditoresDisponibles.add(emp1);

        Empleado emp2 = new Empleado("EMP002", "Ana", "López", "ana.lopez@empresa.com",
                "Coordinadora Calidad", "Calidad");
        emp2.setAuditorInterno(true);
        emp2.agregarCertificacionISO("ISO 9001");
        auditoresDisponibles.add(emp2);

        Empleado emp3 = new Empleado("EMP003", "Roberto", "Silva", "roberto.silva@empresa.com",
                "Jefe Procesos", "Operaciones");
        emp3.setAuditorInterno(true);
        emp3.agregarCertificacionISO("ISO 9001");
        auditoresDisponibles.add(emp3);
    }

    private void crearAuditoriasEjemplo() {
        Auditoria aud1 = new Auditoria("AUD001", "Auditoría Interna Q1 2024", "Interna",
                LocalDate.now().minusMonths(2), "Carlos Mendoza");
        aud1.agregarAuditor("Ana López");
        aud1.agregarProcesoAuditado("P001");
        aud1.agregarProcesoAuditado("P002");
        aud1.agregarHallazgo("Documentación actualizada correctamente");
        aud1.agregarHallazgo("Procesos bien definidos");
        aud1.completarAuditoria(LocalDate.now().minusMonths(2).plusDays(1),
                "Sistema de calidad funcionando correctamente", 8.5);
        agregarAuditoria(aud1);

        Auditoria aud2 = new Auditoria("AUD002", "Auditoría Externa ISO 9001", "Externa",
                LocalDate.now().plusDays(15), "Auditor Externo");
        aud2.agregarAuditor("Carlos Mendoza");
        aud2.agregarAuditor("Ana López");
        aud2.setAlcance("Todos los procesos de la organización");
        aud2.setObjetivos("Verificar cumplimiento de requisitos ISO 9001:2015");
        agregarAuditoria(aud2);

        Auditoria aud3 = new Auditoria("AUD003", "Seguimiento Acciones Correctivas", "Interna",
                LocalDate.now().plusDays(30), "Roberto Silva");
        aud3.agregarProcesoAuditado("P004");
        aud3.setAlcance("Proceso de producción");
        aud3.setObjetivos("Verificar implementación de acciones correctivas");
        agregarAuditoria(aud3);
    }

    public int getTotalAuditorias() {
        return auditorias.size();
    }

    public int getAuditoriasCompletadas() {
        return (int) auditorias.stream().filter(Auditoria::isCompletada).count();
    }

    public int getAuditoriasProgramadas() {
        return obtenerPorEstado("PROGRAMADA").size();
    }

    public int getAuditoriasVencidas() {
        return obtenerAuditoriasVencidas().size();
    }
}