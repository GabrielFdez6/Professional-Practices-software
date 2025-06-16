package professionalpractice.model.pojo;

import java.util.Date;

public class Term {
    private int idTerm;
    private String name;
    private Date startDate;
    private Date endDate;

    public Term() {}

    public Term(int idTerm, String name, Date startDate, Date endDate) {
        this.idTerm = idTerm;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getIdTerm() {
        return idTerm;
    }

    public void setIdTerm(int idTerm) {
        this.idTerm = idTerm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
