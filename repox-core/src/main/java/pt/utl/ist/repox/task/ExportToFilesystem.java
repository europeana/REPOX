package pt.utl.ist.repox.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import pt.utl.ist.repox.RunnableStoppable;
import pt.utl.ist.repox.Urn;
import pt.utl.ist.repox.accessPoint.AccessPointsManager;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.oai.OaiListResponse;
import pt.utl.ist.repox.oai.OaiListResponse.OaiItem;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.repox.util.XmlUtil;
import pt.utl.ist.repox.util.ZipUtil;
import pt.utl.ist.util.DomUtil;
import pt.utl.ist.util.InvalidInputException;

/**
 * Exports all the Records from a Data Source to a specified location of the Filesystem
 *
 * @author dreis
 *
 */
public class ExportToFilesystem implements RunnableStoppable {
    private static final Logger log                 = Logger.getLogger(ExportToFilesystem.class);
    private static final int    RECORDS_PER_REQUEST = 250;

    private String              taskId;
    private String              dataSourceId;
    private File                exportDir;
    private boolean             stopExecution       = false;
    private int                 recordsPerFile      = 1;
    private String              metadataExportFormat;
    private boolean             executeProfile;

    @SuppressWarnings("javadoc")
    public String getTaskId() {
        return taskId;
    }

    @SuppressWarnings("javadoc")
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @SuppressWarnings("javadoc")
    public String getDataSourceId() {
        return dataSourceId;
    }

    @SuppressWarnings("javadoc")
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @SuppressWarnings("javadoc")
    public File getExportDir() {
        return exportDir;
    }

    @SuppressWarnings("javadoc")
    public void setExportDir(File exportDir) {
        this.exportDir = exportDir;
    }

    @SuppressWarnings("javadoc")
    public int getRecordsPerFile() {
        return recordsPerFile;
    }

    @SuppressWarnings("javadoc")
    public void setRecordsPerFile(int recordsPerFile) {
        this.recordsPerFile = recordsPerFile;
    }

    @SuppressWarnings("javadoc")
    public boolean isExecuteProfile() {
        return executeProfile;
    }

    @SuppressWarnings("javadoc")
    public void setExecuteProfile(boolean executeProfile) {
        this.executeProfile = executeProfile;
    }

    @SuppressWarnings("javadoc")
    public String getMetadataExportFormat() {
        return metadataExportFormat;
    }

    @SuppressWarnings("javadoc")
    public void setMetadataExportFormat(String metadataExportFormat) {
        this.metadataExportFormat = metadataExportFormat;
    }

    /**
     * Creates a new instance of this class.
     */
    public ExportToFilesystem() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * @param taskId
     * @param dataSourceId
     * @param exportDirPath
     * @param recordsPerFile
     */
    public ExportToFilesystem(String taskId, String dataSourceId, String exportDirPath, String recordsPerFile) {
        this();
        this.taskId = taskId;
        this.dataSourceId = dataSourceId;
        this.exportDir = new File(exportDirPath);
        this.recordsPerFile = (recordsPerFile == null && Integer.valueOf(recordsPerFile) == 0 ? 1 : Integer.valueOf(recordsPerFile));
        this.metadataExportFormat = "";
    }

    /**
     * Creates a new instance of this class.
     * @param taskId
     * @param dataSourceId
     * @param exportDirPath
     * @param recordsPerFile
     * @param metadataExportFormat
     */
    public ExportToFilesystem(String taskId, String dataSourceId, String exportDirPath, String recordsPerFile, String metadataExportFormat) {
        this();
        this.taskId = taskId;
        this.dataSourceId = dataSourceId;
        this.exportDir = new File(exportDirPath);
        this.recordsPerFile = (recordsPerFile == null && Integer.valueOf(recordsPerFile) == 0 ? 1 : Integer.valueOf(recordsPerFile));
        this.metadataExportFormat = metadataExportFormat;
    }

    /**
     * Creates a new instance of this class.
     * @param taskId
     * @param dataSourceId
     * @param exportDirPath
     * @param recordsPerFile
     * @param metadataExportFormat
     * @param executeProfile
     */
    public ExportToFilesystem(String taskId, String dataSourceId, String exportDirPath, String recordsPerFile, String metadataExportFormat, String executeProfile) {
        this();
        this.taskId = taskId;
        this.dataSourceId = dataSourceId;
        this.exportDir = new File(exportDirPath);
        this.recordsPerFile = (recordsPerFile == null && Integer.valueOf(recordsPerFile) == 0 ? 1 : Integer.valueOf(recordsPerFile));
        this.metadataExportFormat = metadataExportFormat;
        this.executeProfile = Boolean.parseBoolean(executeProfile);
    }

