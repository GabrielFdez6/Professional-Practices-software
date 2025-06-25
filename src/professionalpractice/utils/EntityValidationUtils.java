package professionalpractice.utils;

import java.util.ArrayList;
import java.util.List;
import professionalpractice.model.pojo.*;
import professionalpractice.model.dao.StudentDAO;
import java.sql.SQLException;

/**
 * Utilidades de validación específicas para las entidades del dominio de
 * Prácticas Profesionales.
 * Esta clase proporciona validaciones rigurosas y específicas para cada entidad
 * del sistema.
 */
public class EntityValidationUtils {

  public static List<String> validateStudent(Student student) {
    List<String> errors = new ArrayList<>();

    if (student == null) {
      errors.add("El objeto estudiante no puede ser nulo");
      return errors;
    }

    String firstNameError = ValidationUtils.validateFirstName(student.getFirstName());
    if (!firstNameError.isEmpty())
      errors.add(firstNameError);

    String lastNameFatherError = ValidationUtils.validateLastNameFather(student.getLastNameFather());
    if (!lastNameFatherError.isEmpty())
      errors.add(lastNameFatherError);

    String lastNameMotherError = ValidationUtils.validateLastNameMother(student.getLastNameMother());
    if (!lastNameMotherError.isEmpty())
      errors.add(lastNameMotherError);

    String enrollmentError = ValidationUtils.validateEnrollment(student.getEnrollment(), true);
    if (!enrollmentError.isEmpty())
      errors.add(enrollmentError);

    String emailError = ValidationUtils.validateEmail(student.getEmail(), true);
    if (!emailError.isEmpty())
      errors.add(emailError);

    String phoneError = ValidationUtils.validatePhoneNumber(student.getPhone(), true);
    if (!phoneError.isEmpty())
      errors.add(phoneError);

    String creditsError = ValidationUtils.validateCredits(student.getCredits());
    if (!creditsError.isEmpty())
      errors.add(creditsError);

    String semesterError = ValidationUtils.validateSemester(student.getSemester());
    if (!semesterError.isEmpty())
      errors.add(semesterError);

    String gradeError = ValidationUtils.validateGrade(student.getGrade());
    if (!gradeError.isEmpty())
      errors.add(gradeError);

    String userIdError = ValidationUtils.validateId(student.getIdUser(), "ID de usuario del estudiante");
    if (!userIdError.isEmpty())
      errors.add(userIdError);

    if (student.getCredits() < 240 && student.getSemester().contains("2024")) {
      errors.add("Un estudiante debe tener al menos 240 créditos para cursar prácticas profesionales.");
    }

    return errors;
  }

  public static List<String> validateProject(Project project) {
    List<String> errors = new ArrayList<>();

    if (project == null) {
      errors.add("El objeto proyecto no puede ser nulo");
      return errors;
    }

    String nameError = ValidationUtils.validateProjectName(project.getName());
    if (!nameError.isEmpty())
      errors.add(nameError);

    String departmentError = ValidationUtils.validateDepartment(project.getDepartment());
    if (!departmentError.isEmpty())
      errors.add(departmentError);

    String descriptionError = ValidationUtils.validateProjectDescription(project.getDescription());
    if (!descriptionError.isEmpty())
      errors.add(descriptionError);

    String methodologyError = ValidationUtils.validateMethodology(project.getMethodology());
    if (!methodologyError.isEmpty())
      errors.add(methodologyError);

    String availabilityError = ValidationUtils.validateAvailability(project.getAvailability());
    if (!availabilityError.isEmpty())
      errors.add(availabilityError);

    String projectManagerIdError = ValidationUtils.validateId(project.getIdProjectManager(),
        "ID del responsable de proyecto");
    if (!projectManagerIdError.isEmpty())
      errors.add(projectManagerIdError);

    String organizationIdError = ValidationUtils.validateId(project.getIdLinkedOrganization(),
        "ID de organización vinculada");
    if (!organizationIdError.isEmpty())
      errors.add(organizationIdError);

    String coordinatorIdError = ValidationUtils.validateId(project.getIdCoordinator(), "ID del coordinador");
    if (!coordinatorIdError.isEmpty())
      errors.add(coordinatorIdError);

    if (project.getName() != null && project.getDescription() != null) {
      if (project.getName().length() > project.getDescription().length()) {
        errors.add("La descripción del proyecto debe ser más detallada que el nombre.");
      }
    }

    return errors;
  }

