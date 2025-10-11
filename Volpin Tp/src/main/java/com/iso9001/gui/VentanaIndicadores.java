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
    private final IndicadorManager indicadorManager;

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

    public void configurarVentana() {
        setTitle("Gestión de Indicadores de Calidad");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    public void crearComponentes() {
        JPanel panelFiltros = crearPanelFiltros();
        add(panelFiltros, BorderLayout.NORTH);

        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JPanel panelTabla = crearPanelTabla();

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

        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }

    public void cargarDatos() {
        try {
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
        } catch (Exception e) {
            System.err.println("Error cargando datos de indicadores: " + e.getMessage());
        }
    }

    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Filtros y Búsqueda"));

        panel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        txtBuscar.addActionListener(_ -> filtrarIndicadores());
        panel.add(txtBuscar);

        panel.add(new JLabel("Tipo:"));
        comboTipo = new JComboBox<>();
        comboTipo.addItem(null);
        for (TipoIndicador tipo : TipoIndicador.values()) {
            comboTipo.addItem(tipo);
        }
        comboTipo.addActionListener(_ -> filtrarIndicadores());
        panel.add(comboTipo);

        panel.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>();
        comboEstado.addItem("TODOS");
        comboEstado.addItem("VERDE");
        comboEstado.addItem("AMARILLO");
        comboEstado.addItem("ROJO");
        comboEstado.addActionListener(_ -> filtrarIndicadores());
        panel.add(comboEstado);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(_ -> filtrarIndicadores());
        panel.add(btnFiltrar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(_ -> limpiarFiltros());
        panel.add(btnLimpiar);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Indicadores de Calidad"));

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

        tablaIndicadores.getColumnModel().getColumn(5).setCellRenderer(new EstadoRenderer());

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

        JPanel panelEstadisticas = new JPanel(new GridLayout(2, 2, 5, 5));
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelEstadisticas.add(crearTarjetaEstadistica("Total", Color.BLUE));
        panelEstadisticas.add(crearTarjetaEstadistica("Verde", new Color(39, 174, 96)));
        panelEstadisticas.add(crearTarjetaEstadistica("Amarillo", new Color(241, 196, 15)));
        panelEstadisticas.add(crearTarjetaEstadistica("Rojo", new Color(231, 76, 60)));

        panel.add(panelEstadisticas, BorderLayout.CENTER);

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

        JButton btnNuevo = new JButton("Nuevo Indicador");
        btnNuevo.addActionListener(_ -> mostrarFormularioNuevoIndicador());

        JButton btnMedicion = new JButton("Registrar Medición");
        btnMedicion.addActionListener(_ -> registrarMedicionIndicador());

        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(_ -> editarIndicadorSeleccionado());

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(_ -> eliminarIndicadorSeleccionado());

        JButton btnReporte = new JButton("Generar Reporte");
        btnReporte.addActionListener(_ -> generarReporteIndicadores());

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(_ -> dispose());

        panel.add(btnNuevo);
        panel.add(btnMedicion);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnReporte);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnCerrar);

        return panel;
    }

    private void filtrarIndicadores() {
        String busqueda = txtBuscar.getText().trim();
        TipoIndicador tipoSeleccionado = (TipoIndicador) comboTipo.getSelectedItem();
        String estadoSeleccionado = (String) comboEstado.getSelectedItem();

        modeloTabla.setRowCount(0);
        List<Indicador> indicadores = indicadorManager.obtenerTodosIndicadores();

        for (Indicador indicador : indicadores) {
            boolean coincide = true;

            if (!busqueda.isEmpty()) {
                coincide = indicador.getNombre().toLowerCase().contains(busqueda.toLowerCase());
            }

            if (tipoSeleccionado != null) {
                coincide = coincide && indicador.getTipo() == tipoSeleccionado;
            }

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
        actualizarEstadisticasGenerales();
    }

    private void actualizarEstadisticasGenerales() {
        Component[] componentes = panelGraficos.getComponents();
        if (componentes.length > 0 && componentes[0] instanceof JPanel panelEst) {
            Component[] tarjetas = panelEst.getComponents();

            if (tarjetas.length >= 4) {
                ((JLabel) ((JPanel) tarjetas[0]).getComponent(1)).setText(String.valueOf(indicadorManager.getTotalIndicadores()));
                ((JLabel) ((JPanel) tarjetas[1]).getComponent(1)).setText(String.valueOf(indicadorManager.getIndicadoresVerde()));
                ((JLabel) ((JPanel) tarjetas[2]).getComponent(1)).setText(String.valueOf(indicadorManager.getIndicadoresAmarillo()));
                ((JLabel) ((JPanel) tarjetas[3]).getComponent(1)).setText(String.valueOf(indicadorManager.getIndicadoresRojo()));
            }
        }
    }

    private void mostrarFormularioNuevoIndicador() {
        JDialog dialogo = new JDialog(this, "Nuevo Indicador", true);
        dialogo.setSize(600, 550);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtId = new JTextField(20);
        JTextField txtNombre = new JTextField(30);
        JTextArea txtDescripcion = new JTextArea(3, 30);
        JComboBox<TipoIndicador> cmbTipo = new JComboBox<>(TipoIndicador.values());
        JTextField txtUnidad = new JTextField(15);
        JTextField txtProceso = new JTextField(20);
        JTextField txtObjetivo = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        panelForm.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        panelForm.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        panelForm.add(new JScrollPane(txtDescripcion), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        panelForm.add(cmbTipo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelForm.add(new JLabel("Unidad de Medida:"), gbc);
        gbc.gridx = 1;
        panelForm.add(txtUnidad, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panelForm.add(new JLabel("Proceso ID:"), gbc);
        gbc.gridx = 1;
        panelForm.add(txtProceso, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        panelForm.add(new JLabel("Valor Objetivo:"), gbc);
        gbc.gridx = 1;
        panelForm.add(txtObjetivo, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(_ -> {
            if (validarFormularioIndicador(txtId, txtNombre, txtUnidad, txtProceso, txtObjetivo)) {
                try {
                    String id = txtId.getText().trim();
                    String nombre = txtNombre.getText().trim();
                    String descripcion = txtDescripcion.getText().trim();
                    TipoIndicador tipo = (TipoIndicador) cmbTipo.getSelectedItem();
                    String unidad = txtUnidad.getText().trim();
                    String proceso = txtProceso.getText().trim();
                    double objetivo = Double.parseDouble(txtObjetivo.getText().trim());

                    Indicador nuevoIndicador = new Indicador(id, nombre, descripcion, tipo, unidad, proceso, objetivo);

                    indicadorManager.agregarIndicador(nuevoIndicador);
                    cargarDatos();
                    dialogo.dispose();

                    JOptionPane.showMessageDialog(this, "Indicador creado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(dialogo, "El valor objetivo debe ser numérico",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancelar.addActionListener(_ -> dialogo.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        dialogo.add(new JScrollPane(panelForm), BorderLayout.CENTER);
        dialogo.add(panelBotones, BorderLayout.SOUTH);

        dialogo.setVisible(true);
    }

    private boolean validarFormularioIndicador(JTextField txtId, JTextField txtNombre,
                                               JTextField txtUnidad, JTextField txtProceso, JTextField txtObjetivo) {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El ID es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (indicadorManager.obtenerIndicador(txtId.getText().trim()) != null) {
            JOptionPane.showMessageDialog(this, "Ya existe un indicador con ese ID", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtUnidad.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La unidad de medida es obligatoria", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtProceso.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El proceso es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtObjetivo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El valor objetivo es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Double.parseDouble(txtObjetivo.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El valor objetivo debe ser numérico", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
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

        String indicadorId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Indicador indicador = indicadorManager.obtenerIndicador(indicadorId);

        if (indicador == null) return;

        JDialog dialogo = new JDialog(this, "Editar Indicador", true);
        dialogo.setSize(600, 500);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField(indicador.getNombre(), 30);
        JTextArea txtDescripcion = new JTextArea(indicador.getDescripcion(), 3, 30);
        JTextField txtObjetivo = new JTextField(String.valueOf(indicador.getValorObjetivo()), 10);

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        panelForm.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        panelForm.add(new JScrollPane(txtDescripcion), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Valor Objetivo:"), gbc);
        gbc.gridx = 1;
        panelForm.add(txtObjetivo, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(_ -> {
            if (!txtNombre.getText().trim().isEmpty() && !txtObjetivo.getText().trim().isEmpty()) {
                try {
                    indicador.setNombre(txtNombre.getText().trim());
                    indicador.setDescripcion(txtDescripcion.getText().trim());
                    indicador.setValorObjetivo(Double.parseDouble(txtObjetivo.getText().trim()));

                    cargarDatos();
                    mostrarDetallesIndicador();
                    dialogo.dispose();

                    JOptionPane.showMessageDialog(this, "Indicador actualizado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(dialogo, "El valor objetivo debe ser numérico",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialogo, "Complete todos los campos obligatorios",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(_ -> dialogo.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        dialogo.add(new JScrollPane(panelForm), BorderLayout.CENTER);
        dialogo.add(panelBotones, BorderLayout.SOUTH);

        dialogo.setVisible(true);
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
        btnCerrar.addActionListener(_ -> dialogoReporte.dispose());
        panelBotones.add(btnCerrar);

        dialogoReporte.add(panelBotones, BorderLayout.SOUTH);
        dialogoReporte.setVisible(true);
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