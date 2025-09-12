package com.iso9001.gui;

// Imports básicos que seguro existen
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;

/**
 * Dashboard Principal del Sistema ISO 9001
 * Versión corregida con Look&Feel compatible
 */
public class DashboardPrincipal extends JFrame {

    // Contadores temporales hasta que tengas los managers
    private final int totalProcesos = 5;
    private final int indicadoresVerde = 8;
    private final int indicadoresRojo = 2;
    private final int ncAbiertas = 3;
    private final int auditoriasCompletadas = 2;

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

    // Método main para testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DashboardPrincipal().setVisible(true);
        });
    }

    private void inicializarManagers() {
        // TODO: Inicializar managers reales cuando estén disponibles
        System.out.println("Inicializando managers del sistema...");
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión de Calidad ISO 9001 - Dashboard Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Configurar Look & Feel - Versión compatible
        configurarLookAndFeel();
    }

    /**
     * Configura el Look & Feel de manera segura
     */
    /**
     * Configura el Look & Feel de la aplicación
     */
    private static void configurarLookAndFeel() {
        try {
            // Intentar usar el Look & Feel del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Configuraciones adicionales de UI
            UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 12));
            UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 11));
            UIManager.put("Table.font", new Font("Arial", Font.PLAIN, 11));
            UIManager.put("Table.gridColor", new Color(230, 230, 230));

            System.out.println("Look & Feel configurado correctamente");

        } catch (Exception e) {
            System.err.println("No se pudo configurar el Look & Feel: " + e.getMessage());
            // Continuar con el Look & Feel por defecto
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
        panel.add(crearTarjetaEstadistica("Cumplimiento General", "85%", new Color(52, 152, 219), "cumplimientoIcon"));

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
            // Actualizar estadísticas con datos temporales
            lblTotalProcesos.setText(String.valueOf(totalProcesos));
            lblIndicadoresVerde.setText(String.valueOf(indicadoresVerde));
            lblIndicadoresRojo.setText(String.valueOf(indicadoresRojo));
            lblNCAbiertas.setText(String.valueOf(ncAbiertas));
            lblAuditoriasCompletadas.setText(String.valueOf(auditoriasCompletadas));

            // Actualizar alertas
            actualizarAlertas();

            System.out.println("Dashboard actualizado: " + LocalDate.now());
        });
    }

    private void actualizarAlertas() {
        String alertas = "=== ALERTAS DEL SISTEMA ===\n" +
                "Fecha: " + LocalDate.now() + "\n\n" +
                // Alertas de ejemplo hasta que tengas los managers reales
                "⚠ ALERTAS ACTIVAS:\n\n" +
                "• 2 No conformidades próximas a vencer\n" +
                "• Indicador 'Satisfacción Cliente' por debajo del objetivo\n" +
                "• Auditoría interna programada para la próxima semana\n\n" +
                "📊 RESUMEN DE INDICADORES:\n" +
                "• Procesos en funcionamiento: " + totalProcesos + "\n" +
                "• Indicadores saludables: " + indicadoresVerde + "\n" +
                "• Indicadores críticos: " + indicadoresRojo + "\n";

        areaAlertas.setText(alertas);
        areaAlertas.setCaretPosition(0);
    }

    // Métodos para abrir ventanas específicas (versión temporal)
    private void abrirVentanaProcesos() {
        mostrarMensajeFuncionalidad("Gestión de Procesos",
                "- Lista de procesos por tipo\n" +
                        "- Crear/Editar procesos\n" +
                        "- Asignar responsables\n" +
                        "- Vincular indicadores");
    }

    private void abrirVentanaIndicadores() {
        mostrarMensajeFuncionalidad("Gestión de Indicadores",
                "- Lista de indicadores de calidad\n" +
                        "- Configurar metas y objetivos\n" +
                        "- Registro de mediciones\n" +
                        "- Análisis de tendencias");
    }

    private void abrirVentanaNoConformidades() {
        mostrarMensajeFuncionalidad("No Conformidades",
                "- Lista de no conformidades\n" +
                        "- Gestión de acciones correctivas\n" +
                        "- Seguimiento de estados\n" +
                        "- Análisis de causas raíz");
    }

    private void abrirVentanaAuditorias() {
        mostrarMensajeFuncionalidad("Auditorías",
                "- Programación de auditorías\n" +
                        "- Asignación de auditores\n" +
                        "- Registro de hallazgos\n" +
                        "- Seguimiento de acciones");
    }

    private void abrirVentanaReportes() {
        mostrarMensajeFuncionalidad("Reportes y Análisis",
                "- Reporte general del sistema\n" +
                        "- Análisis de cumplimiento ISO\n" +
                        "- Preparación para auditorías\n" +
                        "- Exportación de datos");
    }

    private void mostrarMensajeFuncionalidad(String titulo, String descripcion) {
        JOptionPane.showMessageDialog(this,
                "Funcionalidad: " + titulo + "\n\n" +
                        "Esta ventana incluirá:\n" + descripcion + "\n\n" +
                        "Estado: En desarrollo",
                titulo,
                JOptionPane.INFORMATION_MESSAGE);
    }

}