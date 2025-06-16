package professionalpractice.model.pojo;

public class SubjectGroup {
    private int idSubjectGroup;
    private int idTerm;
    private int idSubject;
    private String schedule;

    public SubjectGroup() {}

    public SubjectGroup(int idSubjectGroup, int idTerm, int idSubject, String schedule) {
        this.idSubjectGroup = idSubjectGroup;
        this.idTerm = idTerm;
        this.idSubject = idSubject;
        this.schedule = schedule;
    }

    public int getIdSubjectGroup() {
        return idSubjectGroup;
    }

    public void setIdSubjectGroup(int idSubjectGroup) {
        this.idSubjectGroup = idSubjectGroup;
    }

    public int getIdTerm() {
        return idTerm;
    }

    public void setIdTerm(int idTerm) {
        this.idTerm = idTerm;
    }

    public int getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(int idSubject) {
        this.idSubject = idSubject;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
