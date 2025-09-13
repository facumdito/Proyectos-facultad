package com.iso9001.gui;

import com.iso9001.managers.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;

/**
 * Dashboard Principal del Sistema ISO 9001
 * Versión final con integración completa de ventanas
 */
public class DashboardPrincipal extends JFrame {

    // Managers del sistema
    private ProcesoManager procesoManager;
    private IndicadorManager indicadorManager;
    private NoConformidadManager noConformidadManager;
    private AuditoriaManager auditoriaManager;

    // Referencias a ventanas secundarias
    private VentanaProcesos ventanaProcesos;
    private VentanaIndicadores ventanaIndicadores;
    private VentanaNoConformidades ventanaNoConformidades;
    private VentanaAuditorias ventanaAuditorias;
    private VentanaReportes ventanaReportes;

    // Componentes de la interfaz
    private JTextArea areaAlertas;
    private JLabel lblTotalProcesos;
    private JLabel lblIndicadoresVerde;
    private JLabel lblIndicadoresRojo;
    private JLabel lblNCAbiertas;
    private JLabel lblAuditoriasCompletadas;

    // Constructor
    public DashboardPrincipal() {
        inicializarManagers();
        configurarVentana();
        crearComponentes();
        actualizarDashboard();
    }

    private void inicializarManagers() {
        try {
            System.out.println("Inicializando managers del sistema...");

            // Inicializar managers en el orden correcto
            this.indicadorManager = new IndicadorManager();
            this.procesoManager = new ProcesoManager(indicadorManager);
            this.noConformidadManager = new NoConformidadManager();
            this.auditoriaManager = new AuditoriaManager();

            System.out.println("✓ Managers inicializados correctamente");
        } catch (Exception e) {
            System.err.println("⚠ Error al inicializar managers: " + e.getMessage());
            mostrarAdvertenciaManagers();
        }
    }

    private void mostrarAdvertenciaManagers() {
        JOptionPane.showMessageDialog(this,
                "Los managers del sistema no pudieron inicializarse completamente.\n\n" +
                        "El sistema funcionará con funcionalidad limitada.\n" +
                        "Algunas ventanas mostrarán datos temporales hasta que\n" +
                        "los componentes estén completamente disponibles.",
                "Advertencia del Sistema",
                JOptionPane.WARNING_MESSAGE);
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión de Calidad ISO 9001 - Dashboard Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Configurar Look & Feel
        configurarLookAndFeel();
    }

