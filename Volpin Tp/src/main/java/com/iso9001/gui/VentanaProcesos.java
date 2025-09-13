package com.iso9001.gui;

import com.iso9001.managers.ProcesoManager;
import com.iso9001.managers.IndicadorManager;
import com.iso9001.models.Proceso;
import com.iso9001.models.Indicador;
import com.iso9001.enums.TipoProceso;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class VentanaProcesos extends JFrame {
    private final ProcesoManager procesoManager;
    private final IndicadorManager indicadorManager;

    private JTable tablaProcesos;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JComboBox<TipoProceso> comboTipo;
    private JTextArea areaDetalles;

    public VentanaProcesos(ProcesoManager procesoManager, IndicadorManager indicadorManager) {
        this.procesoManager = procesoManager;
        this.indicadorManager = indicadorManager;

        configurarVentana();
        crearComponentes();
        cargarDatos();
    }

    private void configurarVentana() {
        setTitle("Gestión de Procesos ISO 9001");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void crearComponentes() {
        // Panel superior - Búsqueda y filtros
        JPanel panelBusqueda = crearPanelBusqueda();
        add(panelBusqueda, BorderLayout.NORTH);

        // Panel central - Tabla de procesos
        JPanel panelTabla = crearPanelTabla();

        // Panel derecho - Detalles
        JPanel panelDetalles = crearPanelDetalles();

        // Dividir panel central
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelTabla, panelDetalles);
        splitPane.setDividerLocation(600);
        add(splitPane, BorderLayout.CENTER);

        // Panel inferior - Botones
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Filtros y Búsqueda"));

        panel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        txtBuscar.addActionListener(_ -> filtrarProcesos());
        panel.add(txtBuscar);

        panel.add(new JLabel("Tipo:"));
        comboTipo = new JComboBox<>();
        comboTipo.addItem(null); // Opción "Todos"
        for (TipoProceso tipo : TipoProceso.values()) {
            comboTipo.addItem(tipo);
        }
        comboTipo.addActionListener(_ -> filtrarProcesos());
        panel.add(comboTipo);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(_ -> filtrarProcesos());
        panel.add(btnBuscar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(_ -> limpiarFiltros());
        panel.add(btnLimpiar);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Procesos"));

        // Crear modelo de tabla
        String[] columnas = {"ID", "Nombre", "Tipo", "Responsable", "Activo", "Indicadores"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProcesos = new JTable(modeloTabla);
        tablaProcesos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProcesos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    mostrarDetallesProceso();
                }
            }
        });

        // Configurar columnas
        tablaProcesos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaProcesos.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaProcesos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaProcesos.getColumnModel().getColumn(3).setPreferredWidth(150);
        tablaProcesos.getColumnModel().getColumn(4).setPreferredWidth(60);
        tablaProcesos.getColumnModel().getColumn(5).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(tablaProcesos);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelDetalles() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Detalles del Proceso"));
        panel.setPreferredSize(new Dimension(350, 0));

        areaDetalles = new JTextArea();
        areaDetalles.setEditable(false);
        areaDetalles.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaDetalles.setText("Seleccione un proceso para ver los detalles...");

        JScrollPane scrollDetalles = new JScrollPane(areaDetalles);
        panel.add(scrollDetalles, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton btnNuevo = new JButton("Nuevo Proceso");
        btnNuevo.addActionListener(_ -> mostrarFormularioNuevoProceso());

        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(_ -> editarProcesoSeleccionado());

        JButton btnEliminar = new JButton("Desactivar");
        btnEliminar.addActionListener(_ -> desactivarProcesoSeleccionado());

        JButton btnReporte = new JButton("Generar Reporte");
        btnReporte.addActionListener(_ -> generarReporteProcesos());

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(_ -> dispose());

        panel.add(btnNuevo);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnReporte);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnCerrar);

        return panel;
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Proceso> procesos = procesoManager.obtenerTodosProcesos();

        for (Proceso proceso : procesos) {
            List<Indicador> indicadores = indicadorManager.obtenerIndicadoresPorProceso(proceso.getId());

            Object[] fila = {
                    proceso.getId(),
                    proceso.getNombre(),
                    proceso.getTipo().getNombre(),
                    proceso.getResponsable(),
                    proceso.isActivo() ? "Sí" : "No",
                    indicadores.size()
            };

            modeloTabla.addRow(fila);
        }
    }

    private void filtrarProcesos() {
        String busqueda = txtBuscar.getText().trim();
        TipoProceso tipoSeleccionado = (TipoProceso) comboTipo.getSelectedItem();

        modeloTabla.setRowCount(0);
        List<Proceso> procesos = procesoManager.obtenerTodosProcesos();

        for (Proceso proceso : procesos) {
            boolean coincide = true;

            // Filtrar por búsqueda de texto
            if (!busqueda.isEmpty()) {
                coincide = proceso.getNombre().toLowerCase().contains(busqueda.toLowerCase()) ||
                        proceso.getResponsable().toLowerCase().contains(busqueda.toLowerCase());
            }

            // Filtrar por tipo
            if (tipoSeleccionado != null) {
                coincide = coincide && proceso.getTipo() == tipoSeleccionado;
            }

            if (coincide) {
                List<Indicador> indicadores = indicadorManager.obtenerIndicadoresPorProceso(proceso.getId());

                Object[] fila = {
                        proceso.getId(),
                        proceso.getNombre(),
                        proceso.getTipo().getNombre(),
                        proceso.getResponsable(),
                        proceso.isActivo() ? "Sí" : "No",
                        indicadores.size()
                };

                modeloTabla.addRow(fila);
            }
        }
    }

    private void limpiarFiltros() {
        txtBuscar.setText("");
        comboTipo.setSelectedIndex(0);
        cargarDatos();
    }

    private void mostrarDetallesProceso() {
        int filaSeleccionada = tablaProcesos.getSelectedRow();
        if (filaSeleccionada == -1) return;

        String procesoId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Proceso proceso = procesoManager.obtenerProceso(procesoId);

        if (proceso != null) {
            StringBuilder detalles = new StringBuilder();
            detalles.append("DETALLES DEL PROCESO\n");
            detalles.append("=".repeat(30)).append("\n\n");

            detalles.append("ID: ").append(proceso.getId()).append("\n");
            detalles.append("Nombre: ").append(proceso.getNombre()).append("\n");
            detalles.append("Tipo: ").append(proceso.getTipo().getNombre()).append("\n");
            detalles.append("Responsable: ").append(proceso.getResponsable()).append("\n");
            detalles.append("Estado: ").append(proceso.isActivo() ? "Activo" : "Inactivo").append("\n");
            detalles.append("Fecha Creación: ").append(proceso.getFechaCreacion()).append("\n");
            detalles.append("Última Revisión: ").append(proceso.getFechaUltimaRevision()).append("\n\n");

            if (proceso.getObjetivo() != null && !proceso.getObjetivo().isEmpty()) {
                detalles.append("Objetivo:\n").append(proceso.getObjetivo()).append("\n\n");
            }

            if (proceso.getAlcance() != null && !proceso.getAlcance().isEmpty()) {
                detalles.append("Alcance:\n").append(proceso.getAlcance()).append("\n\n");
            }

            if (proceso.getDescripcion() != null && !proceso.getDescripcion().isEmpty()) {
                detalles.append("Descripción:\n").append(proceso.getDescripcion()).append("\n\n");
            }

            // Mostrar indicadores asociados
            List<Indicador> indicadores = indicadorManager.obtenerIndicadoresPorProceso(proceso.getId());
            if (!indicadores.isEmpty()) {
                detalles.append("INDICADORES ASOCIADOS\n");
                detalles.append("-".repeat(20)).append("\n");
                for (Indicador ind : indicadores) {
                    detalles.append("• ").append(ind.getNombre()).append("\n");
                    detalles.append("  Valor: ").append(String.format("%.2f", ind.getValorActual()));
                    detalles.append(" / ").append(String.format("%.2f", ind.getValorObjetivo()));
                    detalles.append(" (").append(ind.getEstadoSemaforo()).append(")\n");
                }
            }

            areaDetalles.setText(detalles.toString());
            areaDetalles.setCaretPosition(0);
        }
    }

    private void mostrarFormularioNuevoProceso() {
        JDialog dialogo = new JDialog(this, "Nuevo Proceso", true);
        dialogo.setSize(500, 400);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        // Crear formulario
        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtId = new JTextField(15);
        JTextField txtNombre = new JTextField(25);
        JTextArea txtDescripcion = new JTextArea(3, 25);
        JComboBox<TipoProceso> cmbTipo = new JComboBox<>(TipoProceso.values());
        JTextField txtResponsable = new JTextField(25);

        // Añadir campos al formulario
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
        panelForm.add(new JLabel("Responsable:"), gbc);
        gbc.gridx = 1;
        panelForm.add(txtResponsable, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(_ -> {
            if (validarFormulario(txtId, txtNombre, txtResponsable)) {
                Proceso nuevoProceso = new Proceso(
                        txtId.getText().trim(),
                        txtNombre.getText().trim(),
                        txtDescripcion.getText().trim(),
                        (TipoProceso) cmbTipo.getSelectedItem(),
                        txtResponsable.getText().trim()
                );

                procesoManager.agregarProceso(nuevoProceso);
                cargarDatos();
                dialogo.dispose();

                JOptionPane.showMessageDialog(this, "Proceso creado exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnCancelar.addActionListener(_ -> dialogo.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        dialogo.add(panelForm, BorderLayout.CENTER);
        dialogo.add(panelBotones, BorderLayout.SOUTH);

        dialogo.setVisible(true);
    }

    private boolean validarFormulario(JTextField txtId, JTextField txtNombre, JTextField txtResponsable) {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El ID es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (procesoManager.existeProceso(txtId.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Ya existe un proceso con ese ID", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtResponsable.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El responsable es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void editarProcesoSeleccionado() {
        int filaSeleccionada = tablaProcesos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proceso para editar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Funcionalidad de edición disponible en la versión completa.",
                "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void desactivarProcesoSeleccionado() {
        int filaSeleccionada = tablaProcesos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proceso para desactivar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String procesoId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea desactivar este proceso?",
                "Confirmar Desactivación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            procesoManager.eliminarProceso(procesoId);
            cargarDatos();
            areaDetalles.setText("Seleccione un proceso para ver los detalles...");

            JOptionPane.showMessageDialog(this, "Proceso desactivado exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void generarReporteProcesos() {
        String reporte = procesoManager.generarReporteProcesos();

        JDialog dialogoReporte = new JDialog(this, "Reporte de Procesos", true);
        dialogoReporte.setSize(600, 500);
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
}