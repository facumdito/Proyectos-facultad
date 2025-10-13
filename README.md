# Sistema de Gestión de Calidad ISO 9001

Sistema integral de gestión de calidad diseñado para cumplir con los estándares ISO 9001, que permite administrar procesos, indicadores, no conformidades y auditorías.

## · Descripción

Este sistema proporciona una solución completa para la gestión de calidad organizacional, incluyendo:

- Gestión de procesos organizacionales
- Seguimiento de indicadores de calidad
- Control de no conformidades
- Programación y seguimiento de auditorías
- Generación de reportes y análisis

## · Características Principales

### Dashboard Principal
- **Procesos Activos**: Visualización de procesos en operación
- **Indicadores Verde/Rojo**: Semáforo de cumplimiento
- **No Conformidades Abiertas**: Seguimiento de NC pendientes
- **Auditorías Completadas**: Estado de auditorías realizadas
- **Cumplimiento General**: Porcentaje global del sistema

### Módulos del Sistema

#### 1. Gestión de Procesos
- Clasificación por tipo: Estratégico, Operativo, Apoyo
- Asignación de responsables
- Estado de activación
- Seguimiento de indicadores asociados

#### 2. Gestión de Indicadores
- Tipos: Verde (cumplimiento), Amarillo (advertencia), Rojo (crítico)
- Valores actuales vs. objetivos
- Análisis de tendencias
- Estados: Saludable, Crítico

#### 3. Gestión de No Conformidades
- Estados: En Corrección, Cerrada, Abierta
- Prioridades: Alta, Media, Baja
- Seguimiento de días restantes
- Asignación a procesos y responsables
- Gestión de acciones correctivas

#### 4. Gestión de Auditorías
- Tipos: Interna, Externa
- Estados: Programada, Completada, Vencida
- Calificaciones de cumplimiento
- Seguimiento de hallazgos

#### 5. Generador de Reportes
Tipos de reportes disponibles:
- Estado General del Sistema
- Reporte de Procesos
- Reporte de Indicadores
- Reporte de No Conformidades
- Reporte de Preparación para Auditoría
- Análisis de Cumplimiento ISO 9001
- Dashboard Ejecutivo

## · Estructura de Datos

### Procesos
```
ID | Nombre | Tipo | Responsable | Activo | Indicadores
```

### Indicadores de Calidad
```
ID | Nombre | Tipo | Actual | Objetivo | Estado | Tendencia
```

### No Conformidades
```
ID | Título | Estado | Prioridad | Proceso | Responsable | Días Restantes
```

### Auditorías
```
ID | Título | Tipo | Estado | Fecha Programada | Auditor Líder | Calificación
```

## · Archivos de Ejemplo

El sistema incluye archivos CSV de ejemplo para cada módulo:
- `procesos_ejemplo.csv`: Procesos organizacionales
- `indicadores_ejemplo.csv`: Indicadores de calidad
- `no_conformidades_ejemplo.csv`: No conformidades
- `auditorias_ejemplo.csv`: Auditorías programadas

## · Uso del Sistema

### Flujo de Trabajo Típico

1. **Definir Procesos**: Configurar los procesos organizacionales
2. **Establecer Indicadores**: Asignar KPIs a cada proceso
3. **Registrar Mediciones**: Actualizar valores de indicadores
4. **Gestionar No Conformidades**: Identificar y dar seguimiento
5. **Programar Auditorías**: Planificar auditorías internas/externas
6. **Generar Reportes**: Analizar cumplimiento y tomar decisiones

## · Indicadores Clave

- **Cumplimiento General**: Porcentaje de cumplimiento del sistema
- **Indicadores Verde**: Procesos dentro de objetivos
- **Indicadores Rojo**: Procesos que requieren atención inmediata
- **NC Abiertas**: No conformidades pendientes de cierre
- **Auditorías Completadas**: Estado del programa de auditorías

## · Cumplimiento ISO 9001:2015

Este sistema está diseñado para cumplir con los requisitos de:
- Cláusula 4: Contexto de la organización
- Cláusula 5: Liderazgo
- Cláusula 6: Planificación
- Cláusula 7: Apoyo
- Cláusula 8: Operación
- Cláusula 9: Evaluación del desempeño
- Cláusula 10: Mejora

---
