package com.iso9001.utils;

import com.iso9001.models.*;
import com.iso9001.enums.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DatabaseHelper {
    private static final String DATA_FOLDER = "resources/data/";
    private static final String SEPARADOR = ",";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Métodos para Procesos
    public static void guardarProcesos(List<Proceso> procesos) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FOLDER + "procesos.csv"))) {
            // Escribir encabezados
            writer.println("id,nombre,descripcion,tipo,responsable,objetivo,alcance,fechaCreacion,activo");

            // Escribir datos
            for (Proceso proceso : procesos) {
                writer.printf("%s,%s,\"%s\",%s,%s,\"%s\",\"%s\",%s,%s%n",
                        proceso.getId(),
                        proceso.getNombre(),
                        proceso.getDescripcion(),
                        proceso.getTipo().name(),
                        proceso.getResponsable(),
                        proceso.getObjetivo() != null ? proceso.getObjetivo() : "",
                        proceso.getAlcance() != null ? proceso.getAlcance() : "",
                        proceso.getFechaCreacion().format(FORMATO_FECHA),
                        proceso.isActivo()
                );
            }
            System.out.println("Procesos guardados exitosamente");
        } catch (IOException e) {
            System.err.println("Error al guardar procesos: " + e.getMessage());
        }
    }

    public static List<Proceso> cargarProcesos() {
        List<Proceso> procesos = new ArrayList<>();
        File archivo = new File(DATA_FOLDER + "procesos.csv");

        if (!archivo.exists()) {
            System.out.println("Archivo de procesos no existe, creando datos de ejemplo...");
            return crearDatosEjemploProcesos();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine(); // Saltar encabezados

            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(SEPARADOR);
                if (datos.length >= 8) {
                    try {
                        Proceso proceso = new Proceso(
                                datos[0].trim(),                           // id
                                datos[1].trim(),                           // nombre
                                datos[2].replace("\"", "").trim(),         // descripcion
                                TipoProceso.valueOf(datos[3].trim()),      // tipo
                                datos[4].trim(),                           // responsable
                                datos[5].replace("\"", "").trim(),         // objetivo
                                datos[6].replace("\"", "").trim()          // alcance
                        );

                        // Configurar fecha y estado
                        if (datos.length > 7) {
                            proceso.setFechaCreacion(LocalDate.parse(datos[7].trim(), FORMATO_FECHA));
                        }
                        if (datos.length > 8) {
                            proceso.setActivo(Boolean.parseBoolean(datos[8].trim()));
                        }

                        procesos.add(proceso);
                    } catch (Exception e) {
                        System.err.println("Error al procesar línea: " + linea + " - " + e.getMessage());
                    }
                }
            }

            System.out.println("Cargados " + procesos.size() + " procesos");
        } catch (IOException e) {
            System.err.println("Error al cargar procesos: " + e.getMessage());
        }

        return procesos;
    }

    // Métodos para Indicadores
    public static void guardarIndicadores(List<Indicador> indicadores) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FOLDER + "indicadores.csv"))) {
            writer.println("id,nombre,descripcion,tipo,unidadMedida,valorObjetivo,valorActual,procesoId,fechaUltimaMedicion");

            for (Indicador indicador : indicadores) {
                writer.printf("%s,%s,\"%s\",%s,%s,%.2f,%.2f,%s,%s%n",
                        indicador.getId(),
                        indicador.getNombre(),
                        indicador.getDescripcion(),
                        indicador.getTipo().name(),
                        indicador.getUnidadMedida(),
                        indicador.getValorObjetivo(),
                        indicador.getValorActual(),
                        indicador.getProcesoId(),
                        indicador.getFechaUltimaMedicion().format(FORMATO_FECHA)
                );
            }
            System.out.println("Indicadores guardados exitosamente");
        } catch (IOException e) {
            System.err.println("Error al guardar indicadores: " + e.getMessage());
        }
    }

    public static List<Indicador> cargarIndicadores() {
        List<Indicador> indicadores = new ArrayList<>();
        File archivo = new File(DATA_FOLDER + "indicadores.csv");

        if (!archivo.exists()) {
            return crearDatosEjemploIndicadores();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine(); // Saltar encabezados

            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(SEPARADOR);
                if (datos.length >= 9) {
                    try {
                        Indicador indicador = new Indicador(
                                datos[0].trim(),                               // id
                                datos[1].trim(),                               // nombre
                                datos[2].replace("\"", "").trim(),             // descripcion
                                TipoIndicador.valueOf(datos[3].trim()),        // tipo
                                datos[4].trim(),                               // unidadMedida
                                Double.parseDouble(datos[5].trim()),           // valorObjetivo
                                datos[7].trim()                                // procesoId
                        );

                        indicador.setValorActual(Double.parseDouble(datos[6].trim()));
                        indicador.setFechaUltimaMedicion(LocalDate.parse(datos[8].trim(), FORMATO_FECHA));

                        indicadores.add(indicador);
                    } catch (Exception e) {
                        System.err.println("Error al procesar indicador: " + linea + " - " + e.getMessage());
                    }
                }
            }

            System.out.println("Cargados " + indicadores.size() + " indicadores");
        } catch (IOException e) {
            System.err.println("Error al cargar indicadores: " + e.getMessage());
        }

        return indicadores;
    }

    // Métodos para No Conformidades
    public static void guardarNoConformidades(List<NoConformidad> noConformidades) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FOLDER + "no_conformidades.csv"))) {
            writer.println("id,titulo,descripcion,estado,prioridad,fechaDeteccion,procesoAfectado,responsableDeteccion,causaRaiz");

            for (NoConformidad nc : noConformidades) {
                writer.printf("%s,%s,\"%s\",%s,%s,%s,%s,%s,\"%s\"%n",
                        nc.getId(),
                        nc.getTitulo(),
                        nc.getDescripcion(),
                        nc.getEstado().name(),
                        nc.getPrioridad().name(),
                        nc.getFechaDeteccion().format(FORMATO_FECHA),
                        nc.getProcesoAfectado(),
                        nc.getResponsableDeteccion(),
                        nc.getCausaRaiz() != null ? nc.getCausaRaiz() : ""
                );
            }
            System.out.println("No conformidades guardadas exitosamente");
        } catch (IOException e) {
            System.err.println("Error al guardar no conformidades: " + e.getMessage());
        }
    }

    public static List<NoConformidad> cargarNoConformidades() {
        List<NoConformidad> noConformidades = new ArrayList<>();
        File archivo = new File(DATA_FOLDER + "no_conformidades.csv");

        if (!archivo.exists()) {
            return crearDatosEjemploNoConformidades();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea = reader.readLine(); // Saltar encabezados

            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(SEPARADOR);
                if (datos.length >= 8) {
                    try {
                        NoConformidad nc = new NoConformidad(
                                datos[0].trim(),                                   // id
                                datos[1].trim(),                                   // titulo
                                datos[2].replace("\"", "").trim(),                 // descripcion
                                Prioridad.valueOf(datos[4].trim()),                // prioridad
                                datos[6].trim(),                                   // procesoAfectado
                                datos[7].trim()                                    // responsableDeteccion
                        );

                        nc.setEstado(EstadoNoConformidad.valueOf(datos[3].trim()));
                        nc.setFechaDeteccion(LocalDate.parse(datos[5].trim(), FORMATO_FECHA));

                        if (datos.length > 8) {
                            nc.setCausaRaiz(datos[8].replace("\"", "").trim());
                        }

                        noConformidades.add(nc);
                    } catch (Exception e) {
                        System.err.println("Error al procesar no conformidad: " + linea + " - " + e.getMessage());
                    }
                }
            }

            System.out.println("Cargadas " + noConformidades.size() + " no conformidades");
        } catch (IOException e) {
            System.err.println("Error al cargar no conformidades: " + e.getMessage());
        }

        return noConformidades;
    }

    // Métodos para crear datos de ejemplo
    private static List<Proceso> crearDatosEjemploProcesos() {
        List<Proceso> procesos = new ArrayList<>();

        // Procesos Estratégicos
        procesos.add(new Proceso("P001", "Planificación Estratégica", "Proceso de definición de objetivos y estrategias",
                TipoProceso.ESTRATEGICO, "Director General",
                "Establecer la dirección estratégica", "Toda la organización"));

        procesos.add(new Proceso("P002", "Gestión de Calidad", "Proceso de gestión del sistema de calidad",
                TipoProceso.ESTRATEGICO, "Gerente Calidad",
                "Mantener certificación ISO 9001", "Todos los procesos"));

        // Procesos Operativos
        procesos.add(new Proceso("P003", "Desarrollo de Productos", "Proceso de creación y desarrollo de nuevos productos",
                TipoProceso.OPERATIVO, "Jefe Ingeniería",
                "Desarrollar productos de calidad", "Área de I+D"));

        procesos.add(new Proceso("P004", "Producción", "Proceso de fabricación de productos",
                TipoProceso.OPERATIVO, "Jefe Producción",
                "Fabricar productos según especificaciones", "Planta de producción"));

        procesos.add(new Proceso("P005", "Ventas y Marketing", "Proceso de comercialización y ventas",
                TipoProceso.OPERATIVO, "Gerente Comercial",
                "Aumentar ventas y satisfacción del cliente", "Área comercial"));

        // Procesos de Apoyo
        procesos.add(new Proceso("P006", "Gestión de Recursos Humanos", "Proceso de gestión del personal",
                TipoProceso.APOYO, "Jefe RRHH",
                "Gestionar el talento humano", "Toda la organización"));

        procesos.add(new Proceso("P007", "Compras y Abastecimiento", "Proceso de adquisición de materiales",
                TipoProceso.APOYO, "Jefe Compras",
                "Asegurar suministro de calidad", "Área de compras"));

        guardarProcesos(procesos);
        return procesos;
    }

    private static List<Indicador> crearDatosEjemploIndicadores() {
        List<Indicador> indicadores = new ArrayList<>();
        Random random = new Random();

        // Indicadores para cada proceso
        indicadores.add(new Indicador("I001", "Cumplimiento Objetivos Estratégicos", "Porcentaje de objetivos cumplidos",
                TipoIndicador.EFICACIA, "%", 90.0, "P001"));
        indicadores.get(0).setValorActual(85.0 + random.nextDouble() * 10);

        indicadores.add(new Indicador("I002", "Satisfacción del Cliente", "Nivel de satisfacción general",
                TipoIndicador.SATISFACCION, "puntos", 4.5, "P005"));
        indicadores.get(1).setValorActual(4.2 + random.nextDouble() * 0.6);

        indicadores.add(new Indicador("I003", "Tiempo Desarrollo Productos", "Tiempo promedio de desarrollo",
                TipoIndicador.TIEMPO, "días", 120.0, "P003"));
        indicadores.get(2).setValorActual(115.0 + random.nextDouble() * 20);

        indicadores.add(new Indicador("I004", "Eficiencia Producción", "Productos por hora",
                TipoIndicador.EFICIENCIA, "unid/h", 50.0, "P004"));
        indicadores.get(3).setValorActual(45.0 + random.nextDouble() * 10);

        indicadores.add(new Indicador("I005", "Defectos por Millón", "Cantidad de defectos por millón",
                TipoIndicador.CALIDAD, "ppm", 100.0, "P004"));
        indicadores.get(4).setValorActual(80.0 + random.nextDouble() * 40);

        guardarIndicadores(indicadores);
        return indicadores;
    }

    private static List<NoConformidad> crearDatosEjemploNoConformidades() {
        List<NoConformidad> noConformidades = new ArrayList<>();

        noConformidades.add(new NoConformidad("NC001", "Documento obsoleto en producción",
                "Se encontró un procedimiento desactualizado en el área de producción",
                Prioridad.MEDIA, "P004", "Auditor Interno"));

        noConformidades.add(new NoConformidad("NC002", "Falta de calibración equipos",
                "Equipos de medición sin calibración vigente",
                Prioridad.ALTA, "P004", "Técnico Calidad"));

        noConformidades.add(new NoConformidad("NC003", "Capacitación no documentada",
                "Personal sin evidencia de capacitación en ISO 9001",
                Prioridad.BAJA, "P006", "Jefe RRHH"));

        guardarNoConformidades(noConformidades);
        return noConformidades;
    }

    // Utilidades generales
    public static void crearCarpetaData() {
        File carpeta = new File(DATA_FOLDER);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
            System.out.println("Carpeta de datos creada: " + DATA_FOLDER);
        }
    }

    public static void inicializarDatos() {
        crearCarpetaData();
        System.out.println("Inicializando base de datos...");

        // Cargar o crear datos de ejemplo
        cargarProcesos();
        cargarIndicadores();
        cargarNoConformidades();

        System.out.println("Base de datos inicializada correctamente");
    }
}