package professionalpractice.utils;

import java.util.ArrayList;
import java.util.List;
import professionalpractice.model.pojo.*;

/**
 * Utilidades de validación específicas para las entidades del dominio de
 * Prácticas Profesionales.
 * Esta clase proporciona validaciones rigurosas y específicas para cada entidad
 * del sistema.
 */
public class EntityValidationUtils {

  // ===== VALIDACIONES DE ESTUDIANTE =====

  public static List<String> validateStudent(Student student) {
    List<String> errors = new ArrayList<>();

    if (student == null) {
      errors.add("El objeto estudiante no puede ser nulo");
      return errors;
    }

    // Validar nombre
    String firstNameError = ValidationUtils.validateFirstName(student.getFirstName());
    if (!firstNameError.isEmpty())
      errors.add(firstNameError);

    // Validar apellido paterno
    String lastNameFatherError = ValidationUtils.validateLastNameFather(student.getLastNameFather());
    if (!lastNameFatherError.isEmpty())
      errors.add(lastNameFatherError);

    // Validar apellido materno (opcional)
    String lastNameMotherError = ValidationUtils.validateLastNameMother(student.getLastNameMother());
    if (!lastNameMotherError.isEmpty())
      errors.add(lastNameMotherError);

    // Validar matrícula
    String enrollmentError = ValidationUtils.validateEnrollment(student.getEnrollment(), true);
    if (!enrollmentError.isEmpty())
      errors.add(enrollmentError);

    // Validar email
    String emailError = ValidationUtils.validateEmail(student.getEmail(), true);
    if (!emailError.isEmpty())
      errors.add(emailError);

    // Validar teléfono
    String phoneError = ValidationUtils.validatePhoneNumber(student.getPhone(), true);
    if (!phoneError.isEmpty())
      errors.add(phoneError);

    // Validar créditos
    String creditsError = ValidationUtils.validateCredits(student.getCredits());
    if (!creditsError.isEmpty())
      errors.add(creditsError);

    // Validar semestre
    String semesterError = ValidationUtils.validateSemester(student.getSemester());
    if (!semesterError.isEmpty())
      errors.add(semesterError);

    // Validar calificación
    String gradeError = ValidationUtils.validateGrade(student.getGrade());
    if (!gradeError.isEmpty())
      errors.add(gradeError);

    // Validar ID de usuario
    String userIdError = ValidationUtils.validateId(student.getIdUser(), "ID de usuario del estudiante");
    if (!userIdError.isEmpty())
      errors.add(userIdError);

    // Validaciones de lógica de negocio
    if (student.getCredits() < 240 && student.getSemester().contains("2024")) {
      errors.add("Un estudiante debe tener al menos 240 créditos para cursar prácticas profesionales.");
    }

    return errors;
  }

  // ===== VALIDACIONES DE PROYECTO =====

  public static List<String> validateProject(Project project) {
    List<String> errors = new ArrayList<>();

    if (project == null) {
      errors.add("El objeto proyecto no puede ser nulo");
      return errors;
    }

    // Validar nombre del proyecto
    String nameError = ValidationUtils.validateProjectName(project.getName());
    if (!nameError.isEmpty())
      errors.add(nameError);

    // Validar departamento
    String departmentError = ValidationUtils.validateDepartment(project.getDepartment());
    if (!departmentError.isEmpty())
      errors.add(departmentError);

    // Validar descripción
    String descriptionError = ValidationUtils.validateProjectDescription(project.getDescription());
    if (!descriptionError.isEmpty())
      errors.add(descriptionError);

    // Validar metodología
    String methodologyError = ValidationUtils.validateMethodology(project.getMethodology());
    if (!methodologyError.isEmpty())
      errors.add(methodologyError);

    // Validar disponibilidad
    String availabilityError = ValidationUtils.validateAvailability(project.getAvailability());
    if (!availabilityError.isEmpty())
      errors.add(availabilityError);

    // Validar IDs relacionados
    String recordIdError = ValidationUtils.validateId(project.getIdRecord(), "ID de expediente");
    if (!recordIdError.isEmpty())
      errors.add(recordIdError);

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

    // Validaciones de lógica de negocio
    if (project.getName() != null && project.getDescription() != null) {
      if (project.getName().length() > project.getDescription().length()) {
        errors.add("La descripción del proyecto debe ser más detallada que el nombre.");
      }
    }

    return errors;
  }

  // ===== VALIDACIONES DE RESPONSABLE DE PROYECTO =====

