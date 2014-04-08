package pt.utl.ist.repox.statistics;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 08-01-2013
 * Time: 14:54
 */
public class MetadataFormatStatistics {

    private int collectionNumber;
    private int recordNumber;

    public MetadataFormatStatistics(int collectionNumber, int recordNumber) {
        this.collectionNumber = collectionNumber;
        this.recordNumber = recordNumber;
    }

    public void addCollectionNumber(){
        collectionNumber++;
    }

    public void addRecordNumber(int recordNumber){
        this.recordNumber += recordNumber;
    }

    public int getCollectionNumber() {
        return collectionNumber;
    }

    public int getRecordNumber() {
        return recordNumber;
    }
}
