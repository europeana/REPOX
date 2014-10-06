package pt.utl.ist.oai.server.catalog;

import java.util.Comparator;

/**
 * Created to REPOX Project. User: Edmundo Date: 24-01-2012 Time: 12:52
 */
public class OaiSet {

    private String dataSetId;
    private String totalRecordNumber;
    private String metadataPrefix;

    /**
     * Creates a new instance of this class.
     * 
     * @param dataSetId
     * @param totalRecordNumber
     * @param metadataPrefix
     */
    public OaiSet(String dataSetId, String totalRecordNumber, String metadataPrefix) {
        this.dataSetId = dataSetId;
        this.totalRecordNumber = totalRecordNumber;
        this.metadataPrefix = metadataPrefix;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }

    public String getTotalRecordNumber() {
        return totalRecordNumber;
    }

    public void setTotalRecordNumber(String totalRecordNumber) {
        this.totalRecordNumber = totalRecordNumber;
    }

    @SuppressWarnings("rawtypes")
    public static Comparator getComparator() {
        return new AlphabeticComparator();
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    static class AlphabeticComparator implements Comparator {
        public int compare(Object emp1, Object emp2) {
            String emp1Age = ((OaiSet)emp1).getDataSetId();
            String emp2Age = ((OaiSet)emp2).getDataSetId();

            return emp1Age.compareTo(emp2Age);
        }

    }
}
