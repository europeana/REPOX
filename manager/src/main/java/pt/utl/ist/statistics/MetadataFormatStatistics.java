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

    @SuppressWarnings("javadoc")
    public void addCollectionNumber() {
        collectionNumber++;
    }

    @SuppressWarnings("javadoc")
    public void addRecordNumber(int recordNumber) {
        this.recordNumber += recordNumber;
    }

    @SuppressWarnings("javadoc")
    public int getCollectionNumber() {
        return collectionNumber;
    }

    @SuppressWarnings("javadoc")
    public int getRecordNumber() {
        return recordNumber;
    }
}
