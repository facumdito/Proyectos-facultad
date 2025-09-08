package com.iso9001.gui;

import com.iso9001.managers.IndicadorManager;
import com.iso9001.models.Indicador;
import com.iso9001.enums.TipoIndicador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class VentanaIndicadores extends JFrame {
    private IndicadorManager indicadorManager;

    private JTable tablaIndicadores;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JComboBox<TipoIndicador> comboTipo;
    private JComboBox<String> comboEstado;
    private JTextArea areaDetalles;
    private JPanel panelGraficos;

    public VentanaIndicadores(IndicadorManager indicadorManager) {
        this.indicadorManager = indicadorManager;

        configurarVentana();
        crearComponentes();
        cargarDatos();
    }

    private void configurarVentana() {
        setTitle("Gestión de Indicadores de Calidad");
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

        // Panel derecho dividido - Detalles y gráficos
        JSplitPane splitDerecho = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel panelDetalles = crearPanelDetalles();
        panelGraficos = crearPanelGraficos();

        splitDerecho.setTopComponent(panelDetalles);
        splitDerecho.setBottomComponent(panelGraficos);
        splitDerecho.setDividerLocation(300);

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
        txtBuscar.addActionListener(e -> filtrarIndicadores());
        panel.add(txtBuscar);

        panel.add(new JLabel("Tipo:"));
        comboTipo = new JComboBox<>();
        comboTipo.addItem(null); // Opción "Todos"
        for (TipoIndicador tipo : TipoIndicador.values()) {
            comboTipo.addItem(tipo);
        }
        comboTipo.addActionListener(e -> filtrarIndicadores());
        panel.add(comboTipo);

        panel.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>();
        comboEstado.addItem("TODOS");
        comboEstado.addItem("VERDE");
        comboEstado.addItem("AMARILLO");
        comboEstado.addItem("ROJO");
        comboEstado.addActionListener(e -> filtrarIndicadores());
        panel.add(comboEstado);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> filtrarIndicadores());
        panel.add(btnFiltrar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panel.add(btnLimpiar);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Indicadores de Calidad"));

        // Crear modelo de tabla
        String[] columnas = {"ID", "Nombre", "Tipo", "Actual", "Objetivo", "Estado", "Tendencia"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaIndicadores = new JTable(modeloTabla);
        tablaIndicadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaIndicadores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    mostrarDetallesIndicador();
                    actualizarGraficos();
                }
            }
        });

        // Configurar renderer para la columna de estado
        tablaIndicadores.getColumnModel().getColumn(5).setCellRenderer(new EstadoRenderer());

        // Configurar anchuras de columnas
        tablaIndicadores.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaIndicadores.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaIndicadores.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaIndicadores.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaIndicadores.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaIndicadores.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaIndicadores.getColumnModel().getColumn(6).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(tablaIndicadores);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelDetalles() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Detalles del Indicador"));

        areaDetalles = new JTextArea();
        areaDetalles.setEditable(false);
        areaDetalles.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaDetalles.setText("Seleccione un indicador para ver los detalles...");

        JScrollPane scrollDetalles = new JScrollPane(areaDetalles);
        panel.add(scrollDetalles, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelGraficos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Análisis Visual"));

        // Panel de resumen estadístico
        JPanel panelEstadisticas = new JPanel(new GridLayout(2, 2, 5, 5));
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crear tarjetas de estadísticas
        panelEstadisticas.add(crearTarjetaEstadistica("Total", "0", Color.BLUE));
        panelEstadisticas.add(crearTarjetaEstadistica("Verde", "0", new Color(39, 174, 96)));
        panelEstadisticas.add(crearTarjetaEstadistica("Amarillo", "0", new Color(241, 196, 15)));
        panelEstadisticas.add(crearTarjetaEstadistica("Rojo", "0", new Color(231, 76, 60)));

        panel.add(panelEstadisticas, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearTarjetaEstadistica(String titulo, String valor, Color color) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(color);
        tarjeta.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("Arial", Font.BOLD, 24));

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        return tarjeta;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton btnNuevo = new JButton("Nuevo Indicador");
        btnNuevo.addActionListener(e -> mostrarFormularioNuevoIndicador());

        JButton btnMedicion = new JButton("Registrar Medición");
        btnMedicion.addActionListener(e -> registrarMedicionIndicador());

        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarIndicadorSeleccionado());

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarIndicadorSeleccionado());

        JButton btnReporte = new JButton("Generar Reporte");
        btnReporte.addActionListener(e -> generarReporteIndicadores());

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        panel.add(btnNuevo);
        panel.add(btnMedicion);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnReporte);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnCerrar);

        return panel;
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Indicador> indicadores = indicadorManager.obtenerTodosIndicadores();

        for (Indicador indicador : indicadores) {
            Object[] fila = {
                    indicador.getId(),
                    indicador.getNombre(),
                    indicador.getTipo().getNombre(),
                    String.format("%.2f", indicador.getValorActual()),
                    String.format("%.2f", indicador.getValorObjetivo()),
                    indicador.getEstadoSemaforo(),
                    String.format("%.1f%%", indicador.calcularTendencia())
            };

            modeloTabla.addRow(fila);
        }

        actualizarEstadisticasGenerales();
    }

    private void filtrarIndicadores() {
        String busqueda = txtBuscar.getText().trim();
        TipoIndicador tipoSeleccionado = (TipoIndicador) comboTipo.getSelectedItem();
        String estadoSeleccionado = (String) comboEstado.getSelectedItem();

        modeloTabla.setRowCount(0);
        List<Indicador> indicadores = indicadorManager.obtenerTodosIndicadores();

        for (Indicador indicador : indicadores) {
            boolean coincide = true;

            // Filtrar por búsqueda de texto
            if (!busqueda.isEmpty()) {
                coincide = indicador.getNombre().toLowerCase().contains(busqueda.toLowerCase());
            }

            // Filtrar por tipo
            if (tipoSeleccionado != null) {
                coincide = coincide && indicador.getTipo() == tipoSeleccionado;
            }

            // Filtrar por estado
            if (!"TODOS".equals(estadoSeleccionado)) {
                coincide = coincide && indicador.getEstadoSemaforo().equals(estadoSeleccionado);
            }

            if (coincide) {
                Object[] fila = {
                        indicador.getId(),
                        indicador.getNombre(),
                        indicador.getTipo().getNombre(),
                        String.format("%.2f", indicador.getValorActual()),
                        String.format("%.2f", indicador.getValorObjetivo()),
                        indicador.getEstadoSemaforo(),
                        String.format("%.1f%%", indicador.calcularTendencia())
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

    private void mostrarDetallesIndicador() {
        int filaSeleccionada = tablaIndicadores.getSelectedRow();
        if (filaSeleccionada == -1) return;

        String indicadorId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Indicador indicador = indicadorManager.obtenerIndicador(indicadorId);

        if (indicador != null) {
            StringBuilder detalles = new StringBuilder();
            detalles.append("DETALLES DEL INDICADOR\n");
            detalles.append("=".repeat(35)).append("\n\n");

            detalles.append("ID: ").append(indicador.getId()).append("\n");
            detalles.append("Nombre: ").append(indicador.getNombre()).append("\n");
            detalles.append("Tipo: ").append(indicador.getTipo().getNombre()).append("\n");
            detalles.append("Unidad: ").append(indicador.getUnidadMedida()).append("\n");
            detalles.append("Proceso: ").append(indicador.getProcesoId()).append("\n\n");

            detalles.append("VALORES\n");
            detalles.append("-".repeat(15)).append("\n");
            detalles.append("Actual: ").append(String.format("%.2f", indicador.getValorActual())).append(" ").append(indicador.getUnidadMedida()).append("\n");
            detalles.append("Objetivo: ").append(String.format("%.2f", indicador.getValorObjetivo())).append(" ").append(indicador.getUnidadMedida()).append("\n");
            detalles.append("Anterior: ").append(String.format("%.2f", indicador.getValorAnterior())).append(" ").append(indicador.getUnidadMedida()).append("\n");
            detalles.append("Estado: ").append(indicador.getEstadoSemaforo()).append("\n");
            detalles.append("Tendencia: ").append(String.format("%.2f%%", indicador.calcularTendencia())).append("\n\n");

            detalles.append("ANÁLISIS\n");
            detalles.append("-".repeat(15)).append("\n");
            detalles.append("Promedio: ").append(String.format("%.2f", indicador.calcularPromedio())).append("\n");
            detalles.append("En objetivo: ").append(indicador.estaDentroObjetivo() ? "Sí" : "No").append("\n");
            detalles.append("Última medición: ").append(indicador.getFechaUltimaMedicion()).append("\n\n");

            if (indicador.getDescripcion() != null && !indicador.getDescripcion().isEmpty()) {
                detalles.append("DESCRIPCIÓN\n");
                detalles.append("-".repeat(15)).append("\n");
                detalles.append(indicador.getDescripcion()).append("\n\n");
            }

            // Historial de valores
            List<Double> historial = indicador.getHistorialValores();
            if (!historial.isEmpty()) {
                detalles.append("HISTORIAL RECIENTE\n");
                detalles.append("-".repeat(20)).append("\n");
                for (int i = Math.max(0, historial.size() - 5); i < historial.size(); i++) {
                    detalles.append("• ").append(String.format("%.2f", historial.get(i))).append("\n");
                }
            }

            areaDetalles.setText(detalles.toString());
            areaDetalles.setCaretPosition(0);
        }
    }

    private void actualizarGraficos() {
        // Actualizar las tarjetas de estadísticas en el panel de gráficos
        actualizarEstadisticasGenerales();
    }

    private void actualizarEstadisticasGenerales() {
        Component[] componentes = panelGraficos.getComponents();
        if (componentes.length > 0 && componentes[0] instanceof JPanel) {
            JPanel panelEst = (JPanel) componentes[0];
            Component[] tarjetas = panelEst.getComponents();

            if (tarjetas.length >= 4) {
                // Actualizar valores
                ((JLabel) ((JPanel) tarjetas[0]).getComponent(1)).setText(String.valueOf(indicadorManager.getTotalIndicadores()));
                ((JLabel) ((JPanel) tarjetas[1]).getComponent(1)).setText(String.valueOf(indicadorManager.getIndicadoresVerde()));
                ((JLabel) ((JPanel) tarjetas[2]).getComponent(1)).setText(String.valueOf(indicadorManager.getIndicadoresAmarillo()));
                ((JLabel) ((JPanel) tarjetas[3]).getComponent(1)).setText(String.valueOf(indicadorManager.getIndicadoresRojo()));
            }
        }
    }

    private void mostrarFormularioNuevoIndicador() {
        JOptionPane.showMessageDialog(this,
                "Funcionalidad de creación de indicadores disponible en la versión completa.\n" +
                        "Esta ventana permitiría:\n" +
                        "- Definir nuevo indicador\n" +
                        "- Configurar fórmula de cálculo\n" +
                        "- Establecer objetivos y umbrales",
                "Nuevo Indicador",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void registrarMedicionIndicador() {
        int filaSeleccionada = tablaIndicadores.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un indicador para registrar medición",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String indicadorId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Indicador indicador = indicadorManager.obtenerIndicador(indicadorId);

        if (indicador != null) {
            String input = JOptionPane.showInputDialog(this,
                    "Ingrese el nuevo valor para " + indicador.getNombre() +
                            " (" + indicador.getUnidadMedida() + "):",
                    "Registrar Medición",
                    JOptionPane.QUESTION_MESSAGE);

            if (input != null && !input.trim().isEmpty()) {
                try {
                    double nuevoValor = Double.parseDouble(input.trim());
                    indicadorManager.registrarMedicion(indicadorId, nuevoValor);
                    cargarDatos();
                    mostrarDetallesIndicador();

                    JOptionPane.showMessageDialog(this, "Medición registrada exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Ingrese un valor numérico válido",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void editarIndicadorSeleccionado() {
        int filaSeleccionada = tablaIndicadores.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un indicador para editar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Funcionalidad de edición disponible en la versión completa.",
                "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarIndicadorSeleccionado() {
        int filaSeleccionada = tablaIndicadores.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un indicador para eliminar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String indicadorId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar este indicador?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            indicadorManager.eliminarIndicador(indicadorId);
            cargarDatos();
            areaDetalles.setText("Seleccione un indicador para ver los detalles...");

            JOptionPane.showMessageDialog(this, "Indicador eliminado exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void generarReporteIndicadores() {
        String reporte = indicadorManager.generarReporteIndicadores();

        JDialog dialogoReporte = new JDialog(this, "Reporte de Indicadores", true);
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

    // Renderer personalizado para la columna de estado
    private class EstadoRenderer extends JLabel implements TableCellRenderer {
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
                case "VERDE":
                    setBackground(new Color(39, 174, 96));
                    setForeground(Color.WHITE);
                    break;
                case "AMARILLO":
                    setBackground(new Color(241, 196, 15));
                    setForeground(Color.BLACK);
                    break;
                case "ROJO":
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
}