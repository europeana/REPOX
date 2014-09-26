package pt.utl.ist.statistics;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 */
public interface StatisticsManager {
    /**
     * @param dataProviderIds
     * @return RepoxStatistics
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     */
    RepoxStatistics generateStatistics(List<String> dataProviderIds) throws IOException, DocumentException, SQLException;

    /**
     * @param repoxStatistics
     * @return Document
     * @throws IOException
     */
    Document getStatisticsReport(RepoxStatistics repoxStatistics) throws IOException;
}