    @Override
    public void run() {
        stopExecution = false;

        try {
            if ((exportDir.exists() && !exportDir.isDirectory()) || (!exportDir.exists() && !exportDir.mkdirs())) { throw new IOException("Invalid directory or unable to create directory with path " + exportDir.getAbsolutePath()); }

            AccessPointsManager accessPointsManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager();
            DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId).getDataSource();
            //force updated count before exporting
            int totalRecords = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId(), true).getCount();

            int recordsCounter = 1;
            int batchNumber = 1;
            int requestOffset = 0;

            // remove old XML exported files
            FilenameFilter xmlFileFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".xml");
                }
            };

            File[] oldFiles = exportDir.listFiles(xmlFileFilter);

            for (File oldFile : oldFiles) {
                oldFile.delete();
            }

            Element rootElement = getRootElement(totalRecords);
            XMLWriter xmlWriter = null;

            while (requestOffset < totalRecords) {
                OaiListResponse oaiListResponse = accessPointsManager.getOaiRecordsFromDataSource(dataSource, null, null, requestOffset, RECORDS_PER_REQUEST, false);

                for (OaiItem currentItem : oaiListResponse.getOaiItems()) {
                    if (stopExecution) {
                        DataProvider dataProviderParent = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviderParent(dataSource.getId());
                        log.warn("Received stop signal: exiting export of Data Source " + dataSource.getId() + " from Data Provider " + dataProviderParent.getName() + " to dir: " + exportDir.getAbsolutePath());
                        return;
                    }

                    boolean isCreateFile = isFileToCreate(recordsCounter);
                    boolean isCloseFile = isFileToClose(totalRecords, recordsCounter);

                    Element recordToExport = getRecordToExport(currentItem);

                    try {
                        if (isCreateFile) {
                            xmlWriter = getNewXmlWriter(batchNumber);
                            startXmlWriter(xmlWriter, rootElement);
                        }

                        xmlWriter.write(recordToExport);

                        if (isCloseFile) {
                            endXmlWriter(xmlWriter, rootElement);
                        }
                    } catch (Exception e) {
                        log.error("Error saving records of batch " + batchNumber + " of Data Source " + dataSourceId, e);
                    } finally {
                        if (isCloseFile) {
                            batchNumber++;
                        }

                        recordsCounter++;
                    }
                }

                requestOffset += RECORDS_PER_REQUEST;
            }

            // create a zip file with all files
            File zipFile = new File(exportDir, dataSourceId + "_" + DateFormatUtils.format(new Date(), TimeUtil.LONG_DATE_FORMAT_COMPACT) + ".zip");

            xmlFileFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".xml");
                }
            };

            if (exportDir.listFiles(xmlFileFilter).length > 0) ZipUtil.zipFiles(exportDir.listFiles(xmlFileFilter), zipFile);

            /*
            if(executeProfile){
                String input = "file:///" + exportDir.getAbsolutePath().replace("\\", "/") + "/" + dataSourceId + "-" + URLEncoder.encode(String.valueOf(batchNumber - 1), "UTF-8") + ".xml";
                String output = exportDir.getAbsolutePath().replace("\\", "/") + "/" + dataSourceId + "-" + URLEncoder.encode(String.valueOf(batchNumber - 1), "UTF-8") + ".profile.xml";

                Profile.createProfile4Collection(input, output);
            }
            */
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean isFileToCreate(int recordsCounter) {
        if (recordsPerFile < 0) {
            return recordsCounter == 1;
        } else {
            return ((recordsCounter - 1) % recordsPerFile) == 0;
        }
    }

    private boolean isFileToClose(int totalRecords, int recordsCounter) {
        if (recordsPerFile < 0) {
            return recordsCounter == totalRecords;
        } else {
            return (recordsCounter % recordsPerFile == 0) || (recordsCounter == totalRecords);
        }
    }

    private XMLWriter getNewXmlWriter(int batchNumber) throws FileNotFoundException, UnsupportedEncodingException {
        File currentBatchFile = new File(exportDir, dataSourceId + "-" + URLEncoder.encode(String.valueOf(batchNumber), "UTF-8") + ".xml");
        FileOutputStream outputStream = new FileOutputStream(currentBatchFile);
        return new XMLWriter(outputStream, OutputFormat.createCompactFormat());
    }

    private void startXmlWriter(XMLWriter xmlWriter, Element rootElement) throws SAXException, IOException {
        xmlWriter.startDocument();
        xmlWriter.writeOpen(rootElement);
    }

    private void endXmlWriter(XMLWriter xmlWriter, Element rootElement) throws IOException {
        xmlWriter.writeClose(rootElement);
        xmlWriter.close();
    }

    //<exportedRecords set="" batchsize="" total="">
    private Element getRootElement(int recordCount) {

        Element rootElement = DocumentHelper.createElement("repox:exportedRecords");
        rootElement.addAttribute("set", dataSourceId);
        rootElement.addAttribute("batchsize", String.valueOf(recordsPerFile));
        rootElement.addAttribute("total", String.valueOf(recordCount));
        rootElement.addAttribute("xmlns:repox", "http://repox.ist.utl.pt");

        return rootElement;
    }

    //<record id="" timestamp="" deleted=""><metadata>...
    private Element getRecordToExport(OaiItem currentItem) {
        try {
            Element recordElement = DocumentHelper.createElement("repox:record");
            String identifier = new Urn(currentItem.getSetSpec(), currentItem.getIdentifier()).toString();
            recordElement.addAttribute("id", identifier);
            recordElement.addAttribute("timestamp", currentItem.getDatestamp());

            Element metadataElement = DocumentHelper.createElement("repox:metadata");

            if (currentItem.isDeleted()) {
                recordElement.addAttribute("deleted", "true");
            } else {

                // to allow export to any XML format
                String encodedIdentifier = DomUtil.xmlEncode(identifier);
                String xmlRecordString = (currentItem.getMetadata() != null ? new String(currentItem.getMetadata(), "UTF-8") : "");

                if (metadataExportFormat != null && !metadataExportFormat.isEmpty()) {
                    DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId).getDataSource();

                    Urn urn = new Urn(identifier);
                    Element rootElement = XmlUtil.getRootElement(currentItem.getMetadata());
                    RecordRepox recordRepox = dataSource.getRecordIdPolicy().createRecordRepox(rootElement, urn.getRecordId().toString(), false, currentItem.isDeleted());

                    String oaiRecordHeader = "<header";

                    if (recordRepox.isDeleted()) {
                        oaiRecordHeader += " status=\"deleted\"";
                    }

                    oaiRecordHeader = oaiRecordHeader + "><identifier>" + encodedIdentifier + "</identifier>" + "<datestamp>" + currentItem.getDatestamp() + "</datestamp>" + "<setSpec>" + urn.getDataSourceId() + "</setSpec></header>";

                    xmlRecordString = MetadataTransformation.getTransformedRecord(encodedIdentifier, metadataExportFormat, dataSource, recordRepox.getDom().asXML());
                    if (xmlRecordString.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
                        xmlRecordString = xmlRecordString.substring("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length());
                    }
                }
                Document recordMetadata = DocumentHelper.parseText(xmlRecordString);
                metadataElement.add(recordMetadata.getRootElement().detach());

                /*String xmlRecordString = new String(currentItem.getMetadata(), "UTF-8");
                Document recordMetadata = DocumentHelper.parseText(xmlRecordString);
                metadataElement.add(recordMetadata.getRootElement().detach());*/
            }
            recordElement.add(metadataElement);

            return recordElement;
        } catch (UnsupportedEncodingException e) {
            log.error("Could not get metadata in UTF-8", e);
            return null;
        } catch (DocumentException e) {
            log.error("Could not parse XML from record of item " + currentItem.getIdentifier() + " of Data Source " + dataSourceId, e);
            return null;
        } catch (TransformerException e) {
            log.error("Could not transform the metadata", e);
            return null;
        } catch (IOException e) {
            log.error("IOException error", e);
            return null;
        } catch (InvalidInputException e) {
            log.error("InvalidInputException error", e);
            return null;
        } catch (Exception e) {
            log.error("Exception error", e);
            return null;
        }
    }

    @Override
    public void stop() {
        stopExecution = true;
    }

    /**
     * @param args
     * @throws DocumentException
     * @throws IOException
     */
    public static void main(String[] args) throws DocumentException, IOException {
        DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer("bn_teses").getDataSource();
        ExportToFilesystem exportToFilesystem = new ExportToFilesystem("112911", dataSource.getId(), "f:/lixo", "-1", "false");
        System.out.println(exportToFilesystem.getRecordsPerFile());
        //		if(true) System.exit(0);
        exportToFilesystem.run();
    }
}
