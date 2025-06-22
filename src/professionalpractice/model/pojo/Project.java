package professionalpractice.model.pojo;

public class Project {
    private int idProject;
    private int idProjectManager;
    private int idLinkedOrganization;
    private int idCoordinator;
    private String name;
    private String department;
    private String description;
    private String methodology;
    private int availability;
    private String linkedOrganizationName;

    public Project() {}

    public Project(int idProject, int idProjectManager, int idLinkedOrganization, int idCoordinator, String name, String department, String description, String methodology, int availability, String linkedOrganizationName) {
        this.idProject = idProject;
        this.idProjectManager = idProjectManager;
        this.idLinkedOrganization = idLinkedOrganization;
        this.idCoordinator = idCoordinator;
        this.name = name;
        this.department = department;
        this.description = description;
        this.methodology = methodology;
        this.availability = availability;
        this.linkedOrganizationName = linkedOrganizationName;
    }

    public int getIdProject() {
        return idProject;
    }

    public void setIdProject(int idProject) {
        this.idProject = idProject;
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

    public int getIdCoordinator() {
        return idCoordinator;
    }

    public void setIdCoordinator(int idCoordinator) {
        this.idCoordinator = idCoordinator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public String getLinkedOrganizationName() {
        return linkedOrganizationName;
    }

    public void setLinkedOrganizationName(String linkedOrganizationName) {
        this.linkedOrganizationName = linkedOrganizationName;
    }

    @Override
    public String toString() {
        return "Project{" +
                "idProject=" + idProject +
                ", name='" + name + '\'' +
                ", idLinkedOrganization=" + idLinkedOrganization +
                ", idProjectManager=" + idProjectManager +
                '}';
    }
}