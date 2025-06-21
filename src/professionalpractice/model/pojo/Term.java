package professionalpractice.model.pojo;

import java.util.Date;

public class Term {
    private int idTerm;
    private String name;
    private String startDate;
    private String endDate;

    public Term() {}

    public Term(int idTerm, String name, Date startDate, Date endDate) {
        this.idTerm = idTerm;
        this.name = name;
        this.startDate = String.valueOf(startDate);
        this.endDate = String.valueOf(endDate);
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
