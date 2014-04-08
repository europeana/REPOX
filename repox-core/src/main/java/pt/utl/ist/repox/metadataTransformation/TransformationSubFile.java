package pt.utl.ist.repox.metadataTransformation;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 03/09/12
 * Time: 13:34
 */
public class TransformationSubFile {

    private Long timestamp;
    private String fileName;

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
