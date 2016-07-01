package pt.utl.ist.oai;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.oclc.oai.harvester.verb.ListRecords;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.util.FileUtil;
import pt.utl.ist.util.RunnableStoppable;
import pt.utl.ist.util.StringUtil;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 */
public class OaiHarvester implements RunnableStoppable {
    private static final Logger log                            = Logger.getLogger(OaiHarvester.class);
    private static final int    SIZE_HTTP_PROTOCOL             = 7;                                   // http://
    private static final int    MAX_OAI_VERB_RETRIES           = 3;
    private static final String SERVERS_FILENAME               = "servers.txt";                       //tab separated file with the servers
    private static final String OAI_COMPLETE_DATE_FORMAT       = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String OAI_DATE_FORMAT                = "yyyy-MM-dd";
    public static final String  REQUEST_FILENAME_START         = "recordsRequest";
    private static final short MAX_CONCURRENT_FILES_IN_SYSTEM = 10;

    private boolean             stopExecution                  = false;
    private boolean             lastResumptionEmptyList        = false;
    private String              sourceUrl;
    private String              sourceSet;
    //	  YYYY = four-digit year ; MM = two-digit month (01=January, etc.) ; DD = two-digit day of month (01 through 31)
    private String              fromDateString;                                                       //Date format: YYYY-MM-DD
    private String              untilDateString;                                                      //Date format: YYYY-MM-DD
    private ResponseTransformer responseTransformer;
    private String              metadataFormat;
    private File                logFile;
    private String              outputDirname;
    private int                 maxRecord4Sample               = -1;                                  // -1 ingest all
    private int                 numberOfRecords2Harvest        = -1;                                  // -1 value not calculated yet; 0 -> unknown info
    private int                 numberOfRecordsPerResponse     = -1;                                  // -1 value not calculated yet; 0 -> unknown info
    private ArrayList<Long>     statisticsHarvest              = new ArrayList<Long>();

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceSet() {
        return sourceSet;
    }

    public void setSourceSet(String sourceSet) {
        this.sourceSet = sourceSet;
    }

    public String getFromDateString() {
        return fromDateString;
    }

    public void setFromDateString(String fromDateString) {
        this.fromDateString = fromDateString;
    }

    public String getUntilDateString() {
        return untilDateString;
    }

    public void setUntilDateString(String untilDateString) {
        this.untilDateString = untilDateString;
    }

    public ResponseTransformer getResponseTransformer() {
        return responseTransformer;
    }

    public void setResponseTransformer(ResponseTransformer responseTransformer) {
        this.responseTransformer = responseTransformer;
    }

    public String getMetadataFormat() {
        return metadataFormat;
    }

    public void setMetadataFormat(String metadataFormat) {
        this.metadataFormat = metadataFormat;
    }

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    public ArrayList<Long> getStatisticsHarvest() {
        return statisticsHarvest;
    }

    public void setStatisticsHarvest(ArrayList<Long> statisticsHarvest) {
        this.statisticsHarvest = statisticsHarvest;
    }

    /**
     * Creates a OAI-PMH Harvester that will harvest a pair of URL/set for
     * records that where changed between fromDate and untilDate, if defined.
     * The resulting requests are stored in outputBaseDir.
     * 
     * @param sourceUrl
     *            OAI-PMH source URL
     * @param sourceSet
     *            OAI-PMH set name
     * @param fromDate
     *            only request Records changed after this date, if null is
     *            ignored
     * @param untilDate
     *            only request Records changed before this date, if null is
     *            ignored
     * @param metadataFormat
     * @param logFile
     * @param maxRecord4Sample
     * @throws javax.xml.transform.TransformerConfigurationException
     */
    public OaiHarvester(String sourceUrl, String sourceSet, Date fromDate, Date untilDate, String metadataFormat, File logFile, int maxRecord4Sample) throws TransformerConfigurationException {
        super();

        SimpleDateFormat format = new SimpleDateFormat(OAI_DATE_FORMAT);
        //SimpleDateFormat format = new SimpleDateFormat(OAI_COMPLETE_DATE_FORMAT);
        if (fromDate != null) {
            fromDateString = format.format(fromDate);
        }
        if (untilDate != null) {
            untilDateString = format.format(untilDate);
        }
        this.sourceUrl = sourceUrl;
        this.sourceSet = sourceSet;
        this.responseTransformer = new ResponseTransformer();
        this.metadataFormat = metadataFormat;
        this.logFile = logFile;
        this.maxRecord4Sample = maxRecord4Sample;
        this.outputDirname = getOutputDirPath(sourceUrl, sourceSet);
    }

