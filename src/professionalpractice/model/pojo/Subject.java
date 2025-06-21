package professionalpractice.model.pojo;

public class Subject {
    private int idSubject;
    private String name;
    private int credits;
    private String nrc;

    public Subject() {}

    public Subject(int idSubject, String name, int credits) {
        this.idSubject = idSubject;
        this.name = name;
        this.credits = credits;
        this.nrc = nrc;
    }

    public int getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(int idSubject) {
        this.idSubject = idSubject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }
}
