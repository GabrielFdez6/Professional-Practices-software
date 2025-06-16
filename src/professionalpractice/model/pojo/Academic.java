package professionalpractice.model.pojo;

public class Academic {
    private int idAcademic;
    private int idSubjectGroup;
    private String firstName;
    private String lastNameFather;
    private String lastNameMother;
    private String email;
    private boolean status;
    private int idUser;

    public Academic() {
    }

    public Academic(int idAcademic, int idSubjectGroup, String firstName, String lastNameFather, String lastNameMother, String email, boolean status, int idUser) {
        this.idAcademic = idAcademic;
        this.idSubjectGroup = idSubjectGroup;
        this.firstName = firstName;
        this.lastNameFather = lastNameFather;
        this.lastNameMother = lastNameMother;
        this.email = email;
        this.status = status;
        this.idUser = idUser;
    }

    public int getIdAcademic() {
        return idAcademic;
    }

    public void setIdAcademic(int idAcademic) {
        this.idAcademic = idAcademic;
    }

    public int getIdSubjectGroup() {
        return idSubjectGroup;
    }

    public void setIdSubjectGroup(int idSubjectGroup) {
        this.idSubjectGroup = idSubjectGroup;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
}
