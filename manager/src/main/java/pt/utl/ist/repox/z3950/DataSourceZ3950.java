package pt.utl.ist.repox.z3950;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import pt.utl.ist.repox.configuration.ConfigSingleton;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.dataSource.RecordIdPolicy;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.reports.LogUtil;
import pt.utl.ist.repox.statistics.RecordCountManager;
import pt.utl.ist.repox.task.Task;
import pt.utl.ist.repox.task.Task.Status;
import pt.utl.ist.repox.util.StringUtil;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.repox.util.date.DateUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 */
public class DataSourceZ3950 extends DataSource {
    private static final Logger log = Logger.getLogger(DataSourceZ3950.class);

    private Harvester       harvestMethod;

    @SuppressWarnings("javadoc")
    public Harvester getHarvestMethod() {
        return harvestMethod;
    }

    @SuppressWarnings("javadoc")
    public void setHarvestMethod(Harvester harvestMethod) {
        this.harvestMethod = harvestMethod;
    }

    /**
     * Creates a new instance of this class.
     */
    public DataSourceZ3950() {
        super();
        this.metadataFormat = MetadataFormat.MarcXchange.toString();
    }

    /**
     * Creates a new instance of this class.
     * @param dataProvider
     * @param id
     * @param description
     * @param schema
     * @param namespace
     * @param harvestMethod
     * @param recordIdPolicy
     * @param metadataTransformations
     */
    public DataSourceZ3950(DataProvider dataProvider, String id, String description, String schema, String namespace, Harvester harvestMethod, RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations) {
        super(dataProvider, id, description, schema, namespace, MetadataFormat.MarcXchange.toString(), recordIdPolicy, metadataTransformations);
        this.harvestMethod = harvestMethod;

    }

    @Override
    public Status ingestRecords(File logFile, boolean fullIngest) throws IOException, DocumentException, SQLException {
        System.out.println("INGESTING NOW - DataSourceZ3950!");
        Status ingestStatus = Task.Status.OK;

        Date startIngestTime = new Date();
        LogUtil.startLogInfo(logFile, startIngestTime, StatusDS.RUNNING.name(), id);

        if (harvestMethod.isFullIngestExclusive() || fullIngest) {
            boolean successfulDeletion = emptyRecords();

            if (!successfulDeletion) {
                StringUtil.simpleLog("Importing aborted - unable to delete the current Records", this.getClass(), logFile);
                LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.ERROR.name(), id, lastIngestCount, lastIngestDeletedCount);
                return Task.Status.FAILED;
            }

            //Clear the last ingest date
            setLastUpdate(null);

            //Update the XML file
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
        }
        Class harvestMethodClass = getHarvestMethod().getClass();

        if (harvestMethodClass == IdSequenceHarvester.class) {
            harvestMethod = new IdSequenceHarvester(harvestMethod.getTarget(), ((IdSequenceHarvester)harvestMethod).getMaximumId());
        } else if (harvestMethodClass == TimestampHarvester.class) {
            harvestMethod = new TimestampHarvester(harvestMethod.getTarget(), ((TimestampHarvester)harvestMethod).getEarliestTimestamp());
        } else if (harvestMethodClass == IdListHarvester.class) {
            harvestMethod = new IdListHarvester(harvestMethod.getTarget(), ((IdListHarvester)harvestMethod).getIdListFile());
        }

        try {
            StringUtil.simpleLog("Importing from Z39.50 Source: " + harvestMethod.getTarget().getAddress() + " using " + harvestMethod.getClass().getSimpleName(), this.getClass(), logFile);

            List<RecordRepox> batchRecords = new ArrayList<RecordRepox>();
            harvestMethod.init();

            TimeUtil.getTimeSinceLastTimerArray(1);

            Iterator<RecordRepox> recordIterator = harvestMethod.getIterator(this, logFile, fullIngest);

            while (recordIterator.hasNext()) {
                if (stopExecution) {
                    if (forceStopExecution) {
                        ingestStatus = Task.Status.FORCE_EMPTY;
                    } else {
                        StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                        ingestStatus = Task.Status.CANCELED;
                    }
                    break;
                }

                RecordRepox newRecord = recordIterator.next();
                if (newRecord != null) {
                    batchRecords.add(newRecord);
                } else {
                    System.out.println(" = ");
                }

                if (maxRecord4Sample == -1 && batchRecords.size() >= RECORDS_BATCH_SIZE) {
                    importBatchRecords(batchRecords, logFile);
                    batchRecords = new ArrayList<RecordRepox>();
                    batchRecords.clear();
                } else if (maxRecord4Sample != -1 && maxRecord4Sample <= batchRecords.size()) {
                    importBatchRecords(batchRecords, logFile);
                    batchRecords = new ArrayList<RecordRepox>();
                    StringUtil.simpleLog("Stop signal received. Sample set: max records number.", this.getClass(), logFile);
                    ingestStatus = Task.Status.OK;
                    break;
                }
            }

            // Import remaining records
            importBatchRecords(batchRecords, logFile);
            addDeletedRecords(batchRecords);
            //            batchRecords = new ArrayList<RecordRepox>();
            //liz
            //            ingestStatus = Task.Status.OK;

        } catch (Exception e) {
            if (stopExecution) {
                if (forceStopExecution) {
                    ingestStatus = Task.Status.FORCE_EMPTY;
                } else {
                    StringUtil.simpleLog("Received stop signal: exiting import.", this.getClass(), logFile);
                    ingestStatus = Task.Status.CANCELED;
                }
            } else {
                log.error("Error ingesting records", e);
                StringUtil.simpleLog("Error ingesting records " + e.getMessage(), e, this.getClass(), logFile);
                ingestStatus = Task.Status.FAILED;
            }
        }