    /**
     * @param url
     * @param set
     * @return String og the output directory path
     */
    public static String getOutputDirPath(String url, String set) {
        String oaiRequestPath = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getOaiRequestPath();

        int endIndex = (url.indexOf("/", SIZE_HTTP_PROTOCOL) > 0 ? url.indexOf("/", SIZE_HTTP_PROTOCOL) : url.length());
        String setName = (set != null ? set : "ALL");
        String outputDirString = oaiRequestPath + File.separator + FileUtil.sanitizeToValidFilename(url.substring(SIZE_HTTP_PROTOCOL, endIndex)) + "-" + FileUtil.sanitizeToValidFilename(setName);

        return outputDirString;
    }

    @Override
    public void stop() {
        stopExecution = true;
    }

    @Override
    public void run() {
        stopExecution = false;

        int ingestionRecords = 0;

        try {
            if (logFile == null || (!logFile.exists() && !logFile.createNewFile())) {
                throw new IOException("Unable to create log file: " + logFile.getAbsolutePath());
            }

            StringUtil.simpleLog("Starting OAI Harvest URL: " + sourceUrl + " - Set:" + sourceSet, this.getClass(), logFile);

            String resumptionTokenPath = outputDirname + "/resumptionToken.txt";

            if (isHarvestFinished()) {
                StringUtil.simpleLog("Harvest finished, skipping source", this.getClass(), logFile);
                return;
            }

            File outputDir = new File(outputDirname);
            outputDir.mkdir();

            int currentRequest;
            ListRecords listRecords;
            File resumptionTokenFile = new File(resumptionTokenPath);
            String resumptionToken = null;
            if (resumptionTokenFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(resumptionTokenFile));
                currentRequest = Integer.parseInt(reader.readLine());
                resumptionToken = reader.readLine();
                reader.close();

                StringUtil.simpleLog("Using previous resumption token: " + resumptionToken, this.getClass(), logFile);

                listRecords = getListRecordsWithRetries(resumptionToken, MAX_OAI_VERB_RETRIES);
            } else {
                currentRequest = 1;
                StringUtil.simpleLog(this.toString(), this.getClass(), logFile);
                listRecords = getListRecordsWithRetries(MAX_OAI_VERB_RETRIES);

                numberOfRecords2Harvest = listRecords.getRecordsNumber();
                numberOfRecordsPerResponse = listRecords.getRecordsNumberPerResponse();
            }

            int batchCounter = listRecords.getDocument().getElementsByTagName("record").getLength();
            if(batchCounter == 0)
              batchCounter = listRecords.getDocument().getElementsByTagNameNS("http://www.openarchives.org/OAI/2.0/", "record").getLength();
            if(batchCounter == 0)
              StringUtil.simpleLog("Cannot read response, namespace of oai record is not correct!!", this.getClass(), logFile);
            boolean isResponseEmpty =  (batchCounter == 0 ? true : false);
            ingestionRecords += batchCounter;
            
            if (listRecords == null || listRecords.isResultEmpty() || isResponseEmpty) {
                if(currentRequest == 1)
                    StringUtil.simpleLog("Response was an empty list in operation ListRecords (may be invalid set or does not exist new records from the last ingest)", this.getClass(), logFile);
                else if(resumptionToken != null && !resumptionToken.equals(""))
                        StringUtil.simpleLog("Response with resumptionToken: " + resumptionToken + " was an empty list in operation ListRecords " +
                                "(may be invalid set or does not exist new records from the last ingest)", this.getClass(), logFile);
              lastResumptionEmptyList = true;
            }

            while (listRecords != null && !listRecords.isResultEmpty() && !isResponseEmpty) {
                //Block when a number of files in system, continue when there is space again
                while (getRequestFile(currentRequest - MAX_CONCURRENT_FILES_IN_SYSTEM).exists()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) { /* safe to ignore */
                    }
                }

                if (stopExecution) {
                    StringUtil.simpleLog("Stop signal received. Exiting harvest.", this.getClass(), logFile);
                    return;
                }

                long startTime = (new Date()).getTime();

                NodeList errors = listRecords.getErrors();
                if (errors != null && errors.getLength() > 0) {
                    processErrors(errors, logFile, listRecords);
                    if (isBadResumptionToken(errors)) { // bad resumptionToken, restarting
                        listRecords = getListRecordsWithRetries(MAX_OAI_VERB_RETRIES);
                    } else {
                        break;
                    }
                }

                writeRequest(currentRequest, listRecords);

                resumptionToken = listRecords.getResumptionToken();

                if (resumptionToken == null || resumptionToken.length() == 0) {
                    StringUtil.simpleLog("Harvest finished - number of requests: " + currentRequest, this.getClass(), logFile);
                    OutputStream harvestFinishedOutputStream = new FileOutputStream(getHarvestFinishedFile());
                    harvestFinishedOutputStream.write(("Harvest Finished.").getBytes("UTF-8"));
                    harvestFinishedOutputStream.close();
                    listRecords = null;
                }
                else {
                    StringUtil.simpleLog("ResumptionToken: " + resumptionToken, this.getClass(), logFile);

                    OutputStream resumptionOutputStream = new FileOutputStream(resumptionTokenPath);
                    resumptionOutputStream.write((currentRequest + "\n").getBytes("UTF-8"));
                    resumptionOutputStream.write(resumptionToken.getBytes("UTF-8"));
                    resumptionOutputStream.close();

                    listRecords = getListRecordsWithRetries(resumptionToken, MAX_OAI_VERB_RETRIES);
                    batchCounter = listRecords.getDocument().getElementsByTagName("record").getLength();
                    if(batchCounter == 0)
                      batchCounter = listRecords.getDocument().getElementsByTagNameNS("http://www.openarchives.org/OAI/2.0/", "record").getLength();
                    isResponseEmpty = (batchCounter == 0 ? true : false);
                    ingestionRecords += batchCounter;

                }
                
                if (listRecords == null || listRecords.isResultEmpty() || isResponseEmpty) {
                    if(currentRequest == 1)
                        StringUtil.simpleLog("Response was an empty list in operation ListRecords (may be invalid set or does not exist new records from the last ingest)", this.getClass(), logFile);
                    else if(resumptionToken != null && !resumptionToken.equals(""))
                        StringUtil.simpleLog("Response with resumptionToken: " + resumptionToken + " was an empty list in operation ListRecords " +
                                "(may be invalid set or does not exist new records from the last ingest)", this.getClass(), logFile);
                  lastResumptionEmptyList = true;
                }

                long totalTime = ((new Date()).getTime() - startTime) / 1000;

                if (totalTime != 0) {
                    StringUtil.simpleLog("End of request - time: " + totalTime + "s", this.getClass(), logFile);
                    statisticsHarvest.add(totalTime);
                }
                currentRequest++;

                if (maxRecord4Sample != -1 && maxRecord4Sample < ingestionRecords) {
                    StringUtil.simpleLog("Stop signal received. Sample set: max records number.", this.getClass(), logFile);
                    OutputStream harvestFinishedOutputStream = new FileOutputStream(getHarvestFinishedFile());
                    harvestFinishedOutputStream.write(("Harvest Finished.").getBytes("UTF-8"));
                    harvestFinishedOutputStream.close();
                    return;
                }
            }
            StringUtil.simpleLog("Finished OAI Harvest URL: " + sourceUrl + " - Set:" + sourceSet, this.getClass(), logFile);
        } catch (SAXParseException e) {
            StringUtil.simpleLog("Harvest ABORTED: SAXParseException.", e, this.getClass(), logFile);
            log.error("Error harvesting: SAXParseException.", e);
        } catch (Exception e) {
            StringUtil.simpleLog("Harvest ABORTED: " + e.getMessage(), e, this.getClass(), logFile);
            log.error("Error harvesting: " + e.getMessage(), e);
        }
    }

    public boolean isHarvestFinished() {
        return getHarvestFinishedFile().exists();
    }
    
    public boolean isLastResumptionWithEmptyList(){
      return lastResumptionEmptyList;
    }

    public File getHarvestFinishedFile() {
        File harvestFinishedFile = new File(outputDirname + "/harvestFinished.txt");

        return harvestFinishedFile;
    }

    private ListRecords getListRecordsWithRetries(String resumptionToken, int retries) throws IOException, ParserConfigurationException, SAXException, TransformerException, InterruptedException {
        try {
            return new ListRecords(sourceUrl, resumptionToken);
        } catch (FileNotFoundException e) { //This is the error returned by a 404
            StringUtil.simpleLog("Error 404 harvesting " + sourceUrl + " - " + e.getMessage(), e, this.getClass(), logFile);
            if (retries > 0) {
                int currentRetry = MAX_OAI_VERB_RETRIES - retries + 1;
                long sleepTime = currentRetry * 10 * 1000;
                StringUtil.simpleLog("Retrying ListRecords - RETRY " + currentRetry + "/" + MAX_OAI_VERB_RETRIES + " sleeping for " + sleepTime + " ms", this.getClass(), logFile);
                Thread.sleep(sleepTime);
                return getListRecordsWithRetries(resumptionToken, retries - 1);
            } else {
                StringUtil.simpleLog("Harvest ABORTED: exceeded " + MAX_OAI_VERB_RETRIES + " retries", this.getClass(), logFile);
                throw e;
            }
        }
    }

    private ListRecords getListRecordsWithRetries(int retries) throws IOException, ParserConfigurationException, SAXException, TransformerException, InterruptedException {
        try {
            ListRecords listRecords = new ListRecords(sourceUrl, fromDateString, untilDateString, sourceSet, metadataFormat);
            return listRecords;
        } catch (FileNotFoundException e) { //This is the error returned by a 404
            StringUtil.simpleLog("Error 404 harvesting " + sourceUrl + " - " + e.getMessage(), e, this.getClass(), logFile);
            if (retries > 0) {
                int currentRetry = MAX_OAI_VERB_RETRIES - retries + 1;
                long sleepTime = currentRetry * 10 * 1000;
                StringUtil.simpleLog("Retrying ListRecords - RETRY " + currentRetry + "/" + MAX_OAI_VERB_RETRIES + " sleeping for " + sleepTime + " ms", this.getClass(), logFile);
                Thread.sleep(sleepTime);
                return getListRecordsWithRetries(retries - 1);
            } else {
                StringUtil.simpleLog("Harvest ABORTED: exceeded " + MAX_OAI_VERB_RETRIES + " retries", this.getClass(), logFile);
                throw e;
            }
        }
    }

    private void writeRequest(int currentRequest, ListRecords listRecords) throws IOException {

        /*
         * FileOutputStream outFile = null;
         * 
         * try{ outFile = new FileOutputStream("c:\\teste.xml"); OutputFormat
         * format = new OutputFormat("XML","UTF-8", true);
         * org.apache.xml.serialize.XMLSerializer output = new
         * org.apache.xml.serialize.XMLSerializer(outFile, format);
         * output.serialize(listRecords.getDocument()); } catch(Exception e){ }
         * finally { try { outFile.close(); } catch(Exception e){ } }
         */

        // System.out.println("listRecords = " + listRecords.toString());

        File outputOAIResponseFile = getRequestFile(currentRequest);
        OutputStream outputStream = new FileOutputStream(outputOAIResponseFile);
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        try {
            //writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.write(listRecords.toString());
            writer.write("\n");
        } finally {
            writer.close();
        }
    }

    public File getRequestFile(int request) {
        return new File(outputDirname + "/" + REQUEST_FILENAME_START + "-" + request + ".xml");
    }

    public File getRequestFileNoRecords() {
        return new File(outputDirname + "/" + REQUEST_FILENAME_START + "--1.xml");
    }

    /*
     * public String readErrorCode(){ SAXReader reader = new SAXReader(); try {
     * Document xmlSource = reader.read(new File(outputDirname + "/" +
     * REQUEST_FILENAME_START + "--1.xml"));
     * 
     * if(xmlSource.getRootElement().element("error") != null){ return
     * xmlSource.getRootElement().element("error").getText(); } } catch
     * (DocumentException e) { return null; } return null; }
     */

    private void processErrors(NodeList errors, File logFile, ListRecords listRecords) throws IOException {
        writeRequest(-1, listRecords); // Write the error as identifier -1 to be read later
        StringUtil.simpleLog("Found errors in operation ListRecords, recorded to file " + getRequestFile(-1).getAbsolutePath(), this.getClass(), logFile);
        int length = errors.getLength();
        for (int i = 0; i < length; ++i) {
            Node item = errors.item(i);
            StringUtil.simpleLog("Message Returned from the Server: " + item.getFirstChild().getTextContent(), this.getClass(), logFile);
        }
    }

    private boolean isBadResumptionToken(NodeList errors) {
        int length = errors.getLength();
        for (int i = 0; i < length; ++i) {
            Node item = errors.item(i);
            if (item.getAttributes() != null && item.getAttributes().getNamedItem("code") != null && item.getAttributes().getNamedItem("code").getNodeValue() != null && item.getAttributes()
                    .getNamedItem("code").getNodeValue().equals("badResumptionToken")) {
                StringUtil.simpleLog("badResumptionToken in operation ListRecords, restarting harvest", this.getClass(), logFile);
                return true;
            }
        }

        return false;
    }

    /**
     * @return Array of Files 
     */
    public File[] getRequestFiles() {
        File requestDir = new File(outputDirname);
        if (requestDir.isDirectory()) {
            File[] targetFileList = requestDir.listFiles();
            List<File> recordRequestFileList = new ArrayList<File>();
            for (File currentFile : targetFileList) {
                if (currentFile.getName().startsWith(REQUEST_FILENAME_START)) {
                    recordRequestFileList.add(currentFile);
                }
            }

            File[] recordRequestFiles = new File[recordRequestFileList.size()];
            recordRequestFileList.toArray(recordRequestFiles);

            return recordRequestFiles;
        } else {
            return null;
        }
    }

    /**
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void cleanUp() throws FileNotFoundException, IOException {
        File requestDir = new File(outputDirname);
        if (requestDir.isDirectory()) {
            try {
                FileUtils.deleteDirectory(requestDir);
            } catch (IOException e) {
                throw new RuntimeException("Unable to delete temporary directory " + requestDir.getAbsolutePath());
            }
        }
    }

    @Override
    public String toString() {
        String returnString = "sourceUrl: " + sourceUrl + ", sourceSet: " + sourceSet + ", outputBaseDir: " + outputDirname + ", fromDateString: " + fromDateString + ", untilDateString: " + untilDateString + ", metadataFormat: " + metadataFormat;
        return returnString;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Date fromDate = null;
        Date untilDate = null;
        String sourceUrl = "http://bd1.inesc-id.pt:8080/repoxel/OAIHandler";
        String sourceSet = "bmfinancas";
        int maxRecord4Sample = -1;
        //		sources.put("http://louisdl.louislibraries.org/cgi-bin/oai.exe", "LHC");
        //		sources.put("http://louisdl.louislibraries.org/cgi-bin/oai.exe", "LMP");
        //		sources.put("http://www.nla.gov.au/apps/oaicat/servlet/OAIHandler", "Map");
        //		sources.put("http://zbc.uz.zgora.pl/dlibra/oai-pmh-repository.xml", "DigitalLibraryZielonaGora:RegionalMaterialss:Dokkartee");
        //		sources.put("http://u2.gmu.edu:8080/dspace-oai/request", "hdl_1920_1935");
        //		sources.put("http://broker10.fcla.edu/cgi/b/broker20/broker20", "palmmfof:mapflbib");
        //		sources.put("http://vacani.icc.cat/cgi-bin/oai.exe", "catalunya");

        OaiHarvester harvester = new OaiHarvester(sourceUrl, sourceSet, fromDate, untilDate, "ese", null, maxRecord4Sample);
        harvester.run();

    }

    /**
     * @return List of Integers
     */
    public ArrayList<Integer> getServerInfos() {
        ArrayList<Integer> results = new ArrayList<Integer>(); // [0] = totalRecordsNumber; [1] = recordsPerResponse
        if (numberOfRecordsPerResponse != -1) {
            results.add(numberOfRecords2Harvest);
            results.add(numberOfRecordsPerResponse);
        }
        return results;
    }

}
