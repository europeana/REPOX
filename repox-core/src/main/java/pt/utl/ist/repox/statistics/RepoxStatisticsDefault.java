package pt.utl.ist.repox.statistics;

import java.util.Map;

public class RepoxStatisticsDefault extends RepoxStatistics {
	public RepoxStatisticsDefault(int dataSourcesIdExtracted, int dataSourcesIdGenerated, int dataSourcesIdProvided,
                                  int dataProviders, int dataSourcesOai, int dataSourcesZ3950, int dataSourcesDirectoryImporter,
                                  Map<String, MetadataFormatStatistics> dataSourcesMetadataFormats, float recordsAvgDataSource, float recordsAvgDataProvider,
                                  Map<String, Integer> countriesRecords, int recordsTotal) {
		super(dataSourcesIdExtracted, dataSourcesIdGenerated, dataSourcesIdProvided, dataProviders, dataSourcesOai,
                dataSourcesZ3950, dataSourcesDirectoryImporter, dataSourcesMetadataFormats, recordsAvgDataSource, recordsAvgDataProvider,
                countriesRecords, recordsTotal);
	}
}
