package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.FinalDocument;
import professionalpractice.model.pojo.InitialDocument;
import professionalpractice.model.pojo.ReportDocument;
import java.sql.SQLException;

public interface IDocumentDAO {

    int saveInitialDocument(InitialDocument document) throws SQLException;

    int saveReportDocument(ReportDocument document) throws SQLException;

    int saveFinalDocument(FinalDocument document) throws SQLException;

}