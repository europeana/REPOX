package pt.utl.ist.metadataTransformation;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxConfiguration;
import pt.utl.ist.util.XmlUtil;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.SameStylesheetTransformationException;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 */
public class MetadataTransformationManager {
    private static final Logger                       log                  = Logger.getLogger(MetadataTransformationManager.class);

    // Map of sourceMetadataFormat to list of available MetadataTransformations for that source
    private Map<String, List<MetadataTransformation>> metadataTransformations;
    private File                                      xsltDir, xmapDir;
    private File                                      configurationFile;
    private Xslt2StylesheetCache                      xslt2StylesheetCache = new Xslt2StylesheetCache();

    /**
     * Creates a new instance of this class.
     * 
     * @param configurationFile
     * @param xsltDir
     * @throws IOException
     * @throws DocumentException
     */
    public MetadataTransformationManager(File configurationFile, File xsltDir) throws IOException, DocumentException {
        super();
        this.configurationFile = configurationFile;
        this.xsltDir = xsltDir;
        this.xmapDir = new File(xsltDir, DefaultRepoxConfiguration.METADATA_TRANSFORMATIONS_XMAP_SUBDIR);
        loadMetadataTransformations();
    }

    @SuppressWarnings("javadoc")
    public Map<String, List<MetadataTransformation>> getMetadataTransformations() {
        return Collections.unmodifiableMap(metadataTransformations);
    }

    @SuppressWarnings("javadoc")
    protected void setMetadataTransformations(Map<String, List<MetadataTransformation>> metadataTransformations) {
        this.metadataTransformations = metadataTransformations;
    }

    @SuppressWarnings("javadoc")
    public File getXsltDir() {
        return xsltDir;
    }

    @SuppressWarnings("javadoc")
    public File getXmapDir() {
        return xmapDir;
    }

    @SuppressWarnings("javadoc")
    public void setXsltDir(File xsltDir) {
        this.xsltDir = xsltDir;
    }

    @SuppressWarnings("javadoc")
    public File getConfigurationFile() {
        return configurationFile;
    }

    @SuppressWarnings("javadoc")
    public void setConfigurationFile(File configurationFile) {
        this.configurationFile = configurationFile;
    }

    //	public File loadStylesheet(MetadataTransformation metadataTransformation) {
    //		return new File(xsltDir, metadataTransformation.getStylesheet());
    //	}

    /**
     * @param metadataTransformation
     * @return Transformer
     * @throws TransformerException
     */
    public Transformer loadStylesheet(MetadataTransformation metadataTransformation) throws TransformerException {
        File xsltFile = new File(xsltDir, metadataTransformation.getStylesheet());
        if (metadataTransformation.isVersionTwo()) {
            return xslt2StylesheetCache.createTransformer(xsltFile);
        } else {
            TransformerFactory tfactory = new org.apache.xalan.processor.TransformerFactoryImpl();
            Source xsltSource = new StreamSource(xsltFile);
            return tfactory.newTransformer(xsltSource);
        }
    }

