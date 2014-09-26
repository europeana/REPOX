/*
 * Created on 2007/01/23
 *
 */
package pt.utl.ist.repox.marc;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import pt.utl.ist.repox.configuration.ConfigSingleton;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.dataSource.*;
import pt.utl.ist.repox.ftp.FtpFileRetrieveStrategy;
import pt.utl.ist.repox.http.HttpFileRetrieveStrategy;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.reports.LogUtil;
import pt.utl.ist.repox.statistics.RecordCount;
import pt.utl.ist.repox.statistics.RecordCountManager;
import pt.utl.ist.repox.task.Task;
import pt.utl.ist.repox.task.Task.Status;
import pt.utl.ist.repox.util.FileUtil;
import pt.utl.ist.repox.util.StringUtil;
import pt.utl.ist.repox.util.TarGz;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.util.date.DateUtil;

import java.io.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 */
public class DirectoryImporterDataSource extends DataSource {
    private static final Logger  log = Logger.getLogger(DirectoryImporterDataSource.class);

    private FileExtractStrategy  extractStrategy;
    private FileRetrieveStrategy retrieveStrategy;
    private CharacterEncoding    characterEncoding;
    private String               sourcesDirPath;
    private String               recordXPath;
    private Map<String, String>  namespaces;
    private String               idTypePolicy;

    @SuppressWarnings("javadoc")
    public String getIdTypePolicy() {
        return idTypePolicy;
    }

    @SuppressWarnings("javadoc")
    public void setIdTypePolicy(String idTypePolicy) {
        this.idTypePolicy = idTypePolicy;
    }

    @SuppressWarnings("javadoc")
    public FileRetrieveStrategy getRetrieveStrategy() {
        return retrieveStrategy;
    }

    @SuppressWarnings("javadoc")
    public void setRetrieveStrategy(FileRetrieveStrategy retrieveStrategy) {
        this.retrieveStrategy = retrieveStrategy;
    }

    @SuppressWarnings("javadoc")
    public FileExtractStrategy getExtractStrategy() {
        return extractStrategy;
    }

    @SuppressWarnings("javadoc")
    public void setExtractStrategy(FileExtractStrategy extractStrategy) {
        this.extractStrategy = extractStrategy;
    }

    @SuppressWarnings("javadoc")
    public CharacterEncoding getCharacterEncoding() {
        return characterEncoding;
    }

