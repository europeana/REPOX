package pt.utl.ist.statistics;

import java.util.Map;

/**
 */
public class DefaultRepoxStatistics extends RepoxStatistics {
    /**
     * Creates a new instance of this class.
     * @param dataSourcesIdExtracted
     * @param dataSourcesIdGenerated
     * @param dataSourcesIdProvided
     * @param dataProviders
     * @param dataSourcesOai
     * @param dataSourcesZ3950
     * @param dataSourcesDirectoryImporter
     * @param dataSourcesMetadataFormats
     * @param recordsAvgDataSource
     * @param recordsAvgDataProvider
     * @param countriesRecords
     * @param recordsTotal
     */
    public DefaultRepoxStatistics(int dataSourcesIdExtracted, int dataSourcesIdGenerated, int dataSourcesIdProvided, int dataProviders, int dataSourcesOai, int dataSourcesZ3950, int dataSourcesDirectoryImporter, Map<String, MetadataFormatStatistics> dataSourcesMetadataFormats,
                                  float recordsAvgDataSource, float recordsAvgDataProvider, Map<String, Integer> countriesRecords, int recordsTotal) {
        super(dataSourcesIdExtracted, dataSourcesIdGenerated, dataSourcesIdProvided, dataProviders, dataSourcesOai, dataSourcesZ3950, dataSourcesDirectoryImporter, dataSourcesMetadataFormats, recordsAvgDataSource, recordsAvgDataProvider, countriesRecords, recordsTotal);
    }
}
