package pt.utl.ist.repox.metadataSchemas;

import java.util.List;

/**
 * Created to Project REPOX User: Edmundo Date: 14-06-2012 Time: 16:58
 */
public class MetadataSchema {

    private String                      designation;
    private String                      shortDesignation;
    private String                      description;
    //    private Date creationDate;
    private String                      namespace;
    private String                      notes;
    private boolean                     bOAIAvailable;

    private List<MetadataSchemaVersion> metadataSchemaVersions;

    /**
     * Creates a new instance of this class.
     * 
     * @param designation
     * @param shortDesignation
     * @param description
     * @param namespace
     * @param notes
     * @param metadataSchemaVersions
     */
    public MetadataSchema(String designation, String shortDesignation, String description, String namespace, String notes, List<MetadataSchemaVersion> metadataSchemaVersions) {
        this.designation = designation;
        this.shortDesignation = shortDesignation;
        this.description = description;
        //        this.creationDate = creationDate;
        this.namespace = namespace;
        this.notes = notes;
        this.metadataSchemaVersions = metadataSchemaVersions;
    }

    @SuppressWarnings("javadoc")
    public String getDesignation() {
        return designation;
    }

    @SuppressWarnings("javadoc")
    public String getShortDesignation() {
        return shortDesignation;
    }

    @SuppressWarnings("javadoc")
    public String getDescription() {
        return description;
    }

    //    public Date getCreationDate() {
    //        return creationDate;
    //    }

    @SuppressWarnings("javadoc")
    public String getNamespace() {
        return namespace;
    }

    @SuppressWarnings("javadoc")
    public String getNotes() {
        return notes;
    }

    @SuppressWarnings("javadoc")
    public void setOAIAvailable(boolean value) {
        bOAIAvailable = value;
    }

    @SuppressWarnings("javadoc")
    public boolean isOAIAvailable() {
        return bOAIAvailable;
    }

    @SuppressWarnings("javadoc")
    public List<MetadataSchemaVersion> getMetadataSchemaVersions() {
        return metadataSchemaVersions;
    }

    @SuppressWarnings("javadoc")
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @SuppressWarnings("javadoc")
    public void setShortDesignation(String shortDesignation) {
        this.shortDesignation = shortDesignation;
    }

    @SuppressWarnings("javadoc")
    public void setDescription(String description) {
        this.description = description;
    }

    @SuppressWarnings("javadoc")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @SuppressWarnings("javadoc")
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @SuppressWarnings("javadoc")
    public void setbOAIAvailable(boolean bOAIAvailable) {
        this.bOAIAvailable = bOAIAvailable;
    }

    @SuppressWarnings("javadoc")
    public void setMetadataSchemaVersions(List<MetadataSchemaVersion> metadataSchemaVersions) {
        this.metadataSchemaVersions = metadataSchemaVersions;
    }
}
