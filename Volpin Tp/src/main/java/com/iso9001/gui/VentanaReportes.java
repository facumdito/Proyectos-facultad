package com.iso9001.gui;

import com.iso9001.managers.*;
import com.iso9001.models.*;
import com.iso9001.utils.ReporteGenerator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class VentanaReportes extends JFrame {
    private ProcesoManager procesoManager;
    private IndicadorManager indicadorManager;
    private NoConformidadManager noConformidadManager;

    private JTextArea areaReporte;
    private JComboBox<String> comboTipoReporte;
    private JButton btnGenerar;
    private JButton btnExportar;
    private String ultimoReporte;

    public VentanaReportes(ProcesoManager procesoManager, IndicadorManager indicadorManager,
                           NoConformidadManager noConformidadManager) {
        this.procesoManager = procesoManager;
        this.indicadorManager = indicadorManager;
        this.noConformidadManager = noConformidadManager;

        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setTitle("Generador de Reportes ISO 9001");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void crearComponentes() {
        // Panel superior - Controles
        JPanel panelControles = crearPanelControles();
        add(panelControles, BorderLayout.NORTH);

        // Panel central - Área de reporte
        JPanel panelReporte = crearPanelReporte();
        add(panelReporte, BorderLayout.CENTER);

        // Panel inferior - Botones
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Opciones de Reporte"));

        panel.add(new JLabel("Tipo de Reporte:"));

        comboTipoReporte = new JComboBox<>();
        comboTipoReporte.addItem("Estado General del Sistema");
        comboTipoReporte.addItem("Reporte de Procesos");
        comboTipoReporte.addItem("Reporte de Indicadores");
        comboTipoReporte.addItem("Reporte de No Conformidades");
        comboTipoReporte.addItem("Reporte de Preparación para Auditoría");
        comboTipoReporte.addItem("Análisis de Cumplimiento ISO 9001");
        comboTipoReporte.addItem("Dashboard Ejecutivo");

        panel.add(comboTipoReporte);

        btnGenerar = new JButton("Generar Reporte");
        btnGenerar.addActionListener(this::generarReporte);
        panel.add(btnGenerar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarReporte());
        panel.add(btnLimpiar);

        return panel;
    }

    private JPanel crearPanelReporte() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Reporte Generado"));

        areaReporte = new JTextArea();
        areaReporte.setEditable(false);
        areaReporte.setFont(new Font("Courier New", Font.PLAIN, 11));
        areaReporte.setText("Seleccione un tipo de reporte y haga clic en 'Generar Reporte'...");

        JScrollPane scrollPane = new JScrollPane(areaReporte);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout());

        btnExportar = new JButton("Exportar a Archivo");
        btnExportar.addActionListener(this::exportarReporte);
        btnExportar.setEnabled(false);

        JButton btnImprimir = new JButton("Vista Previa de Impresión");
        btnImprimir.addActionListener(this::mostrarVistaPrevia);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        panel.add(btnExportar);
        panel.add(btnImprimir);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnCerrar);

        return panel;
    }

    private void generarReporte(ActionEvent e) {
        String tipoReporte = (String) comboTipoReporte.getSelectedItem();
        StringBuilder reporte = new StringBuilder();

        try {
            switch (tipoReporte) {
                case "Estado General del Sistema":
                    reporte.append(generarReporteEstadoGeneral());
                    break;

                case "Reporte de Procesos":
                    reporte.append(generarReporteProcesos());
                    break;

                case "Reporte de Indicadores":
                    reporte.append(generarReporteIndicadores());
                    break;

                case "Reporte de No Conformidades":
                    reporte.append(generarReporteNoConformidades());
                    break;

                case "Reporte de Preparación para Auditoría":
                    reporte.append(generarReporteAuditoria());
                    break;

                case "Análisis de Cumplimiento ISO 9001":
                    reporte.append(generarAnalisisCumplimiento());
                    break;

                case "Dashboard Ejecutivo":
                    reporte.append(generarDashboardEjecutivo());
                    break;

                default:
                    reporte.append("Tipo de reporte no reconocido.");
            }

            ultimoReporte = reporte.toString();
            areaReporte.setText(ultimoReporte);
            areaReporte.setCaretPosition(0);
            btnExportar.setEnabled(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el reporte: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generarReporteEstadoGeneral() {
        List<Proceso> procesos = procesoManager.obtenerTodosProcesos();
        List<Indicador> indicadores = indicadorManager.obtenerTodosIndicadores();
        List<NoConformidad> noConformidades = noConformidadManager.obtenerTodasNoConformidades();

        return ReporteGenerator.generarReporteEstadoGeneral(procesos, indicadores, noConformidades);
    }

    private String generarReporteProcesos() {
        StringBuilder reporte = new StringBuilder();

        reporte.append("REPORTE DETALLADO DE PROCESOS\n");
        reporte.append("=".repeat(50)).append("\n");
        reporte.append("Fecha: ").append(LocalDate.now()).append("\n\n");

        // Estadísticas generales
        reporte.append("RESUMEN EJECUTIVO\n");
        reporte.append("-".repeat(25)).append("\n");
        reporte.append("Total de procesos: ").append(procesoManager.getTotalProcesos()).append("\n");
        reporte.append("Procesos activos: ").append(procesoManager.getProcesosActivos()).append("\n");
        reporte.append("Eficiencia promedio: ").append(String.format("%.1f%%", procesoManager.calcularEficienciaPromedio())).append("\n\n");

        // Procesos por tipo
        reporte.append("DISTRIBUCIÓN POR TIPO\n");
        reporte.append("-".repeat(25)).append("\n");
        procesoManager.obtenerEstadisticasPorTipo().forEach((tipo, cantidad) ->
                reporte.append(tipo.getNombre()).append(": ").append(cantidad).append(" procesos\n")
        );

        // Procesos con indicadores críticos
        List<Proceso> procesosConProblemas = procesoManager.obtenerProcesosConIndicadoresCriticos();
        if (!procesosConProblemas.isEmpty()) {
            reporte.append("\nPROCESOS CON INDICADORES CRÍTICOS\n");
            reporte.append("-".repeat(35)).append("\n");
            procesosConProblemas.forEach(p ->
                    reporte.append("• ").append(p.getNombre()).append(" (").append(p.getResponsable()).append(")\n")
            );
        }

        return reporte.toString();
    }

    private String generarReporteIndicadores() {
        List<Indicador> indicadores = indicadorManager.obtenerTodosIndicadores();
        return ReporteGenerator.generarReporteIndicadores(indicadores);
    }

    private String generarReporteNoConformidades() {
        List<NoConformidad> noConformidades = noConformidadManager.obtenerTodasNoConformidades();
        return ReporteGenerator.generarReporteNoConformidades(noConformidades);
    }

    private String generarReporteAuditoria() {
        List<Proceso> procesos = procesoManager.obtenerTodosProcesos();
        List<Indicador> indicadores = indicadorManager.obtenerTodosIndicadores();
        List<NoConformidad> noConformidades = noConformidadManager.obtenerTodasNoConformidades();

        return ReporteGenerator.generarReporteAuditoria(procesos, indicadores, noConformidades);
    }

    private String generarAnalisisCumplimiento() {
        StringBuilder reporte = new StringBuilder();

        reporte.append("ANÁLISIS DE CUMPLIMIENTO ISO 9001:2015\n");
        reporte.append("=".repeat(45)).append("\n");
        reporte.append("Fecha: ").append(LocalDate.now()).append("\n\n");

        // Evaluación por requisitos
        reporte.append("EVALUACIÓN POR REQUISITOS ISO 9001\n");
        reporte.append("-".repeat(35)).append("\n");

        // 4. Contexto de la organización
        reporte.append("4. Contexto de la organización: ");
        reporte.append(evaluarRequisito("contexto")).append("\n");

        // 5. Liderazgo
        reporte.append("5. Liderazgo: ");
        reporte.append(evaluarRequisito("liderazgo")).append("\n");

        // 6. Planificación
        reporte.append("6. Planificación: ");
        reporte.append(evaluarRequisito("planificacion")).append("\n");

        // 7. Apoyo
        reporte.append("7. Apoyo: ");
        reporte.append(evaluarRequisito("apoyo")).append("\n");

        // 8. Operación
        reporte.append("8. Operación: ");
        reporte.append(evaluarRequisito("operacion")).append("\n");

        // 9. Evaluación del desempeño
        reporte.append("9. Evaluación del desempeño: ");
        reporte.append(evaluarRequisito("evaluacion")).append("\n");

        // 10. Mejora
        reporte.append("10. Mejora: ");
        reporte.append(evaluarRequisito("mejora")).append("\n\n");

        // Recomendaciones
        reporte.append("RECOMENDACIONES\n");
        reporte.append("-".repeat(20)).append("\n");
        reporte.append("• Mantener documentación actualizada\n");
        reporte.append("• Realizar auditorías internas periódicas\n");
        reporte.append("• Implementar mejora continua\n");
        reporte.append("• Capacitar personal en ISO 9001\n");

        return reporte.toString();
    }

    private String evaluarRequisito(String requisito) {
        // Evaluación simplificada basada en el estado del sistema
        double cumplimiento = indicadorManager.calcularPorcentajeCumplimientoGeneral();

        if (cumplimiento >= 90) return "CONFORME ✓";
        else if (cumplimiento >= 75) return "PARCIALMENTE CONFORME ⚠";
        else return "NO CONFORME ✗";
    }

    private String generarDashboardEjecutivo() {
        StringBuilder reporte = new StringBuilder();

        reporte.append("DASHBOARD EJECUTIVO - SISTEMA DE CALIDAD\n");
        reporte.append("=".repeat(50)).append("\n");
        reporte.append("Fecha: ").append(LocalDate.now()).append("\n\n");

        // KPIs principales
        reporte.append("INDICADORES CLAVE DE RENDIMIENTO (KPIs)\n");
        reporte.append("-".repeat(40)).append("\n");
        reporte.append("Procesos Activos: ").append(procesoManager.getProcesosActivos()).append("\n");
        reporte.append("Cumplimiento de Indicadores: ").append(String.format("%.1f%%", indicadorManager.calcularPorcentajeCumplimientoGeneral())).append("\n");
        reporte.append("No Conformidades Abiertas: ").append(noConformidadManager.getNoConformidadesAbiertas()).append("\n");
        reporte.append("No Conformidades Vencidas: ").append(noConformidadManager.getNoConformidadesVencidas()).append("\n\n");

        // Semáforo de estado
        reporte.append("SEMÁFORO DE ESTADO\n");
        reporte.append("-".repeat(20)).append("\n");
        reporte.append("Indicadores Verde: ").append(indicadorManager.getIndicadoresVerde()).append("\n");
        reporte.append("Indicadores Amarillo: ").append(indicadorManager.getIndicadoresAmarillo()).append("\n");
        reporte.append("Indicadores Rojo: ").append(indicadorManager.getIndicadoresRojo()).append("\n\n");

        // Tendencias
        reporte.append("TENDENCIAS\n");
        reporte.append("-".repeat(12)).append("\n");
        List<Indicador> mejoresTendencias = indicadorManager.obtenerIndicadoresConMejorTendencia();
        if (!mejoresTendencias.isEmpty()) {
            reporte.append("Indicadores con mejor tendencia:\n");
            mejoresTendencias.forEach(ind ->
                    reporte.append("• ").append(ind.getNombre())
                            .append(" (+").append(String.format("%.1f%%", ind.calcularTendencia())).append(")\n")
            );
        }

        return reporte.toString();
    }

    private void limpiarReporte() {
        areaReporte.setText("Seleccione un tipo de reporte y haga clic en 'Generar Reporte'...");
        ultimoReporte = null;
        btnExportar.setEnabled(false);
    }

    private void exportarReporte(ActionEvent e) {
        if (ultimoReporte == null || ultimoReporte.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay reporte para exportar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar Reporte");
        fileChooser.setSelectedFile(new java.io.File("reporte_iso9001_" + LocalDate.now() + ".txt"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File archivoDestino = fileChooser.getSelectedFile();

                try (FileWriter writer = new FileWriter(archivoDestino)) {
                    writer.write(ultimoReporte);
                }

                JOptionPane.showMessageDialog(this,
                        "Reporte exportado exitosamente a:\n" + archivoDestino.getAbsolutePath(),
                        "Exportación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al exportar el reporte: " + ex.getMessage(),
                        "Error de Exportación",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarVistaPrevia(ActionEvent e) {
        if (ultimoReporte == null || ultimoReporte.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay reporte para mostrar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialogoVista = new JDialog(this, "Vista Previa de Impresión", true);
        dialogoVista.setSize(700, 800);
        dialogoVista.setLocationRelativeTo(this);

        JTextArea areaVista = new JTextArea(ultimoReporte);
        areaVista.setEditable(false);
        areaVista.setFont(new Font("Courier New", Font.PLAIN, 10));
        areaVista.setBackground(Color.WHITE);
        areaVista.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollVista = new JScrollPane(areaVista);
        dialogoVista.add(scrollVista, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnCerrarVista = new JButton("Cerrar Vista");
        btnCerrarVista.addActionListener(ev -> dialogoVista.dispose());
        panelBotones.add(btnCerrarVista);

        dialogoVista.add(panelBotones, BorderLayout.SOUTH);
        dialogoVista.setVisible(true);
    }
}