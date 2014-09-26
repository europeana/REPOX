package pt.utl.ist.metadataSchemas;

import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import pt.utl.ist.dataProvider.MessageType;
import pt.utl.ist.util.XmlUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created to Project REPOX User: Edmundo Date: 14-06-2012 Time: 17:05
 */
public class MetadataSchemaManager {
    private static final Logger  log = Logger.getLogger(MetadataSchemaManager.class);

    private List<MetadataSchema> metadataSchemas;
    private File                 metadataSchemasFile;

    /**
     * Creates a new instance of this class.
     * 
     * @param metadataSchemasFile
     */
    public MetadataSchemaManager(File metadataSchemasFile) {
        metadataSchemas = new ArrayList<MetadataSchema>();
        this.metadataSchemasFile = metadataSchemasFile;
        loadMetadataSchemas();
    }

    private void loadMetadataSchemas() {
        if (!metadataSchemasFile.exists()) {
            fillDefaultData();
            return;
        }

        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(metadataSchemasFile);
            List<Element> metadataTransformationElements = document.getRootElement().elements();

            for (Element currentElement : metadataTransformationElements) {
                String shortDesignation = currentElement.elementText("shortDesignation");
                String designation = currentElement.elementText("designation");
                String description = currentElement.elementText("description");
                //                Date creationDate = new Date(currentElement.elementText("creationDate").replace("BST","GMT"));
                String namespace = currentElement.elementText("namespace");
                String notes = currentElement.elementText("notes");
                String sOAIAvailable = currentElement.elementText("bOAIAvailable");
                boolean bOAIAvailable;
                if (sOAIAvailable != null)
                    bOAIAvailable = Boolean.valueOf(sOAIAvailable);
                else
                    bOAIAvailable = true;

                List<Node> versionsNode = currentElement.selectNodes("versions/version");
                List<MetadataSchemaVersion> metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
                for (Node versionNode : versionsNode) {
                    double version = Double.valueOf(versionNode.valueOf("@number"));
                    String xsdLink = versionNode.valueOf("@xsdLink");
                    metadataSchemaVersions.add(new MetadataSchemaVersion(version, xsdLink));
                }

                MetadataSchema metadataSchema = new MetadataSchema(designation, shortDesignation, description, namespace, notes, metadataSchemaVersions);
                metadataSchema.setOAIAvailable(bOAIAvailable);
                metadataSchemas.add(metadataSchema);
            }
        } catch (DocumentException e) {
            log.error("Error loading the metadata schemas file (metadataSchemas.xml).");
        }

    }

    /**
     * @param designation
     * @param shortDesignation
     * @param description
     * @param namespace
     * @param notes
     * @param oldId
     * @param metadataSchemaVersions
     * @param bOAIAvailable
     * @return MessageType
     */
    public MessageType saveMetadataSchema(String designation, String shortDesignation, String description, String namespace, String notes, String oldId, List<MetadataSchemaVersion> metadataSchemaVersions, boolean bOAIAvailable) {
        if (oldId != null && oldId.equals(shortDesignation)) {
            // Do nothing
        } else if (schemaExists(shortDesignation)) return MessageType.ALREADY_EXISTS;

        deleteOldMetadataSchema(oldId);
        // New metadata schema
        //        if(creationDate == null){
        ////            identifier = UUID.randomUUID().toString();
        //            creationDate = new Date();
        //        }
        MetadataSchema metadataSchema = new MetadataSchema(designation, shortDesignation, description, namespace, notes, metadataSchemaVersions);
        metadataSchema.setOAIAvailable(bOAIAvailable);
        metadataSchemas.add(metadataSchema);
        saveMetadataSchemas();

        return MessageType.OK;
    }

    /**
     * @param designation
     * @param shortDesignation
     * @param description
     * @param namespace
     * @param notes
     * @param oldId
     * @param metadataSchemaVersions
     * @param bOAIAvailable
     * @return MessageType
     */
    public MessageType updateMetadataSchema(String designation, String shortDesignation, String description, String namespace, String notes, String oldId, List<MetadataSchemaVersion> metadataSchemaVersions, boolean bOAIAvailable) {
        if (oldId != null && oldId.equals(shortDesignation)) {
            // Do nothing
        } else if (schemaExists(shortDesignation)) return MessageType.ALREADY_EXISTS;

        MetadataSchema metadataSchema = getMetadataSchema(oldId);
        if (designation != null) metadataSchema.setDesignation(designation);
        if (description != null) metadataSchema.setDescription(description);
        if (namespace != null) metadataSchema.setNamespace(namespace);
        if (notes != null) metadataSchema.setNotes(notes);
        if (metadataSchemaVersions != null && metadataSchemaVersions.size() > 0) metadataSchema.setMetadataSchemaVersions(metadataSchemaVersions);

        metadataSchema.setOAIAvailable(bOAIAvailable);
        saveMetadataSchemas();

        return MessageType.OK;
    }

    private void deleteOldMetadataSchema(String oldSchemaId) {
        if (oldSchemaId == null) return;

        for (MetadataSchema metadataSchema : metadataSchemas) {
            if (oldSchemaId.equals(metadataSchema.getShortDesignation())) {
                deleteMetadataSchema(oldSchemaId);
                break;
            }
        }
    }

    /**
     * 
     */
    public synchronized void saveMetadataSchemas() {
        try {
            Document document = DocumentHelper.createDocument();

            Element rootNode = document.addElement("metadataSchemas");

            for (MetadataSchema metadataSchema : metadataSchemas) {
                Element metadataSchemaElement = rootNode.addElement("metadataSchema");
                //                metadataSchemaElement.addElement("id").setText(metadataSchema.getIdentifier());
                metadataSchemaElement.addElement("shortDesignation").setText(metadataSchema.getShortDesignation());
                //                metadataSchemaElement.addElement("creationDate").setText(metadataSchema.getCreationDate().toString());
                metadataSchemaElement.addElement("namespace").setText(metadataSchema.getNamespace());
                metadataSchemaElement.addElement("bOAIAvailable").setText(String.valueOf(metadataSchema.isOAIAvailable()));

                // optional fields
                if (metadataSchema.getDesignation() != null) metadataSchemaElement.addElement("designation").setText(metadataSchema.getDesignation());
                if (metadataSchema.getDescription() != null) metadataSchemaElement.addElement("description").setText(metadataSchema.getDescription());
                if (metadataSchema.getNotes() != null) metadataSchemaElement.addElement("notes").setText(metadataSchema.getNotes());

                Element versionsEl = metadataSchemaElement.addElement("versions");
                for (MetadataSchemaVersion metadataSchemaVersion : metadataSchema.getMetadataSchemaVersions()) {
                    Element metadataVersionElement = versionsEl.addElement("version");
                    metadataVersionElement.addAttribute("number", String.valueOf(metadataSchemaVersion.getVersion()));
                    metadataVersionElement.addAttribute("xsdLink", metadataSchemaVersion.getXsdLink());
                }
            }

            XmlUtil.writePrettyPrint(metadataSchemasFile, document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param metadataSchemaId
     * @return boolean
     */
    public boolean schemaExists(String metadataSchemaId) {
        for (MetadataSchema metadataSchema : metadataSchemas) {
            if (metadataSchemaId.equals(metadataSchema.getShortDesignation())) { return true; }
        }
        return false;
    }

    /**
     * @param metadataSchemaId
     * @param version
     * @return String
     */
    public String getSchemaXSD(String metadataSchemaId, double version) {
        for (MetadataSchema metadataSchema : metadataSchemas) {
            if (metadataSchemaId.equals(metadataSchema.getShortDesignation())) {
                for (MetadataSchemaVersion metadataSchemaVersion : metadataSchema.getMetadataSchemaVersions()) {
                    if (metadataSchemaVersion.getVersion() == version) { return metadataSchemaVersion.getXsdLink(); }
                }
            }
        }
        return null;
    }

    private MetadataSchema getMetadataSchema(String id) {
        for (MetadataSchema metadataSchema : metadataSchemas) {
            if (id.equals(metadataSchema.getShortDesignation())) { return metadataSchema; }
        }
        return null;
    }

    /**
     * @param metadataSchemaId
     * @return boolean
     */
    public boolean deleteMetadataSchema(String metadataSchemaId) {
        for (MetadataSchema metadataSchema : metadataSchemas) {
            if (metadataSchemaId.equals(metadataSchema.getShortDesignation())) {
                metadataSchemas.remove(metadataSchema);
                saveMetadataSchemas();
                return true;
            }
        }
        return false;
    }

    /**
     * @return List of MetadataSchema
     */
    public List<MetadataSchema> getMetadataSchemas() {
        return metadataSchemas;
    }

    /**
     * @param metadataSchemaId
     * @return boolean
     */
    public boolean isMetadataSchemaOaiAvailable(String metadataSchemaId) {
        for (MetadataSchema metadataSchema : metadataSchemas) {
            if (metadataSchemaId.equals(metadataSchema.getShortDesignation())) { return metadataSchema.isOAIAvailable(); }
        }
        return false;
    }

    private void fillDefaultData() {
        List<MetadataSchemaVersion> metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
        metadataSchemaVersions.add(new MetadataSchemaVersion(3.4, "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd"));
        metadataSchemaVersions.add(new MetadataSchemaVersion(3.3, "http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd"));
        saveMetadataSchema(null, "ese", null, "http://www.europeana.eu/schemas/ese/", null, "", metadataSchemaVersions, true);

        metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
        metadataSchemaVersions.add(new MetadataSchemaVersion(1.0, "info:lc/xmlns/marcxchange-v1.xsd"));
        saveMetadataSchema(null, "MarcXchange", null, "info:lc/xmlns/marcxchange-v1", null, "", metadataSchemaVersions, true);

        metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
        metadataSchemaVersions.add(new MetadataSchemaVersion(1.0, "info:lc/xmlns/marcxchange-v1.xsd"));
        saveMetadataSchema(null, "ISO2709", null, "info:lc/xmlns/marcxchange-v1", null, "", metadataSchemaVersions, true);

        metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
        metadataSchemaVersions.add(new MetadataSchemaVersion(3.4, "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd"));
        saveMetadataSchema(null, "tel", null, "http://krait.kb.nl/coop/tel/handbook/telterms.html", null, "", metadataSchemaVersions, true);

        metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
        metadataSchemaVersions.add(new MetadataSchemaVersion(2.0, "http://www.openarchives.org/OAI/2.0/oai_dc.xsd"));
        saveMetadataSchema(null, "oai_dc", null, "http://www.openarchives.org/OAI/2.0/", null, "", metadataSchemaVersions, true);

        metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
        metadataSchemaVersions.add(new MetadataSchemaVersion(2.0, "ncbi-mathml2/mathml2.xsd"));
        saveMetadataSchema(null, "NLM-AI", null, "http://www.w3.org/1998/Math/MathML", null, "", metadataSchemaVersions, true);

        metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
        metadataSchemaVersions.add(new MetadataSchemaVersion(2.0, "ncbi-mathml2/mathml2.xsd"));
        saveMetadataSchema(null, "NLM-Book", null, "http://www.w3.org/1998/Math/MathML", null, "", metadataSchemaVersions, true);

        metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
        metadataSchemaVersions.add(new MetadataSchemaVersion(1.0, "http://www.lido-schema.org/schema/v1.0/lido-v1.0.xsd"));
        saveMetadataSchema(null, "lido", null, "http://www.lido-schema.org", null, "", metadataSchemaVersions, true);

        metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
        metadataSchemaVersions.add(new MetadataSchemaVersion(1.0, "http://www.europeana.eu/schemas/edm/EDM.xsd"));
        saveMetadataSchema(null, "edm", null, "http://www.europeana.eu/schemas/edm/", null, "", metadataSchemaVersions, true);
    }
}
