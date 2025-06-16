package professionalpractice.model;

public class SesionUsuario {

    private static SesionUsuario instancia;
    private String rolUsuario;
    private int idUsuario;

    private SesionUsuario() {}

    public static synchronized SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    public static void setInstancia(SesionUsuario instancia) {
        SesionUsuario.instancia = instancia;
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

    public void cerrarSesion() {
        this.rolUsuario = null;
    }
}