    @SuppressWarnings("javadoc")
    public void setCharacterEncoding(CharacterEncoding characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    @SuppressWarnings("javadoc")
    public String getSourcesDirPath() {
        return sourcesDirPath;
    }

    @SuppressWarnings("javadoc")
    public void setSourcesDirPath(String sourcesDirPath) {
        this.sourcesDirPath = sourcesDirPath;
    }

    @SuppressWarnings("javadoc")
    public String getRecordXPath() {
        return recordXPath;
    }

    @SuppressWarnings("javadoc")
    public void setRecordXPath(String recordXPath) {
        this.recordXPath = recordXPath;
    }

    @SuppressWarnings("javadoc")
    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    @SuppressWarnings("javadoc")
    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    /**
     * Creates a new instance of this class.
     */
    public DirectoryImporterDataSource() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param dataProvider
     * @param id
     * @param description
     * @param schema
     * @param namespace
     * @param metadataFormat
     * @param extractStrategy
     * @param retrieveStrategy
     * @param characterEncoding
     * @param sourcesDirPath
     * @param recordIdPolicy
     * @param metadataTransformations
     * @param recordXPath
     * @param namespacesMap
     */
    public DirectoryImporterDataSource(DataProvider dataProvider, String id, String description, String schema, String namespace, String metadataFormat, FileExtractStrategy extractStrategy, FileRetrieveStrategy retrieveStrategy, CharacterEncoding characterEncoding, String sourcesDirPath,
                                       RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations, String recordXPath, Map<String, String> namespacesMap) {
        super(dataProvider, id, description, schema, namespace, metadataFormat, recordIdPolicy, metadataTransformations);
        this.characterEncoding = characterEncoding;
        this.extractStrategy = extractStrategy;
        this.sourcesDirPath = sourcesDirPath;
        this.recordXPath = recordXPath;
        this.namespaces = namespacesMap;
        this.retrieveStrategy = retrieveStrategy;
    }

    @Override
    public Status ingestRecords(File logFile, boolean fullIngest) throws IOException, DocumentException, SQLException {
        log.debug("INGESTING NOW - Directory Importer!");
        Status ingestStatus = Status.OK;
        numberOfRecords2Harvest = -1;

        Date startIngestTime = new Date();
        LogUtil.startLogInfo(logFile, startIngestTime, StatusDS.RUNNING.name(), id);

        if (retrieveStrategy instanceof HttpFileRetrieveStrategy) {
            log.debug("INGESTING NOW - Retrieving Files from HTTP...");
            retrieveStrategy.retrieveFiles(getId());
        } else if (retrieveStrategy instanceof FtpFileRetrieveStrategy) {
            log.debug("INGESTING NOW - Retrieving Files from FTP...");
            retrieveStrategy.retrieveFiles(getId());
        }

        // Remove all records from IdGenerated because there is no version management or it is a full ingest
        if (this.getRecordIdPolicy() instanceof IdGeneratedRecordIdPolicy || fullIngest) {
            boolean successfulDeletion = emptyRecords();

            if (!successfulDeletion) {
                StringUtil.simpleLog("Importing aborted - unable to delete the current Records", this.getClass(), logFile);
                LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.ERROR.name(), id, lastIngestCount, lastIngestDeletedCount);
                return Status.FAILED;
            }

            //Clear the last ingest date
            setLastUpdate(null);

            //Update the XML file
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
        } else {
            //if there is a previous successful harvest and has finished
            RecordCount recordCount = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(id, true);
            if (recordCount != null && recordCount.getCount() > 0 && getLastUpdate() != null) {
                String syncDateString = DateUtil.date2String(getLastUpdate(), TimeUtil.SHORT_DATE_FORMAT);
                StringUtil.simpleLog("Directory Importer harvest from date: " + syncDateString, this.getClass(), logFile);
            }
        }

        File sourcesDir = new File(sourcesDirPath);
        File[] changedFiles = FileUtil.getChangedFiles(getLastUpdate(), sourcesDir.listFiles());

        StringUtil.simpleLog("Importing from directory: " + sourcesDirPath, this.getClass(), logFile);

        List<RecordRepox> batchRecords = new ArrayList<RecordRepox>();

        /*
         * //todo long startTime = (new Date()).getTime(); long totalTime =
         * ((new Date()).getTime() - startTime ) / 1000;
         * statisticsHarvester.add(totalTime);
         */

        int totalRecords = 0;
        for (File file : changedFiles) {
            if (stopExecution) {
                if (forceStopExecution) {
                    LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.ERROR.name(), id, lastIngestCount, lastIngestDeletedCount);
                    return Task.Status.FORCE_EMPTY;
                }
                StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.CANCELED.name(), id, lastIngestCount, lastIngestDeletedCount);
                return Status.CANCELED;
            }

