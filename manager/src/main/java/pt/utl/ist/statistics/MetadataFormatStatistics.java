package pt.utl.ist.statistics;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 08-01-2013
 * Time: 14:54
 */
public class MetadataFormatStatistics {

    private int collectionNumber;
    private int recordNumber;

    /**
     * Creates a new instance of this class.
     * @param collectionNumber
     * @param recordNumber
     */
    public MetadataFormatStatistics(int collectionNumber, int recordNumber) {
        this.collectionNumber = collectionNumber;
        this.recordNumber = recordNumber;
    }

    public void addCollectionNumber() {
        collectionNumber++;
    }

    public void addRecordNumber(int recordNumber) {
        this.recordNumber += recordNumber;
    }

    public int getCollectionNumber() {
        return collectionNumber;
    }

    public int getRecordNumber() {
        return recordNumber;
    }
}
