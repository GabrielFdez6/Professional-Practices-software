package professionalpractice.model.pojo;

public class StudentProgress {

    private Student student;
    private String projectName;
    private int accumulatedHours;

    public StudentProgress() {
    }

    public StudentProgress(Student student, String projectName, int accumulatedHours) {
        this.student = student;
        this.projectName = projectName;
        this.accumulatedHours = accumulatedHours;
    }


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