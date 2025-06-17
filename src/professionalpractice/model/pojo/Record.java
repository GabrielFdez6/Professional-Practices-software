package professionalpractice.model.pojo;

public class Record {
        private int idRecord;
        private int idStudent;
        private int idSubjectGroup;
        private int hoursCount;
        private String reportPath;
        private String presentationPath;
        private int idTerm;

        public Record() {}

        public int getIdRecord() {
            return idRecord;
        }

        public void setIdRecord(int idRecord) {
            this.idRecord = idRecord;
        }

        public int getIdStudent() {
            return idStudent;
        }

        public void setIdStudent(int idStudent) {
            this.idStudent = idStudent;
        }

        public int getIdSubjectGroup() {
            return idSubjectGroup;
        }

        public void setIdSubjectGroup(int idSubjectGroup) {
            this.idSubjectGroup = idSubjectGroup;
        }

        public int getHoursCount() {
            return hoursCount;
        }

        public void setHoursCount(int hoursCount) {
            this.hoursCount = hoursCount;
        }

        public String getReportPath() {
            return reportPath;
        }

        public void setReportPath(String reportPath) {
            this.reportPath = reportPath;
        }

        public String getPresentationPath() {
            return presentationPath;
        }

        public void setPresentationPath(String presentationPath) {
            this.presentationPath = presentationPath;
        }

        public int getIdTerm() {
            return idTerm;
        }

        public void setIdTerm(int idTerm) {
            this.idTerm = idTerm;
        }
}
