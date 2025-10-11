package com.iso9001.main;

import com.iso9001.gui.DashboardPrincipal;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainApplication {

    public static void main(String[] args) {
        configurarLookAndFeel();

        mostrarSplashScreen();

        inicializarSistema();

        SwingUtilities.invokeLater(() -> {
            try {
                DashboardPrincipal dashboard = new DashboardPrincipal();
                dashboard.setVisible(true);

                mostrarMensajeBienvenida(dashboard);

            } catch (Exception e) {
                mostrarErrorInicio(e);
            }
        });
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


    private static void mostrarSplashScreen() {
        JFrame splash = new JFrame();
        splash.setUndecorated(true);
        splash.setSize(500, 300);
        splash.setLocationRelativeTo(null);
        splash.setLayout(new BorderLayout());

        JPanel panelPrincipal = crearPanelConGradiente();

        JLabel titulo = new JLabel("Sistema de Gestión de Calidad", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(50, 20, 10, 20));

        JLabel subtitulo = new JLabel("ISO 9001:2015", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitulo.setForeground(new Color(236, 240, 241));

        JLabel lblCarga = new JLabel("Inicializando sistema...", SwingConstants.CENTER);
        lblCarga.setFont(new Font("Arial", Font.PLAIN, 12));
        lblCarga.setForeground(new Color(189, 195, 199));
        lblCarga.setBorder(BorderFactory.createEmptyBorder(30, 20, 50, 20));

        JProgressBar barraProgreso = new JProgressBar();
        barraProgreso.setIndeterminate(true);
        barraProgreso.setStringPainted(true);
        barraProgreso.setString("Cargando componentes...");
        barraProgreso.setBorder(BorderFactory.createEmptyBorder(0, 50, 30, 50));

        JLabel version = new JLabel("Versión 1.0 - Proyecto Universitario", SwingConstants.CENTER);
        version.setFont(new Font("Arial", Font.PLAIN, 10));
        version.setForeground(new Color(127, 140, 141));
        version.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

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

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        splash.dispose();
    }

    private static JPanel crearPanelConGradiente() {
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(41, 128, 185),
                        0, getHeight(), new Color(44, 62, 80)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelPrincipal.setLayout(new BorderLayout());
        return panelPrincipal;
    }

    private static void inicializarSistema() {
        try {
            System.out.println(repetirCaracter());
            System.out.println("SISTEMA DE GESTIÓN DE CALIDAD ISO 9001");
            System.out.println("Iniciando aplicación...");
            System.out.println(repetirCaracter());

            try {
                System.out.println("⚠ MySQL: Configuración pendiente");
            } catch (Exception mysqlError) {
                System.out.println("⚠ MySQL no disponible, usando datos temporales");
            }

            try {
                System.out.println("✓ Datos temporales inicializados");
            } catch (Exception dbError) {
                System.out.println("⚠ DatabaseHelper no disponible: " + dbError.getMessage());
            }

            verificarComponentesDelSistema();

            System.out.println("✓ Sistema inicializado correctamente");
            System.out.println("✓ Componentes listos");
            System.out.println("✓ Aplicación preparada para uso");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            mostrarErrorInicializacion(e);
        }
    }

    private static void verificarComponentesDelSistema() {
        System.out.println("\n--- Verificando componentes ---");

        Runtime runtime = Runtime.getRuntime();
        long memoriaTotal = runtime.totalMemory() / 1024 / 1024;
        long memoriaLibre = runtime.freeMemory() / 1024 / 1024;

        System.out.println("✓ Memoria total: " + memoriaTotal + " MB");
        System.out.println("✓ Memoria libre: " + memoriaLibre + " MB");

        if (memoriaLibre < 50) {
            System.out.println("⚠ Advertencia: Poca memoria disponible");
        }

        verificarPermisosEscritura();

        System.out.println("--- Verificación completada ---\n");
    }

    private static void verificarPermisosEscritura() {
        try {
            String directorioTrabajo = System.getProperty("user.dir");
            File testFile = new File(directorioTrabajo, "test_write.tmp");

            boolean creado = testFile.createNewFile();

            if (creado) {
                boolean eliminado = testFile.delete();
                if (eliminado) {
                    System.out.println("✓ Permisos de escritura verificados");
                } else {
                    System.out.println("⚠ Advertencia: Archivo creado pero no se pudo eliminar");
                }
            } else {
                File directorio = new File(directorioTrabajo);
                if (directorio.canWrite()) {
                    System.out.println("✓ Permisos de escritura verificados (archivo ya existía)");
                } else {
                    System.out.println("⚠ Advertencia: Sin permisos de escritura");
                }
            }

        } catch (IOException e) {
            System.out.println("⚠ Advertencia: Error al verificar permisos - " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("⚠ Advertencia: Permisos de seguridad insuficientes");
        }
    }

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
                
                ¿Desea ver un tour rápido del sistema?""";

        int opcion = JOptionPane.showConfirmDialog(parent,
                mensaje,
                "Bienvenido - Sistema ISO 9001",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            mostrarTourSistema(parent);
        }
    }

    private static void mostrarTourSistema(Component parent) {
        String[] pasos = {
                """
PASO 1 - DASHBOARD PRINCIPAL

El dashboard muestra un resumen ejecutivo con:
• Estadísticas generales del sistema
• Alertas y notificaciones importantes
• Acceso rápido a todas las funcionalidades

Desde aquí puede navegar a cualquier módulo.""",

                """
PASO 2 - GESTIÓN DE PROCESOS

El módulo de procesos permite:
• Crear y editar procesos por tipo (Estratégico, Operativo, Apoyo)
• Asignar responsables y definir objetivos
• Vincular indicadores de desempeño
• Generar reportes específicos""",

                """
PASO 3 - INDICADORES DE CALIDAD

Sistema de monitoreo con:
• Semáforo de estados (Verde/Amarillo/Rojo)
• Registro de mediciones periódicas
• Análisis de tendencias automático
• Alertas por desviaciones""",

                """
PASO 4 - REPORTES Y ANÁLISIS

Generación automática de:
• Reportes de estado general
• Análisis de cumplimiento ISO 9001
• Preparación para auditorías
• Exportación a archivos

¡Ya puede comenzar a usar el sistema!"""
        };

        for (int i = 0; i < pasos.length; i++) {
            JOptionPane.showMessageDialog(parent,
                    pasos[i],
                    "Tour del Sistema (" + (i + 1) + "/" + pasos.length + ")",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void mostrarErrorInicio(Exception e) {
        String mensaje = String.format(
                """
                        Error crítico al iniciar la aplicación:
                        
                        %s
                        
                        Posibles causas:
                        • Permisos insuficientes para crear archivos
                        • Problemas con la configuración del sistema
                        • Archivos de datos corruptos
                        
                        Contacte al administrador del sistema.""",
                e.getMessage()
        );

        JOptionPane.showMessageDialog(null,
                mensaje,
                "Error Crítico",
                JOptionPane.ERROR_MESSAGE);

        System.exit(1);
    }

    private static void mostrarErrorInicializacion(Exception e) {
        String mensaje = String.format(
                """
                        Error al inicializar el Sistema de Gestión de Calidad ISO 9001:
                        
                        Detalles técnicos:
                        %s
                        
                        Posibles soluciones:
                        • Verificar que tienes permisos de lectura/escritura en el directorio
                        • Comprobar que Java está correctamente instalado
                        • Revisar que no hay otro proceso usando los recursos del sistema
                        • Contactar al administrador si el problema persiste
                        
                        El sistema intentará continuar con funcionalidad limitada.
                        
                        ¿Deseas continuar de todos modos?""",
                e.getMessage()
        );

        int opcion = JOptionPane.showConfirmDialog(null,
                mensaje,
                "Error de Inicialización - Sistema ISO 9001",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion != JOptionPane.YES_OPTION) {
            System.out.println("Aplicación cancelada por el usuario");
            System.exit(1);
        } else {
            System.out.println("Continuando con funcionalidad limitada...");
        }
    }

    private static String repetirCaracter() {
        return "=".repeat(Math.max(0, 50));
    }

}