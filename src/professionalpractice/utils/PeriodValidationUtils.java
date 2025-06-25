package professionalpractice.utils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import professionalpractice.model.dao.StudentDAO;
import professionalpractice.model.dao.TermDAO;
import professionalpractice.model.pojo.Term;
import professionalpractice.model.ConectionBD;

/**
 * Utilidades específicas para validaciones relacionadas con períodos escolares.
 * Esta clase centraliza toda la lógica de validación de períodos en el sistema
 * de Prácticas Profesionales.
 */
public class PeriodValidationUtils {

  /**
   * Valida si un estudiante está en el período escolar actual
   * 
   * @param idStudent ID del estudiante a validar
   * @return PeriodValidationResult con el resultado de la validación
   */
  public static PeriodValidationResult validateStudentCurrentPeriod(int idStudent) {
    try {
      if (StudentDAO.isStudentInCurrentPeriod(idStudent)) {
        Term currentPeriod = StudentDAO.getCurrentPeriodForStudent(idStudent);
        return new PeriodValidationResult(true,
            "El estudiante está registrado en el período actual: " +
                (currentPeriod != null ? currentPeriod.getName() : "Período actual"));
      } else {
        return new PeriodValidationResult(false,
            "El estudiante no está registrado en el período escolar actual. " +
                "Por favor, contacte al coordinador para verificar su registro.");
      }
    } catch (SQLException e) {
      return new PeriodValidationResult(false,
          "Error al verificar el período del estudiante: " + e.getMessage());
    }
  }

  /**
   * Obtiene información detallada del período actual del sistema
   * 
   * @return PeriodInfo con información del período actual o null si no hay
   *         período activo
   */
  public static PeriodInfo getCurrentPeriodInfo() {
    try {
      Term currentTerm = TermDAO.getCurrentPeriod(ConectionBD.getConnection());
      if (currentTerm != null) {
        return new PeriodInfo(
            currentTerm.getIdTerm(),
            currentTerm.getName(),
            currentTerm.getStartDate(),
            currentTerm.getEndDate(),
            true);
      }
    } catch (SQLException e) {
      System.err.println("Error al obtener información del período actual: " + e.getMessage());
    }
    return null;
  }

  /**
   * Valida si la fecha actual está dentro de un período específico
   * 
   * @param startDate fecha de inicio del período (formato: "yyyy-MM-dd")
   * @param endDate   fecha de fin del período (formato: "yyyy-MM-dd")
   * @return true si la fecha actual está dentro del período, false en caso
   *         contrario
   */
  public static boolean isCurrentDateInPeriod(String startDate, String endDate) {
    try {
      LocalDate today = LocalDate.now();
      LocalDate start = LocalDate.parse(startDate);
      LocalDate end = LocalDate.parse(endDate);

      return !today.isBefore(start) && !today.isAfter(end);
    } catch (Exception e) {
      System.err.println("Error al validar fechas del período: " + e.getMessage());
      return false;
    }
  }

  /**
   * Genera un mensaje informativo sobre el período actual
   * 
   * @return String con información del período actual o mensaje de error
   */
  public static String getCurrentPeriodMessage() {
    PeriodInfo periodInfo = getCurrentPeriodInfo();
    if (periodInfo != null) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
      return String.format("Período actual: %s (%s - %s)",
          periodInfo.getName(),
          LocalDate.parse(periodInfo.getStartDate()).format(formatter),
          LocalDate.parse(periodInfo.getEndDate()).format(formatter));
    } else {
      return "No hay un período escolar activo en este momento.";
    }
  }

  /**
   * Clase interna para encapsular el resultado de validaciones de período
   */
  public static class PeriodValidationResult {
    private final boolean isValid;
    private final String message;

    public PeriodValidationResult(boolean isValid, String message) {
      this.isValid = isValid;
      this.message = message;
    }

    public boolean isValid() {
      return isValid;
    }

    public String getMessage() {
      return message;
    }
  }

  /**
   * Clase interna para encapsular información de un período
   */
  public static class PeriodInfo {
    private final int idTerm;
    private final String name;
    private final String startDate;
    private final String endDate;
    private final boolean isActive;

    public PeriodInfo(int idTerm, String name, String startDate, String endDate, boolean isActive) {
      this.idTerm = idTerm;
      this.name = name;
      this.startDate = startDate;
      this.endDate = endDate;
      this.isActive = isActive;
    }

    public int getIdTerm() {
      return idTerm;
    }

    public String getName() {
      return name;
    }

    public String getStartDate() {
      return startDate;
    }

    public String getEndDate() {
      return endDate;
    }

    public boolean isActive() {
      return isActive;
    }
  }
}