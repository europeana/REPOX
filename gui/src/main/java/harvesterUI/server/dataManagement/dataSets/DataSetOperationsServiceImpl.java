package harvesterUI.server.dataManagement.dataSets;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import harvesterUI.client.servlets.dataManagement.DataSetOperationsService;
import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;
import harvesterUI.shared.dataTypes.dataSet.DataSetStatus;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.DatasetType;
import harvesterUI.shared.servletResponseStates.ResponseState;
import harvesterUI.shared.tasks.OldTaskUI;
import org.dom4j.*;
import org.oclc.oai.harvester.verb.ListMetadataFormats;
import org.oclc.oai.harvester.verb.ListSets;
import org.xml.sax.SAXParseException;
import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.LogFilenameComparator;
import pt.utl.ist.marc.DirectoryImporterDataSource;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.task.OldTask;
import pt.utl.ist.task.ScheduledTask;
import pt.utl.ist.util.FileUtil;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataSetOperationsServiceImpl extends RemoteServiceServlet implements DataSetOperationsService {

    public DataSetOperationsServiceImpl() {
    }

    private HttpSession getSession() {
        return this.getThreadLocalRequest().getSession();
    }

    public SaveDataResponse saveDataSource(boolean update, DatasetType type, String originalDSset, DataSourceUI dataSourceUI, int pageSize) throws ServerSideException {
        return RepoxServiceImpl.getProjectManager().saveDataSource(update, type, originalDSset, dataSourceUI, pageSize);
    }

    public Map<String, List<String>> checkOAIURL(String url) throws ServerSideException {
        Map<String, List<String>> checkMap = new HashMap<String, List<String>>();
        List<String> sets = new ArrayList<String>();
        List<String> mdPrefixes = new ArrayList<String>();
        List<String> setNames = new ArrayList<String>();
        try {
            // Check http URLs
            String checkUrlResult = checkURL(url);
            if (checkUrlResult.equals("URL_MALFORMED")) {
                sets.add("URL_MALFORMED");
                checkMap.put("ERROR", sets);
                return checkMap;
            } else if (checkUrlResult.equals("URL_NOT_EXISTS")) {
                sets.add("URL_NOT_EXISTS");
                checkMap.put("ERROR", sets);
                return checkMap;
            }

            String resumptionToken = null;
            List<Node> completeNodeList = new ArrayList<>();
            ListSets listSets = new ListSets(url);
            do {
                if(resumptionToken != null && !resumptionToken.equals(""))
                    listSets = new ListSets(url, resumptionToken);
                if ((listSets.getErrors() != null && listSets.getErrors().getLength() > 0)) {
                    XPath xPathError = DocumentHelper.createXPath("//oai:error[@code='noSetHierarchy']");
                    HashMap<String, String> nameSpaces = new HashMap<String, String>();
                    nameSpaces.put("oai", "http://www.openarchives.org/OAI/2.0/");
                    xPathError.setNamespaceURIs(nameSpaces);
                    List errorNodes = xPathError.selectNodes(DocumentHelper.parseText(listSets.toString()));
                    if (errorNodes != null && errorNodes.size() > 0) {
                    } else {
                        return null;
                    }
                } else {
                    // Sets
                    Document document = DocumentHelper.parseText(listSets.toString());
                    XPath xPath = DocumentHelper.createXPath("//oai:set");
                    HashMap<String, String> nameSpaces = new HashMap<String, String>();
                    nameSpaces.put("oai", "http://www.openarchives.org/OAI/2.0/");
                    xPath.setNamespaceURIs(nameSpaces);
                    List<Node> nodeList = xPath.selectNodes(document.getRootElement());
                    completeNodeList.addAll(nodeList);

                    resumptionToken = listSets.getResumptionToken();
                }
            } while (resumptionToken != null && !resumptionToken.equals(""));

            if (completeNodeList == null || completeNodeList.size() == 0) {
                return null;
            }

            for (Node aNodeList : completeNodeList) {
                Element currentSetElement = (Element) aNodeList;
                String setSpec = currentSetElement.element("setSpec").getText();
                sets.add(setSpec);
                Element setNameElement = currentSetElement.element("setName");
                if (setNameElement != null) {
                    String setName = setNameElement.getText();
                    setNames.add(setName);
                }
            }

            // MetadataPrefixes
            ListMetadataFormats listMetadataFormats = new ListMetadataFormats(url);
            Document documentMD = DocumentHelper.parseText(listMetadataFormats.toString());
            XPath xPathMD = DocumentHelper.createXPath("//oai:metadataFormat");
            HashMap<String, String> nameSpacesMD = new HashMap<String, String>();
            nameSpacesMD.put("oai", "http://www.openarchives.org/OAI/2.0/");
            xPathMD.setNamespaceURIs(nameSpacesMD);
            List<Node> nodeListMD = xPathMD.selectNodes(documentMD.getRootElement());

            if (nodeListMD == null || nodeListMD.size() == 0) {
                // TODO: ERROR NO SETS
            }

            for (Node aNodeList : nodeListMD) {
                Element currentSetElement = (Element) aNodeList;
                String mdPrefix = currentSetElement.element("metadataPrefix").getText();
                mdPrefixes.add(mdPrefix);
            }


        } catch (SAXParseException e) {
            sets.add("URL_NOT_EXISTS");
            checkMap.put("ERROR", sets);
            return checkMap;
        } catch (MalformedURLException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        checkMap.put("mdPrefixes", mdPrefixes);
        checkMap.put("sets", sets);
        checkMap.put("setNames", setNames);
        return checkMap;
    }

    public static String checkURL(String inputURL) throws ServerSideException {
        try {
            if (inputURL == null)
                return null;

            URL url;
            if (!inputURL.equals("")) {
                url = new URL(inputURL);
            } else
                url = null;

            // Url doesn't exist
            if (url != null && !FileUtil.checkUrl(inputURL))
                return "URL_NOT_EXISTS";

            return "SUCCESS";
        } catch (MalformedURLException e) {
            return "URL_MALFORMED";
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String addAllOAIURL(String url, String dataProviderID, String dsSchema, String dsNamespace,
                               String dsMTDFormat, String name, String nameCode, String exportPath) throws ServerSideException {
        return RepoxServiceImpl.getProjectManager().addAllOAIURL(url, dataProviderID, dsSchema, dsNamespace, dsMTDFormat, name, nameCode, exportPath, this);
    }

    public Map<String, DataSetStatus> getAllDataSourceStatus(List<DataContainer> dataContainers) throws ServerSideException {
        Map<String, DataSetStatus> statusMap = new HashMap<String, DataSetStatus>();
        try {
            for (DataContainer dataContainer : dataContainers) {
                if (dataContainer instanceof DataProviderUI && ((DataProviderUI) dataContainer).getDataSourceUIList().size() == 1)
                    getDSStatus(((DataProviderUI) dataContainer).getDataSourceUIList().get(0), statusMap);
                if (dataContainer instanceof DataSourceUI)
                    getDSStatus((DataSourceUI) dataContainer, statusMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }

        if (statusMap.size() <= 0)
            return null;
        else {
            return statusMap;
        }
    }

    protected void getDSStatus(DataSourceUI dataSourceUI, Map<String, DataSetStatus> statusMap) throws ServerSideException {
        try {
            DataSource dataSource = RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet()).getDataSource();
            DataSetStatus data = new DataSetStatus();

            // Get a the Next Ingest date for each data source
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
            Date nextDate = null;

            for (ScheduledTask scheduledTask : RepoxServiceImpl.getRepoxManager().getTaskManager().getScheduledTasks()) {
                // Do this only for ingests and not exports
                if (scheduledTask.getTaskClass().getSimpleName().equals("IngestDataSource")) {
                    if (scheduledTask.getParameters()[1].equals(dataSource.getId())) {
                        if (!scheduledTask.getNextIngestDate().isEmpty()) {
                            Date scheduleDate = Util.getDate(scheduledTask.getNextIngestDate());
                            if (nextDate == null)
                                nextDate = scheduleDate;
                            else if (scheduleDate.before(nextDate))
                                nextDate = scheduleDate;
                        }
                    }
                }
            }
            if (nextDate != null) {
                // check if task has passed already because TODO repox doesn't erase past scheduled tasks
                if (nextDate.after(new Date()))
                    data.set("nextIngestStr", formatter.format(nextDate));
            } else
                data.set("nextIngestStr", "");

            // Get the old tasks for each data source
//            data.set("oldTasks", getOldTasks(dataSource));

            RepoxServiceImpl.getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId(), true);

            int maxSample = dataSource.getMaxRecord4Sample();
            if (maxSample == -1)
                data.set("status", dataSource.getStatusString());
            else
                data.set("status", dataSource.getStatusString() + "_SAMPLE");

            // Check if has a retrying the task
            if (Util.hasRunningTask(dataSource.getId()))
                data.set("hasRunningTask", true);
            else
                data.set("hasRunningTask", false);

            data.set("recordNum", dataSource.getNumberRecords()[2]);
            if ((dataSource instanceof OaiDataSource || dataSource instanceof DirectoryImporterDataSource)
                    && dataSource.getStatusString().equals("RUNNING")) {
                try {
                    data.set("totalRecordNum", dataSource.getNumberOfRecords2Harvest());
                    data.set("totalRecordNumStr", dataSource.getNumberOfRecords2HarvestStr());
                    data.set("ingestPercentage", dataSource.getPercentage());
                    data.set("ingestTimeLeft", dataSource.getTimeLeft());
                } catch (NullPointerException e) {
                    data.set("totalRecordNum", null);
                    data.set("totalRecordNumStr", null);
                    data.set("ingestPercentage", null);
                    data.set("ingestTimeLeft", null);
                }
            }
            statusMap.put(dataSource.getId(), data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    private List<OldTaskUI> getOldTasks(DataSource dataSource) {
        List<OldTaskUI> result = new ArrayList<OldTaskUI>();
        for (OldTask oldTask : dataSource.getOldTasksList()) {
            OldTaskUI oldTaskUI = new OldTaskUI(dataSource.getId(), oldTask.getId(), oldTask.getLogName(),
                    oldTask.getIngestType(), oldTask.getStatus(), oldTask.getRetries(),
                    oldTask.getRetryMax(), oldTask.getDelay(), oldTask.getDateString(), oldTask.getRecords());
            result.add(oldTaskUI);
        }
        return result;
    }

    public String getLogFile(DataSourceUI dataSourceUI) throws ServerSideException {
        try {
            DataSource dataSource = RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet()).getDataSource();

            String delimFolder = "\\\\";
            String[] tokensFolder = dataSource.getLogFilenames().get(0).split(delimFolder);
            String correctFilename;
            if (tokensFolder.length > 1)
                correctFilename = tokensFolder[0] + "/" + tokensFolder[1];
            else
                correctFilename = dataSource.getLogFilenames().get(0);
            String path = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
                    .getConfiguration().getRepositoryPath() + "/" + dataSource.getId() + "/logs/" + correctFilename;
            File f = new File(path);

            File logFile = new File(getSession().getServletContext().getRealPath("resources/logs/" +
                    correctFilename));
            logFile.getParentFile().getParentFile().mkdir();
            logFile.getParentFile().mkdir();
            logFile.createNewFile();

            FileWriter fstream = new FileWriter(logFile);
            BufferedReader reader = new BufferedReader(new FileReader(f));
            BufferedWriter writer = new BufferedWriter(fstream);

            String line = null;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();   // Write system dependent end of line.
            }

            reader.close();
            writer.close();

            return correctFilename;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String getLogFileFromFileName(DataSourceUI dataSourceUI, String fileName) throws ServerSideException {
        try {
            DataSource dataSource = RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet()).getDataSource();

            String correctFilename = null;
            for (String logName : dataSource.getLogFilenames()) {
                if (logName.equals(fileName)) {
                    correctFilename = logName;
                    String path = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
                            .getConfiguration().getRepositoryPath() + "/" + dataSource.getId() + "/logs/" + logName;
                    File f = new File(path);

                    File logFile = new File(getSession().getServletContext().getRealPath("resources/logs/" + logName));
                    logFile.getParentFile().getParentFile().mkdir();
                    logFile.getParentFile().mkdir();
                    logFile.createNewFile();

                    FileWriter fstream = new FileWriter(logFile);
                    BufferedReader reader = new BufferedReader(new FileReader(f));
                    BufferedWriter writer = new BufferedWriter(fstream);

                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();   // Write system dependent end of line.
                    }

                    reader.close();
                    writer.close();
                    break;
                }
            }

            return correctFilename;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String getExportPath(String dataSourceID) throws ServerSideException {
        try {
            File exportFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceID).getDataSource().getExportDir());

            if (exportFile == null)
                return "NOT_FOUND";

            if (!exportFile.exists())
                return "NOT_FOUND";

            List<String> fileNames = getExportFileNames(exportFile, dataSourceID);

            if (fileNames.size() == 0)
                return "NOT_FOUND";
            else {
                return exportFile + "&&" + fileNames.get(0);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            return "ERROR";
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<String> getExportFileNames(File exportDir, String dataSourceId) throws ServerSideException {
        try {
            if (!exportDir.exists()) {
                exportDir.mkdir();
            }

            List<File> exportDirNames = Arrays.asList(exportDir.listFiles());
            List<String> exportFileNames = new ArrayList<String>();

            for (File exportFile : exportDirNames) {
                if (exportFile.isFile() && exportFile.getName().endsWith(".zip") &&
                        exportFile.getName().contains(dataSourceId)) {
                    exportFileNames.add(exportFile.getName());
                }
            }

            Collections.sort(exportFileNames, new LogFilenameComparator());
            Collections.reverse(exportFileNames);

            return exportFileNames;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public SaveDataResponse moveDataSources(List<DataSourceUI> dataSourceUIs, ModelData dataProviderUI, int pageSize) throws ServerSideException {
        return RepoxServiceImpl.getProjectManager().moveDataSources(dataSourceUIs, dataProviderUI, pageSize);
    }


    public String deleteDataSources(List<DataSourceUI> dataSourceUIs) throws ServerSideException {
        return RepoxServiceImpl.getProjectManager().deleteDataSources(dataSourceUIs);
    }

    public ResponseState stopRunningDataSet(String dataSetId) throws ServerSideException {
        try {
            DataSource dataSource = RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(dataSetId).getDataSource();
            dataSource.setStatus(DataSource.StatusDS.CANCELED);
            RepoxServiceImpl.getRepoxManager().getDataManager().saveData();
            return ResponseState.SUCCESS;
        } catch (DocumentException e) {
            e.printStackTrace();
            return ResponseState.ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseState.ERROR;
        }
    }

    public ResponseState startSingleExternalService(String serviceId, String dataSetId) throws ServerSideException {
        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getExternalRestServicesManager().startExternalService(serviceId, dataSetId);
            return ResponseState.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public ResponseState forceDataSetRecordUpdate(List<DataSourceUI> dataSourceUIs) {
        try {
            for (DataSourceUI dataSourceUI : dataSourceUIs)
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(dataSourceUI.getDataSourceSet(), true);
            return ResponseState.SUCCESS;
        } catch (IOException e) {
            return ResponseState.ERROR;
        } catch (DocumentException e) {
            return ResponseState.ERROR;
        } catch (SQLException e) {
            return ResponseState.ERROR;
        }
    }

    public ResponseState clearLogsAndOldTasks(List<DataSourceUI> dataSourceUIs) {
        try {
            for (DataSourceUI dataSourceUI : dataSourceUIs)
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().removeLogsAndOldTasks(dataSourceUI.getDataSourceSet());
            return ResponseState.SUCCESS;
        } catch (IOException e) {
            return ResponseState.SUCCESS;
        } catch (DocumentException e) {
            return ResponseState.ERROR;
        }
    }
}