            if (file.exists() && file.isFile()) {
                //                StringUtil.simpleLog("Checking file: " + file.getName(), this.getClass(), logFile,false);

                if (file.getName().endsWith(".zip")) {
                    // zip file
                    ZipInputStream in = null;
                    try {
                        in = new ZipInputStream(new FileInputStream(file));
                        ZipEntry entry;
                        TimeUtil.startTimers();

                        // Folders inside the zip file with special characters in the name are not allowed
                        while ((entry = in.getNextEntry()) != null) {
                            if (stopExecution) {
                                if (forceStopExecution) {
                                    LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.ERROR.name(), id, lastIngestCount, lastIngestDeletedCount);
                                    return Task.Status.FORCE_EMPTY;
                                }
                                StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                                in.close();
                                LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.CANCELED.name(), id, lastIngestCount, lastIngestDeletedCount);
                                return Status.CANCELED;
                            }

                            TimeUtil.getTimeSinceLastTimerArray(0);

                            if (extractStrategy instanceof Iso2709FileExtractStrategy || entry.getName().toLowerCase().endsWith(".xml")) {
                                String outFilename = UUID.randomUUID().toString() + ".xml";
                                File tempDir = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getTempDir();
                                File unzippedFile = new File(tempDir, outFilename);
                                StringUtil.simpleLog("Importing zip entry: " + entry.getName(), this.getClass(), logFile, false);

                                // Open the output file
                                OutputStream out = new FileOutputStream(unzippedFile);

                                // Transfer bytes from the ZIP file to the output file
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }

                                // Close the streams
                                out.close();

                                TimeUtil.getTimeSinceLastTimerArray(1);

                                RepoxRecordHandler repoxRecordHandler = new RepoxRecordHandler(batchRecords, logFile, unzippedFile, totalRecords);
                                try {
                                    extractStrategy.iterateRecords(repoxRecordHandler, this, unzippedFile, characterEncoding, logFile);
                                    totalRecords = repoxRecordHandler.countTotalRecords;
                                    if (batchRecords.size() >= RECORDS_BATCH_SIZE) {
                                        repoxRecordHandler.savePendingRecords();
                                    }

                                    if (stopExecution && maxRecord4Sample != -1 && maxRecord4Sample <= totalRecords) {
                                        // stop execution created by ingest source code (activated when has the sample's number records)
                                        LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.OK.name(), id, lastIngestCount, lastIngestDeletedCount);
                                        return Status.OK;
                                    } else if (stopExecution) {
                                        // stop forced by REPOX user (tasks)
                                        if (forceStopExecution) {
                                            LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.ERROR.name(), id, lastIngestCount, lastIngestDeletedCount);
                                            return Task.Status.FORCE_EMPTY;
                                        }
                                        StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                                        LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.CANCELED.name(), id, lastIngestCount, lastIngestDeletedCount);
                                        return Status.CANCELED;
                                    }
                                } catch (Exception e) {
                                    log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                                    StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                                }

                                unzippedFile.delete();
                            }

                            log.debug("Total entry time: " + TimeUtil.getTimeSinceLastTimerArray(0));
                        }

                        log.debug("Total time: " + TimeUtil.getTotalTime());

                    } catch (FileNotFoundException e) {
                        StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                    } finally {
                        in.close();
                    }
                } else if (file.getName().endsWith(".tar.gz")) {
                    // tar.gz file
                    try {
                        File tempDir = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getTempDir();
                        List<File> listFiles = TarGz.unTarGz(file, tempDir);
                        TimeUtil.startTimers();

                        // Folders inside the zip file with special characters in the name are not allowed
                        for (File file2Ingest : listFiles) {
                            if (stopExecution) {
                                if (forceStopExecution) {
                                    LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.ERROR.name(), id, lastIngestCount, lastIngestDeletedCount);
                                    return Task.Status.FORCE_EMPTY;
                                }
                                StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                                LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.CANCELED.name(), id, lastIngestCount, lastIngestDeletedCount);
                                return Status.CANCELED;
                            }

                            TimeUtil.getTimeSinceLastTimerArray(0);

                            if (extractStrategy instanceof Iso2709FileExtractStrategy || file2Ingest.getName().toLowerCase().endsWith(".xml")) {
                                StringUtil.simpleLog("Importing zip entry: " + file2Ingest.getName(), this.getClass(), logFile, false);

                                TimeUtil.getTimeSinceLastTimerArray(1);

                                RepoxRecordHandler repoxRecordHandler = new RepoxRecordHandler(batchRecords, logFile, file2Ingest, totalRecords);
                                try {
                                    extractStrategy.iterateRecords(repoxRecordHandler, this, file2Ingest, characterEncoding, logFile);
                                    totalRecords = repoxRecordHandler.countTotalRecords;
                                    if (batchRecords.size() >= RECORDS_BATCH_SIZE) {
                                        repoxRecordHandler.savePendingRecords();
                                    }

                                    if (stopExecution && maxRecord4Sample != -1 && maxRecord4Sample <= totalRecords) {
                                        // stop execution created by ingest source code (activated when has the sample's number records)
                                        LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.OK.name(), id, lastIngestCount, lastIngestDeletedCount);
                                        return Status.OK;
                                    } else if (stopExecution) {
                                        // stop forced by REPOX user (tasks)
                                        if (forceStopExecution) {
                                            LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.ERROR.name(), id, lastIngestCount, lastIngestDeletedCount);
                                            return Task.Status.FORCE_EMPTY;
                                        }
                                        StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                                        LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.CANCELED.name(), id, lastIngestCount, lastIngestDeletedCount);
                                        return Status.CANCELED;
                                    }
                                } catch (Exception e) {
                                    log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                                    StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                                }
                                FileUtils.forceDelete(file2Ingest);
                            }

                            log.debug("Total entry time: " + TimeUtil.getTimeSinceLastTimerArray(0));
                        }

                        log.debug("Total time: " + TimeUtil.getTotalTime());

                    } catch (FileNotFoundException e) {
                        StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                    } catch (ArchiveException e) {
                        StringUtil.simpleLog("Error extracting file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                    }
                } else {
                    RepoxRecordHandler repoxRecordHandler = new RepoxRecordHandler(batchRecords, logFile, file, totalRecords);
                    try {
                        extractStrategy.iterateRecords(repoxRecordHandler, this, file, characterEncoding, logFile);
                        totalRecords = repoxRecordHandler.countTotalRecords;
                        /*
                         * // records are stored during iterator process
                         * if(batchRecords.size() >= RECORDS_BATCH_SIZE){
                         * repoxRecordHandler.savePendingRecords(); }
                         */

                        if (stopExecution && maxRecord4Sample != -1 && maxRecord4Sample <= totalRecords) {
                            // stop execution created by ingest source code (activated when has the sample's number records)
                            LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.OK.name(), id, lastIngestCount, lastIngestDeletedCount);
                            return Status.OK;
                        } else if (stopExecution) {
                            // stop forced by REPOX user (tasks)
                            if (forceStopExecution) {
                                LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.ERROR.name(), id, lastIngestCount, lastIngestDeletedCount);
                                return Task.Status.FORCE_EMPTY;
                            }
                            StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                            LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.CANCELED.name(), id, lastIngestCount, lastIngestDeletedCount);
                            return Status.CANCELED;
                        }
                    } catch (Exception e) {
                        log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                        StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                    }
                }
            }
        }

        // Import remaining records
        importBatchRecords(batchRecords, logFile);
        addDeletedRecords(batchRecords);

        LogUtil.endLogInfo(logFile, startIngestTime, new Date(), ingestStatus.name(), id, lastIngestCount, lastIngestDeletedCount);

        return ingestStatus;
    }

    /**
     * @param batchRecords
     * @param logFile
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     */
    protected void importBatchRecords(List<RecordRepox> batchRecords, File logFile) throws IOException, DocumentException, SQLException {
        //long memBefore = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        TimeUtil.getTimeSinceLastTimerArray(9);

        RecordCountManager recordCountManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager();
        if (recordCountManager.getRecordCount(id) != null) {
            log.debug("[BEFORE] Count: " + recordCountManager.getRecordCount(id).getCount());
        }

        // to avoid duplicates
        Map<String, RecordRepox> batchRecordsWithoutDuplicates = new HashMap<String, RecordRepox>();
        for (RecordRepox record : batchRecords) {
            batchRecordsWithoutDuplicates.put(record.getId().toString(), record);
        }
        batchRecords = new ArrayList<RecordRepox>(batchRecordsWithoutDuplicates.values());

        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().processRecords(this, batchRecords, logFile);

        if (recordCountManager.getRecordCount(id) != null) {
            log.debug("[AFTER]  count: " + recordCountManager.getRecordCount(id).getCount());
        }

        /*
         * double importTime = TimeUtil.getTimeSinceLastTimerArray(9) / 1000.0;
         * long memAfter = Runtime.getRuntime().totalMemory() / (1024 * 1024);
         */
        if (batchRecords.size() != 0) {
            /*
             * log.info(batchRecords.size() + " records imported in " +
             * importTime + "s." + " Memory before/after (MB) : " + memBefore +
             * "/"+ memAfter);
             */
            StringUtil.simpleLog(batchRecords.size() + " records imported", this.getClass(), logFile);
            lastIngestCount += batchRecords.size();
        }
    }

    @Override
    public boolean isWorking() {
        File sourcesDir = new File(sourcesDirPath);

        return (sourcesDir.isDirectory() && sourcesDir.exists());
    }

    @Override
    public Element addSpecificInfo(Element sourceNode) {
        sourceNode.addAttribute("type", "DataSourceDirectoryImporter");
        sourceNode.addElement("sourcesDirPath").setText(getSourcesDirPath());

        Element extractStrategyNode = sourceNode.addElement("retrieveStrategy");

        if (getRetrieveStrategy() instanceof FtpFileRetrieveStrategy) {
            FtpFileRetrieveStrategy dataSourceFtp = (FtpFileRetrieveStrategy)getRetrieveStrategy();
            extractStrategyNode.addAttribute("type", FtpFileRetrieveStrategy.class.getName());
            extractStrategyNode.addElement("server").setText(dataSourceFtp.getServer());
            if (dataSourceFtp.getIdTypeAccess().equals(FtpFileRetrieveStrategy.NORMAL) && !dataSourceFtp.getIdTypeAccess().isEmpty()) {
                extractStrategyNode.addElement("user").setText((dataSourceFtp.getUser() != null ? dataSourceFtp.getUser() : ""));
                extractStrategyNode.addElement("password").setText(dataSourceFtp.getPassword() != null ? dataSourceFtp.getPassword() : "");
            }
            extractStrategyNode.addElement("folderPath").setText(dataSourceFtp.getFtpPath());
        } else if (getRetrieveStrategy() instanceof HttpFileRetrieveStrategy) {
            HttpFileRetrieveStrategy dataSourceHttp = (HttpFileRetrieveStrategy)getRetrieveStrategy();
            extractStrategyNode.addAttribute("type", HttpFileRetrieveStrategy.class.getName());
            extractStrategyNode.addElement("url").setText(dataSourceHttp.getUrl());
        } else if (getRetrieveStrategy() instanceof FolderFileRetrieveStrategy) {
            extractStrategyNode.addAttribute("type", FolderFileRetrieveStrategy.class.getName());
        }

        if (getExtractStrategy() instanceof Iso2709FileExtractStrategy) {
            Iso2709FileExtractStrategy extractStrategy = (Iso2709FileExtractStrategy)getExtractStrategy();
            sourceNode.addAttribute("isoImplementationClass", extractStrategy.getIsoImplementationClass().toString());
            sourceNode.addAttribute("characterEncoding", getCharacterEncoding().toString());
            sourceNode.addElement("fileExtract").setText(Iso2709FileExtractStrategy.class.getSimpleName());
        } else if (getExtractStrategy() instanceof MarcXchangeFileExtractStrategy) {
            sourceNode.addElement("fileExtract").setText(MarcXchangeFileExtractStrategy.class.getSimpleName());
        } else if (getExtractStrategy() instanceof SimpleFileExtractStrategy) {
            sourceNode.addElement("fileExtract").setText(SimpleFileExtractStrategy.class.getSimpleName());

            if (getRecordXPath() != null) {
                Element splitRecords = sourceNode.addElement("splitRecords");
                splitRecords.addElement("recordXPath").setText(getRecordXPath());

                if (getNamespaces() != null && getNamespaces().size() > 0) {
                    Element namespacesElement = splitRecords.addElement("namespaces");

                    //System.out.println("currentDataSource.getNamespaces() = " + currentDataSource.getNamespaces());

                    for (String currentKey : getNamespaces().keySet()) {
                        Element namespaceElement = namespacesElement.addElement("namespace");
                        namespaceElement.addElement("namespacePrefix").setText(currentKey);
                        namespaceElement.addElement("namespaceUri").setText(getNamespaces().get(currentKey));
                    }
                }
            }
        }
        return sourceNode;
    }

    class RepoxRecordHandler implements FileExtractStrategy.RecordHandler {
        List<RecordRepox> batchRecords;
        File              logFile;
        File              file;
        int               countTotalRecords;
        Status            ingestStatus;
        long              startTime;

        public RepoxRecordHandler(List<RecordRepox> batchRecords, File logFile, File file, int actualNumber) {
            this.batchRecords = batchRecords;
            this.logFile = logFile;
            this.file = file;
            this.countTotalRecords = actualNumber;
            startTime = (new Date()).getTime();
        }

        public Status getIngestStatus() {
            return ingestStatus;
        }

        public void setIngestStatus(Status ingestStatus) {
            this.ingestStatus = ingestStatus;
        }

        @Override
        public void handleRecord(RecordRepox record) {
            if (stopExecution) { return; }

            try {
                batchRecords.add(record);

                // todo - to be removed
                //log.debug("IDs -> " + record.getId() + " ; " + file.getName());

                if (maxRecord4Sample == -1 && batchRecords.size() >= RECORDS_BATCH_SIZE) {
                    importBatchRecords(batchRecords, logFile);
                    addDeletedRecords(batchRecords);
                    countTotalRecords = countTotalRecords + batchRecords.size();
                    long totalTime = ((new Date()).getTime() - startTime) / 1000;
                    statisticsHarvester.add(totalTime);
                    startTime = (new Date()).getTime();
                    batchRecords.clear();
                } else if (maxRecord4Sample != -1 && maxRecord4Sample <= (countTotalRecords + batchRecords.size())) {
                    // test used when REPOX is getting a sample and the XML file has more records than the maxRecord4Sample
                    importBatchRecords(batchRecords, logFile);
                    addDeletedRecords(batchRecords);
                    StringUtil.simpleLog("Stop signal received. Sample set: max records number.", this.getClass(), logFile);
                    countTotalRecords = countTotalRecords + batchRecords.size();
                    stopExecution = true;
                }

                ingestStatus = Status.OK;
            } catch (Exception e) {
                if (stopExecution) {
                    if (forceStopExecution) {
                        ingestStatus = Task.Status.FORCE_EMPTY;
                    }
                    StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                    ingestStatus = Task.Status.CANCELED;
                } else {
                    log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                    StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                    ingestStatus = Status.ERRORS;
                }
            }
        }

        public void savePendingRecords() {
            if (stopExecution) { return; }

            try {
                if (batchRecords.size() > 0) {
                    importBatchRecords(batchRecords, logFile);
                    addDeletedRecords(batchRecords);
                    countTotalRecords = countTotalRecords + batchRecords.size();
                    batchRecords.clear();
                }
                ingestStatus = Status.OK;
            } catch (Exception e) {
                if (stopExecution) {
                    if (forceStopExecution) {
                        ingestStatus = Task.Status.FORCE_EMPTY;
                    }
                    StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                    ingestStatus = Task.Status.CANCELED;
                } else {
                    log.error("Error importing batch " + file.getAbsolutePath() + ": " + e.getMessage(), e);
                    StringUtil.simpleLog("Error importing file " + file.getAbsolutePath() + ": " + e.getMessage(), this.getClass(), logFile);
                    ingestStatus = Status.ERRORS;
                }
            }
        }

        public int getCountTotalRecords() {
            return countTotalRecords;
        }

        public void setCountTotalRecords(int countTotalRecords) {
            this.countTotalRecords = countTotalRecords;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }
    }

    @Override
    public int getTotalRecords2Harvest() {
        try {
            if (numberOfRecords2Harvest == -1) {
                File sourcesDir = new File(sourcesDirPath);
                File[] changedFiles = FileUtil.getChangedFiles(getLastUpdate(), sourcesDir.listFiles());
                numberOfRecords2Harvest = 0;

                for (File file : changedFiles) {
                    if (file.getName().endsWith(".zip")) {
                        // zip file
                        ZipInputStream in = new ZipInputStream(new FileInputStream(file));

                        // Folders inside the zip file with special characters in the name are not allowed
                        while ((in.getNextEntry()) != null) {
                            numberOfRecords2Harvest++;
                        }

                    } else if (file.getName().endsWith(".tar.gz")) {
                        // tar.gz file
                        File tempDir = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getTempDir();
                        List<File> listFiles = TarGz.unTarGz(file, tempDir);
                        numberOfRecords2Harvest += listFiles.size();
                    } else {
                        numberOfRecords2Harvest++;
                    }
                }
            }

            return numberOfRecords2Harvest;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public String getNumberOfRecords2HarvestStr() {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMAN);
        return numberFormat.format(getTotalRecords2Harvest());
        //        return String.valueOf(new File(sourcesDirPath).listFiles().length);
    }

    @Override
    public int getRecordsPerResponse() {
        return RECORDS_BATCH_SIZE;
    }

    @Override
    public ArrayList<Long> getStatisticsHarvester() {
        return statisticsHarvester;
    }
}
