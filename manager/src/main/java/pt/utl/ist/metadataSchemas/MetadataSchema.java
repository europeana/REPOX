package pt.utl.ist.metadataSchemas;

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

    public String getDesignation() {
        return designation;
    }

    public String getShortDesignation() {
        return shortDesignation;
    }

    public String getDescription() {
        return description;
    }

    //    public Date getCreationDate() {
    //        return creationDate;
    //    }

    public String getNamespace() {
        return namespace;
    }

    public String getNotes() {
        return notes;
    }

    public void setOAIAvailable(boolean value) {
        bOAIAvailable = value;
    }

    public boolean isOAIAvailable() {
        return bOAIAvailable;
    }

    public List<MetadataSchemaVersion> getMetadataSchemaVersions() {
        return metadataSchemaVersions;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setShortDesignation(String shortDesignation) {
        this.shortDesignation = shortDesignation;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setbOAIAvailable(boolean bOAIAvailable) {
        this.bOAIAvailable = bOAIAvailable;
    }

    public void setMetadataSchemaVersions(List<MetadataSchemaVersion> metadataSchemaVersions) {
        this.metadataSchemaVersions = metadataSchemaVersions;
    }
}
