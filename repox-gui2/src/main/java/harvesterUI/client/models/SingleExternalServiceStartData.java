package harvesterUI.client.models;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 25/07/12
 * Time: 18:47
 */
public class SingleExternalServiceStartData {

    private String externalServiceId;
    private String dataSetId;

    public SingleExternalServiceStartData(String externalServiceId, String dataSetId) {
        this.externalServiceId = externalServiceId;
        this.dataSetId = dataSetId;
    }

    public String getExternalServiceId() {
        return externalServiceId;
    }

    public String getDataSetId() {
        return dataSetId;
    }
}
