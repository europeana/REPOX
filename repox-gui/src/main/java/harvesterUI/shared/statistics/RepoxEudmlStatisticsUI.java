package harvesterUI.shared.statistics;

import java.util.Date;
import java.util.Map;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 28-06-2011
 * Time: 13:33
 */
public class RepoxEudmlStatisticsUI extends RepoxStatisticsUI {

    public RepoxEudmlStatisticsUI() {}

    public RepoxEudmlStatisticsUI(Date generationDate, int dataSourcesIdExtracted, int dataSourcesIdGenerated,
                                  int dataSourcesIdProvided, int dataProviders, int dataSourcesOai,
                                  int dataSourcesZ3950, int dataSourcesDirectoryImporter,
                                  int dataSourcesYadda,
                                  Map<String, Integer> dataSourcesMetadataFormats,
                                  Map<String, Integer> recordsMetadataFormats,
                                  int recordsAvgDataSource, int recordsAvgDataProvider,
                                  Map<String, Integer> countriesRecords,
                                  String recordsTotal) {
        super(generationDate, dataSourcesIdExtracted, dataSourcesIdGenerated, dataSourcesIdProvided,
                dataProviders,
                dataSourcesOai + dataSourcesZ3950 + dataSourcesDirectoryImporter + dataSourcesYadda,
                dataSourcesOai, dataSourcesZ3950, dataSourcesDirectoryImporter,
                dataSourcesMetadataFormats, recordsMetadataFormats, recordsAvgDataSource,
                recordsAvgDataProvider, countriesRecords, recordsTotal);

        set("dataSourcesYadda",dataSourcesYadda);
    }

    public Integer getDataSourcesYadda() { return (Integer) get("dataSourcesYadda");}

    // Europeana specific statistic parameters
    public Integer getAggregators() { return (Integer) get("aggregators");}
    public void setAggregators(int aggregators) {set("aggregators",aggregators);}
}
