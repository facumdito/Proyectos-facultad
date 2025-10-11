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
        JPanel panelFiltros = crearPanelFiltros();
        add(panelFiltros, BorderLayout.NORTH);

        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JPanel panelTabla = crearPanelTabla();

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

        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Filtros y Búsqueda"));

        panel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        txtBuscar.addActionListener(_ -> filtrarNoConformidades());
        panel.add(txtBuscar);

        panel.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>();
        comboEstado.addItem(null);
        for (EstadoNoConformidad estado : EstadoNoConformidad.values()) {
            comboEstado.addItem(estado);
        }
        comboEstado.addActionListener(_ -> filtrarNoConformidades());
        panel.add(comboEstado);

        panel.add(new JLabel("Prioridad:"));
        comboPrioridad = new JComboBox<>();
        comboPrioridad.addItem(null);
        for (Prioridad prioridad : Prioridad.values()) {
            comboPrioridad.addItem(prioridad);
        }
        comboPrioridad.addActionListener(_ -> filtrarNoConformidades());
        panel.add(comboPrioridad);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(_ -> filtrarNoConformidades());
        panel.add(btnFiltrar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(_ -> limpiarFiltros());
        panel.add(btnLimpiar);

        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("No Conformidades"));

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

        tablaNoConformidades.getColumnModel().getColumn(2).setCellRenderer(new EstadoRenderer());
        tablaNoConformidades.getColumnModel().getColumn(3).setCellRenderer(new PrioridadRenderer());
        tablaNoConformidades.getColumnModel().getColumn(6).setCellRenderer(new DiasRenderer());

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

        JPanel panelStats = new JPanel(new GridLayout(2, 2, 5, 5));
        panelStats.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelStats.add(crearTarjetaEstadistica("Total", Color.BLUE));
        panelStats.add(crearTarjetaEstadistica("Abiertas", new Color(128, 60, 231)));
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
        btnNueva.addActionListener(_ -> mostrarFormularioNuevaNC());

        JButton btnAcciones = new JButton("Gestionar Acciones");
        btnAcciones.addActionListener(_ -> gestionarAccionesCorrectivas());

        JButton btnCambiarEstado = new JButton("Cambiar Estado");
        btnCambiarEstado.addActionListener(_ -> cambiarEstadoNC());

        JButton btnCerrar = new JButton("Cerrar NC");
        btnCerrar.addActionListener(_ -> cerrarNoConformidad());

        JButton btnReporte = new JButton("Generar Reporte");
        btnReporte.addActionListener(_ -> generarReporteNC());

        JButton btnSalir = new JButton("Cerrar Ventana");
        btnSalir.addActionListener(_ -> dispose());

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

            if (!busqueda.isEmpty()) {
                coincide = nc.getTitulo().toLowerCase().contains(busqueda.toLowerCase()) ||
                        nc.getDescripcion().toLowerCase().contains(busqueda.toLowerCase());
            }

            if (estadoSeleccionado != null) {
                coincide = coincide && nc.getEstado() == estadoSeleccionado;
            }

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
                ((JLabel) ((JPanel) tarjetas[0]).getComponent(1)).setText(String.valueOf(noConformidadManager.getTotalNoConformidades()));
                ((JLabel) ((JPanel) tarjetas[1]).getComponent(1)).setText(String.valueOf(noConformidadManager.getNoConformidadesAbiertas()));
                ((JLabel) ((JPanel) tarjetas[2]).getComponent(1)).setText(String.valueOf(noConformidadManager.getNoConformidadesCerradas()));
                ((JLabel) ((JPanel) tarjetas[3]).getComponent(1)).setText(String.valueOf(noConformidadManager.getNoConformidadesVencidas()));
            }
        }
    }

    private void mostrarFormularioNuevaNC() {
        JDialog dialogo = new JDialog(this, "Nueva No Conformidad", true);
        dialogo.setSize(600, 500);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtId = new JTextField(20);
        JTextField txtTitulo = new JTextField(30);
        JTextArea txtDescripcion = new JTextArea(4, 30);
        JTextField txtProceso = new JTextField(25);
        JTextField txtResponsable = new JTextField(25);
        JComboBox<Prioridad> cmbPrioridad = new JComboBox<>(Prioridad.values());
        JSpinner spinnerDias = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        panelForm.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1;
        panelForm.add(txtTitulo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        panelForm.add(new JScrollPane(txtDescripcion), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Proceso Afectado:"), gbc);
        gbc.gridx = 1;
        panelForm.add(txtProceso, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelForm.add(new JLabel("Responsable:"), gbc);
        gbc.gridx = 1;
        panelForm.add(txtResponsable, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panelForm.add(new JLabel("Prioridad:"), gbc);
        gbc.gridx = 1;
        panelForm.add(cmbPrioridad, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        panelForm.add(new JLabel("Días para corrección:"), gbc);
        gbc.gridx = 1;
        panelForm.add(spinnerDias, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(_ -> {
            if (validarFormularioNC(txtId, txtTitulo, txtDescripcion, txtProceso, txtResponsable)) {
                String id = txtId.getText().trim();
                String titulo = txtTitulo.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                String proceso = txtProceso.getText().trim();
                String responsable = txtResponsable.getText().trim();
                Prioridad prioridad = (Prioridad) cmbPrioridad.getSelectedItem();
                int diasLimite = (Integer) spinnerDias.getValue();

                LocalDate fechaDeteccion = LocalDate.now();
                LocalDate fechaLimite = fechaDeteccion.plusDays(diasLimite);

                NoConformidad nuevaNC = new NoConformidad(
                        id, titulo, descripcion, fechaDeteccion, fechaLimite,
                        proceso, responsable, prioridad
                );

                noConformidadManager.agregarNoConformidad(nuevaNC);
                cargarDatos();
                dialogo.dispose();

                JOptionPane.showMessageDialog(this, "No conformidad creada exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnCancelar.addActionListener(_ -> dialogo.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        dialogo.add(new JScrollPane(panelForm), BorderLayout.CENTER);
        dialogo.add(panelBotones, BorderLayout.SOUTH);

        dialogo.setVisible(true);
    }

    private boolean validarFormularioNC(JTextField txtId, JTextField txtTitulo, JTextArea txtDescripcion,
                                        JTextField txtProceso, JTextField txtResponsable) {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El ID es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (noConformidadManager.obtenerNoConformidad(txtId.getText().trim()) != null) {
            JOptionPane.showMessageDialog(this, "Ya existe una NC con ese ID", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtTitulo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El título es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtDescripcion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La descripción es obligatoria", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtProceso.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El proceso afectado es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtResponsable.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El responsable es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void gestionarAccionesCorrectivas() {
        int filaSeleccionada = tablaNoConformidades.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una no conformidad para gestionar acciones",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ncId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        NoConformidad nc = noConformidadManager.obtenerNoConformidad(ncId);

        if (nc == null) return;

        JDialog dialogo = new JDialog(this, "Acciones Correctivas - " + nc.getTitulo(), true);
        dialogo.setSize(700, 500);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        String[] columnas = {"Descripción", "Responsable", "Fecha Límite", "Completada"};
        DefaultTableModel modeloAcciones = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        for (AccionCorrectiva accion : nc.getAccionesCorrectivas()) {
            modeloAcciones.addRow(new Object[]{
                    accion.getDescripcion(),
                    accion.getResponsable(),
                    accion.getFechaLimite(),
                    accion.isCompletada()
            });
        }

        JTable tablaAcciones = new JTable(modeloAcciones);
        JScrollPane scrollPane = new JScrollPane(tablaAcciones);
        dialogo.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnNueva = new JButton("Nueva Acción");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnGuardar = new JButton("Guardar Cambios");
        JButton btnCerrar = new JButton("Cerrar");

        btnNueva.addActionListener(_ -> {
            JTextField txtDescripcion = new JTextField(30);
            JTextField txtResponsable = new JTextField(20);
            JSpinner spinnerDias = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));

            JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
            panel.add(new JLabel("Descripción:"));
            panel.add(txtDescripcion);
            panel.add(new JLabel("Responsable:"));
            panel.add(txtResponsable);
            panel.add(new JLabel("Días para completar:"));
            panel.add(spinnerDias);

            int result = JOptionPane.showConfirmDialog(dialogo, panel, "Nueva Acción Correctiva",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                if (!txtDescripcion.getText().trim().isEmpty() && !txtResponsable.getText().trim().isEmpty()) {
                    int dias = (Integer) spinnerDias.getValue();
                    LocalDate fechaLimite = LocalDate.now().plusDays(dias);

                    AccionCorrectiva nuevaAccion = new AccionCorrectiva(
                            txtDescripcion.getText().trim(),
                            txtResponsable.getText().trim(),
                            fechaLimite
                    );

                    nc.agregarAccionCorrectiva(nuevaAccion);
                    modeloAcciones.addRow(new Object[]{
                            nuevaAccion.getDescripcion(),
                            nuevaAccion.getResponsable(),
                            nuevaAccion.getFechaLimite(),
                            false
                    });
                } else {
                    JOptionPane.showMessageDialog(dialogo, "Complete todos los campos",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEliminar.addActionListener(_ -> {
            int fila = tablaAcciones.getSelectedRow();
            if (fila != -1) {
                int confirm = JOptionPane.showConfirmDialog(dialogo,
                        "¿Eliminar esta acción correctiva?", "Confirmar",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    List<AccionCorrectiva> acciones = nc.getAccionesCorrectivas();
                    if (fila < acciones.size()) {
                        acciones.remove(fila);
                        modeloAcciones.removeRow(fila);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(dialogo, "Seleccione una acción para eliminar",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnGuardar.addActionListener(_ -> {
            List<AccionCorrectiva> acciones = nc.getAccionesCorrectivas();
            for (int i = 0; i < modeloAcciones.getRowCount() && i < acciones.size(); i++) {
                Boolean completada = (Boolean) modeloAcciones.getValueAt(i, 3);
                if (completada != null && completada) {
                    acciones.get(i).marcarComoCompletada();
                }
            }

            cargarDatos();
            mostrarDetallesNoConformidad();

            JOptionPane.showMessageDialog(dialogo, "Cambios guardados exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });

        btnCerrar.addActionListener(_ -> dialogo.dispose());

        panelBotones.add(btnNueva);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCerrar);

        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
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
        NoConformidad nc = noConformidadManager.obtenerNoConformidad(ncId);

        if (nc == null) return;

        if (!nc.todasAccionesCompletadas() && !nc.getAccionesCorrectivas().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "No todas las acciones correctivas están completadas.\n¿Desea cerrar de todas formas?",
                    "Advertencia", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

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
                    setBackground(new Color(248, 47, 26));
                    setForeground(Color.WHITE);
                    break;
                case ALTA:
                    setBackground(new Color(248, 115, 100));
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
                setBackground(new Color(192, 57, 43));
                setForeground(Color.WHITE);
            } else if (dias <= 3) {
                setBackground(new Color(241, 196, 15));
                setForeground(Color.BLACK);
            } else {
                setBackground(new Color(39, 174, 96));
                setForeground(Color.WHITE);
            }

            if (isSelected) {
                setBackground(getBackground().darker());
            }

            return this;
        }
    }
}