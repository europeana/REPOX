package pt.utl.ist.repox.metadataSchemas;

/**
 * Created to Project REPOX User: Edmundo Date: 25-06-2012 Time: 11:52
 */
public class MetadataSchemaVersion {

    private double version;
    private String xsdLink;

    /**
     * Creates a new instance of this class.
     * 
     * @param version
     * @param xsdLink
     */
    public MetadataSchemaVersion(double version, String xsdLink) {
        this.version = version;
        this.xsdLink = xsdLink;
    }

    @SuppressWarnings("javadoc")
    public double getVersion() {
        return version;
    }

    @SuppressWarnings("javadoc")
    public String getXsdLink() {
        return xsdLink;
    }
}