    /**
     * @param metadataTransformation
     * @param oldMtdTransId
     * @throws AlreadyExistsException
     * @throws IOException
     * @throws DocumentException
     * @throws SameStylesheetTransformationException
     */
    public synchronized void saveMetadataTransformation(MetadataTransformation metadataTransformation, String oldMtdTransId) throws AlreadyExistsException, IOException, DocumentException, SameStylesheetTransformationException {
        MetadataTransformation savedMtdTrans;
        boolean isNewTransformation = oldMtdTransId.isEmpty();

        if (isNewTransformation)
            checkTransformationExists(metadataTransformation.getId(), metadataTransformation.getSourceFormat(), metadataTransformation.getDestinationFormat(), metadataTransformation.getStylesheet());
        else //delete old transformation files
        if (metadataTransformation.isDeleteOldFiles()) {
            MetadataTransformation old = loadMetadataTransformation(oldMtdTransId);
            //delete xsl?
            String oldFilename = FilenameUtils.removeExtension(old.getStylesheet());
            if (!oldFilename.equals(FilenameUtils.removeExtension(metadataTransformation.getStylesheet()))) TransformationsFileManager.deleteXslFile(oldFilename, xsltDir);
            //delete xmap?
            if (old.isEditable()) TransformationsFileManager.deleteXmapFile(oldFilename, xmapDir);
        }

        if (oldMtdTransId.equals(metadataTransformation.getId()))
            savedMtdTrans = loadMetadataTransformation(metadataTransformation.getId());
        else
            savedMtdTrans = loadMetadataTransformation(oldMtdTransId);

        if (savedMtdTrans == null) {
            // New Transformation
            savedMtdTrans = metadataTransformation;
        } else {
            // Update Transformation
            Boolean bEditable = metadataTransformation.isEditable();
            savedMtdTrans.setId(metadataTransformation.getId());
            savedMtdTrans.setDescription(metadataTransformation.getDescription());
            savedMtdTrans.setDestinationFormat(metadataTransformation.getDestinationFormat());
            savedMtdTrans.setEditable(bEditable);
            savedMtdTrans.setVersionTwo(metadataTransformation.isVersionTwo());
            savedMtdTrans.setDestNamespace(metadataTransformation.getDestNamespace());
            savedMtdTrans.setDestSchema(metadataTransformation.getDestSchema());
            savedMtdTrans.setSourceFormat(metadataTransformation.getSourceFormat());
            savedMtdTrans.setStylesheet(metadataTransformation.getStylesheet());

            savedMtdTrans.setSourceSchema(metadataTransformation.getSourceSchema());
            savedMtdTrans.setMDRCompliant(metadataTransformation.isMDRCompliant());

            deleteMetadataTransformationFromHash(savedMtdTrans.getId());
        }

        //        if(loadMetadataTransformation(metadataTransformation.getId()) != null) {
        //            deleteMetadataTransformation(metadataTransformation.getId());
        //        }

        //		metadataTransformations = loadMetadataTransformations();
        List<MetadataTransformation> transformationsList = metadataTransformations.get(savedMtdTrans.getSourceFormat());
        if (transformationsList == null) {
            transformationsList = new ArrayList<MetadataTransformation>();
        }
        transformationsList.add(savedMtdTrans);
        metadataTransformations.put(savedMtdTrans.getSourceFormat(), transformationsList);
        saveMetadataTransformations();
    }

    /**
     * @param transformId
     * @param xslFileName
     * @param oldTransfId
     * @throws AlreadyExistsException
     * @throws SameStylesheetTransformationException
     * @throws DocumentException
     * @throws IOException
     */
    public synchronized void checkTransformationValidity(String transformId, String xslFileName, String oldTransfId) throws AlreadyExistsException, SameStylesheetTransformationException, DocumentException, IOException { //todo tests?

        MetadataTransformation savedMtdTrans;
        boolean isNewTransformation = oldTransfId.isEmpty();

        if (isNewTransformation)
            for (String sourceFormat : metadataTransformations.keySet()) {

                List<MetadataTransformation> transformationsList = metadataTransformations.get(sourceFormat);
                for (MetadataTransformation metadataTransformation : transformationsList) {

                    Boolean bIdAlreadyExists = metadataTransformation.getId().toLowerCase().equals(transformId.toLowerCase());
                    if (bIdAlreadyExists) throw new AlreadyExistsException(transformId);

                    Boolean bFilenameAlreadyExists = metadataTransformation.getStylesheet().toLowerCase().equals(xslFileName.toLowerCase());
                    if (bFilenameAlreadyExists) throw new SameStylesheetTransformationException(xslFileName);
                }
            }
        else {// Its an edit so we have an old transformation
            savedMtdTrans = loadMetadataTransformation(oldTransfId);
            for (String sourceFormat : metadataTransformations.keySet()) {

                List<MetadataTransformation> transformationsList = metadataTransformations.get(sourceFormat);
                for (MetadataTransformation metadataTransformation : transformationsList) {
                    //First check the id
                    //reject if we find the same id and the new id is different from the old one.
                    Boolean bIdAlreadyExists = metadataTransformation.getId().toLowerCase().equals(transformId.toLowerCase());
                    Boolean bNewIdEqualsOld = transformId.toLowerCase().equals(oldTransfId.toLowerCase());
                    if (bIdAlreadyExists && !bNewIdEqualsOld) throw new AlreadyExistsException(transformId);

                    //Second check the filename
                    //reject if we find the same filena and the new is different form the old one.
                    Boolean bFilenameAlreadyExists = metadataTransformation.getStylesheet().toLowerCase().equals(xslFileName.toLowerCase());
                    Boolean bNewFilenameEqualsOld = xslFileName.toLowerCase().equals(savedMtdTrans.getStylesheet().toLowerCase());
                    if (bFilenameAlreadyExists && !bNewFilenameEqualsOld) throw new SameStylesheetTransformationException(xslFileName);
                }
            }
        }
    }

