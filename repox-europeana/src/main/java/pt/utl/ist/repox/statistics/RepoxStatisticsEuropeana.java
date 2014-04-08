package pt.utl.ist.repox.statistics;

import java.util.Map;

public class RepoxStatisticsEuropeana extends RepoxStatistics {

    private int aggregators;

    public int getAggregators() {
        return aggregators;
    }

    public void setAggregators(int aggregators) {
        this.aggregators = aggregators;
    }

    public RepoxStatisticsEuropeana(int dataSourcesIdExtracted, int dataSourcesIdGenerated, int dataSourcesIdProvided,
                                    int aggregators, int dataProviders, int dataSourcesOai, int dataSourcesZ3950, int dataSourcesDirectoryImporter,
                                    Map<String, MetadataFormatStatistics> dataSourcesMetadataFormats, float recordsAvgDataSource, float recordsAvgDataProvider,
                                    Map<String, Integer> countriesRecords, int recordsTotal) {
		super(dataSourcesIdExtracted, dataSourcesIdGenerated, dataSourcesIdProvided, dataProviders, dataSourcesOai,
                dataSourcesZ3950, dataSourcesDirectoryImporter, dataSourcesMetadataFormats, recordsAvgDataSource, recordsAvgDataProvider,
                countriesRecords, recordsTotal);
        this.aggregators = aggregators;
	}
}
