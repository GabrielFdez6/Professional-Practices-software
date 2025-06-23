package professionalpractice.model.pojo;

import professionalpractice.model.pojo.Academic;
import professionalpractice.model.pojo.Subject;
import professionalpractice.model.pojo.Term;

public class Group {
    private int idGroup;
    private String section;
    private String block;
    private Academic academic;
    private Subject educationalExperience;
    private Term term;

    public Group() {
    }

    public int getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public Academic getAcademic() {
        return academic;
    }

    public void setAcademic(Academic academic) {
        this.academic = academic;
    }

    public Subject getEducationalExperience() {
        return educationalExperience;
    }

    public void setEducationalExperience(Subject educationalExperience) {
        this.educationalExperience = educationalExperience;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    @Override
    public String toString() {
        return "Sección " + section + " - NRC " + educationalExperience.getNrc();
    }
}