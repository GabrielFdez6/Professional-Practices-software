package professionalpractice.model;

public class SesionUsuario {

    private static SesionUsuario instancia;
    private String rolUsuario;
    private int idUsuario;
    private String username;

    private SesionUsuario() {}

    public static synchronized SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    public static void setInstancia(SesionUsuario instancia) {
        SesionUsuario.setInstancia(instancia);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void cerrarSesion() {
        this.rolUsuario = null;
        this.username = null;
    }
}