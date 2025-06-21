package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.FinalDocument;
import professionalpractice.model.pojo.InitialDocument;
import professionalpractice.model.pojo.ReportDocument;
import java.sql.SQLException;
import java.util.List;

public interface IDocumentDAO {

    int saveInitialDocument(InitialDocument document) throws SQLException;

    int saveReportDocument(ReportDocument document) throws SQLException;

    int saveFinalDocument(FinalDocument document) throws SQLException;

    List<InitialDocument> getAllInitialDocuments() throws SQLException;

    List<ReportDocument> getAllReportDocuments() throws SQLException;

    List<FinalDocument> getAllFinalDocuments() throws SQLException;

    List<String> getDistinctInitialDocumentNames() throws SQLException;

    List<String> getDistinctReportDocumentNames() throws SQLException;

    List<String> getDistinctFinalDocumentNames() throws SQLException;
}