package com.iso9001.gui;

import com.iso9001.managers.NoConformidadManager;
import com.iso9001.models.NoConformidad;
import com.iso9001.models.AccionCorrectiva;
import com.iso9001.enums.EstadoNoConformidad;
import com.iso9001.enums.Prioridad;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

public class VentanaNoConformidades extends JFrame {
    private final NoConformidadManager noConformidadManager;

    private JTable tablaNoConformidades;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JComboBox<EstadoNoConformidad> comboEstado;
    private JComboBox<Prioridad> comboPrioridad;
    private JTextArea areaDetalles;
    private JPanel panelEstadisticas;

    public VentanaNoConformidades(NoConformidadManager noConformidadManager) {
        this.noConformidadManager = noConformidadManager;
        configurarVentana();
        crearComponentes();
        cargarDatos();
    }

    private void configurarVentana() {
        setTitle("Gestión de No Conformidades ISO 9001");
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
        txtBuscar.addActionListener(e -> filtrarNoConformidades());
        panel.add(txtBuscar);

        panel.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>();
        comboEstado.addItem(null); // Opción "Todos"
        for (EstadoNoConformidad estado : EstadoNoConformidad.values()) {
            comboEstado.addItem(estado);
        }
        comboEstado.addActionListener(e -> filtrarNoConformidades());
        panel.add(comboEstado);

        panel.add(new JLabel("Prioridad:"));
        comboPrioridad = new JComboBox<>();
        comboPrioridad.addItem(null); // Opción "Todas"
        for (Prioridad prioridad : Prioridad.values()) {
            comboPrioridad.addItem(prioridad);
        }
        comboPrioridad.addActionListener(e -> filtrarNoConformidades());
        panel.add(comboPrioridad);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> filtrarNoConformidades());
        panel.add(btnFiltrar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panel.add(btnLimpiar);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("No Conformidades"));

