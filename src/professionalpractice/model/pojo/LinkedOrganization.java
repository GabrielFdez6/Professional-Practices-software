package professionalpractice.model.pojo;

public class LinkedOrganization {
    private int idLinkedOrganization;
    private String name;
    private String address;
    private String phone;
    private boolean isActive;

    public LinkedOrganization() {}

    public LinkedOrganization(int idLinkedOrganization, String name, String address, String phone, boolean isActive) {
        this.idLinkedOrganization = idLinkedOrganization;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.isActive = isActive;
    }

    public int getIdLinkedOrganization() {
        return idLinkedOrganization;
    }

    public void setIdLinkedOrganization(int idLinkedOrganization) {
        this.idLinkedOrganization = idLinkedOrganization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