  public static List<String> validateProjectManager(ProjectManager projectManager) {
    List<String> errors = new ArrayList<>();

    if (projectManager == null) {
      errors.add("El objeto responsable de proyecto no puede ser nulo");
      return errors;
    }

    // Validar nombre
    String firstNameError = ValidationUtils.validateFirstName(projectManager.getFirstName());
    if (!firstNameError.isEmpty())
      errors.add(firstNameError);

    // Validar apellido paterno
    String lastNameFatherError = ValidationUtils.validateLastNameFather(projectManager.getLastNameFather());
    if (!lastNameFatherError.isEmpty())
      errors.add(lastNameFatherError);

    // Validar apellido materno (opcional)
    String lastNameMotherError = ValidationUtils.validateLastNameMother(projectManager.getLastNameMother());
    if (!lastNameMotherError.isEmpty())
      errors.add(lastNameMotherError);

    // Validar cargo
    String positionError = ValidationUtils.validatePosition(projectManager.getPosition());
    if (!positionError.isEmpty())
      errors.add(positionError);

    // Validar email (opcional pero si está presente debe ser válido)
    String emailError = ValidationUtils.validateEmail(projectManager.getEmail(), false);
    if (!emailError.isEmpty())
      errors.add(emailError);

    // Validar teléfono
    String phoneError = ValidationUtils.validatePhoneNumber(projectManager.getPhone(), true);
    if (!phoneError.isEmpty())
      errors.add(phoneError);

    // Validar ID de organización vinculada
    String organizationIdError = ValidationUtils.validateId(projectManager.getIdLinkedOrganization(),
        "ID de organización vinculada");
    if (!organizationIdError.isEmpty())
      errors.add(organizationIdError);


    return errors;
  }

  // ===== VALIDACIONES DE ORGANIZACIÓN VINCULADA =====

  public static List<String> validateLinkedOrganization(LinkedOrganization organization) {
    List<String> errors = new ArrayList<>();

    if (organization == null) {
      errors.add("El objeto organización vinculada no puede ser nulo");
      return errors;
    }

    // Validar nombre de la organización
    String nameError = ValidationUtils.validateOrganizationName(organization.getName());
    if (!nameError.isEmpty())
      errors.add(nameError);

    // Aquí se pueden agregar más validaciones específicas según los atributos de
    // LinkedOrganization

    return errors;
  }

  // ===== VALIDACIONES DE CUENTA DE USUARIO =====

  public static List<String> validateUserAccount(UserAccount userAccount) {
    List<String> errors = new ArrayList<>();

    if (userAccount == null) {
      errors.add("El objeto cuenta de usuario no puede ser nulo");
      return errors;
    }

    // Validar nombre de usuario
    String usernameError = ValidationUtils.validateUsername(userAccount.getUsername());
    if (!usernameError.isEmpty())
      errors.add(usernameError);

    // Validar contraseña (solo si se está creando/actualizando)
    if (userAccount.getPassword() != null && !userAccount.getPassword().isEmpty()) {
      String passwordError = ValidationUtils.validatePassword(userAccount.getPassword());
      if (!passwordError.isEmpty())
        errors.add(passwordError);
    }

    // Validar rol
    String roleError = ValidationUtils.validateRole(userAccount.getRole());
    if (!roleError.isEmpty())
      errors.add(roleError);

    return errors;
  }

  // ===== VALIDACIONES DE ENTREGA =====

  public static List<String> validateDelivery(Delivery delivery) {
    List<String> errors = new ArrayList<>();

    if (delivery == null) {
      errors.add("El objeto entrega no puede ser nulo");
      return errors;
    }

    // Validar nombre de la entrega
    String nameError = ValidationUtils.validateDeliveryName(delivery.getName());
    if (!nameError.isEmpty())
      errors.add(nameError);

    // Validar descripción (opcional)
    String descriptionError = ValidationUtils.validateDescription(delivery.getDescription());
    if (!descriptionError.isEmpty())
      errors.add(descriptionError);

    // Validar tipo de entrega
    String typeError = ValidationUtils.validateDeliveryType(delivery.getDeliveryType());
    if (!typeError.isEmpty())
      errors.add(typeError);

    // Validar fecha de entrega
    if (delivery.getEndDate() != null) {
      String deliveryDateError = ValidationUtils.validateDate(delivery.getEndDate().toString(),
          "Fecha de entrega");
      if (!deliveryDateError.isEmpty())
        errors.add(deliveryDateError);
    }

    // Validar ID de registro
    String recordIdError = ValidationUtils.validateId(delivery.getIdRecord(), "ID de registro");
    if (!recordIdError.isEmpty())
      errors.add(recordIdError);

    return errors;
  }

  // ===== VALIDACIONES DE EVALUACIÓN DE PRESENTACIÓN =====

