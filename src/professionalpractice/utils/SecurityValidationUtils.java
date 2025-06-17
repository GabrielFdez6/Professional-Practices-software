package professionalpractice.utils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class SecurityValidationUtils {

  // Patrones de ataques comunes
  private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
      ".*(union|select|insert|delete|update|drop|create|alter|exec|execute|script|javascript|vbscript).*",
      Pattern.CASE_INSENSITIVE);

  private static final Pattern XSS_PATTERN = Pattern.compile(
      ".*(<script|</script|<iframe|</iframe|<object|</object|<embed|</embed|javascript:|vbscript:|data:).*",
      Pattern.CASE_INSENSITIVE);

  private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
      ".*(\\.\\.[\\\\/]|\\.\\.%|%2e%2e).*",
      Pattern.CASE_INSENSITIVE);

  private static final Pattern COMMAND_INJECTION_PATTERN = Pattern.compile(
      ".*(;|\\||&|\\$\\(|`|\\$\\{).*");

  // Listas negras de contenido
  private static final Set<String> FORBIDDEN_EXTENSIONS = new HashSet<>(Arrays.asList(
      ".exe", ".bat", ".cmd", ".com", ".scr", ".pif", ".jar", ".war", ".ear",
      ".vbs", ".vbe", ".js", ".jse", ".ws", ".wsf", ".wsc", ".wsh", ".ps1",
      ".php", ".asp", ".aspx", ".jsp", ".py", ".pl", ".rb", ".sh"));

  private static final Set<String> SUSPICIOUS_KEYWORDS = new HashSet<>(Arrays.asList(
      "admin", "administrator", "root", "system", "sa", "dbo", "guest",
      "password", "passwd", "pwd", "secret", "key", "token", "hash",
      "credit", "card", "ssn", "social", "security", "tax", "id"));

  // ===== VALIDACIONES DE INYECCIÓN SQL =====

  /**
   * Verifica si el input contiene patrones de inyección SQL
   * 
   * @param input texto a verificar
   * @return true si contiene patrones de SQL injection, false en caso contrario
   */
  public static boolean containsSQLInjection(String input) {
    if (input == null || input.trim().isEmpty()) {
      return false;
    }

    String cleanInput = input.toLowerCase().trim();

    // Verificar patrones de inyección SQL
    if (SQL_INJECTION_PATTERN.matcher(cleanInput).matches()) {
      return true;
    }

    // Verificar caracteres específicos de SQL
    if (cleanInput.contains("'") && (cleanInput.contains("or") || cleanInput.contains("and"))) {
      return true;
    }

    // Verificar comentarios SQL
    if (cleanInput.contains("--") || cleanInput.contains("/*") || cleanInput.contains("*/")) {
      return true;
    }

    return false;
  }

  public static String validateSQLInjection(String input, String fieldName) {
    if (input == null || input.trim().isEmpty()) {
      return "";
    }

    String cleanInput = input.toLowerCase().trim();

    if (SQL_INJECTION_PATTERN.matcher(cleanInput).matches()) {
      logSecurityViolation("SQL_INJECTION_ATTEMPT", fieldName, input);
      return fieldName + " contiene patrones sospechosos de inyección SQL.";
    }

    // Verificar caracteres específicos de SQL
    if (cleanInput.contains("'") && (cleanInput.contains("or") || cleanInput.contains("and"))) {
      logSecurityViolation("SQL_INJECTION_QUOTES", fieldName, input);
      return fieldName + " contiene patrones potencialmente peligrosos.";
    }

    return "";
  }

  // ===== VALIDACIONES DE XSS =====

  public static String validateXSS(String input, String fieldName) {
    if (input == null || input.trim().isEmpty()) {
      return "";
    }

    if (XSS_PATTERN.matcher(input).matches()) {
      logSecurityViolation("XSS_ATTEMPT", fieldName, input);
      return fieldName + " contiene contenido potencialmente peligroso.";
    }

    // Verificar encoding malicioso
    if (input.contains("%3C") || input.contains("%3E") || input.contains("&#")) {
      logSecurityViolation("XSS_ENCODED", fieldName, input);
      return fieldName + " contiene caracteres codificados sospechosos.";
    }

    return "";
  }

  // ===== VALIDACIONES DE PATH TRAVERSAL =====

  public static String validatePathTraversal(String input, String fieldName) {
    if (input == null || input.trim().isEmpty()) {
      return "";
    }

    if (PATH_TRAVERSAL_PATTERN.matcher(input).matches()) {
      logSecurityViolation("PATH_TRAVERSAL_ATTEMPT", fieldName, input);
      return fieldName + " contiene patrones de navegación de directorios no válidos.";
    }

    return "";
  }

  // ===== VALIDACIONES DE INYECCIÓN DE COMANDOS =====

  public static String validateCommandInjection(String input, String fieldName) {
    if (input == null || input.trim().isEmpty()) {
      return "";
    }

    if (COMMAND_INJECTION_PATTERN.matcher(input).matches()) {
      logSecurityViolation("COMMAND_INJECTION_ATTEMPT", fieldName, input);
      return fieldName + " contiene caracteres de comando no válidos.";
    }

    return "";
  }

  // ===== VALIDACIONES DE ARCHIVOS =====

  public static String validateFileUpload(String fileName, long fileSize) {
    List<String> errors = new ArrayList<>();

    if (fileName == null || fileName.trim().isEmpty()) {
      return "Nombre de archivo es obligatorio.";
    }

    fileName = fileName.toLowerCase().trim();

    // Verificar extensión prohibida
    for (String ext : FORBIDDEN_EXTENSIONS) {
      if (fileName.endsWith(ext)) {
        logSecurityViolation("FORBIDDEN_FILE_EXTENSION", "fileName", fileName);
        errors.add("Tipo de archivo no permitido por políticas de seguridad.");
        break;
      }
    }

    // Verificar doble extensión
    if (fileName.matches(".*\\.[a-z]+\\.[a-z]+$")) {
      logSecurityViolation("DOUBLE_EXTENSION", "fileName", fileName);
      errors.add("Los archivos con doble extensión no están permitidos.");
    }

    // Verificar tamaño del archivo
    if (fileSize > Constants.MAX_FILE_SIZE) {
      errors.add("El archivo excede el tamaño máximo permitido de " +
          (Constants.MAX_FILE_SIZE / (1024 * 1024)) + " MB.");
    }

    // Verificar caracteres peligrosos en nombre
    if (fileName.matches(".*[<>:\"/\\\\|?*].*")) {
      errors.add("El nombre del archivo contiene caracteres no válidos.");
    }

    // Verificar longitud del nombre
    if (fileName.length() > Constants.MAX_FILENAME_LENGTH) {
      errors.add("El nombre del archivo es demasiado largo.");
    }

    return String.join("; ", errors);
  }

  // ===== VALIDACIONES DE INFORMACIÓN SENSIBLE =====

  public static String validateSensitiveData(String input, String fieldName) {
    if (input == null || input.trim().isEmpty()) {
      return "";
    }

    String lowerInput = input.toLowerCase();

    // Verificar posibles números de tarjeta de crédito
    if (lowerInput.matches(".*\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}.*")) {
      logSecurityViolation("POTENTIAL_CREDIT_CARD", fieldName, "***REDACTED***");
      return fieldName + " parece contener información de tarjeta de crédito.";
    }

    // Verificar posibles números de seguro social
    if (lowerInput.matches(".*\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{4}.*")) {
      logSecurityViolation("POTENTIAL_SSN", fieldName, "***REDACTED***");
      return fieldName + " parece contener un número de seguro social.";
    }

    // Verificar palabras clave sospechosas
    for (String keyword : SUSPICIOUS_KEYWORDS) {
      if (lowerInput.contains(keyword)) {
        logSecurityViolation("SUSPICIOUS_KEYWORD", fieldName, keyword);
        return fieldName + " contiene términos que requieren revisión adicional.";
      }
    }

    return "";
  }

  // ===== VALIDACIÓN DE INTEGRIDAD DE DATOS =====

  public static String validateDataIntegrity(String input, String expectedChecksum) {
    if (input == null || expectedChecksum == null) {
      return "No se puede verificar la integridad de los datos.";
    }

    // Aquí implementarías verificación de checksum/hash
    // Por simplicidad, solo verificamos que no esté vacío
    if (input.trim().isEmpty()) {
      return "Los datos no pueden estar vacíos para verificación de integridad.";
    }

    return "";
  }

  // ===== VALIDACIONES DE RATE LIMITING =====

  private static final java.util.Map<String, List<LocalDateTime>> requestHistory = new java.util.concurrent.ConcurrentHashMap<>();

  public static String validateRateLimit(String userId, int maxRequests, int timeWindowMinutes) {
    if (userId == null || userId.trim().isEmpty()) {
      return "ID de usuario requerido para validación de límite de velocidad.";
    }

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime windowStart = now.minusMinutes(timeWindowMinutes);

    requestHistory.putIfAbsent(userId, new ArrayList<>());
    List<LocalDateTime> userRequests = requestHistory.get(userId);

    // Limpiar solicitudes antiguas
    userRequests.removeIf(request -> request.isBefore(windowStart));

    if (userRequests.size() >= maxRequests) {
      logSecurityViolation("RATE_LIMIT_EXCEEDED", "userId", userId);
      return "Se ha excedido el límite de solicitudes. Intente más tarde.";
    }

    userRequests.add(now);
    return "";
  }

  // ===== VALIDACIÓN COMPLETA DE SEGURIDAD =====

  public static List<String> performComprehensiveSecurityValidation(String input, String fieldName) {
    List<String> errors = new ArrayList<>();

    if (input == null || input.trim().isEmpty()) {
      return errors; // Campo vacío, no hay riesgos de seguridad
    }

    // Ejecutar todas las validaciones de seguridad
    String sqlError = validateSQLInjection(input, fieldName);
    if (!sqlError.isEmpty())
      errors.add(sqlError);

    String xssError = validateXSS(input, fieldName);
    if (!xssError.isEmpty())
      errors.add(xssError);

    String pathError = validatePathTraversal(input, fieldName);
    if (!pathError.isEmpty())
      errors.add(pathError);

    String commandError = validateCommandInjection(input, fieldName);
    if (!commandError.isEmpty())
      errors.add(commandError);

    String sensitiveError = validateSensitiveData(input, fieldName);
    if (!sensitiveError.isEmpty())
      errors.add(sensitiveError);

    return errors;
  }

  // ===== SANITIZACIÓN AVANZADA =====

  public static String advancedSanitization(String input) {
    if (input == null) {
      return null;
    }

    // Remover caracteres peligrosos
    String sanitized = input
        .replaceAll("[<>\"'%;()&+\\\\]", "")
        .replaceAll("\\s+", " ")
        .trim();

    // Convertir caracteres especiales a entidades HTML
    sanitized = sanitized
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;");

    return sanitized;
  }

  // ===== LOGGING DE SEGURIDAD =====

  private static void logSecurityViolation(String violationType, String fieldName, String input) {
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    String logMessage = String.format("[SECURITY_VIOLATION] %s - Type: %s, Field: %s, Input: %s",
        timestamp, violationType, fieldName,
        input.length() > 100 ? input.substring(0, 100) + "..." : input);

    // En un sistema real, esto se escribiría a un archivo de log o sistema de
    // monitoreo
    System.err.println(logMessage);

    // También podrías enviar alertas a administradores de seguridad
    // sendSecurityAlert(violationType, fieldName, input);
  }

  // ===== VALIDACIONES DE CONTEXTO =====

  public static String validateUserContext(String currentUserId, String targetUserId, String action) {
    if (currentUserId == null || targetUserId == null || action == null) {
      return "Contexto de usuario incompleto para validación de seguridad.";
    }

    // Verificar si el usuario está intentando acceder a datos de otro usuario
    if (!currentUserId.equals(targetUserId) && !isAuthorizedForAction(currentUserId, action)) {
      logSecurityViolation("UNAUTHORIZED_ACCESS_ATTEMPT", "targetUserId", targetUserId);
      return "No tiene permisos para realizar esta acción en este contexto.";
    }

    return "";
  }

  private static boolean isAuthorizedForAction(String userId, String action) {
    // Aquí implementarías la lógica de autorización específica
    // Por ahora, retornamos false para ser conservadores
    return false;
  }

  // ===== VALIDACIONES DE TIEMPO =====

  public static String validateTimeBasedSecurity(LocalDateTime actionTime, String action) {
    LocalDateTime now = LocalDateTime.now();

    // Verificar si la acción está siendo realizada en horario laboral
    int hour = actionTime.getHour();
    if (hour < 6 || hour > 22) {
      logSecurityViolation("OFF_HOURS_ACCESS", "action", action);
      return "Acceso fuera del horario laboral requiere autorización especial.";
    }

    // Verificar si el timestamp es razonable (no muy en el futuro o pasado)
    if (actionTime.isAfter(now.plusMinutes(5)) || actionTime.isBefore(now.minusHours(24))) {
      logSecurityViolation("SUSPICIOUS_TIMESTAMP", "action", action);
      return "Timestamp de la acción es sospechoso.";
    }

    return "";
  }

  // ===== MÉTODOS ADICIONALES PARA VALIDACIÓN DE ARCHIVOS =====

  /**
   * Verifica si la extensión del archivo está permitida
   * 
   * @param fileName nombre del archivo con extensión
   * @return true si la extensión está permitida, false en caso contrario
   */
  public static boolean isFileExtensionAllowed(String fileName) {
    if (fileName == null || fileName.trim().isEmpty()) {
      return false;
    }

    String lowerFileName = fileName.toLowerCase();

    // Verificar extensiones permitidas
    for (String allowedExt : Constants.ALLOWED_FILE_EXTENSIONS) {
      if (lowerFileName.endsWith(allowedExt.toLowerCase())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Verifica si el tamaño del archivo es válido
   * 
   * @param file archivo a verificar
   * @return true si el tamaño es válido, false en caso contrario
   */
  public static boolean isFileSizeValid(File file) {
    if (file == null || !file.exists()) {
      return false;
    }

    return file.length() <= Constants.MAX_FILE_SIZE;
  }

  /**
   * Verifica si el contenido (nombre) del archivo contiene elementos sospechosos
   * 
   * @param content contenido o nombre a verificar
   * @return true si contiene contenido sospechoso, false en caso contrario
   */
  public static boolean containsSuspiciousContent(String content) {
    if (content == null || content.trim().isEmpty()) {
      return false;
    }

    String lowerContent = content.toLowerCase();

    // Verificar palabras clave sospechosas
    for (String keyword : SUSPICIOUS_KEYWORDS) {
      if (lowerContent.contains(keyword)) {
        return true;
      }
    }

    // Verificar patrones de path traversal
    if (PATH_TRAVERSAL_PATTERN.matcher(content).matches()) {
      return true;
    }

    // Verificar caracteres especiales sospechosos
    if (content.matches(".*[<>:\"|?*\\\\].*")) {
      return true;
    }

    // Verificar URLs o protocolos sospechosos
    if (lowerContent.contains("http://") || lowerContent.contains("https://") ||
        lowerContent.contains("ftp://") || lowerContent.contains("file://")) {
      return true;
    }

    return false;
  }
}