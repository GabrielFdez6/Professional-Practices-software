package professionalpractice.model.pojo;

// Este POJO ahora es un "ViewModel" que agrupa un Estudiante y su progreso específico.
public class StudentProgress {

    private Student student; // Contiene toda la información del estudiante
    private String projectName;
    private int accumulatedHours;

    public StudentProgress() {
    }

    public StudentProgress(Student student, String projectName, int accumulatedHours) {
        this.student = student;
        this.projectName = projectName;
        this.accumulatedHours = accumulatedHours;
    }

    // Getters y Setters

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getAccumulatedHours() {
        return accumulatedHours;
    }

    public void setAccumulatedHours(int accumulatedHours) {
        this.accumulatedHours = accumulatedHours;
    }
}