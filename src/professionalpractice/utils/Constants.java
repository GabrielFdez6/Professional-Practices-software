package professionalpractice.utils;

public class Constants {

  // ===== CÓDIGOS DE RESPUESTA =====
  public static final int OPERATION_SUCCESFUL = 200;
  public static final int CONNECTION_FAILED = 500;
  public static final int OPERATION_FAILED = 500;

  // ===== LÍMITES DE LONGITUD PARA CAMPOS =====
  public static final int MAX_FIRST_NAME_LENGTH = 45;
  public static final int MAX_LAST_NAME_LENGTH = 45;
  public static final int MAX_EMAIL_LENGTH = 100;
  public static final int MAX_PHONE_LENGTH = 10;
  public static final int MAX_ENROLLMENT_LENGTH = 9;
  public static final int MAX_USERNAME_LENGTH = 20;
  public static final int MAX_PASSWORD_LENGTH = 128;
  public static final int MAX_POSITION_LENGTH = 50;
  public static final int MAX_ORGANIZATION_NAME_LENGTH = 100;
  public static final int MAX_PROJECT_NAME_LENGTH = 100;
  public static final int MAX_PROJECT_DESCRIPTION_LENGTH = 1000;
  public static final int MAX_DEPARTMENT_LENGTH = 50;
  public static final int MAX_METHODOLOGY_LENGTH = 500;
  public static final int MAX_FILENAME_LENGTH = 255;

  // ===== LÍMITES MÍNIMOS =====
  public static final int MIN_NAME_LENGTH = 2;
  public static final int MIN_USERNAME_LENGTH = 3;
  public static final int MIN_PASSWORD_LENGTH = 8;
  public static final int MIN_DESCRIPTION_LENGTH = 10;

  // ===== LÍMITES NUMÉRICOS =====
  public static final int MIN_CREDITS = 0;
  public static final int MAX_CREDITS = 500;
  public static final int MIN_AVAILABILITY = 1;
  public static final int MAX_AVAILABILITY = 50;
  public static final int MAX_ID_VALUE = 999999999;
  public static final double MIN_GRADE = 0.0;
  public static final double MAX_GRADE = 10.0;
  public static final int MAX_DELIVERY_PERIOD_DAYS = 30;

  // ===== CONFIGURACIONES DE SEGURIDAD =====
  public static final int MAX_LOGIN_ATTEMPTS = 3;
  public static final int SESSION_TIMEOUT_MINUTES = 30;
  public static final int PASSWORD_EXPIRY_DAYS = 90;

  // ===== CONFIGURACIONES DE ARCHIVOS =====
  public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
  public static final String[] ALLOWED_FILE_EXTENSIONS = {
      ".pdf", ".doc", ".docx", ".txt", ".jpg", ".jpeg", ".png"
  };

  // ===== ROLES DEL SISTEMA =====
  public static final String ROLE_STUDENT = "STUDENT";
  public static final String ROLE_COORDINATOR = "COORDINATOR";
  public static final String ROLE_ACADEMIC = "ACADEMIC";
  public static final String ROLE_EVALUATOR = "EVALUATOR";
  public static final String ROLE_PROJECT_MANAGER = "PROJECT_MANAGER";

  // ===== ESTADOS DE PROYECTO =====
  public static final String PROJECT_STATUS_ACTIVE = "ACTIVE";
  public static final String PROJECT_STATUS_INACTIVE = "INACTIVE";
  public static final String PROJECT_STATUS_COMPLETED = "COMPLETED";
  public static final String PROJECT_STATUS_CANCELLED = "CANCELLED";

  // ===== ESTADOS DE ENTREGA =====
  public static final String DELIVERY_STATUS_PENDING = "PENDING";
  public static final String DELIVERY_STATUS_DELIVERED = "DELIVERED";
  public static final String DELIVERY_STATUS_LATE = "LATE";
  public static final String DELIVERY_STATUS_GRADED = "GRADED";

  // ===== TIPOS DE DOCUMENTO =====
  public static final String DOCUMENT_TYPE_INITIAL = "INITIAL";
  public static final String DOCUMENT_TYPE_REPORT = "REPORT";
  public static final String DOCUMENT_TYPE_FINAL = "FINAL";

  // ===== MENSAJES DE ERROR COMUNES =====
  public static final String ERROR_INVALID_CREDENTIALS = "Credenciales inválidas";
  public static final String ERROR_ACCESS_DENIED = "Acceso denegado";
  public static final String ERROR_EXPIRED_SESSION = "Sesión expirada";
  public static final String ERROR_INVALID_DATA = "Datos inválidos";
  public static final String ERROR_DATABASE_CONNECTION = "Error de conexión a la base de datos";
  public static final String ERROR_DUPLICATE_ENTRY = "El registro ya existe";
  public static final String ERROR_RECORD_NOT_FOUND = "Registro no encontrado";
  public static final String ERROR_INSUFFICIENT_PERMISSIONS = "Permisos insuficientes";

  // ===== MENSAJES DE ÉXITO =====
  public static final String SUCCESS_RECORD_CREATED = "Registro creado exitosamente";
  public static final String SUCCESS_RECORD_UPDATED = "Registro actualizado exitosamente";
  public static final String SUCCESS_RECORD_DELETED = "Registro eliminado exitosamente";
  public static final String SUCCESS_FILE_UPLOADED = "Archivo subido exitosamente";
  public static final String SUCCESS_EMAIL_SENT = "Correo enviado exitosamente";

  // ===== CONFIGURACIONES DE VALIDACIÓN =====
  public static final int MAX_VALIDATION_ERRORS = 10;
  public static final int MAX_LIST_SIZE = 1000;
  public static final int MIN_LIST_SIZE_REQUIRED = 1;

  // ===== PATRONES REGEX =====
  public static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
  public static final String REGEX_PHONE = "^\\d{10}$";
  public static final String REGEX_ENROLLMENT = "^S\\d{8}$";
  public static final String REGEX_USERNAME = "^[a-zA-Z0-9_]{3,20}$";
  public static final String REGEX_NAME = "^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ\\s]+$";
  public static final String REGEX_ALPHANUMERIC = "^[a-zA-Z0-9\\s]+$";

  // ===== CONFIGURACIONES DE BASE DE DATOS =====
  public static final int DB_CONNECTION_TIMEOUT = 30; // segundos
  public static final int DB_MAX_RETRIES = 3;
  public static final int DB_RETRY_DELAY = 1000; // milisegundos
}
