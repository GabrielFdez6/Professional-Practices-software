package professionalpractice.model.pojo;

public class ProjectManager {
    private int idProjectManager;
    private int idLinkedOrganization;
    private String firstName;
    private String lastNameFather;
    private String lastNameMother;
    private String position;
    private String email;
    private String phone;

    public ProjectManager() {}

    public ProjectManager(int idProjectManager, int idLinkedOrganization, String firstName, String lastNameFather, String lastNameMother, String position, String email, String phone) {
        this.idProjectManager = idProjectManager;
        this.idLinkedOrganization = idLinkedOrganization;
        this.firstName = firstName;
        this.lastNameFather = lastNameFather;
        this.lastNameMother = lastNameMother;
        this.position = position;
        this.email = email;
        this.phone = phone;
    }

    public int getIdProjectManager() {
        return idProjectManager;
    }

    public void setIdProjectManager(int idProjectManager) {
        this.idProjectManager = idProjectManager;
    }

    public int getIdLinkedOrganization() {
        return idLinkedOrganization;
    }

    public void setIdLinkedOrganization(int idLinkedOrganization) {
        this.idLinkedOrganization = idLinkedOrganization;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public String getFullName() {
        return firstName + " " + lastNameFather + " " + lastNameMother;
    }
}
