package com.iso9001.gui;

import com.iso9001.managers.AuditoriaManager;
import com.iso9001.models.Auditoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

public class VentanaAuditorias extends JFrame {
    private final AuditoriaManager auditoriaManager;

    private JTable tablaAuditorias;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JComboBox<String> comboTipo;
    private JComboBox<String> comboEstado;
    private JTextArea areaDetalles;
    private JPanel panelEstadisticas;

    public VentanaAuditorias(AuditoriaManager auditoriaManager) {
        this.auditoriaManager = auditoriaManager;
        configurarVentana();
        crearComponentes();
        cargarDatos();
    }

    private void configurarVentana() {
        setTitle("Gestión de Auditorías ISO 9001");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void crearComponentes() {
        // Panel superior - Filtros
        JPanel panelFiltros = crearPanelFiltros();
        add(panelFiltros, BorderLayout.NORTH);

        // Panel central dividido
        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Panel izquierdo - Tabla
        JPanel panelTabla = crearPanelTabla();

        // Panel derecho dividido - Detalles y estadísticas
        JSplitPane splitDerecho = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel panelDetalles = crearPanelDetalles();
        panelEstadisticas = crearPanelEstadisticas();

        splitDerecho.setTopComponent(panelDetalles);
        splitDerecho.setBottomComponent(panelEstadisticas);
        splitDerecho.setDividerLocation(350);

        splitPrincipal.setLeftComponent(panelTabla);
        splitPrincipal.setRightComponent(splitDerecho);
        splitPrincipal.setDividerLocation(700);

        add(splitPrincipal, BorderLayout.CENTER);

        // Panel inferior - Botones
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Filtros y Búsqueda"));

        panel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        txtBuscar.addActionListener(e -> filtrarAuditorias());
        panel.add(txtBuscar);

        panel.add(new JLabel("Tipo:"));
        comboTipo = new JComboBox<>();
        comboTipo.addItem("TODOS");
        comboTipo.addItem("Interna");
        comboTipo.addItem("Externa");
        comboTipo.addActionListener(e -> filtrarAuditorias());
        panel.add(comboTipo);

        panel.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>();
        comboEstado.addItem("TODOS");
        comboEstado.addItem("PROGRAMADA");
        comboEstado.addItem("EN_CURSO");
        comboEstado.addItem("COMPLETADA");
        comboEstado.addItem("VENCIDA");
        comboEstado.addActionListener(e -> filtrarAuditorias());
        panel.add(comboEstado);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> filtrarAuditorias());
        panel.add(btnFiltrar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panel.add(btnLimpiar);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Auditorías"));

        // Crear modelo de tabla
        String[] columnas = {"ID", "Título", "Tipo", "Estado", "Fecha Programada", "Auditor Líder", "Calificación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaAuditorias = new JTable(modeloTabla);
        tablaAuditorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAuditorias.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    mostrarDetallesAuditoria();
                }
            }
        });

        // Configurar renderers
        tablaAuditorias.getColumnModel().getColumn(2).setCellRenderer(new TipoRenderer());
        tablaAuditorias.getColumnModel().getColumn(3).setCellRenderer(new EstadoRenderer());
        tablaAuditorias.getColumnModel().getColumn(6).setCellRenderer(new CalificacionRenderer());

        // Configurar anchuras de columnas
        tablaAuditorias.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaAuditorias.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaAuditorias.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaAuditorias.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaAuditorias.getColumnModel().getColumn(4).setPreferredWidth(120);
        tablaAuditorias.getColumnModel().getColumn(5).setPreferredWidth(150);
        tablaAuditorias.getColumnModel().getColumn(6).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(tablaAuditorias);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelDetalles() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Detalles de la Auditoría"));

        areaDetalles = new JTextArea();
        areaDetalles.setEditable(false);
        areaDetalles.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaDetalles.setText("Seleccione una auditoría para ver los detalles...");

        JScrollPane scrollDetalles = new JScrollPane(areaDetalles);
        panel.add(scrollDetalles, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Estadísticas"));

        // Panel de resumen estadístico
        JPanel panelStats = new JPanel(new GridLayout(2, 2, 5, 5));
        panelStats.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crear tarjetas de estadísticas
        panelStats.add(crearTarjetaEstadistica("Total", Color.BLUE));
        panelStats.add(crearTarjetaEstadistica("Programadas", new Color(241, 196, 15)));
        panelStats.add(crearTarjetaEstadistica("Completadas", new Color(39, 174, 96)));
        panelStats.add(crearTarjetaEstadistica("Vencidas", new Color(231, 76, 60)));

        panel.add(panelStats, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearTarjetaEstadistica(String titulo, Color color) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(color);
        tarjeta.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblValor = new JLabel("0", SwingConstants.CENTER);
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("Arial", Font.BOLD, 24));

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        return tarjeta;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton btnNueva = new JButton("Nueva Auditoría");
        btnNueva.addActionListener(e -> mostrarFormularioNuevaAuditoria());

        JButton btnIniciar = new JButton("Iniciar Auditoría");
        btnIniciar.addActionListener(e -> iniciarAuditoria());

        JButton btnHallazgos = new JButton("Registrar Hallazgos");
        btnHallazgos.addActionListener(e -> registrarHallazgos());

        JButton btnCompletar = new JButton("Completar Auditoría");
        btnCompletar.addActionListener(e -> completarAuditoria());

        JButton btnReporte = new JButton("Generar Reporte");
        btnReporte.addActionListener(e -> generarReporteAuditorias());

        JButton btnSalir = new JButton("Cerrar Ventana");
        btnSalir.addActionListener(e -> dispose());

        panel.add(btnNueva);
        panel.add(btnIniciar);
        panel.add(btnHallazgos);
        panel.add(btnCompletar);
        panel.add(btnReporte);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnSalir);

        return panel;
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Auditoria> auditorias = auditoriaManager.obtenerTodasAuditorias();

        for (Auditoria auditoria : auditorias) {
            Object[] fila = {
                    auditoria.getId(),
                    auditoria.getTitulo(),
                    auditoria.getTipo(),
                    auditoria.getEstadoAuditoria(),
                    auditoria.getFechaProgramada(),
                    auditoria.getAuditorLider(),
                    auditoria.isCompletada() ? String.format("%.1f", auditoria.getCalificacionGeneral()) : "N/A"
            };

            modeloTabla.addRow(fila);
        }

        actualizarEstadisticas();
    }

    private void filtrarAuditorias() {
        String busqueda = txtBuscar.getText().trim();
        String tipoSeleccionado = (String) comboTipo.getSelectedItem();
        String estadoSeleccionado = (String) comboEstado.getSelectedItem();

        modeloTabla.setRowCount(0);
        List<Auditoria> auditorias = auditoriaManager.obtenerTodasAuditorias();

        for (Auditoria auditoria : auditorias) {
            boolean coincide = true;

            // Filtrar por búsqueda de texto
            if (!busqueda.isEmpty()) {
                coincide = auditoria.getTitulo().toLowerCase().contains(busqueda.toLowerCase()) ||
                        auditoria.getAuditorLider().toLowerCase().contains(busqueda.toLowerCase());
            }

            // Filtrar por tipo
            if (!"TODOS".equals(tipoSeleccionado)) {
                coincide = coincide && auditoria.getTipo().equals(tipoSeleccionado);
            }

            // Filtrar por estado
            if (!"TODOS".equals(estadoSeleccionado)) {
                coincide = coincide && auditoria.getEstadoAuditoria().equals(estadoSeleccionado);
            }

            if (coincide) {
                Object[] fila = {
                        auditoria.getId(),
                        auditoria.getTitulo(),
                        auditoria.getTipo(),
                        auditoria.getEstadoAuditoria(),
                        auditoria.getFechaProgramada(),
                        auditoria.getAuditorLider(),
                        auditoria.isCompletada() ? String.format("%.1f", auditoria.getCalificacionGeneral()) : "N/A"
                };

                modeloTabla.addRow(fila);
            }
        }
    }

    private void limpiarFiltros() {
        txtBuscar.setText("");
        comboTipo.setSelectedIndex(0);
        comboEstado.setSelectedIndex(0);
        cargarDatos();
    }

    private void mostrarDetallesAuditoria() {
        int filaSeleccionada = tablaAuditorias.getSelectedRow();
        if (filaSeleccionada == -1) return;

        String auditoriaId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Auditoria auditoria = auditoriaManager.obtenerAuditoria(auditoriaId);

        if (auditoria != null) {
            StringBuilder detalles = new StringBuilder();
            detalles.append("DETALLES DE LA AUDITORÍA\n");
            detalles.append("=".repeat(35)).append("\n\n");

            detalles.append("ID: ").append(auditoria.getId()).append("\n");
            detalles.append("Título: ").append(auditoria.getTitulo()).append("\n");
            detalles.append("Tipo: ").append(auditoria.getTipo()).append("\n");
            detalles.append("Estado: ").append(auditoria.getEstadoAuditoria()).append("\n");
            detalles.append("Auditor Líder: ").append(auditoria.getAuditorLider()).append("\n");

            if (!auditoria.getAuditores().isEmpty()) {
                detalles.append("Auditores: ").append(String.join(", ", auditoria.getAuditores())).append("\n");
            }

            detalles.append("\nFECHAS\n");
            detalles.append("-".repeat(15)).append("\n");
            detalles.append("Fecha Programada: ").append(auditoria.getFechaProgramada()).append("\n");

            if (auditoria.getFechaRealizada() != null) {
                detalles.append("Fecha Realizada: ").append(auditoria.getFechaRealizada()).append("\n");
            }

            if (auditoria.getAlcance() != null && !auditoria.getAlcance().isEmpty()) {
                detalles.append("\nALCANCE\n");
                detalles.append("-".repeat(15)).append("\n");
                detalles.append(auditoria.getAlcance()).append("\n");
            }

            if (auditoria.getObjetivos() != null && !auditoria.getObjetivos().isEmpty()) {
                detalles.append("\nOBJETIVOS\n");
                detalles.append("-".repeat(15)).append("\n");
                detalles.append(auditoria.getObjetivos()).append("\n");
            }

            // Procesos auditados
            if (!auditoria.getProcesosAuditados().isEmpty()) {
                detalles.append("\nPROCESOS AUDITADOS\n");
                detalles.append("-".repeat(20)).append("\n");
                for (String proceso : auditoria.getProcesosAuditados()) {
                    detalles.append("• ").append(proceso).append("\n");
                }
            }

            // Hallazgos
            if (!auditoria.getHallazgos().isEmpty()) {
                detalles.append("\nHALLAZGOS\n");
                detalles.append("-".repeat(15)).append("\n");
                for (int i = 0; i < auditoria.getHallazgos().size(); i++) {
                    detalles.append((i + 1)).append(". ").append(auditoria.getHallazgos().get(i)).append("\n");
                }
            }

            // No conformidades detectadas
            if (!auditoria.getNoConformidadesDetectadas().isEmpty()) {
                detalles.append("\nNO CONFORMIDADES DETECTADAS\n");
                detalles.append("-".repeat(30)).append("\n");
                for (String nc : auditoria.getNoConformidadesDetectadas()) {
                    detalles.append("• ").append(nc).append("\n");
                }
            }

            // Resultados (si está completada)
            if (auditoria.isCompletada()) {
                detalles.append("\nRESULTADOS\n");
                detalles.append("-".repeat(15)).append("\n");
                detalles.append("Calificación General: ").append(String.format("%.1f", auditoria.getCalificacionGeneral())).append("\n");

                if (auditoria.getConclusion() != null && !auditoria.getConclusion().isEmpty()) {
                    detalles.append("Conclusión: ").append(auditoria.getConclusion()).append("\n");
                }
            }

            if (auditoria.getObservaciones() != null && !auditoria.getObservaciones().isEmpty()) {
                detalles.append("\nOBSERVACIONES\n");
                detalles.append("-".repeat(15)).append("\n");
                detalles.append(auditoria.getObservaciones()).append("\n");
            }

            areaDetalles.setText(detalles.toString());
            areaDetalles.setCaretPosition(0);
        }
    }

    private void actualizarEstadisticas() {
        Component[] componentes = panelEstadisticas.getComponents();
        if (componentes.length > 0 && componentes[0] instanceof JPanel panelEst) {
            Component[] tarjetas = panelEst.getComponents();

            if (tarjetas.length >= 4) {
                // Actualizar valores
                ((JLabel) ((JPanel) tarjetas[0]).getComponent(1)).setText(String.valueOf(auditoriaManager.getTotalAuditorias()));
                ((JLabel) ((JPanel) tarjetas[1]).getComponent(1)).setText(String.valueOf(auditoriaManager.getAuditoriasProgramadas()));
                ((JLabel) ((JPanel) tarjetas[2]).getComponent(1)).setText(String.valueOf(auditoriaManager.getAuditoriasCompletadas()));
                ((JLabel) ((JPanel) tarjetas[3]).getComponent(1)).setText(String.valueOf(auditoriaManager.getAuditoriasVencidas()));
            }
        }
    }

    private void mostrarFormularioNuevaAuditoria() {
        JOptionPane.showMessageDialog(this,
                "Funcionalidad de creación de auditorías disponible en la versión completa.\n\n" +
                        "Esta ventana permitiría:\n" +
                        "- Programar nueva auditoría\n" +
                        "- Asignar auditores\n" +
                        "- Definir alcance y objetivos",
                "Nueva Auditoría",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void iniciarAuditoria() {
        int filaSeleccionada = tablaAuditorias.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una auditoría para iniciar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String auditoriaId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        auditoriaManager.iniciarAuditoria(auditoriaId);
        cargarDatos();
        mostrarDetallesAuditoria();

        JOptionPane.showMessageDialog(this, "Auditoría iniciada exitosamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void registrarHallazgos() {
        int filaSeleccionada = tablaAuditorias.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una auditoría para registrar hallazgos",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String auditoriaId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);

        String hallazgo = JOptionPane.showInputDialog(this,
                "Ingrese el hallazgo de la auditoría:", "Registrar Hallazgo",
                JOptionPane.QUESTION_MESSAGE);

        if (hallazgo != null && !hallazgo.trim().isEmpty()) {
            auditoriaManager.agregarHallazgo(auditoriaId, hallazgo.trim());
            cargarDatos();
            mostrarDetallesAuditoria();

            JOptionPane.showMessageDialog(this, "Hallazgo registrado exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void completarAuditoria() {
        int filaSeleccionada = tablaAuditorias.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una auditoría para completar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String auditoriaId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);

        // Solicitar conclusión
        String conclusion = JOptionPane.showInputDialog(this,
                "Ingrese la conclusión de la auditoría:", "Completar Auditoría",
                JOptionPane.QUESTION_MESSAGE);

        if (conclusion != null && !conclusion.trim().isEmpty()) {
            // Solicitar calificación
            String calificacionStr = JOptionPane.showInputDialog(this,
                    "Ingrese la calificación general (0.0 - 10.0):", "Calificación",
                    JOptionPane.QUESTION_MESSAGE);

            if (calificacionStr != null && !calificacionStr.trim().isEmpty()) {
                try {
                    double calificacion = Double.parseDouble(calificacionStr.trim());
                    if (calificacion >= 0.0 && calificacion <= 10.0) {
                        auditoriaManager.completarAuditoria(auditoriaId, conclusion.trim(), calificacion);
                        cargarDatos();
                        mostrarDetallesAuditoria();

                        JOptionPane.showMessageDialog(this, "Auditoría completada exitosamente",
                                "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "La calificación debe estar entre 0.0 y 10.0",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Ingrese una calificación numérica válida",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void generarReporteAuditorias() {
        String reporte = auditoriaManager.generarReporteAuditorias();

        JDialog dialogoReporte = new JDialog(this, "Reporte de Auditorías", true);
        dialogoReporte.setSize(700, 600);
        dialogoReporte.setLocationRelativeTo(this);

        JTextArea areaReporte = new JTextArea(reporte);
        areaReporte.setEditable(false);
        areaReporte.setFont(new Font("Courier New", Font.PLAIN, 12));

        JScrollPane scrollReporte = new JScrollPane(areaReporte);
        dialogoReporte.add(scrollReporte, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialogoReporte.dispose());
        panelBotones.add(btnCerrar);

        dialogoReporte.add(panelBotones, BorderLayout.SOUTH);
        dialogoReporte.setVisible(true);
    }

    // Renderers personalizados
    private static class TipoRenderer extends JLabel implements TableCellRenderer {
        public TipoRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Arial", Font.BOLD, 11));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String tipo = (String) value;
            setText(tipo);

            switch (tipo) {
                case "Interna":
                    setBackground(new Color(52, 152, 219));
                    setForeground(Color.WHITE);
                    break;
                case "Externa":
                    setBackground(new Color(155, 89, 182));
                    setForeground(Color.WHITE);
                    break;
                default:
                    setBackground(Color.GRAY);
                    setForeground(Color.WHITE);
            }

            if (isSelected) {
                setBackground(getBackground().darker());
            }

            return this;
        }
    }

    private static class EstadoRenderer extends JLabel implements TableCellRenderer {
        public EstadoRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Arial", Font.BOLD, 11));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String estado = (String) value;
            setText(estado);

            switch (estado) {
                case "PROGRAMADA":
                    setBackground(new Color(241, 196, 15));
                    setForeground(Color.BLACK);
                    break;
                case "EN_CURSO":
                    setBackground(new Color(52, 152, 219));
                    setForeground(Color.WHITE);
                    break;
                case "COMPLETADA":
                    setBackground(new Color(39, 174, 96));
                    setForeground(Color.WHITE);
                    break;
                case "VENCIDA":
                    setBackground(new Color(231, 76, 60));
                    setForeground(Color.WHITE);
                    break;
                default:
                    setBackground(Color.GRAY);
                    setForeground(Color.WHITE);
            }

            if (isSelected) {
                setBackground(getBackground().darker());
            }

            return this;
        }
    }

    private static class CalificacionRenderer extends JLabel implements TableCellRenderer {
        public CalificacionRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Arial", Font.BOLD, 11));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String calificacion = (String) value;
            setText(calificacion);

            if ("N/A".equals(calificacion)) {
                setBackground(Color.LIGHT_GRAY);
                setForeground(Color.BLACK);
            } else {
                try {
                    double cal = Double.parseDouble(calificacion);
                    if (cal >= 8.0) {
                        setBackground(new Color(39, 174, 96)); // Verde - Excelente
                        setForeground(Color.WHITE);
                    } else if (cal >= 6.0) {
                        setBackground(new Color(241, 196, 15)); // Amarillo - Bueno
                        setForeground(Color.BLACK);
                    } else {
                        setBackground(new Color(231, 76, 60)); // Rojo - Necesita mejoras
                        setForeground(Color.WHITE);
                    }
                } catch (NumberFormatException e) {
                    setBackground(Color.LIGHT_GRAY);
                    setForeground(Color.BLACK);
                }
            }

            if (isSelected) {
                setBackground(getBackground().darker());
            }

            return this;
        }
    }
}