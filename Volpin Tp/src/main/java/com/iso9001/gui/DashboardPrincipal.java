package com.iso9001.gui;

import com.iso9001.managers.*;
import com.iso9001.models.*;
import com.iso9001.utils.ReporteGenerator;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class DashboardPrincipal extends JFrame {
    private ProcesoManager procesoManager;
    private IndicadorManager indicadorManager;
    private NoConformidadManager noConformidadManager;
    private AuditoriaManager auditoriaManager;

    // Componentes de la interfaz
    private JPanel panelEstadisticas;
    private JPanel panelAlertas;
    private JTextArea areaAlertas;
    private JLabel lblTotalProcesos;
    private JLabel lblIndicadoresVerde;
    private JLabel lblIndicadoresRojo;
    private JLabel lblNCAbiertas;
    private JLabel lblAuditoriasCompletadas;

    public DashboardPrincipal() {
        inicializarManagers();
        configurarVentana();
        crearComponentes();
        actualizarDashboard();
    }

    private void inicializarManagers() {
        indicadorManager = new IndicadorManager();
        procesoManager = new ProcesoManager(indicadorManager);
        noConformidadManager = new NoConformidadManager();
        auditoriaManager = new AuditoriaManager();
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión de Calidad ISO 9001 - Dashboard Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Configurar Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void crearComponentes() {
        // Panel superior - Título y fecha
        JPanel panelTitulo = crearPanelTitulo();
        add(panelTitulo, BorderLayout.NORTH);

        // Panel central - Estadísticas
        panelEstadisticas = crearPanelEstadisticas();

        // Panel de alertas
        panelAlertas = crearPanelAlertas();

        // Panel de navegación
        JPanel panelNavegacion = crearPanelNavegacion();

        // Organizar paneles centrales
        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelCentral.add(panelEstadisticas);
        panelCentral.add(panelAlertas);

        add(panelCentral, BorderLayout.CENTER);
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

        // Tarjetas de estadísticas
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

        // Guardar referencia para actualizaciones
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

        // Botones de navegación
        JButton btnProcesos = crearBotonNavegacion("Gestionar Procesos", new Color(41, 128, 185));
        JButton btnIndicadores = crearBotonNavegacion("Gestionar Indicadores", new Color(39, 174, 96));
        JButton btnNoConformidades = crearBotonNavegacion("No Conformidades", new Color(231, 76, 60));
        JButton btnAuditorias = crearBotonNavegacion("Auditorías", new Color(155, 89, 182));
        JButton btnReportes = crearBotonNavegacion("Reportes", new Color(241, 196, 15));
        JButton btnActualizar = crearBotonNavegacion("Actualizar", new Color(52, 73, 94));

        // Agregar eventos
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
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    private void actualizarDashboard() {
        SwingUtilities.invokeLater(() -> {
            // Actualizar estadísticas
            lblTotalProcesos.setText(String.valueOf(procesoManager.getProcesosActivos()));
            lblIndicadoresVerde.setText(String.valueOf(indicadorManager.getIndicadoresVerde()));
            lblIndicadoresRojo.setText(String.valueOf(indicadorManager.getIndicadoresRojo()));
            lblNCAbiertas.setText(String.valueOf(noConformidadManager.getNoConformidadesAbiertas()));
            lblAuditoriasCompletadas.setText(String.valueOf(auditoriaManager.getAuditoriasCompletadas()));

            // Actualizar alertas
            actualizarAlertas();

            System.out.println("Dashboard actualizado: " + LocalDate.now());
        });
    }

    private void actualizarAlertas() {
        StringBuilder alertas = new StringBuilder();
        alertas.append("=== ALERTAS DEL SISTEMA ===\n");
        alertas.append("Fecha: ").append(LocalDate.now()).append("\n\n");

        // Obtener alertas de todos los managers
        List<String> alertasNC = noConformidadManager.generarAlertas();
        List<String> alertasAud = auditoriaManager.generarAlertas();

        if (alertasNC.isEmpty() && alertasAud.isEmpty()) {
            alertas.append("✓ No hay alertas críticas en este momento.\n");
            alertas.append("✓ Sistema funcionando correctamente.\n");
        } else {
            alertas.append("⚠ ALERTAS ACTIVAS:\n\n");

            for (String alerta : alertasNC) {
                alertas.append("• ").append(alerta).append("\n");
            }

            for (String alerta : alertasAud) {
                alertas.append("• ").append(alerta).append("\n");
            }
        }

        // Agregar resumen de indicadores críticos
        List<Indicador> indicadoresCriticos = indicadorManager.obtenerIndicadoresCriticos();
        if (!indicadoresCriticos.isEmpty()) {
            alertas.append("\n📊 INDICADORES CRÍTICOS:\n");
            indicadoresCriticos.stream().limit(3).forEach(ind ->
                    alertas.append("• ").append(ind.getNombre())
                            .append(" (").append(String.format("%.1f", ind.getValorActual()))
                            .append("/").append(String.format("%.1f", ind.getValorObjetivo()))
                            .append(")\n")
            );
        }

        areaAlertas.setText(alertas.toString());
        areaAlertas.setCaretPosition(0);
    }

    // Métodos para abrir ventanas específicas
    private void abrirVentanaProcesos() {
        VentanaProcesos ventana = new VentanaProcesos(procesoManager, indicadorManager);
        ventana.setVisible(true);
    }

    private void abrirVentanaIndicadores() {
        VentanaIndicadores ventana = new VentanaIndicadores(indicadorManager);
        ventana.setVisible(true);
    }

    private void abrirVentanaNoConformidades() {
        JOptionPane.showMessageDialog(this,
                "Funcionalidad de No Conformidades disponible en la versión completa.\n" +
                        "Esta ventana mostraría:\n" +
                        "- Lista de no conformidades\n" +
                        "- Gestión de acciones correctivas\n" +
                        "- Seguimiento de estados",
                "No Conformidades",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void abrirVentanaAuditorias() {
        JOptionPane.showMessageDialog(this,
                "Funcionalidad de Auditorías disponible en la versión completa.\n" +
                        "Esta ventana mostraría:\n" +
                        "- Programación de auditorías\n" +
                        "- Asignación de auditores\n" +
                        "- Registro de hallazgos",
                "Auditorías",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void abrirVentanaReportes() {
        VentanaReportes ventana = new VentanaReportes(procesoManager, indicadorManager, noConformidadManager);
        ventana.setVisible(true);
    }

    // Getters para testing
    public ProcesoManager getProcesoManager() { return procesoManager; }
    public IndicadorManager getIndicadorManager() { return indicadorManager; }
    public NoConformidadManager getNoConformidadManager() { return noConformidadManager; }
    public AuditoriaManager getAuditoriaManager() { return auditoriaManager; }
}