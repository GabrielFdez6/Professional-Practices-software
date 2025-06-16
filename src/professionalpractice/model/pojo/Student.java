package professionalpractice.model.pojo;

public class Student {

    private int idStudent;
    private String firstName;
    private String lastNameFather;
    private String lastNameMother;
    private String enrollment;
    private String email;
    private String phone;
    private int credits;
    private String semester;
    private boolean isAssignedToProject;
    private double grade;
    private int idUser;

    public Student() {
    }

    public Student(int idStudent, String firstName, String lastNameFather, String lastNameMother, String enrollment, String email, String phone, int credits, String semester, boolean isAssignedToProject, double grade, int idUser) {
        this.idStudent = idStudent;
        this.firstName = firstName;
        this.lastNameFather = lastNameFather;
        this.lastNameMother = lastNameMother;
        this.enrollment = enrollment;
        this.email = email;
        this.phone = phone;
        this.credits = credits;
        this.semester = semester;
        this.isAssignedToProject = isAssignedToProject;
        this.grade = grade;
        this.idUser = idUser;
    }

    public int getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(int idStudent) {
        this.idStudent = idStudent;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastNameFather() {
        return lastNameFather;
    }

    public void setLastNameFather(String lastNameFather) {
        this.lastNameFather = lastNameFather;
    }

    public String getLastNameMother() {
        return lastNameMother;
    }

    public void setLastNameMother(String lastNameMother) {
        this.lastNameMother = lastNameMother;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public boolean isAssignedToProject() {
        return isAssignedToProject;
    }

    public void setAssignedToProject(boolean assignedToProject) {
        isAssignedToProject = assignedToProject;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    // Método de utilidad para obtener el nombre completo
    public String getFullName() {
        return this.firstName + " " + this.lastNameFather + (this.lastNameMother != null ? " " + this.lastNameMother : "");
    }
}