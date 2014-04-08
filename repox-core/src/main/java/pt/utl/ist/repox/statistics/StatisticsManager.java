package pt.utl.ist.repox.statistics;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface StatisticsManager {

    public RepoxStatistics generateStatistics(List<String> dataProviderIds) throws IOException, DocumentException, SQLException;

    public Document getStatisticsReport(RepoxStatistics repoxStatistics) throws IOException;
}
