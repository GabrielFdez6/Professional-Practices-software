package professionalpractice.model.pojo;

public class Coordinator {
    private int idCoordinator;
    private String firstName;
    private String lastNameFather;
    private String lastNameMother;
    private String email;
    private int idUser;

    public Coordinator() {}

    public Coordinator(int idCoordinator, String firstName, String lastNameFather, String lastNameMother, String email, int idUser) {
        this.idCoordinator = idCoordinator;
        this.firstName = firstName;
        this.lastNameFather = lastNameFather;
        this.lastNameMother = lastNameMother;
        this.email = email;
        this.idUser = idUser;
    }

    public int getIdCoordinator() {
        return idCoordinator;
    }

    public void setIdCoordinator(int idCoordinator) {
        this.idCoordinator = idCoordinator;
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

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
}