  public static List<String> validatePresentationEvaluation(PresentationEvaluation evaluation) {
    List<String> errors = new ArrayList<>();

    if (evaluation == null) {
      errors.add("El objeto evaluación de presentación no puede ser nulo");
      return errors;
    }

    // Validar título de la evaluación
    String titleError = ValidationUtils.validateEvaluationTitle(evaluation.getTitle());
    if (!titleError.isEmpty())
      errors.add(titleError);

    // Validar observaciones (opcional)
    String observationsError = ValidationUtils.validateObservations(evaluation.getObservations());
    if (!observationsError.isEmpty())
      errors.add(observationsError);

    // Validar ID del estudiante
    String studentIdError = ValidationUtils.validateId(evaluation.getStudentId(), "ID del estudiante");
    if (!studentIdError.isEmpty())
      errors.add(studentIdError);

    // Validar calificaciones individuales si están presentes
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

    // Validar que se tenga al menos una calificación
    boolean hasAnyScore = evaluation.getMethodsTechniquesScore() != null ||
        evaluation.getRequirementsScore() != null ||
        evaluation.getSecurityMasteryScore() != null ||
        evaluation.getContentScore() != null ||
        evaluation.getSpellingGrammarScore() != null;

    if (!hasAnyScore) {
      errors.add("Se debe proporcionar al menos una calificación.");
    }

    // Validar consistencia del promedio
    if (evaluation.getAverageScore() != null && hasAnyScore) {
      // Verificar que el promedio esté en el rango válido
      if (evaluation.getAverageScore().doubleValue() < 5.0 || evaluation.getAverageScore().doubleValue() > 10.0) {
        errors.add("El promedio de calificaciones debe estar entre 5.0 y 10.0.");
      }
    }

    return errors;
  }

  // ===== VALIDACIONES DE COORDINADOR =====

  public static List<String> validateCoordinator(Coordinator coordinator) {
    List<String> errors = new ArrayList<>();

    if (coordinator == null) {
      errors.add("El objeto coordinador no puede ser nulo");
      return errors;
    }

    // Validar nombre
    String firstNameError = ValidationUtils.validateFirstName(coordinator.getFirstName());
    if (!firstNameError.isEmpty())
      errors.add(firstNameError);

    // Validar apellido paterno
    String lastNameFatherError = ValidationUtils.validateLastNameFather(coordinator.getLastNameFather());
    if (!lastNameFatherError.isEmpty())
      errors.add(lastNameFatherError);

    // Validar apellido materno (opcional)
    String lastNameMotherError = ValidationUtils.validateLastNameMother(coordinator.getLastNameMother());
    if (!lastNameMotherError.isEmpty())
      errors.add(lastNameMotherError);

    // Validar email
    String emailError = ValidationUtils.validateEmail(coordinator.getEmail(), true);
    if (!emailError.isEmpty())
      errors.add(emailError);

    // El coordinador no tiene campo de teléfono en esta implementación
    // Se omite la validación de teléfono

    // Validar ID de usuario
    String userIdError = ValidationUtils.validateId(coordinator.getIdUser(), "ID de usuario del coordinador");
    if (!userIdError.isEmpty())
      errors.add(userIdError);

    return errors;
  }

  // ===== VALIDACIONES DE ACADÉMICO =====

  public static List<String> validateAcademic(Academic academic) {
    List<String> errors = new ArrayList<>();

    if (academic == null) {
      errors.add("El objeto académico no puede ser nulo");
      return errors;
    }

    // Validar nombre
    String firstNameError = ValidationUtils.validateFirstName(academic.getFirstName());
    if (!firstNameError.isEmpty())
      errors.add(firstNameError);

    // Validar apellido paterno
    String lastNameFatherError = ValidationUtils.validateLastNameFather(academic.getLastNameFather());
    if (!lastNameFatherError.isEmpty())
      errors.add(lastNameFatherError);

    // Validar apellido materno (opcional)
    String lastNameMotherError = ValidationUtils.validateLastNameMother(academic.getLastNameMother());
    if (!lastNameMotherError.isEmpty())
      errors.add(lastNameMotherError);

    // Validar email
    String emailError = ValidationUtils.validateEmail(academic.getEmail(), true);
    if (!emailError.isEmpty())
      errors.add(emailError);

    // Validar ID de usuario
    String userIdError = ValidationUtils.validateId(academic.getIdUser(), "ID de usuario del académico");
    if (!userIdError.isEmpty())
      errors.add(userIdError);

    return errors;
  }

  // ===== MÉTODO UTILITARIO PARA MOSTRAR ERRORES =====

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

  // ===== VALIDACIONES COMBINADAS =====

  public static List<String> validateStudentProjectAssignment(Student student, Project project) {
    List<String> errors = new ArrayList<>();

    // Validar entidades individualmente
    errors.addAll(validateStudent(student));
    errors.addAll(validateProject(project));

    // Validaciones específicas de la asignación
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
    }

    return errors;
  }

  public static List<String> validateProjectCreation(Project project, ProjectManager manager,
      LinkedOrganization organization) {
    List<String> errors = new ArrayList<>();

    // Validar entidades individualmente
    errors.addAll(validateProject(project));
    errors.addAll(validateProjectManager(manager));
    errors.addAll(validateLinkedOrganization(organization));

    // Validaciones de integridad referencial
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