        finally {
            harvestMethod.cleanup();
        }

        LogUtil.endLogInfo(logFile, startIngestTime, new Date(), ingestStatus.name(), id, lastIngestCount, lastIngestDeletedCount);

        return ingestStatus;
    }

    private void importBatchRecords(List<RecordRepox> batchRecords, File logFile) throws IOException, DocumentException, SQLException {

        /*System.out.println("****************************************************************************************************");
        System.out.println("********************************************************batchRecords.size() = " + batchRecords.size());
        System.out.println("****************************************************************************************************");
        System.out.println("****************************************************************************************************");
        */

        long memBefore = Runtime.getRuntime().totalMemory() / (1024 * 1024);
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

        double importTime = TimeUtil.getTimeSinceLastTimerArray(9) / 1000.0;
        long memAfter = Runtime.getRuntime().totalMemory() / (1024 * 1024);

        if (batchRecords.size() != 0) {
            log.info(batchRecords.size() + " records imported in " + importTime + "s." + " Memory before/after (MB) : " + memBefore + "/" + memAfter);
            StringUtil.simpleLog(batchRecords.size() + " records imported", this.getClass(), logFile);
            lastIngestCount += batchRecords.size();
        }
    }

    @Override
    public boolean isWorking() {
        throw new RuntimeException("Unimplemented Operation");
    }

    @Override
    public Element addSpecificInfo(Element sourceElement) {
        sourceElement.addAttribute("type", "DataSourceZ3950");

        Harvester harvestMethod = getHarvestMethod();
        Target target = harvestMethod.getTarget();
        Element targetElement = sourceElement.addElement("target");
        targetElement.addElement("address").setText(target.getAddress());
        targetElement.addElement("port").setText(String.valueOf(target.getPort()));
        targetElement.addElement("database").setText(target.getDatabase());
        targetElement.addElement("user").setText((target.getUser() != null ? target.getUser() : ""));
        targetElement.addElement("password").setText(target.getPassword() != null ? target.getPassword() : "");
        targetElement.addElement("charset").setText(target.getCharacterEncoding() != null ? target.getCharacterEncoding().toString() : "");

        targetElement.addElement("recordSyntax").setText(target.getRecordSyntax());

        if (harvestMethod instanceof TimestampHarvester) {

            sourceElement.addElement("harvestMethod").setText(TimestampHarvester.class.getSimpleName());
            TimestampHarvester timestampHarvester = (TimestampHarvester)harvestMethod;
            String timestampString = DateUtil.date2String(timestampHarvester.getEarliestTimestamp(), "yyyyMMdd");
            sourceElement.addElement("earliestTimestamp").setText(timestampString);

        } else if (harvestMethod instanceof IdListHarvester) {

            sourceElement.addElement("harvestMethod").setText(IdListHarvester.class.getSimpleName());
            IdListHarvester idListHarvester = (IdListHarvester)harvestMethod;
            sourceElement.addElement("idListFile").setText(idListHarvester.getIdListFile().getAbsolutePath());

        } else if (harvestMethod instanceof IdSequenceHarvester) {
            sourceElement.addElement("harvestMethod").setText(IdSequenceHarvester.class.getSimpleName());
            IdSequenceHarvester idSequenceHarvester = (IdSequenceHarvester)harvestMethod;
            if (idSequenceHarvester.getMaximumId() != null) {
                sourceElement.addElement("maximumId").setText(String.valueOf(idSequenceHarvester.getMaximumId()));
            }
        }
        return sourceElement;
    }

    @Override
    public int getTotalRecords2Harvest() {
        return 0;
    }

    @Override
    public String getNumberOfRecords2HarvestStr() {
        return "";
    }

    @Override
    public int getRecordsPerResponse() {
        return -1;
    }

    @Override
    public ArrayList<Long> getStatisticsHarvester() {
        return null;
    }
}
