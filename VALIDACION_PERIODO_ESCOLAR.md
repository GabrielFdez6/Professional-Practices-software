# Validación de Período Escolar - Sistema de Prácticas Profesionales

## Resumen
Se implementó un sistema de validación de período escolar que asegura que los estudiantes solo puedan acceder al sistema y ser asignados a proyectos durante su período activo correspondiente.

## Funcionalidades Implementadas

### 1. Validación en el Login
- **Ubicación**: `FXMLLogInController.goStudentHomeScreen()`
- **Funcionamiento**: Antes de permitir el acceso al sistema, se valida que el estudiante tenga un registro activo en el período escolar actual
- **Comportamiento**: Si el estudiante no está en el período actual, se muestra un mensaje de error y se impide el acceso

### 2. Filtrado de Estudiantes en Asignación de Proyectos
- **Ubicación**: `StudentDAO.getUnassignedStudents()`
- **Funcionamiento**: La consulta se modificó para incluir solo estudiantes que:
  - No estén asignados a un proyecto (`isAssignedToProject = 0 OR isAssignedToProject IS NULL`)
  - Tengan un registro en el período escolar actual (`CURDATE() BETWEEN t.startDate AND t.endDate`)

### 3. Validación Adicional en Asignación
- **Ubicación**: `FXMLInfoStudentsProjectController.btnAssign()`
- **Funcionamiento**: Doble validación antes de asignar un proyecto para asegurar que el estudiante esté en el período actual

### 4. Métodos de Utilidad Agregados

#### En `StudentDAO.java`:
```java
// Verifica si un estudiante está en el período actual
public static boolean isStudentInCurrentPeriod(int idStudent) throws SQLException

// Obtiene el período actual de un estudiante específico
public static Term getCurrentPeriodForStudent(int idStudent) throws SQLException
```

#### En `PeriodValidationUtils.java` (Nueva clase):
```java
// Validación completa con resultado detallado
public static PeriodValidationResult validateStudentCurrentPeriod(int idStudent)

// Información del período actual del sistema
public static PeriodInfo getCurrentPeriodInfo()

// Validación de fechas
public static boolean isCurrentDateInPeriod(String startDate, String endDate)

// Mensaje informativo del período
public static String getCurrentPeriodMessage()
```

#### En `EntityValidationUtils.java`:
- Agregada validación de período en `validateStudentProjectAssignment()`

## Estructura de Base de Datos Utilizada

La validación se basa en las siguientes tablas:
- **`term`**: Define los períodos escolares con fechas de inicio y fin
- **`record`**: Conecta estudiantes con períodos específicos
- **`student`**: Información de estudiantes

## Consultas SQL Principales

### Verificar estudiante en período actual:
```sql
SELECT COUNT(*) FROM record r 
JOIN term t ON r.idTerm = t.idTerm 
WHERE r.idStudent = ? AND CURDATE() BETWEEN t.startDate AND t.endDate
```

### Obtener estudiantes no asignados del período actual:
```sql
SELECT DISTINCT s.idStudent, s.enrollment, s.semester, s.email, s.firstName, s.lastNameMother, s.lastNameFather 
FROM student s 
JOIN record r ON s.idStudent = r.idStudent 
JOIN term t ON r.idTerm = t.idTerm 
WHERE (s.isAssignedToProject = 0 OR s.isAssignedToProject IS NULL) 
AND CURDATE() BETWEEN t.startDate AND t.endDate
```

## Mensajes de Usuario

### Login denegado:
"No puedes acceder al sistema porque no estás registrado en el período escolar actual. Por favor, contacta al coordinador de prácticas profesionales."

### Asignación de proyecto denegada:
"No se puede asignar el proyecto porque el estudiante '[Nombre]' no está registrado en el período escolar actual."

## Beneficios de la Implementación

1. **Seguridad**: Previene accesos no autorizados de estudiantes fuera de período
2. **Integridad de datos**: Asegura que solo se asignen proyectos a estudiantes activos
3. **Experiencia de usuario**: Mensajes claros sobre el estado del período
4. **Mantenimiento**: Código centralizado y reutilizable para validaciones de período
5. **Flexibilidad**: Sistema adaptable a diferentes períodos escolares

## Consideraciones de Implementación

- Las validaciones utilizan `CURDATE()` para obtener la fecha actual del servidor de base de datos
- Se implementó manejo de errores SQL en todas las validaciones
- Las consultas están optimizadas para evitar múltiples llamadas a la base de datos
- Se mantiene compatibilidad con el código existente 