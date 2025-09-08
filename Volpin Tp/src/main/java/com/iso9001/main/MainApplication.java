package com.iso9001.main;

import com.iso9001.gui.DashboardPrincipal;
import com.iso9001.utils.DatabaseHelper;
import javax.swing.*;
import java.awt.*;

/**
 * Clase principal del Sistema de Gestión de Calidad ISO 9001
 *
 * Este sistema permite:
 * - Gestión completa de procesos empresariales
 * - Monitoreo de indicadores de calidad en tiempo real
 * - Seguimiento de no conformidades y acciones correctivas
 * - Generación de reportes para auditorías ISO 9001
 * - Dashboard ejecutivo con métricas clave
 *
 * @author Tu Nombre
 * @version 1.0
 * @since 2024
 */
public class MainApplication {

    public static void main(String[] args) {
        // Configurar Look & Feel del sistema
        configurarLookAndFeel();

        // Mostrar splash screen
        mostrarSplashScreen();

        // Inicializar base de datos
        inicializarSistema();

        // Lanzar aplicación principal
        SwingUtilities.invokeLater(() -> {
            try {
                DashboardPrincipal dashboard = new DashboardPrincipal();
                dashboard.setVisible(true);

                // Mensaje de bienvenida
                mostrarMensajeBienvenida(dashboard);

            } catch (Exception e) {
                mostrarErrorInicio(e);
            }
        });
    }

