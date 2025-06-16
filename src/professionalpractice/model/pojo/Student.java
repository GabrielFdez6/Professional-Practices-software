package professionalpractice.model.pojo;

public class Student {

    private int studentId;
    private String studentNumber;
    private String semester;
    private String email;
    private String firstName;
    private String maternalLastName;
    private String paternalLastName;
    private String phone;
    private int credits;
    private boolean assignedToProject;
    private String projectSelection;
    private double finalGrade;
    private int statusId;
    private int userId;

    public Student() {
    }

    public Student(int studentId, String studentNumber, String semester, String email, String firstName, String maternalLastName, String paternalLastName, String phone, int credits, boolean assignedToProject, String projectSelection, double finalGrade, int statusId, int userId) {
        this.studentId = studentId;
        this.studentNumber = studentNumber;
        this.semester = semester;
        this.email = email;
        this.firstName = firstName;
        this.maternalLastName = maternalLastName;
        this.paternalLastName = paternalLastName;
        this.phone = phone;
        this.credits = credits;
        this.assignedToProject = assignedToProject;
        this.projectSelection = projectSelection;
        this.finalGrade = finalGrade;
        this.statusId = statusId;
        this.userId = userId;
    }

    // Getters y Setters

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMaternalLastName() {
        return maternalLastName;
    }

    public void setMaternalLastName(String maternalLastName) {
        this.maternalLastName = maternalLastName;
    }

    public String getPaternalLastName() {
        return paternalLastName;
    }

    public void setPaternalLastName(String paternalLastName) {
        this.paternalLastName = paternalLastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public boolean isAssignedToProject() {
        return assignedToProject;
    }

    public void setAssignedToProject(boolean assignedToProject) {
        this.assignedToProject = assignedToProject;
    }

    public String getProjectSelection() {
        return projectSelection;
    }

    public void setProjectSelection(String projectSelection) {
        this.projectSelection = projectSelection;
    }

    public double getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(double finalGrade) {
        this.finalGrade = finalGrade;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Método de utilidad para obtener el nombre completo
    public String getFullName() {
        return this.firstName + " " + this.paternalLastName + (this.maternalLastName != null ? " " + this.maternalLastName : "");
    }
}