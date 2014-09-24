package harvesterUI.shared.statistics;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;
import java.util.Map;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 28-06-2011
 * Time: 13:33
 */
public class RepoxStatisticsUI extends BaseModel implements IsSerializable {

    public RepoxStatisticsUI() {}
    
    public RepoxStatisticsUI(Date generationDate, int dataSourcesIdExtracted, int dataSourcesIdGenerated,
                             int dataSourcesIdProvided,int dataProviders,int dataSourcesTotal, int dataSourcesOai,
                             int dataSourcesZ3950, int dataSourcesDirectoryImporter,
                             Map<String, Integer> dataSourcesMetadataFormats,
                             Map<String, Integer> recordsMetadataFormats,
                             int recordsAvgDataSource, int recordsAvgDataProvider,
                             Map<String, Integer> countriesRecords,
                             String recordsTotal) {
        set("generationDate",generationDate);
        set("dataSourcesIdExtracted",dataSourcesIdExtracted);
        set("dataSourcesIdGenerated",dataSourcesIdGenerated);
        set("dataSourcesIdProvided",dataSourcesIdProvided);
        set("dataProviders",dataProviders);
        set("dataSourcesTotal",dataSourcesTotal);
        set("dataSourcesOai",dataSourcesOai);
        set("dataSourcesZ3950",dataSourcesZ3950);
        set("dataSourcesDirectoryImporter",dataSourcesDirectoryImporter);
        set("dataSourcesMetadataFormats",dataSourcesMetadataFormats);
        set("recordsMetadataFormats",recordsMetadataFormats);
        set("recordsAvgDataSource",recordsAvgDataSource);
        set("recordsAvgDataProvider",recordsAvgDataProvider);
        set("countriesRecords",countriesRecords);
        set("recordsTotal",recordsTotal);
    }

    public Date getGenerationDate() { return (Date) get("generationDate");}
    public Integer getDataSourcesIdExtracted() { return (Integer) get("dataSourcesIdExtracted");}
    public Integer getDataSourcesIdGenerated() { return (Integer) get("dataSourcesIdGenerated");}
    public Integer getDataSourcesIdProvided() { return (Integer) get("dataSourcesIdProvided");}
    public Integer getDataProviders() { return (Integer) get("dataProviders");}
    public Integer getDataSourcesTotal() { return (Integer) get("dataSourcesTotal");}
    public Integer getDataSourcesOai() { return (Integer) get("dataSourcesOai");}
    public Integer getDataSourcesZ3950() { return (Integer) get("dataSourcesZ3950");}
    public Integer getDataSourcesDirectoryImporter() { return (Integer) get("dataSourcesDirectoryImporter");}
    public Map<String, Integer> getDataSourcesMetadataFormats() { return (Map<String, Integer>) get("dataSourcesMetadataFormats");}
    public Map<String, Integer> getRecordsMetadataFormats() { return (Map<String, Integer>) get("recordsMetadataFormats");}
    public Integer getRecordsAvgDataSource() { return (Integer) get("recordsAvgDataSource");}
    public Integer getRecordsAvgDataProvider() { return (Integer) get("recordsAvgDataProvider");}
    public Map<String, Integer> getCountriesRecords() { return (Map<String, Integer>) get("countriesRecords");}
    public String getRecordsTotal() { return (String) get("recordsTotal");}

    // Europeana specific statistic parameters
    public Integer getAggregators() { return (Integer) get("aggregators");}
    public void setAggregators(int aggregators) {set("aggregators",aggregators);}
}