  public static List<String> validateProjectManager(ProjectManager projectManager) {
    List<String> errors = new ArrayList<>();

    if (projectManager == null) {
      errors.add("El objeto responsable de proyecto no puede ser nulo");
      return errors;
    }

    String firstNameError = ValidationUtils.validateFirstName(projectManager.getFirstName());
    if (!firstNameError.isEmpty())
      errors.add(firstNameError);

    String lastNameFatherError = ValidationUtils.validateLastNameFather(projectManager.getLastNameFather());
    if (!lastNameFatherError.isEmpty())
      errors.add(lastNameFatherError);

    String lastNameMotherError = ValidationUtils.validateLastNameMother(projectManager.getLastNameMother());
    if (!lastNameMotherError.isEmpty())
      errors.add(lastNameMotherError);

    String positionError = ValidationUtils.validatePosition(projectManager.getPosition());
    if (!positionError.isEmpty())
      errors.add(positionError);

    String emailError = ValidationUtils.validateEmail(projectManager.getEmail(), false);
    if (!emailError.isEmpty())
      errors.add(emailError);

    String phoneError = ValidationUtils.validatePhoneNumber(projectManager.getPhone(), true);
    if (!phoneError.isEmpty())
      errors.add(phoneError);

    String organizationIdError = ValidationUtils.validateId(projectManager.getIdLinkedOrganization(),
        "ID de organización vinculada");
    if (!organizationIdError.isEmpty())
      errors.add(organizationIdError);

    return errors;
  }

  public static List<String> validateLinkedOrganization(LinkedOrganization organization) {
    List<String> errors = new ArrayList<>();

    if (organization == null) {
      errors.add("El objeto organización vinculada no puede ser nulo");
      return errors;
    }

    String nameError = ValidationUtils.validateOrganizationName(organization.getName());
    if (!nameError.isEmpty())
      errors.add(nameError);

    return errors;
  }

  public static List<String> validateUserAccount(UserAccount userAccount) {
    List<String> errors = new ArrayList<>();

    if (userAccount == null) {
      errors.add("El objeto cuenta de usuario no puede ser nulo");
      return errors;
    }

    String usernameError = ValidationUtils.validateUsername(userAccount.getUsername());
    if (!usernameError.isEmpty())
      errors.add(usernameError);

    if (userAccount.getPassword() != null && !userAccount.getPassword().isEmpty()) {
      String passwordError = ValidationUtils.validatePassword(userAccount.getPassword());
      if (!passwordError.isEmpty())
        errors.add(passwordError);
    }

    String roleError = ValidationUtils.validateRole(userAccount.getRole());
    if (!roleError.isEmpty())
      errors.add(roleError);

    return errors;
  }

  public static List<String> validatePresentationEvaluation(PresentationEvaluation evaluation) {
    List<String> errors = new ArrayList<>();

    if (evaluation == null) {
      errors.add("El objeto evaluación de presentación no puede ser nulo");
      return errors;
    }

    String observationsError = ValidationUtils.validateObservations(evaluation.getObservations());
    if (!observationsError.isEmpty())
      errors.add(observationsError);

    String studentIdError = ValidationUtils.validateId(evaluation.getStudentId(), "ID del estudiante");
    if (!studentIdError.isEmpty())
      errors.add(studentIdError);

    if (evaluation.getMethodsTechniquesScore() != null) {
      String methodsError = ValidationUtils.validateEvaluationScore(evaluation.getMethodsTechniquesScore().intValue());
      if (!methodsError.isEmpty())
        errors.add("Métodos y Técnicas: " + methodsError);
    }

    if (evaluation.getRequirementsScore() != null) {
      String requirementsError = ValidationUtils.validateEvaluationScore(evaluation.getRequirementsScore().intValue());
      if (!requirementsError.isEmpty())
        errors.add("Requisitos: " + requirementsError);
    }

    if (evaluation.getSecurityMasteryScore() != null) {
      String securityError = ValidationUtils.validateEvaluationScore(evaluation.getSecurityMasteryScore().intValue());
      if (!securityError.isEmpty())
        errors.add("Seguridad y Dominio: " + securityError);
    }

    if (evaluation.getContentScore() != null) {
      String contentError = ValidationUtils.validateEvaluationScore(evaluation.getContentScore().intValue());
      if (!contentError.isEmpty())
        errors.add("Contenido: " + contentError);
    }

    if (evaluation.getSpellingGrammarScore() != null) {
      String spellingError = ValidationUtils.validateEvaluationScore(evaluation.getSpellingGrammarScore().intValue());
      if (!spellingError.isEmpty())
        errors.add("Ortografía y Gramática: " + spellingError);
    }

    boolean hasAnyScore = evaluation.getMethodsTechniquesScore() != null ||
        evaluation.getRequirementsScore() != null ||
        evaluation.getSecurityMasteryScore() != null ||
        evaluation.getContentScore() != null ||
        evaluation.getSpellingGrammarScore() != null;

    if (!hasAnyScore) {
      errors.add("Se debe proporcionar al menos una calificación.");
    }

    if (evaluation.getAverageScore() != null && hasAnyScore) {
      if (evaluation.getAverageScore().doubleValue() < 5.0 || evaluation.getAverageScore().doubleValue() > 10.0) {
        errors.add("El promedio de calificaciones debe estar entre 5.0 y 10.0.");
      }
    }

    return errors;
  }