    private static void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 12));
            UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 11));
            UIManager.put("Table.font", new Font("Arial", Font.PLAIN, 11));
            UIManager.put("Table.gridColor", new Color(230, 230, 230));
            System.out.println("Look & Feel configurado correctamente");
        } catch (Exception e) {
            System.err.println("No se pudo configurar el Look & Feel: " + e.getMessage());
        }
    }

    private void crearComponentes() {
        // Panel superior - Título y fecha
        JPanel panelTitulo = crearPanelTitulo();
        add(panelTitulo, BorderLayout.NORTH);

        // Panel central - Estadísticas y alertas
        JPanel panelEstadisticas = crearPanelEstadisticas();
        JPanel panelAlertas = crearPanelAlertas();

        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelCentral.add(panelEstadisticas);
        panelCentral.add(panelAlertas);

        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior - Navegación
        JPanel panelNavegacion = crearPanelNavegacion();
        add(panelNavegacion, BorderLayout.SOUTH);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Sistema de Gestión de Calidad ISO 9001");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        JLabel fecha = new JLabel("Última actualización: " + LocalDate.now());
        fecha.setFont(new Font("Arial", Font.PLAIN, 14));
        fecha.setForeground(Color.WHITE);

        panel.add(titulo, BorderLayout.WEST);
        panel.add(fecha, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBorder(new TitledBorder("Estadísticas Generales"));
        panel.setBackground(Color.WHITE);

        // Tarjetas de estadísticas con referencias guardadas
        panel.add(crearTarjetaEstadistica("Procesos Activos", "0", new Color(46, 204, 113), "procesoIcon"));
        panel.add(crearTarjetaEstadistica("Indicadores Verde", "0", new Color(39, 174, 96), "indicadorIcon"));
        panel.add(crearTarjetaEstadistica("Indicadores Rojo", "0", new Color(231, 76, 60), "alertIcon"));
        panel.add(crearTarjetaEstadistica("NC Abiertas", "0", new Color(241, 196, 15), "ncIcon"));
        panel.add(crearTarjetaEstadistica("Auditorías Completadas", "0", new Color(155, 89, 182), "auditoriaIcon"));
        panel.add(crearTarjetaEstadistica("Cumplimiento General", "0%", new Color(52, 152, 219), "cumplimientoIcon"));

        return panel;
    }

    private JPanel crearTarjetaEstadistica(String titulo, String valor, Color color, String iconType) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(color);
        tarjeta.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("Arial", Font.BOLD, 28));

        // Guardar referencias para actualizaciones
        switch (iconType) {
            case "procesoIcon":
                lblTotalProcesos = lblValor;
                break;
            case "indicadorIcon":
                lblIndicadoresVerde = lblValor;
                break;
            case "alertIcon":
                lblIndicadoresRojo = lblValor;
                break;
            case "ncIcon":
                lblNCAbiertas = lblValor;
                break;
            case "auditoriaIcon":
                lblAuditoriasCompletadas = lblValor;
                break;
        }

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        return tarjeta;
    }

    private JPanel crearPanelAlertas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Alertas y Notificaciones"));

        areaAlertas = new JTextArea(8, 50);
        areaAlertas.setEditable(false);
        areaAlertas.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaAlertas.setBackground(new Color(248, 249, 250));

        JScrollPane scrollAlertas = new JScrollPane(areaAlertas);
        panel.add(scrollAlertas, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelNavegacion() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Botones de navegación con eventos reales
        JButton btnProcesos = crearBotonNavegacion("Gestionar Procesos", new Color(41, 128, 185));
        JButton btnIndicadores = crearBotonNavegacion("Gestionar Indicadores", new Color(39, 174, 96));
        JButton btnNoConformidades = crearBotonNavegacion("No Conformidades", new Color(231, 76, 60));
        JButton btnAuditorias = crearBotonNavegacion("Auditorías", new Color(155, 89, 182));
        JButton btnReportes = crearBotonNavegacion("Reportes", new Color(241, 196, 15));
        JButton btnActualizar = crearBotonNavegacion("Actualizar", new Color(52, 73, 94));

        // Asignar eventos para abrir ventanas reales
        btnProcesos.addActionListener(e -> abrirVentanaProcesos());
        btnIndicadores.addActionListener(e -> abrirVentanaIndicadores());
        btnNoConformidades.addActionListener(e -> abrirVentanaNoConformidades());
        btnAuditorias.addActionListener(e -> abrirVentanaAuditorias());
        btnReportes.addActionListener(e -> abrirVentanaReportes());
        btnActualizar.addActionListener(e -> actualizarDashboard());

        panel.add(btnProcesos);
        panel.add(btnIndicadores);
        panel.add(btnNoConformidades);
        panel.add(btnAuditorias);
        panel.add(btnReportes);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnActualizar);

        return panel;
    }

    private JButton crearBotonNavegacion(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(150, 40));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setFont(new Font("Arial", Font.BOLD, 12));

        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    private void actualizarDashboard() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Actualizar estadísticas con datos reales
                    actualizarEstadisticasReales();

                    // Actualizar alertas
                    actualizarAlertas();

                    System.out.println("Dashboard actualizado: " + LocalDate.now());
                } catch (Exception e) {
                    System.err.println("Error al actualizar dashboard: " + e.getMessage());
                    actualizarEstadisticasTemporales();
                }
            }
        });
    }

    private void actualizarEstadisticasReales() {
        try {
            if (procesoManager != null) {
                lblTotalProcesos.setText(String.valueOf(procesoManager.getProcesosActivos()));
            }

            if (indicadorManager != null) {
                lblIndicadoresVerde.setText(String.valueOf(indicadorManager.getIndicadoresVerde()));
                lblIndicadoresRojo.setText(String.valueOf(indicadorManager.getIndicadoresRojo()));
            }

            if (noConformidadManager != null) {
                lblNCAbiertas.setText(String.valueOf(noConformidadManager.getNoConformidadesAbiertas()));
            }

            if (auditoriaManager != null) {
                lblAuditoriasCompletadas.setText(String.valueOf(auditoriaManager.getAuditoriasCompletadas()));
            }
        } catch (Exception e) {
            System.err.println("Error actualizando estadísticas reales: " + e.getMessage());
            actualizarEstadisticasTemporales();
        }
    }

    private void actualizarEstadisticasTemporales() {
        // Datos temporales para desarrollo
        lblTotalProcesos.setText("5");
        lblIndicadoresVerde.setText("8");
        lblIndicadoresRojo.setText("2");
        lblNCAbiertas.setText("3");
        lblAuditoriasCompletadas.setText("2");
    }

    private void actualizarAlertas() {
        StringBuilder alertas = new StringBuilder();
        alertas.append("=== ALERTAS DEL SISTEMA ===\n");
        alertas.append("Fecha: ").append(LocalDate.now()).append("\n\n");

        try {
            // Alertas reales de los managers
            if (noConformidadManager != null) {
                for (String alerta : noConformidadManager.generarAlertas()) {
                    alertas.append("• ").append(alerta).append("\n");
                }
            }

            if (auditoriaManager != null) {
                for (String alerta : auditoriaManager.generarAlertas()) {
                    alertas.append("• ").append(alerta).append("\n");
                }
            }

            // Resumen de indicadores
            if (indicadorManager != null) {
                alertas.append("\n📊 RESUMEN DE INDICADORES:\n");
                alertas.append("• Total indicadores: ").append(indicadorManager.getTotalIndicadores()).append("\n");
                alertas.append("• Indicadores saludables: ").append(indicadorManager.getIndicadoresVerde()).append("\n");
                alertas.append("• Indicadores críticos: ").append(indicadorManager.getIndicadoresRojo()).append("\n");
            }

        } catch (Exception e) {
            // Alertas de ejemplo en caso de error
            alertas.append("⚠ ALERTAS ACTIVAS:\n\n");
            alertas.append("• 2 No conformidades próximas a vencer\n");
            alertas.append("• Indicador 'Satisfacción Cliente' por debajo del objetivo\n");
            alertas.append("• Auditoría interna programada para la próxima semana\n");
        }

        areaAlertas.setText(alertas.toString());
        areaAlertas.setCaretPosition(0);
    }

    // ===============================================================
    // MÉTODOS PARA ABRIR VENTANAS REALES
    // ===============================================================

    private void abrirVentanaProcesos() {
        try {
            if (procesoManager != null && indicadorManager != null) {
                if (ventanaProcesos == null || !ventanaProcesos.isDisplayable()) {
                    ventanaProcesos = new VentanaProcesos(procesoManager, indicadorManager);
                }
                ventanaProcesos.setVisible(true);
                ventanaProcesos.toFront();
                ventanaProcesos.requestFocus();
            } else {
                manejarManagerNoDisponible("Gestión de Procesos");
            }
        } catch (Exception e) {
            manejarErrorVentana("Gestión de Procesos", e);
        }
    }

    private void abrirVentanaIndicadores() {
        try {
            if (indicadorManager != null) {
                if (ventanaIndicadores == null || !ventanaIndicadores.isDisplayable()) {
                    ventanaIndicadores = new VentanaIndicadores(indicadorManager);
                    ventanaIndicadores.configurarVentana();
                    ventanaIndicadores.crearComponentes();
                    ventanaIndicadores.cargarDatos();
                }
                ventanaIndicadores.setVisible(true);
                ventanaIndicadores.toFront();
                ventanaIndicadores.requestFocus();
            } else {
                manejarManagerNoDisponible("Gestión de Indicadores");
            }
        } catch (Exception e) {
            manejarErrorVentana("Gestión de Indicadores", e);
        }
    }

    private void abrirVentanaNoConformidades() {
        try {
            if (noConformidadManager != null) {
                if (ventanaNoConformidades == null || !ventanaNoConformidades.isDisplayable()) {
                    ventanaNoConformidades = new VentanaNoConformidades(noConformidadManager);
                }
                ventanaNoConformidades.setVisible(true);
                ventanaNoConformidades.toFront();
                ventanaNoConformidades.requestFocus();
            } else {
                manejarManagerNoDisponible("No Conformidades");
            }
        } catch (Exception e) {
            manejarErrorVentana("No Conformidades", e);
        }
    }

    private void abrirVentanaAuditorias() {
        try {
            if (auditoriaManager != null) {
                if (ventanaAuditorias == null || !ventanaAuditorias.isDisplayable()) {
                    ventanaAuditorias = new VentanaAuditorias(auditoriaManager);
                }
                ventanaAuditorias.setVisible(true);
                ventanaAuditorias.toFront();
                ventanaAuditorias.requestFocus();
            } else {
                manejarManagerNoDisponible("Auditorías");
            }
        } catch (Exception e) {
            manejarErrorVentana("Auditorías", e);
        }
    }

    private void abrirVentanaReportes() {
        try {
            if (procesoManager != null && indicadorManager != null && noConformidadManager != null) {
                if (ventanaReportes == null || !ventanaReportes.isDisplayable()) {
                    ventanaReportes = new VentanaReportes(procesoManager, indicadorManager, noConformidadManager);
                }
                ventanaReportes.setVisible(true);
                ventanaReportes.toFront();
                ventanaReportes.requestFocus();
            } else {
                manejarManagerNoDisponible("Reportes y Análisis");
            }
        } catch (Exception e) {
            manejarErrorVentana("Reportes y Análisis", e);
        }
    }

    // Método para manejar cuando un manager no está disponible
    private void manejarManagerNoDisponible(String nombreVentana) {
        JOptionPane.showMessageDialog(this,
                "El módulo " + nombreVentana + " no está disponible.\n\n" +
                        "Posibles causas:\n" +
                        "• Los managers del sistema no se inicializaron correctamente\n" +
                        "• Faltan dependencias o configuraciones\n" +
                        "• Error en la conexión a base de datos\n\n" +
                        "Por favor, revise la consola para más detalles o\n" +
                        "reinicie la aplicación.",
                "Módulo No Disponible - " + nombreVentana,
                JOptionPane.WARNING_MESSAGE);
    }

    // Método para manejar errores al abrir ventanas
    private void manejarErrorVentana(String nombreVentana, Exception e) {
        String mensajeError = "Error al abrir la ventana: " + nombreVentana +
                "\nDetalle: " + e.getMessage();

        System.err.println(mensajeError);
        e.printStackTrace();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(DashboardPrincipal.this,
                        "No se pudo abrir la ventana de " + nombreVentana + ".\n\n" +
                                "Error técnico: " + e.getMessage() + "\n\n" +
                                "Por favor, contacte al administrador del sistema.",
                        "Error - " + nombreVentana,
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Métodos auxiliares para cerrar ventanas
    public void cerrarTodasLasVentanas() {
        try {
            if (ventanaProcesos != null && ventanaProcesos.isDisplayable()) {
                ventanaProcesos.dispose();
            }
            if (ventanaIndicadores != null && ventanaIndicadores.isDisplayable()) {
                ventanaIndicadores.dispose();
            }
            if (ventanaNoConformidades != null && ventanaNoConformidades.isDisplayable()) {
                ventanaNoConformidades.dispose();
            }
            if (ventanaAuditorias != null && ventanaAuditorias.isDisplayable()) {
                ventanaAuditorias.dispose();
            }
            if (ventanaReportes != null && ventanaReportes.isDisplayable()) {
                ventanaReportes.dispose();
            }
        } catch (Exception e) {
            System.err.println("Error cerrando ventanas: " + e.getMessage());
        }
    }

    public void dispose() {
        cerrarTodasLasVentanas();
        super.dispose();
    }

    // Método main para testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DashboardPrincipal().setVisible(true);
            }
        });
    }
}