    /**
     * Configura el Look & Feel de la aplicación
     */
    private static void configurarLookAndFeel() {
        try {
            // Intentar usar el Look & Feel del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());

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

    /**
     * Muestra una pantalla de carga inicial
     */
    private static void mostrarSplashScreen() {
        JFrame splash = new JFrame();
        splash.setUndecorated(true);
        splash.setSize(500, 300);
        splash.setLocationRelativeTo(null);
        splash.setLayout(new BorderLayout());

        // Panel principal con gradiente
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Crear gradiente
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(41, 128, 185),
                        0, getHeight(), new Color(44, 62, 80)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelPrincipal.setLayout(new BorderLayout());

        // Título principal
        JLabel titulo = new JLabel("Sistema de Gestión de Calidad", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(50, 20, 10, 20));

        // Subtítulo
        JLabel subtitulo = new JLabel("ISO 9001:2015", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitulo.setForeground(new Color(236, 240, 241));

        // Información de carga
        JLabel lblCarga = new JLabel("Inicializando sistema...", SwingConstants.CENTER);
        lblCarga.setFont(new Font("Arial", Font.PLAIN, 12));
        lblCarga.setForeground(new Color(189, 195, 199));
        lblCarga.setBorder(BorderFactory.createEmptyBorder(30, 20, 50, 20));

        // Barra de progreso
        JProgressBar barraProgreso = new JProgressBar();
        barraProgreso.setIndeterminate(true);
        barraProgreso.setStringPainted(true);
        barraProgreso.setString("Cargando componentes...");
        barraProgreso.setBorder(BorderFactory.createEmptyBorder(0, 50, 30, 50));

        // Versión
        JLabel version = new JLabel("Versión 1.0 - Proyecto Universitario", SwingConstants.CENTER);
        version.setFont(new Font("Arial", Font.PLAIN, 10));
        version.setForeground(new Color(127, 140, 141));
        version.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        // Ensamblar componentes
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setOpaque(false);
        panelCentral.add(subtitulo, BorderLayout.NORTH);
        panelCentral.add(lblCarga, BorderLayout.CENTER);
        panelCentral.add(barraProgreso, BorderLayout.SOUTH);

        panelPrincipal.add(titulo, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(version, BorderLayout.SOUTH);

        splash.add(panelPrincipal);
        splash.setVisible(true);

        // Simular tiempo de carga
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        splash.dispose();
    }

    /**
     * Inicializa el sistema y la base de datos
     */
    private static void inicializarSistema() {
        try {
            System.out.println("=".repeat(50));
            System.out.println("SISTEMA DE GESTIÓN DE CALIDAD ISO 9001");
            System.out.println("Iniciando aplicación...");
            System.out.println("=".repeat(50));

            // Inicializar base de datos
            DatabaseHelper.inicializarDatos();

            System.out.println("✓ Sistema inicializado correctamente");
            System.out.println("✓ Base de datos cargada");
            System.out.println("✓ Componentes listos");

        } catch (Exception e) {
            System.err.println("✗ Error durante la inicialización: " + e.getMessage());
            e.printStackTrace();

            // Mostrar error al usuario
            JOptionPane.showMessageDialog(null,
                    "Error al inicializar el sistema:\n" + e.getMessage() +
                            "\n\nEl sistema puede no funcionar correctamente.",
                    "Error de Inicialización",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Muestra un mensaje de bienvenida al usuario
     */
    private static void mostrarMensajeBienvenida(Component parent) {
        String mensaje = """
            ¡Bienvenido al Sistema de Gestión de Calidad ISO 9001!
            
            Este sistema le permitirá:
            
            ✓ Gestionar procesos empresariales
            ✓ Monitorear indicadores de calidad
            ✓ Rastrear no conformidades
            ✓ Generar reportes para auditorías
            ✓ Analizar el cumplimiento ISO 9001
            
            Funcionalidades principales:
            • Dashboard ejecutivo con métricas en tiempo real
            • Gestión completa de procesos por tipo
            • Seguimiento de indicadores con semáforos
            • Reportes automáticos para auditorías
            • Alertas de vencimientos y problemas
            
            ¿Desea ver un tour rápido del sistema?
            """;

        int opcion = JOptionPane.showConfirmDialog(parent,
                mensaje,
                "Bienvenido - Sistema ISO 9001",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            mostrarTourSistema(parent);
        }
    }

    /**
     * Muestra un tour básico del sistema
     */
    private static void mostrarTourSistema(Component parent) {
        String[] pasos = {
                "PASO 1 - DASHBOARD PRINCIPAL\n\n" +
                        "El dashboard muestra un resumen ejecutivo con:\n" +
                        "• Estadísticas generales del sistema\n" +
                        "• Alertas y notificaciones importantes\n" +
                        "• Acceso rápido a todas las funcionalidades\n\n" +
                        "Desde aquí puede navegar a cualquier módulo.",

                "PASO 2 - GESTIÓN DE PROCESOS\n\n" +
                        "El módulo de procesos permite:\n" +
                        "• Crear y editar procesos por tipo (Estratégico, Operativo, Apoyo)\n" +
                        "• Asignar responsables y definir objetivos\n" +
                        "• Vincular indicadores de desempeño\n" +
                        "• Generar reportes específicos",

                "PASO 3 - INDICADORES DE CALIDAD\n\n" +
                        "Sistema de monitoreo con:\n" +
                        "• Semáforo de estados (Verde/Amarillo/Rojo)\n" +
                        "• Registro de mediciones periódicas\n" +
                        "• Análisis de tendencias automático\n" +
                        "• Alertas por desviaciones",

                "PASO 4 - REPORTES Y ANÁLISIS\n\n" +
                        "Generación automática de:\n" +
                        "• Reportes de estado general\n" +
                        "• Análisis de cumplimiento ISO 9001\n" +
                        "• Preparación para auditorías\n" +
                        "• Exportación a archivos\n\n" +
                        "¡Ya puede comenzar a usar el sistema!"
        };

        for (int i = 0; i < pasos.length; i++) {
            JOptionPane.showMessageDialog(parent,
                    pasos[i],
                    "Tour del Sistema (" + (i + 1) + "/" + pasos.length + ")",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Muestra un error crítico de inicio
     */
    private static void mostrarErrorInicio(Exception e) {
        String mensaje = """
            Error crítico al iniciar la aplicación:
            
            %s
            
            Posibles causas:
            • Permisos insuficientes para crear archivos
            • Problemas con la configuración del sistema
            • Archivos de datos corruptos
            
            Contacte al administrador del sistema.
            """.formatted(e.getMessage());

        JOptionPane.showMessageDialog(null,
                mensaje,
                "Error Crítico",
                JOptionPane.ERROR_MESSAGE);

        System.exit(1);
    }

    /**
     * Información del sistema para depuración
     */
    public static void imprimirInfoSistema() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("INFORMACIÓN DEL SISTEMA");
        System.out.println("=".repeat(60));
        System.out.println("Sistema Operativo: " + System.getProperty("os.name"));
        System.out.println("Versión Java: " + System.getProperty("java.version"));
        System.out.println("Directorio de trabajo: " + System.getProperty("user.dir"));
        System.out.println("Usuario: " + System.getProperty("user.name"));
        System.out.println("Memoria total: " + Runtime.getRuntime().totalMemory() / 1024 / 1024 + " MB");
        System.out.println("Memoria libre: " + Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB");
        System.out.println("=".repeat(60));
    }

    /**
     * Método para ejecutar la aplicación en modo debug
     */
    public static void mainDebug(String[] args) {
        System.setProperty("java.awt.headless", "false");
        imprimirInfoSistema();
        main(args);
    }
}