  public static List<String> validateCoordinator(Coordinator coordinator) {
    List<String> errors = new ArrayList<>();

    if (coordinator == null) {
      errors.add("El objeto coordinador no puede ser nulo");
      return errors;
    }

    String firstNameError = ValidationUtils.validateFirstName(coordinator.getFirstName());
    if (!firstNameError.isEmpty())
      errors.add(firstNameError);

    String lastNameFatherError = ValidationUtils.validateLastNameFather(coordinator.getLastNameFather());
    if (!lastNameFatherError.isEmpty())
      errors.add(lastNameFatherError);

    String lastNameMotherError = ValidationUtils.validateLastNameMother(coordinator.getLastNameMother());
    if (!lastNameMotherError.isEmpty())
      errors.add(lastNameMotherError);

    String emailError = ValidationUtils.validateEmail(coordinator.getEmail(), true);
    if (!emailError.isEmpty())
      errors.add(emailError);

    String userIdError = ValidationUtils.validateId(coordinator.getIdUser(), "ID de usuario del coordinador");
    if (!userIdError.isEmpty())
      errors.add(userIdError);

    return errors;
  }

  public static List<String> validateAcademic(Academic academic) {
    List<String> errors = new ArrayList<>();

    if (academic == null) {
      errors.add("El objeto académico no puede ser nulo");
      return errors;
    }

    String firstNameError = ValidationUtils.validateFirstName(academic.getFirstName());
    if (!firstNameError.isEmpty())
      errors.add(firstNameError);

    String lastNameFatherError = ValidationUtils.validateLastNameFather(academic.getLastNameFather());
    if (!lastNameFatherError.isEmpty())
      errors.add(lastNameFatherError);

    String lastNameMotherError = ValidationUtils.validateLastNameMother(academic.getLastNameMother());
    if (!lastNameMotherError.isEmpty())
      errors.add(lastNameMotherError);

    String emailError = ValidationUtils.validateEmail(academic.getEmail(), true);
    if (!emailError.isEmpty())
      errors.add(emailError);

    String userIdError = ValidationUtils.validateId(academic.getIdUser(), "ID de usuario del académico");
    if (!userIdError.isEmpty())
      errors.add(userIdError);

    return errors;
  }

  public static String formatValidationErrors(List<String> errors) {
    if (errors == null || errors.isEmpty()) {
      return "";
    }

    StringBuilder formattedErrors = new StringBuilder();
    for (int i = 0; i < errors.size(); i++) {
      formattedErrors.append(i + 1).append(". ").append(errors.get(i));
      if (i < errors.size() - 1) {
        formattedErrors.append("\n");
      }
    }

    return formattedErrors.toString();
  }

  public static boolean hasValidationErrors(List<String> errors) {
    return errors != null && !errors.isEmpty();
  }

  public static List<String> validateStudentProjectAssignment(Student student, Project project) {
    List<String> errors = new ArrayList<>();

    errors.addAll(validateStudent(student));
    errors.addAll(validateProject(project));

    if (student != null && project != null) {
      if (student.isAssignedToProject()) {
        errors.add("El estudiante ya está asignado a un proyecto.");
      }

      if (project.getAvailability() <= 0) {
        errors.add("El proyecto no tiene disponibilidad para más estudiantes.");
      }

      if (student.getCredits() < 240) {
        errors.add("El estudiante no cumple con los créditos mínimos requeridos para prácticas profesionales.");
      }

      // Validación del período escolar actual
      try {
        if (!StudentDAO.isStudentInCurrentPeriod(student.getIdStudent())) {
          errors.add(
              "El estudiante no está registrado en el período escolar actual y no se puede asignar a un proyecto.");
        }
      } catch (SQLException e) {
        errors.add("Error al verificar el período escolar del estudiante: " + e.getMessage());
      }
    }

    return errors;
  }

  public static List<String> validateProjectCreation(Project project, ProjectManager manager,
      LinkedOrganization organization) {
    List<String> errors = new ArrayList<>();

    errors.addAll(validateProject(project));
    errors.addAll(validateProjectManager(manager));
    errors.addAll(validateLinkedOrganization(organization));

    if (project != null && manager != null) {
      if (project.getIdProjectManager() != manager.getIdProjectManager()) {
        errors.add("La referencia del responsable de proyecto no coincide.");
      }
    }

    if (project != null && organization != null) {
      if (project.getIdLinkedOrganization() != organization.getIdLinkedOrganization()) {
        errors.add("La referencia de la organización vinculada no coincide.");
      }
    }

    return errors;
  }
}