    private boolean checkTransformationExists(String transformId, String srcFormat, String destFormat, String xslFileName) throws AlreadyExistsException, SameStylesheetTransformationException {
        for (String sourceFormat : metadataTransformations.keySet()) {
            List<MetadataTransformation> transformationsList = metadataTransformations.get(sourceFormat);

            for (MetadataTransformation metadataTransformation : transformationsList) {
                if (metadataTransformation.getId().toLowerCase().equals(transformId.toLowerCase())) throw new AlreadyExistsException(transformId);
                //                if((metadataTransformation.getSourceFormat().toLowerCase().equals(srcFormat.toLowerCase()) && metadataTransformation.getDestinationFormat().toLowerCase().equals(destFormat.toLowerCase())))
                //                    throw new SameSrcAndDestFormatTransformationException(srcFormat + "->" + destFormat);
                if (metadataTransformation.getStylesheet().toLowerCase().equals(xslFileName.toLowerCase())) throw new SameStylesheetTransformationException(xslFileName);
            }
        }
        return false;
    }

    private synchronized boolean deleteMetadataTransformationFromHash(String metadataTransformationId) throws IOException, DocumentException {
        //        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().removeTransformationFromDataSource(metadataTransformationId);

        for (String sourceFormat : metadataTransformations.keySet()) {
            List<MetadataTransformation> transformationsList = metadataTransformations.get(sourceFormat);

            Iterator<MetadataTransformation> transformationIterator = transformationsList.iterator();
            while (transformationIterator.hasNext()) {
                MetadataTransformation metadataTransformation = (MetadataTransformation)transformationIterator.next();
                if (metadataTransformation.getId().equals(metadataTransformationId)) {
                    transformationIterator.remove();
                    metadataTransformations.put(sourceFormat, transformationsList);
                    saveMetadataTransformations();
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param metadataTransformationId
     * @return boolean
     * @throws IOException
     * @throws DocumentException
     */
    public synchronized boolean deleteMetadataTransformation(String metadataTransformationId) throws IOException, DocumentException {
        //		metadataTransformations = loadMetadataTransformations();

        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().removeTransformationFromDataSource(metadataTransformationId);

        for (String sourceFormat : metadataTransformations.keySet()) {
            List<MetadataTransformation> transformationsList = metadataTransformations.get(sourceFormat);

            Iterator<MetadataTransformation> transformationIterator = transformationsList.iterator();
            while (transformationIterator.hasNext()) {
                MetadataTransformation metadataTransformation = (MetadataTransformation)transformationIterator.next();
                if (metadataTransformation.getId().equals(metadataTransformationId)) {
                    // Delete XSLT File
                    String fileName = FilenameUtils.removeExtension(metadataTransformation.getStylesheet());
                    TransformationsFileManager.deleteXslFile(fileName, xsltDir);

                    //Delete XMAP file
                    if (metadataTransformation.isEditable()) TransformationsFileManager.deleteXmapFile(fileName, xmapDir);

                    transformationIterator.remove();
                    metadataTransformations.put(sourceFormat, transformationsList);
                    saveMetadataTransformations();
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param metadataTransformationId
     * @return MetadataTransformation
     * @throws IOException
     * @throws DocumentException
     */
    public synchronized MetadataTransformation loadMetadataTransformation(String metadataTransformationId) throws IOException, DocumentException {
        //		Map<String, List<MetadataTransformation>> metadataTransformations = loadMetadataTransformations();

        for (String sourceFormat : metadataTransformations.keySet()) {
            List<MetadataTransformation> transformationsList = metadataTransformations.get(sourceFormat);

            for (MetadataTransformation metadataTransformation : transformationsList) {
                if (metadataTransformation.getId().equals(metadataTransformationId)) { return metadataTransformation; }
            }
        }

        return null;
    }

    /**
     * @throws IOException
     */
    public synchronized void saveMetadataTransformations() throws IOException {
        Document document = DocumentHelper.createDocument();

        Element rootNode = document.addElement("metadataTransformations");

        Set<String> metadataFormats = metadataTransformations.keySet();
        for (String currentMetadataFormat : metadataFormats) {
            for (MetadataTransformation metadataTransformation : metadataTransformations.get(currentMetadataFormat)) {
                Element metadataTransformationElement = rootNode.addElement("metadataTransformation");
                metadataTransformationElement.addElement("id").setText(metadataTransformation.getId());
                metadataTransformationElement.addElement("description").setText(metadataTransformation.getDescription());
                metadataTransformationElement.addElement("sourceFormat").setText(metadataTransformation.getSourceFormat());
                metadataTransformationElement.addElement("destinationFormat").setText(metadataTransformation.getDestinationFormat());
                metadataTransformationElement.addElement("stylesheet").setText(metadataTransformation.getStylesheet());
                metadataTransformationElement.addElement("sourceSchema").setText(metadataTransformation.getSourceSchema());
                metadataTransformationElement.addElement("destSchema").setText(metadataTransformation.getDestSchema());
                metadataTransformationElement.addElement("destNamespace").setText(metadataTransformation.getDestNamespace());
                metadataTransformationElement.addElement("bMDRCompliant").setText(String.valueOf(metadataTransformation.isMDRCompliant()));
                metadataTransformationElement.addAttribute("editable", String.valueOf(metadataTransformation.isEditable()));
                metadataTransformationElement.addAttribute("version", metadataTransformation.isVersionTwo() ? "2.0" : "1.0");
            }
        }

        XmlUtil.writePrettyPrint(configurationFile, document);
    }

    /**
     * 
     */
    protected synchronized void loadMetadataTransformations() {

        metadataTransformations = new HashMap<String, List<MetadataTransformation>>();
        if (!configurationFile.exists()) { return; }

        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(configurationFile);
            List<Element> metadataTransformationElements = document.getRootElement().elements();

            for (Element currentElement : metadataTransformationElements) {
                boolean bMDRCompliant = Boolean.valueOf(currentElement.elementText("bMDRCompliant"));

                String id, description, sourceFormat, destinationFormat, stylesheet, destNamespace, destSchema, sourceSchema;
                id = currentElement.element("id").getText();
                description = currentElement.elementText("description");
                sourceFormat = currentElement.elementText("sourceFormat");
                destinationFormat = currentElement.elementText("destinationFormat");
                stylesheet = currentElement.elementText("stylesheet");
                if (bMDRCompliant) {
                    destSchema = currentElement.elementText("destSchema");
                    sourceSchema = currentElement.elementText("sourceSchema");
                    destNamespace = currentElement.elementText("destNamespace");
                } else {
                    destSchema = currentElement.elementText("schema");
                    sourceSchema = "N/A";
                    destNamespace = currentElement.elementText("namespace");
                }

                /*
                 * System.out.println("SERVER - ID: "+id);
                 * System.out.println("SERVER - MDRCOMPLIANT (STRING): "
                 * +currentElement.elementText("bMDRCompliant"));
                 * System.out.println("SERVER - MDRCOMPLIANT: "+bMDRCompliant);
                 */

                if (destNamespace == null) {
                    destNamespace = getNamespace(destinationFormat);
                }
                if (destSchema == null) {
                    destSchema = getSchema(destinationFormat);
                }

                boolean editable = Boolean.parseBoolean(currentElement.attributeValue("editable"));
                boolean version2 = currentElement.attributeValue("version") != null && currentElement.attributeValue("version").equals("2.0");
                MetadataTransformation metadataTransformation = new MetadataTransformation(id, description, sourceFormat, destinationFormat, stylesheet, editable, version2, destSchema, destNamespace);
                metadataTransformation.setSourceSchema(sourceSchema);
                metadataTransformation.setMDRCompliant(bMDRCompliant);

                List<MetadataTransformation> transformationsList = metadataTransformations.get(sourceFormat);
                if (transformationsList == null) {
                    transformationsList = new ArrayList<MetadataTransformation>();
                }
                transformationsList.add(metadataTransformation);
                Collections.sort(transformationsList, getTransformationComparator());

                metadataTransformations.put(sourceFormat, transformationsList);
            }
        } catch (DocumentException e) {
            log.error("Error loading the metadata transformations file (metadataTransformations.xml).");
        }
    }

    /**
     * @param transformationId
     * @return File
     * @throws IOException
     * @throws DocumentException
     */
    public File getXsltFile(String transformationId) throws IOException, DocumentException {
        MetadataTransformation metadataTransformation = loadMetadataTransformation(transformationId);
        String xsltFilename = ((metadataTransformation != null && metadataTransformation.getStylesheet() != null) ? metadataTransformation.getStylesheet() : transformationId + ".xsl");
        return new File(xsltDir, xsltFilename);
    }

    /**
     * @return TransformationComparator
     */
    public TransformationComparator getTransformationComparator() {
        return new TransformationComparator();
    }

    /**
     * @param metadataFormat
     * @return String of the schema link
     */
    public static String getSchema(String metadataFormat) {
        if (metadataFormat.equalsIgnoreCase("ese")) {
            return "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd";
        } else if (metadataFormat.equalsIgnoreCase("MarcXchange") || (metadataFormat.equalsIgnoreCase("ISO2709"))) {
            return "info:lc/xmlns/marcxchange-v1.xsd";
        } else if (metadataFormat.equalsIgnoreCase("tel")) {
            return "http://krait.kb.nl/coop/tel/handbook/telterms.html";
        } else if (metadataFormat.equalsIgnoreCase("oai_dc")) {
            return "http://www.openarchives.org/OAI/2.0/oai_dc.xsd";
        } else if (metadataFormat.equalsIgnoreCase("NLM-AI") || metadataFormat.equalsIgnoreCase("NLM-Book")) {
            return "ncbi-mathml2/mathml2.xsd";
        } else if (metadataFormat.equalsIgnoreCase("lido")) {
            return "http://www.lido-schema.org/schema/v1.0/lido-v1.0.xsd";
        } else if (metadataFormat.equalsIgnoreCase("edm")) { return "http://www.europeana.eu/schemas/edm/EDM.xsd"; }
        return "";
    }

    /**
     * @param metadataFormat
     * @return String of the namespace link
     */
    public static String getNamespace(String metadataFormat) {
        if (metadataFormat.equalsIgnoreCase("ese")) {
            return "http://www.europeana.eu/schemas/ese/";
        } else if (metadataFormat.equalsIgnoreCase("MarcXchange") || (metadataFormat.equalsIgnoreCase("ISO2709"))) {
            return "info:lc/xmlns/marcxchange-v1";
        } else if (metadataFormat.equalsIgnoreCase("tel")) {
            return "http://krait.kb.nl/coop/tel/handbook/telterms.html";
        } else if (metadataFormat.equalsIgnoreCase("oai_dc")) {
            return "http://www.openarchives.org/OAI/2.0/";
        } else if (metadataFormat.equalsIgnoreCase("NLM-AI") || metadataFormat.equalsIgnoreCase("NLM-Book")) {
            return "http://www.w3.org/1998/Math/MathML";
        } else if (metadataFormat.equalsIgnoreCase("lido")) {
            return "http://www.lido-schema.org";
        } else if (metadataFormat.equalsIgnoreCase("edm")) { return "http://www.europeana.eu/schemas/edm/"; }
        return "";
    }

    /**
     * @param args
     * @throws IOException
     * @throws DocumentException
     */
    public static void main(String[] args) throws IOException, DocumentException {
        MetadataTransformationManager manager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager();
        Map<String, List<MetadataTransformation>> transformationsMap = manager.getMetadataTransformations();
        List<MetadataTransformation> transformationsList = new ArrayList<MetadataTransformation>();
        transformationsList.add(new MetadataTransformation("marc212tel", "", MetadataFormat.MarcXchange.toString(), MetadataFormat.tel.toString(), "marc212tel.xsl", false, false, "info:lc/xmlns/marcxchange-v1.xsd", "info:lc/xmlns/marcxchange-v1"));
        transformationsList.add(new MetadataTransformation("unimarc2tel", "", MetadataFormat.MarcXchange.toString(), MetadataFormat.tel.toString(), "unimarc2tel.xsl", false, false, "http://krait.kb.nl/coop/tel/handbook/telterms.html", "http://krait.kb.nl/coop/tel/handbook/telterms.html"));
        transformationsList.add(new MetadataTransformation("unimarc2tel-notbn", "", MetadataFormat.MarcXchange.toString(), MetadataFormat.tel.toString(), "unimarc2tel-notbn.xsl", false, false, "http://krait.kb.nl/coop/tel/handbook/telterms.html", "http://krait.kb.nl/coop/tel/handbook/telterms.html"));
        transformationsMap.put(MetadataFormat.MarcXchange.toString(), transformationsList);
        manager.setMetadataTransformations(transformationsMap);
        manager.saveMetadataTransformations();
    }
}

class TransformationComparator implements Comparator<MetadataTransformation> {
    public int compare(MetadataTransformation mT1, MetadataTransformation mT2) {
        return mT1.getId().compareTo(mT2.getId());
    }
}