        // Crear modelo de tabla
        String[] columnas = {"ID", "Título", "Estado", "Prioridad", "Proceso", "Responsable", "Días Restantes"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaNoConformidades = new JTable(modeloTabla);
        tablaNoConformidades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaNoConformidades.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    mostrarDetallesNoConformidad();
                }
            }
        });

        // Configurar renderers
        tablaNoConformidades.getColumnModel().getColumn(2).setCellRenderer(new EstadoRenderer());
        tablaNoConformidades.getColumnModel().getColumn(3).setCellRenderer(new PrioridadRenderer());
        tablaNoConformidades.getColumnModel().getColumn(6).setCellRenderer(new DiasRenderer());

        // Configurar anchuras de columnas
        tablaNoConformidades.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaNoConformidades.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaNoConformidades.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaNoConformidades.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaNoConformidades.getColumnModel().getColumn(4).setPreferredWidth(120);
        tablaNoConformidades.getColumnModel().getColumn(5).setPreferredWidth(120);
        tablaNoConformidades.getColumnModel().getColumn(6).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tablaNoConformidades);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelDetalles() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Detalles de la No Conformidad"));

        areaDetalles = new JTextArea();
        areaDetalles.setEditable(false);
        areaDetalles.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaDetalles.setText("Seleccione una no conformidad para ver los detalles...");

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
        panelStats.add(crearTarjetaEstadistica("Abiertas", new Color(231, 76, 60)));
        panelStats.add(crearTarjetaEstadistica("Cerradas", new Color(39, 174, 96)));
        panelStats.add(crearTarjetaEstadistica("Vencidas", new Color(192, 57, 43)));

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

        JButton btnNueva = new JButton("Nueva No Conformidad");
        btnNueva.addActionListener(e -> mostrarFormularioNuevaNC());

        JButton btnAcciones = new JButton("Gestionar Acciones");
        btnAcciones.addActionListener(e -> gestionarAccionesCorrectivas());

        JButton btnCambiarEstado = new JButton("Cambiar Estado");
        btnCambiarEstado.addActionListener(e -> cambiarEstadoNC());

        JButton btnCerrar = new JButton("Cerrar NC");
        btnCerrar.addActionListener(e -> cerrarNoConformidad());

        JButton btnReporte = new JButton("Generar Reporte");
        btnReporte.addActionListener(e -> generarReporteNC());

        JButton btnSalir = new JButton("Cerrar Ventana");
        btnSalir.addActionListener(e -> dispose());

        panel.add(btnNueva);
        panel.add(btnAcciones);
        panel.add(btnCambiarEstado);
        panel.add(btnCerrar);
        panel.add(btnReporte);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnSalir);

        return panel;
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<NoConformidad> noConformidades = noConformidadManager.obtenerTodasNoConformidades();

        for (NoConformidad nc : noConformidades) {
            Object[] fila = {
                    nc.getId(),
                    nc.getTitulo(),
                    nc.getEstado(),
                    nc.getPrioridad(),
                    nc.getProcesoAfectado(),
                    nc.getResponsableDeteccion(),
                    nc.diasParaVencimiento()
            };

            modeloTabla.addRow(fila);
        }

        actualizarEstadisticas();
    }

    private void filtrarNoConformidades() {
        String busqueda = txtBuscar.getText().trim();
        EstadoNoConformidad estadoSeleccionado = (EstadoNoConformidad) comboEstado.getSelectedItem();
        Prioridad prioridadSeleccionada = (Prioridad) comboPrioridad.getSelectedItem();

        modeloTabla.setRowCount(0);
        List<NoConformidad> noConformidades = noConformidadManager.obtenerTodasNoConformidades();

        for (NoConformidad nc : noConformidades) {
            boolean coincide = true;

            // Filtrar por búsqueda de texto
            if (!busqueda.isEmpty()) {
                coincide = nc.getTitulo().toLowerCase().contains(busqueda.toLowerCase()) ||
                        nc.getDescripcion().toLowerCase().contains(busqueda.toLowerCase());
            }

            // Filtrar por estado
            if (estadoSeleccionado != null) {
                coincide = coincide && nc.getEstado() == estadoSeleccionado;
            }

            // Filtrar por prioridad
            if (prioridadSeleccionada != null) {
                coincide = coincide && nc.getPrioridad() == prioridadSeleccionada;
            }

            if (coincide) {
                Object[] fila = {
                        nc.getId(),
                        nc.getTitulo(),
                        nc.getEstado(),
                        nc.getPrioridad(),
                        nc.getProcesoAfectado(),
                        nc.getResponsableDeteccion(),
                        nc.diasParaVencimiento()
                };

                modeloTabla.addRow(fila);
            }
        }
    }

    private void limpiarFiltros() {
        txtBuscar.setText("");
        comboEstado.setSelectedIndex(0);
        comboPrioridad.setSelectedIndex(0);
        cargarDatos();
    }

    private void mostrarDetallesNoConformidad() {
        int filaSeleccionada = tablaNoConformidades.getSelectedRow();
        if (filaSeleccionada == -1) return;

        String ncId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        NoConformidad nc = noConformidadManager.obtenerNoConformidad(ncId);

        if (nc != null) {
            StringBuilder detalles = new StringBuilder();
            detalles.append("DETALLES DE LA NO CONFORMIDAD\n");
            detalles.append("=".repeat(40)).append("\n\n");

            detalles.append("ID: ").append(nc.getId()).append("\n");
            detalles.append("Título: ").append(nc.getTitulo()).append("\n");
            detalles.append("Estado: ").append(nc.getEstado().getNombre()).append("\n");
            detalles.append("Prioridad: ").append(nc.getPrioridad().getNombre()).append("\n");
            detalles.append("Proceso Afectado: ").append(nc.getProcesoAfectado()).append("\n");
            detalles.append("Responsable Detección: ").append(nc.getResponsableDeteccion()).append("\n");

            if (nc.getResponsableCorreccion() != null) {
                detalles.append("Responsable Corrección: ").append(nc.getResponsableCorreccion()).append("\n");
            }

            detalles.append("\nFECHAS\n");
            detalles.append("-".repeat(15)).append("\n");
            detalles.append("Fecha Detección: ").append(nc.getFechaDeteccion()).append("\n");
            detalles.append("Fecha Límite: ").append(nc.getFechaLimiteCorreccion()).append("\n");

            if (nc.getFechaCierre() != null) {
                detalles.append("Fecha Cierre: ").append(nc.getFechaCierre()).append("\n");
            }

            detalles.append("Días para vencimiento: ").append(nc.diasParaVencimiento()).append("\n");

            detalles.append("\nDESCRIPCIÓN\n");
            detalles.append("-".repeat(15)).append("\n");
            detalles.append(nc.getDescripcion()).append("\n\n");

            if (nc.getCausaRaiz() != null && !nc.getCausaRaiz().isEmpty()) {
                detalles.append("CAUSA RAÍZ\n");
                detalles.append("-".repeat(15)).append("\n");
                detalles.append(nc.getCausaRaiz()).append("\n\n");
            }

            // Acciones correctivas
            List<AccionCorrectiva> acciones = nc.getAccionesCorrectivas();
            if (!acciones.isEmpty()) {
                detalles.append("ACCIONES CORRECTIVAS\n");
                detalles.append("-".repeat(25)).append("\n");
                for (AccionCorrectiva accion : acciones) {
                    detalles.append("• ").append(accion.getDescripcion());
                    detalles.append(" (").append(accion.isCompletada() ? "Completada" : "Pendiente").append(")\n");
                }
                detalles.append("\nCompletitud: ").append(nc.calcularPorcentajeCompletitud()).append("%\n");
            }

            if (nc.getObservaciones() != null && !nc.getObservaciones().isEmpty()) {
                detalles.append("\nOBSERVACIONES\n");
                detalles.append("-".repeat(15)).append("\n");
                detalles.append(nc.getObservaciones()).append("\n");
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
                ((JLabel) ((JPanel) tarjetas[0]).getComponent(1)).setText(String.valueOf(noConformidadManager.getTotalNoConformidades()));
                ((JLabel) ((JPanel) tarjetas[1]).getComponent(1)).setText(String.valueOf(noConformidadManager.getNoConformidadesAbiertas()));
                ((JLabel) ((JPanel) tarjetas[2]).getComponent(1)).setText(String.valueOf(noConformidadManager.getNoConformidadesCerradas()));
                ((JLabel) ((JPanel) tarjetas[3]).getComponent(1)).setText(String.valueOf(noConformidadManager.getNoConformidadesVencidas()));
            }
        }
    }

    private void mostrarFormularioNuevaNC() {
        JOptionPane.showMessageDialog(this,
                "Funcionalidad de creación de no conformidades disponible en la versión completa.\n\n" +
                        "Esta ventana permitiría:\n" +
                        "- Definir nueva no conformidad\n" +
                        "- Asignar responsables\n" +
                        "- Establecer prioridad y fechas límite",
                "Nueva No Conformidad",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void gestionarAccionesCorrectivas() {
        int filaSeleccionada = tablaNoConformidades.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una no conformidad para gestionar acciones",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Gestión de acciones correctivas disponible en la versión completa.",
                "Acciones Correctivas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cambiarEstadoNC() {
        int filaSeleccionada = tablaNoConformidades.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una no conformidad para cambiar estado",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ncId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        EstadoNoConformidad[] estados = EstadoNoConformidad.values();

        EstadoNoConformidad nuevoEstado = (EstadoNoConformidad) JOptionPane.showInputDialog(
                this, "Seleccione el nuevo estado:", "Cambiar Estado",
                JOptionPane.QUESTION_MESSAGE, null, estados, estados[0]);

        if (nuevoEstado != null) {
            noConformidadManager.cambiarEstado(ncId, nuevoEstado);
            cargarDatos();
            mostrarDetallesNoConformidad();
            JOptionPane.showMessageDialog(this, "Estado cambiado exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cerrarNoConformidad() {
        int filaSeleccionada = tablaNoConformidades.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una no conformidad para cerrar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ncId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);

        String observaciones = JOptionPane.showInputDialog(this,
                "Ingrese observaciones de cierre:", "Cerrar No Conformidad",
                JOptionPane.QUESTION_MESSAGE);

        if (observaciones != null) {
            noConformidadManager.cerrarNoConformidad(ncId, observaciones);
            cargarDatos();
            mostrarDetallesNoConformidad();
            JOptionPane.showMessageDialog(this, "No conformidad cerrada exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void generarReporteNC() {
        String reporte = noConformidadManager.generarReporteNoConformidades();

        JDialog dialogoReporte = new JDialog(this, "Reporte de No Conformidades", true);
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
    private static class EstadoRenderer extends JLabel implements TableCellRenderer {
        public EstadoRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Arial", Font.BOLD, 11));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            EstadoNoConformidad estado = (EstadoNoConformidad) value;
            setText(estado.getNombre());

            switch (estado) {
                case ABIERTA:
                    setBackground(new Color(231, 76, 60));
                    setForeground(Color.WHITE);
                    break;
                case EN_ANALISIS:
                    setBackground(new Color(241, 196, 15));
                    setForeground(Color.BLACK);
                    break;
                case EN_CORRECCION:
                    setBackground(new Color(52, 152, 219));
                    setForeground(Color.WHITE);
                    break;
                case CERRADA:
                    setBackground(new Color(39, 174, 96));
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

    private static class PrioridadRenderer extends JLabel implements TableCellRenderer {
        public PrioridadRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Arial", Font.BOLD, 11));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Prioridad prioridad = (Prioridad) value;
            setText(prioridad.getNombre());

            switch (prioridad) {
                case CRITICA:
                    setBackground(new Color(192, 57, 43));
                    setForeground(Color.WHITE);
                    break;
                case ALTA:
                    setBackground(new Color(231, 76, 60));
                    setForeground(Color.WHITE);
                    break;
                case MEDIA:
                    setBackground(new Color(241, 196, 15));
                    setForeground(Color.BLACK);
                    break;
                case BAJA:
                    setBackground(new Color(39, 174, 96));
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

    private static class DiasRenderer extends JLabel implements TableCellRenderer {
        public DiasRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Arial", Font.BOLD, 11));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Long dias = (Long) value;
            setText(dias.toString());

            if (dias < 0) {
                setBackground(new Color(192, 57, 43)); // Rojo - Vencida
                setForeground(Color.WHITE);
            } else if (dias <= 3) {
                setBackground(new Color(241, 196, 15)); // Amarillo - Próxima a vencer
                setForeground(Color.BLACK);
            } else {
                setBackground(new Color(39, 174, 96)); // Verde - Tiempo suficiente
                setForeground(Color.WHITE);
            }

            if (isSelected) {
                setBackground(getBackground().darker());
            }

            return this;
        }
    }
}