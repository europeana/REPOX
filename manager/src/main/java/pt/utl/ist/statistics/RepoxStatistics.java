package pt.utl.ist.statistics;

import java.util.Date;
import java.util.Map;

/**
 */
public abstract class RepoxStatistics {
    private Date                                  generationDate;

    private int                                   dataSourcesIdExtracted;
    private int                                   dataSourcesIdGenerated;
    private int                                   dataSourcesIdProvided;

    private int                                   dataProviders;
    private int                                   dataSourcesOai;
    private int                                   dataSourcesDirectoryImporter;
    private int                                   dataSourcesZ3950;

    //TODO: same as before, but for records with Digital Objects
    //TODO: Data Providers / by protocol (when z39.50 is ready)

    private Map<String, Integer>                  dataSourcesScheduled;        // "daily" | "weekly" | "monthly" -> #
    private Map<String, Map<String, Integer>>     harvestStatesPerPeriod;      //State of harvest -> [period ("daily", ...) -> #]
    private Map<String, Integer>                  harvestStatesLength;         //State of harvest -> length (s)

    private Map<String, MetadataFormatStatistics> dataSourcesMetadataFormats;  // Map of format -> number of DataSources and records

    private float                                 recordsAvgDataSource;
    private float                                 recordsAvgDataProvider;
    private Map<String, Integer>                  countriesRecords;
    private int                                   recordsTotal;

    @SuppressWarnings("javadoc")
    public Date getGenerationDate() {
        return generationDate;
    }

    @SuppressWarnings("javadoc")
    public void setGenerationDate(Date generationDate) {
        this.generationDate = generationDate;
    }

    @SuppressWarnings("javadoc")
    public int getDataSourcesIdExtracted() {
        return dataSourcesIdExtracted;
    }

    @SuppressWarnings("javadoc")
    public void setDataSourcesIdExtracted(int dataSourcesIdExtracted) {
        this.dataSourcesIdExtracted = dataSourcesIdExtracted;
    }

    @SuppressWarnings("javadoc")
    public int getDataSourcesIdGenerated() {
        return dataSourcesIdGenerated;
    }

    @SuppressWarnings("javadoc")
    public void setDataSourcesIdGenerated(int dataSourcesIdGenerated) {
        this.dataSourcesIdGenerated = dataSourcesIdGenerated;
    }

    @SuppressWarnings("javadoc")
    public int getDataSourcesIdProvided() {
        return dataSourcesIdProvided;
    }

    @SuppressWarnings("javadoc")
    public void setDataSourcesIdProvided(int dataSourcesIdProvided) {
        this.dataSourcesIdProvided = dataSourcesIdProvided;
    }

    @SuppressWarnings("javadoc")
    public int getDataProviders() {
        return dataProviders;
    }

    @SuppressWarnings("javadoc")
    public void setDataProviders(int dataProviders) {
        this.dataProviders = dataProviders;
    }

    @SuppressWarnings("javadoc")
    public int getDataSourcesOai() {
        return dataSourcesOai;
    }

    @SuppressWarnings("javadoc")
    public void setDataSourcesOai(int dataSourcesOai) {
        this.dataSourcesOai = dataSourcesOai;
    }

    @SuppressWarnings("javadoc")
    public int getDataSourcesZ3950() {
        return dataSourcesZ3950;
    }

    @SuppressWarnings("javadoc")
    public void setDataSourcesZ3950(int dataSourcesZ3950) {
        this.dataSourcesZ3950 = dataSourcesZ3950;
    }

    @SuppressWarnings("javadoc")
    public int getDataSourcesDirectoryImporter() {
        return dataSourcesDirectoryImporter;
    }

    @SuppressWarnings("javadoc")
    public void setDataSourcesDirectoryImporter(int dataSourcesDirectoryImporter) {
        this.dataSourcesDirectoryImporter = dataSourcesDirectoryImporter;
    }

    @SuppressWarnings("javadoc")
    public Map<String, MetadataFormatStatistics> getDataSourcesMetadataFormats() {
        return dataSourcesMetadataFormats;
    }

    @SuppressWarnings("javadoc")
    public void setDataSourcesMetadataFormats(Map<String, MetadataFormatStatistics> dataSourcesMetadataFormats) {
        this.dataSourcesMetadataFormats = dataSourcesMetadataFormats;
    }

    @SuppressWarnings("javadoc")
    public float getRecordsAvgDataSource() {
        return recordsAvgDataSource;
    }

    @SuppressWarnings("javadoc")
    public void setRecordsAvgDataSource(float recordsAvgDataSource) {
        this.recordsAvgDataSource = recordsAvgDataSource;
    }

    @SuppressWarnings("javadoc")
    public float getRecordsAvgDataProvider() {
        return recordsAvgDataProvider;
    }

    @SuppressWarnings("javadoc")
    public void setRecordsAvgDataProvider(float recordsAvgDataProvider) {
        this.recordsAvgDataProvider = recordsAvgDataProvider;
    }

    @SuppressWarnings("javadoc")
    public Map<String, Integer> getCountriesRecords() {
        return countriesRecords;
    }

    @SuppressWarnings("javadoc")
    public void setCountriesRecords(Map<String, Integer> countriesRecords) {
        this.countriesRecords = countriesRecords;
    }

    @SuppressWarnings("javadoc")
    public int getRecordsTotal() {
        return recordsTotal;
    }

    @SuppressWarnings("javadoc")
    public void setRecordsTotal(int recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    /**
     * Creates a new instance of this class.
     */
    public RepoxStatistics() {
        super();
        generationDate = new Date();
    }

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
    public RepoxStatistics(int dataSourcesIdExtracted, int dataSourcesIdGenerated, int dataSourcesIdProvided, int dataProviders, int dataSourcesOai, int dataSourcesZ3950, int dataSourcesDirectoryImporter, Map<String, MetadataFormatStatistics> dataSourcesMetadataFormats, float recordsAvgDataSource,
                           float recordsAvgDataProvider, Map<String, Integer> countriesRecords, int recordsTotal) {
        this();
        this.dataSourcesIdExtracted = dataSourcesIdExtracted;
        this.dataSourcesIdGenerated = dataSourcesIdGenerated;
        this.dataSourcesIdProvided = dataSourcesIdProvided;
        this.dataProviders = dataProviders;
        this.dataSourcesOai = dataSourcesOai;
        this.dataSourcesZ3950 = dataSourcesZ3950;
        this.dataSourcesDirectoryImporter = dataSourcesDirectoryImporter;
        this.dataSourcesMetadataFormats = dataSourcesMetadataFormats;
        this.recordsAvgDataSource = recordsAvgDataSource;
        this.recordsAvgDataProvider = recordsAvgDataProvider;
        this.countriesRecords = countriesRecords;
        this.recordsTotal = recordsTotal;
    }

}
