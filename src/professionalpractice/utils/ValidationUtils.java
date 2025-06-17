package professionalpractice.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

  // Patrones de validación
  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

  private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

  private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ\\s]+$");

  private static final Pattern ENROLLMENT_PATTERN = Pattern.compile("^S\\d{8}$");

  private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

  private static final Pattern PASSWORD_PATTERN = Pattern.compile(
      "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

  private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s]+$");

  private static final Pattern PROJECT_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9áéíóúüñÁÉÍÓÚÜÑ\\s\\-_.,()]+$");

  private static final Pattern DESCRIPTION_PATTERN = Pattern
      .compile("^[a-zA-Z0-9áéíóúüñÁÉÍÓÚÜÑ\\s\\-_.,();\\/\\n\\r]+$");

  // Listas de palabras prohibidas y caracteres peligrosos
  private static final Set<String> MALICIOUS_PATTERNS = new HashSet<>(Arrays.asList(
      "script", "javascript", "vbscript", "onload", "onerror", "onclick", "alert", "eval",
      "drop", "delete", "insert", "update", "select", "union", "where", "--", "/*", "*/",
      "xp_", "sp_", "exec", "execute", "declare", "char", "varchar", "nchar", "nvarchar",
      "<script", "</script", "<iframe", "</iframe", "<object", "</object", "<embed", "</embed"));

  private static final Set<String> VALID_ROLES = new HashSet<>(Arrays.asList(
      "STUDENT", "COORDINATOR", "TEACHER", "EVALUATOR", "PROJECT_MANAGER"));

  private static final Set<String> VALID_SEMESTERS = new HashSet<>(Arrays.asList(
      "AGOSTO 2023 - FEBRERO 2024", "FEBRERO 2024 - AGOSTO 2024",
      "AGOSTO 2024 - FEBRERO 2025", "FEBRERO 2025 - AGOSTO 2025",
      "AGOSTO 2025 - FEBRERO 2026"));

  // ===== VALIDACIONES BÁSICAS =====

  public static String validateMandatoryString(String text, String fieldName, int maxLength) {
    if (text == null || text.trim().isEmpty()) {
      return fieldName + " es un campo obligatorio.";
    }

    text = text.trim();

    if (text.length() > maxLength) {
      return fieldName + " no puede exceder los " + maxLength + " caracteres.";
    }

    if (text.length() < 2) {
      return fieldName + " debe tener al menos 2 caracteres.";
    }

    // Verificar contenido malicioso
    String maliciousCheck = checkForMaliciousContent(text, fieldName);
    if (!maliciousCheck.isEmpty()) {
      return maliciousCheck;
    }

    return "";
  }

  public static String validateOptionalString(String text, String fieldName, int maxLength) {
    if (text == null || text.trim().isEmpty()) {
      return ""; // Campo opcional, puede estar vacío
    }

    text = text.trim();

    if (text.length() > maxLength) {
      return fieldName + " no puede exceder los " + maxLength + " caracteres.";
    }

    // Verificar contenido malicioso
    String maliciousCheck = checkForMaliciousContent(text, fieldName);
    if (!maliciousCheck.isEmpty()) {
      return maliciousCheck;
    }

    return "";
  }

  // ===== VALIDACIONES DE NOMBRES =====

  public static String validatePersonName(String name, String fieldName, boolean mandatory) {
    if (mandatory) {
      String basicValidation = validateMandatoryString(name, fieldName, 45);
      if (!basicValidation.isEmpty()) {
        return basicValidation;
      }
    } else {
      String basicValidation = validateOptionalString(name, fieldName, 45);
      if (!basicValidation.isEmpty()) {
        return basicValidation;
      }
      if (name == null || name.trim().isEmpty()) {
        return "";
      }
    }

    name = name.trim();

    if (!NAME_PATTERN.matcher(name).matches()) {
      return fieldName + " solo puede contener letras y espacios.";
    }

    // Verificar que no tenga más de 3 espacios consecutivos
    if (name.contains("    ")) {
      return fieldName + " no puede tener más de 3 espacios consecutivos.";
    }

    // Verificar que no empiece o termine con espacio
    if (!name.equals(name.trim())) {
      return fieldName + " no puede empezar o terminar con espacios.";
    }

    return "";
  }

  public static String validateFirstName(String firstName) {
    return validatePersonName(firstName, "Nombre", true);
  }

  public static String validateLastNameFather(String lastNameFather) {
    return validatePersonName(lastNameFather, "Apellido Paterno", true);
  }

  public static String validateLastNameMother(String lastNameMother) {
    return validatePersonName(lastNameMother, "Apellido Materno", false);
  }

  // ===== VALIDACIONES DE CONTACTO =====

  public static String validateEmail(String email, boolean mandatory) {
    if (!mandatory && (email == null || email.trim().isEmpty())) {
      return "";
    }

    if (mandatory && (email == null || email.trim().isEmpty())) {
      return "Correo electrónico es un campo obligatorio.";
    }

    email = email.trim().toLowerCase();

    if (email.length() > 100) {
      return "Correo electrónico no puede exceder los 100 caracteres.";
    }

    if (!EMAIL_PATTERN.matcher(email).matches()) {
      return "El formato del correo electrónico no es válido.";
    }

    // Verificar dominios sospechosos
    String[] suspiciousDomains = { "tempmail", "10minutemail", "guerrillamail", "throwaway" };
    for (String domain : suspiciousDomains) {
      if (email.contains(domain)) {
        return "No se permiten correos de dominios temporales.";
      }
    }

    return "";
  }

  public static String validatePhoneNumber(String phone, boolean mandatory) {
    if (!mandatory && (phone == null || phone.trim().isEmpty())) {
      return "";
    }

    if (mandatory && (phone == null || phone.trim().isEmpty())) {
      return "Teléfono es un campo obligatorio.";
    }

    phone = phone.trim().replaceAll("[\\s\\-()]", "");
    //Verificar que sea un número
    if (!phone.matches("\\d+")) {
      return "El número de teléfono solo puede contener dígitos.";
    }


    if (!PHONE_PATTERN.matcher(phone).matches()) {
      return "El número de teléfono debe contener exactamente 10 dígitos.";
    }

    // Verificar que no sean todos números iguales
    if (phone.matches("(.)\\1{9}")) {
      return "El número de teléfono no puede tener todos los dígitos iguales.";
    }

    return "";
  }

  private static boolean isValidMexicanAreaCode(String areaCode) {
    // Algunos códigos de área válidos en México
    String[] validCodes = { "228", "229", "271", "272", "273", "274", "276", "278", "279",
        "281", "282", "283", "284", "285", "287", "288", "294", "296" };
    return Arrays.asList(validCodes).contains(areaCode);
  }

  // ===== VALIDACIONES ACADÉMICAS =====

  public static String validateEnrollment(String enrollment, boolean mandatory) {
    if (!mandatory && (enrollment == null || enrollment.trim().isEmpty())) {
      return "";
    }

    if (mandatory && (enrollment == null || enrollment.trim().isEmpty())) {
      return "Matrícula es un campo obligatorio.";
    }

    enrollment = enrollment.trim().toUpperCase();

    if (!ENROLLMENT_PATTERN.matcher(enrollment).matches()) {
      return "La matrícula debe tener el formato S12345678 (S seguido de 8 dígitos).";
    }

    // Verificar que el año sea válido (entre 2010 y 2030)
    String year = enrollment.substring(1, 3);
    int yearInt = Integer.parseInt(year);
    if (yearInt < 10 || yearInt > 30) {
      return "El año en la matrícula debe estar entre 2010 y 2030.";
    }

    return "";
  }

  public static String validateCredits(int credits) {
    if (credits < 0) {
      return "Los créditos no pueden ser negativos.";
    }

    if (credits > 500) {
      return "Los créditos no pueden exceder 500.";
    }

    return "";
  }

  public static String validateGrade(double grade) {
    if (grade < 0.0) {
      return "La calificación no puede ser negativa.";
    }

    if (grade > 10.0) {
      return "La calificación no puede exceder 10.0.";
    }

    // Verificar que tenga máximo 2 decimales
    String gradeStr = String.valueOf(grade);
    if (gradeStr.contains(".") && gradeStr.split("\\.")[1].length() > 2) {
      return "La calificación no puede tener más de 2 decimales.";
    }

    return "";
  }

  public static String validateSemester(String semester) {
    if (semester == null || semester.trim().isEmpty()) {
      return "Semestre es un campo obligatorio.";
    }

    semester = semester.trim();

    if (!VALID_SEMESTERS.contains(semester)) {
      return "El semestre seleccionado no es válido.";
    }

    return "";
  }

  // ===== VALIDACIONES DE USUARIO Y SEGURIDAD =====

  public static String validateUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      return "Nombre de usuario es un campo obligatorio.";
    }

    username = username.trim();

    if (username.length() < 3) {
      return "El nombre de usuario debe tener al menos 3 caracteres.";
    }

    if (username.length() > 20) {
      return "El nombre de usuario no puede exceder 20 caracteres.";
    }

    // Verificar palabras reservadas
    String[] reservedWords = { "admin", "root", "administrator", "system", "user", "guest", "test" };
    for (String word : reservedWords) {
      if (username.toLowerCase().contains(word)) {
        return "El nombre de usuario no puede contener palabras reservadas.";
      }
    }

    return "";
  }

  public static String validatePassword(String password) {
    if (password == null || password.isEmpty()) {
      return "Contraseña es un campo obligatorio.";
    }

    if (password.length() < 8) {
      return "La contraseña debe tener al menos 8 caracteres.";
    }

    if (password.length() > 128) {
      return "La contraseña no puede exceder 128 caracteres.";
    }

    if (!PASSWORD_PATTERN.matcher(password).matches()) {
      return "La contraseña debe contener al menos: 1 minúscula, 1 mayúscula, 1 número y 1 carácter especial (@$!%*?&).";
    }

    // Verificar patrones comunes débiles
    String[] weakPatterns = { "password", "123456", "qwerty", "abc123", "password123" };
    for (String pattern : weakPatterns) {
      if (password.toLowerCase().contains(pattern)) {
        return "La contraseña no puede contener patrones comunes.";
      }
    }

    return "";
  }

  public static String validateRole(String role) {
    if (role == null || role.trim().isEmpty()) {
      return "Rol es un campo obligatorio.";
    }

    role = role.trim().toUpperCase();

    if (!VALID_ROLES.contains(role)) {
      return "El rol especificado no es válido.";
    }

    return "";
  }

  // ===== VALIDACIONES DE PROYECTO =====

  public static String validateProjectName(String name) {
    String basicValidation = validateMandatoryString(name, "Nombre del proyecto", 100);
    if (!basicValidation.isEmpty()) {
      return basicValidation;
    }

    name = name.trim();

    if (!PROJECT_NAME_PATTERN.matcher(name).matches()) {
      return "El nombre del proyecto contiene caracteres no válidos.";
    }

    return "";
  }

  public static String validateProjectDescription(String description) {
    String basicValidation = validateMandatoryString(description, "Descripción del proyecto", 1000);
    if (!basicValidation.isEmpty()) {
      return basicValidation;
    }

    description = description.trim();

    if (description.length() < 10) {
      return "La descripción del proyecto debe tener al menos 10 caracteres.";
    }

    if (!DESCRIPTION_PATTERN.matcher(description).matches()) {
      return "La descripción contiene caracteres no válidos.";
    }

    return "";
  }

  public static String validateDepartment(String department) {
    return validateMandatoryString(department, "Departamento", 50);
  }

  public static String validateMethodology(String methodology) {
    return validateMandatoryString(methodology, "Metodología", 500);
  }

  public static String validateAvailability(int availability) {
    if (availability < 1) {
      return "La disponibilidad debe ser al menos 1.";
    }

    if (availability > 50) {
      return "La disponibilidad no puede exceder 50 estudiantes.";
    }

    return "";
  }

  // ===== VALIDACIONES DE IDS =====

  public static String validateId(int id, String fieldName) {
    if (id <= 0) {
      return fieldName + " debe ser un número positivo.";
    }

    if (id > 999999999) {
      return fieldName + " excede el valor máximo permitido.";
    }

    return "";
  }

  // ===== VALIDACIONES DE FECHAS =====

  public static String validateDate(String dateStr, String fieldName) {
    if (dateStr == null || dateStr.trim().isEmpty()) {
      return fieldName + " es un campo obligatorio.";
    }

    try {
      LocalDate date = LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);

      // Verificar que la fecha no sea muy antigua (más de 50 años)
      if (date.isBefore(LocalDate.now().minusYears(50))) {
        return fieldName + " no puede ser anterior a 50 años.";
      }

      // Verificar que la fecha no sea muy futura (más de 10 años)
      if (date.isAfter(LocalDate.now().plusYears(10))) {
        return fieldName + " no puede ser posterior a 10 años.";
      }

    } catch (DateTimeParseException e) {
      return fieldName + " debe tener formato válido (YYYY-MM-DD).";
    }

    return "";
  }

  public static String validateFutureDate(String dateStr, String fieldName) {
    String basicValidation = validateDate(dateStr, fieldName);
    if (!basicValidation.isEmpty()) {
      return basicValidation;
    }

    try {
      LocalDate date = LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);

      if (date.isBefore(LocalDate.now())) {
        return fieldName + " debe ser una fecha futura.";
      }

    } catch (DateTimeParseException e) {
      return fieldName + " debe tener formato válido (YYYY-MM-DD).";
    }

    return "";
  }

  // ===== VALIDACIONES DE SEGURIDAD =====

  public static String sanitizeInput(String input) {
    if (input == null) {
      return null;
    }

    // Remover caracteres peligrosos
    input = input.replaceAll("[<>\"'%;()&+]", "");

    // Reemplazar múltiples espacios con uno solo
    input = input.replaceAll("\\s+", " ");

    // Trim
    input = input.trim();

    return input;
  }

  public static String checkForMaliciousContent(String input, String fieldName) {
    if (input == null) {
      return "";
    }

    String lowerInput = input.toLowerCase();

    for (String pattern : MALICIOUS_PATTERNS) {
      if (lowerInput.contains(pattern)) {
        return fieldName + " contiene contenido no permitido.";
      }
    }

    // Verificar caracteres de control
    for (char c : input.toCharArray()) {
      if (Character.isISOControl(c) && c != '\n' && c != '\r' && c != '\t') {
        return fieldName + " contiene caracteres de control no válidos.";
      }
    }

    return "";
  }

  // ===== VALIDACIONES ESPECÍFICAS DE ORGANIZACIÓN =====

  public static String validateOrganizationName(String name) {
    String basicValidation = validateMandatoryString(name, "Nombre de organización", 100);
    if (!basicValidation.isEmpty()) {
      return basicValidation;
    }

    name = name.trim();

    if (!PROJECT_NAME_PATTERN.matcher(name).matches()) {
      return "El nombre de la organización contiene caracteres no válidos.";
    }

    return "";
  }

  public static String validatePosition(String position) {
    return validateMandatoryString(position, "Cargo", 40);
  }

  // ===== VALIDACIONES DE ARCHIVOS =====

  public static String validateFileName(String fileName) {
    if (fileName == null || fileName.trim().isEmpty()) {
      return "Nombre de archivo es obligatorio.";
    }

    fileName = fileName.trim();

    if (fileName.length() > 255) {
      return "El nombre del archivo no puede exceder 255 caracteres.";
    }

    // Verificar caracteres prohibidos en nombres de archivo
    if (fileName.matches(".*[<>:\"/\\\\|?*].*")) {
      return "El nombre del archivo contiene caracteres no válidos.";
    }

    // Verificar extensiones permitidas
    String[] allowedExtensions = { ".pdf", ".doc", ".docx", ".txt", ".jpg", ".jpeg", ".png" };
    boolean hasValidExtension = false;
    for (String ext : allowedExtensions) {
      if (fileName.toLowerCase().endsWith(ext)) {
        hasValidExtension = true;
        break;
      }
    }

    if (!hasValidExtension) {
      return "El archivo debe tener una extensión válida (.pdf, .doc, .docx, .txt, .jpg, .jpeg, .png).";
    }

    return "";
  }

  // ===== VALIDACIONES DE LISTAS =====

  public static String validateList(List<?> list, String fieldName, int minSize, int maxSize) {
    if (list == null) {
      return fieldName + " no puede ser nulo.";
    }

    if (list.size() < minSize) {
      return fieldName + " debe tener al menos " + minSize + " elemento(s).";
    }

    if (list.size() > maxSize) {
      return fieldName + " no puede tener más de " + maxSize + " elemento(s).";
    }

    return "";
  }

  // ===== VALIDACIÓN GENERAL PARA OBJETOS =====

  public static String validateNotNull(Object obj, String fieldName) {
    if (obj == null) {
      return fieldName + " es obligatorio.";
    }
    return "";
  }

  // ===== VALIDACIONES DE ENTREGA =====

  public static String validateDeliveryName(String name) {
    String basicValidation = validateMandatoryString(name, "Nombre de la entrega", 100);
    if (!basicValidation.isEmpty()) {
      return basicValidation;
    }

    name = name.trim();

    if (!PROJECT_NAME_PATTERN.matcher(name).matches()) {
      return "El nombre de la entrega contiene caracteres no válidos.";
    }

    // Verificar que no contenga solo números
    if (name.matches("^\\d+$")) {
      return "El nombre de la entrega no puede ser solo números.";
    }

    return "";
  }

  public static String validateDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      return ""; // Descripción es opcional
    }

    description = description.trim();

    if (description.length() > 500) {
      return "La descripción no puede exceder 500 caracteres.";
    }

    if (!DESCRIPTION_PATTERN.matcher(description).matches()) {
      return "La descripción contiene caracteres no válidos.";
    }

    return "";
  }

  public static String validateDeliveryType(String type) {
    if (type == null || type.trim().isEmpty()) {
      return "El tipo de entrega es obligatorio.";
    }

    type = type.trim();

    Set<String> validTypes = new HashSet<>();
    validTypes.add("INITIAL DOCUMENT");
    validTypes.add("REPORT");
    validTypes.add("FINAL DOCUMENT");

    if (!validTypes.contains(type)) {
      return "El tipo de entrega no es válido.";
    }

    return "";
  }

  // ===== VALIDACIONES DE EVALUACIÓN =====

  public static String validateEvaluationScore(int score) {
    if (score < 5) {
      return "La calificación mínima es 5.";
    }

    if (score > 10) {
      return "La calificación máxima es 10.";
    }

    return "";
  }

  public static String validateEvaluationTitle(String title) {
    String basicValidation = validateMandatoryString(title, "Título de evaluación", 150);
    if (!basicValidation.isEmpty()) {
      return basicValidation;
    }

    title = title.trim();

    if (title.length() < 5) {
      return "El título de la evaluación debe tener al menos 5 caracteres.";
    }

    if (!PROJECT_NAME_PATTERN.matcher(title).matches()) {
      return "El título contiene caracteres no válidos.";
    }

    return "";
  }

  public static String validateObservations(String observations) {
    if (observations == null || observations.trim().isEmpty()) {
      return ""; // Observaciones son opcionales
    }

    observations = observations.trim();

    if (observations.length() > 1000) {
      return "Las observaciones no pueden exceder 1000 caracteres.";
    }

    if (!DESCRIPTION_PATTERN.matcher(observations).matches()) {
      return "Las observaciones contienen caracteres no válidos.";
    }

    return "";
  }

  public static String validateReportedHours(String hoursText) {
    if (hoursText == null || hoursText.trim().isEmpty()) {
      return "Las horas reportadas son obligatorias.";
    }

    try {
      int hours = Integer.parseInt(hoursText.trim());

      if (hours < 1) {
        return "Las horas reportadas deben ser al menos 1.";
      }

      if (hours > 100) {
        return "Las horas reportadas no pueden exceder 100 por período.";
      }

      return "";
    } catch (NumberFormatException e) {
      return "Las horas reportadas deben ser un número válido.";
    }
  }

  public static String validateGrade(String gradeText) {
    if (gradeText == null || gradeText.trim().isEmpty()) {
      return "La calificación es obligatoria.";
    }

    try {
      double grade = Double.parseDouble(gradeText.trim());
      return validateGrade(grade);
    } catch (NumberFormatException e) {
      return "La calificación debe ser un número válido.";
    }
  }
}