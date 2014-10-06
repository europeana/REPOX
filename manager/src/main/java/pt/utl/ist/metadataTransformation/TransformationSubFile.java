package pt.utl.ist.metadataTransformation;

/**
 * Created to project REPOX. User: Edmundo Date: 03/09/12 Time: 13:34
 */
public class TransformationSubFile {

    private Long   timestamp;
    private String fileName;

    /**
     * Creates a new instance of this class.
     * 
     * @param timestamp
     * @param fileName
     */
    public TransformationSubFile(Long timestamp, String fileName) {
        this.timestamp = timestamp;
        this.fileName = fileName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getFileName() {
        return fileName;
    }
}
