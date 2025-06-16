package professionalpractice.model.pojo;

public class StudentProject {

    private int idStudent;
    private String enrollment;
    private String studentFullName;
    private String semester;
    private String projectName;
    private int responseCode;

    public StudentProject() {
    }

    public StudentProject(int idStudent, String enrollment, String studentFullName, String semester, String projectName) {
        this.idStudent = idStudent;
        this.enrollment = enrollment;
        this.studentFullName = studentFullName;
        this.semester = semester;
        this.projectName = projectName;
    }

    // Getters and Setters for all attributes
    public int getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(int idStudent) {
        this.idStudent = idStudent;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public String getStudentFullName() {
        return studentFullName;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName = studentFullName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public String toString() {
        return studentFullName;
    }
}