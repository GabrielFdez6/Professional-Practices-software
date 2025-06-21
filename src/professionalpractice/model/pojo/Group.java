package professionalpractice.model.pojo;

// Asumo que ya existen los POJOs Academico y ExperienciaEducativa
// import javafxapppracticasprofesionales.modelo.pojo.Academico;
// import javafxapppracticasprofesionales.modelo.pojo.ExperienciaEducativa;

import professionalpractice.model.pojo.Academic;
import professionalpractice.model.pojo.Subject;
import professionalpractice.model.pojo.Term;

public class Group {
    private int idGrupo;
    private String seccion;
    private String bloque;
    private Academic academico;
    private Subject experienciaEducativa;
    private Term periodo;

    public Group() {
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getBloque() {
        return bloque;
    }

    public void setBloque(String bloque) {
        this.bloque = bloque;
    }

    public Academic getAcademico() {
        return academico;
    }

    public void setAcademico(Academic academico) {
        this.academico = academico;
    }

    public Subject getExperienciaEducativa() {
        return experienciaEducativa;
    }

    public void setExperienciaEducativa(Subject experienciaEducativa) {
        this.experienciaEducativa = experienciaEducativa;
    }

    public Term getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Term periodo) {
        this.periodo = periodo;
    }

    @Override
    public String toString() {
        return "Sección " + seccion + " - NRC " + experienciaEducativa.getNrc();
    }
}