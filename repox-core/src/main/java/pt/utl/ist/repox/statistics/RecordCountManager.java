package pt.utl.ist.repox.statistics;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.repox.util.XmlUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RecordCountManager {
    private static final Logger log = Logger.getLogger(RecordCountManager.class);
    private static final int CYCLE_TIME = 60 * 5;  						// 1 minute
    private static final int MAX_WAIT_TIME_CHANGES = 60 * 5;  		// 5 minutes
    private static final int MAX_WAIT_TIME_NO_CHANGES = 60 * 60; 	// 60 minutes
    private static final long MIN_WAIT_TIME_FULL_COUNT = 60 * 60 * 24 * 1000;		// number of milliseconds in a day

    private File configurationFile;
    private Calendar lastGenerationCalendar;
    private Calendar lastFullCount;
    private HashMap<String, RecordCount> recordCounts;

    public RecordCountManager(File configurationFile) throws DocumentException, ParseException {
        super();
        this.configurationFile = configurationFile;
        recordCounts = loadRecordCounts();
    }

    private HashMap<String, RecordCount> loadRecordCounts() throws DocumentException, ParseException {
        HashMap<String, RecordCount> recordCounts = new HashMap<String, RecordCount>();

        if(!configurationFile.exists()) {
            return recordCounts;
        }

        Element rootNode;
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(configurationFile);
            rootNode = document.getRootElement();
        }
        catch (Exception e) {
            log.error("Error loading Record Count configuration file", e);
            return recordCounts;
        }

        if(rootNode.elements("recordcount") != null) {
            for (Object recordCountObject : rootNode.elements("recordcount")) {
                Element recordCountElement = (Element) recordCountObject;
                Calendar lastCountDate = Calendar.getInstance();
                lastCountDate.setTime(new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT).parse(recordCountElement.elementText("lastCountDate")));
                Calendar lastCountWithChangesDate = Calendar.getInstance();
                lastCountWithChangesDate.setTime(new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT).parse(recordCountElement.elementText("lastCountWithChangesDate")));
                String dataSourceId = recordCountElement.elementText("dataSourceId");
                String deletedRecords = recordCountElement.elementText("deletedRecords");
                RecordCount recordCount = new RecordCount(
                        dataSourceId,
                        Integer.parseInt(recordCountElement.elementText("count")),
                        Integer.parseInt((deletedRecords == null || deletedRecords.isEmpty()) ? "0" : deletedRecords),
                        Integer.parseInt(recordCountElement.elementText("lastLineCounted")),
                        lastCountDate,
                        lastCountWithChangesDate);
                recordCounts.put(dataSourceId, recordCount);
            }
        }

        return recordCounts;
    }

    private RecordCount getCountFromRow(DataSource dataSource, Integer fromRow) throws SQLException {
        RecordCount recordCount;

        int[] recordCountLastrowPair;
        recordCountLastrowPair = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager()
                .getRecordCountLastrowPair(dataSource, fromRow, null, null);

        int count = recordCountLastrowPair[0];
        int lastRow = recordCountLastrowPair[1];
        int deleteRecords = recordCountLastrowPair[2];
        Calendar now = Calendar.getInstance();
        recordCount = new RecordCount(dataSource.getId(), count, deleteRecords, lastRow,  now, now);
        return recordCount;
    }

    private void saveRecordCounts(HashMap<String, RecordCount> recordCounts) throws IOException {
        Document document = DocumentHelper.createDocument();

        Element rootNode = document.addElement("recordcounts");

        for (RecordCount recordCount : recordCounts.values()) {
            if(recordCount != null){
                Element currentRecordNode = rootNode.addElement("recordcount");
                String lastCountDateString = new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT).format(recordCount.getLastCountDate().getTime());
                String lastCountWithChangesDateString = new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT).format(recordCount.getLastCountWithChangesDate().getTime());
                currentRecordNode.addElement("dataSourceId").addText(recordCount.getDataSourceId());
                currentRecordNode.addElement("count").addText(Integer.toString(recordCount.getCount()));
                currentRecordNode.addElement("lastLineCounted").addText(Integer.toString(recordCount.getLastLineCounted()));
                currentRecordNode.addElement("deletedRecords").addText(Integer.toString(recordCount.getDeleted()));
                currentRecordNode.addElement("lastCountDate").addText(lastCountDateString);
                currentRecordNode.addElement("lastCountWithChangesDate").addText(lastCountWithChangesDateString);
            }
        }

        XmlUtil.writePrettyPrint(configurationFile, document);
    }

    private RecordCount generateCount(String dataSourceId, boolean forceFullCount, boolean withDeleted) throws IOException, DocumentException, SQLException {
        DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId).getDataSource();
        if(dataSource == null) {
            throw new RuntimeException("Data Source not found: " + dataSourceId);
        }

        RecordCount recordCount;

        if(!forceFullCount && recordCounts.containsKey(dataSourceId)) {
            recordCount = recordCounts.get(dataSourceId);
            long now = System.currentTimeMillis();
            long lastChangesTimeDiff = (now -recordCount.getLastCountWithChangesDate().getTimeInMillis()) / 1000;
            long lastCountTimeDiff = (now - recordCount.getLastCountDate().getTimeInMillis()) / 1000;

            if(lastChangesTimeDiff <= MAX_WAIT_TIME_CHANGES || lastCountTimeDiff > MAX_WAIT_TIME_NO_CHANGES) {
                recordCount = getUpdatedRecordCount(dataSource, recordCount);
            }
        }
        else {
            recordCount = getCountFromRow(dataSource, null);
        }

        return recordCount;
    }

    private RecordCount getUpdatedRecordCount(DataSource dataSource, RecordCount currentRecordCount) throws SQLException {
        RecordCount newRecordCount = getCountFromRow(dataSource, currentRecordCount.getLastLineCounted());

        if(newRecordCount.getCount() > 0) {
            newRecordCount.setCount(currentRecordCount.getCount() + newRecordCount.getCount());
            currentRecordCount = newRecordCount;
        }
        else {
            currentRecordCount.setLastCountDate(newRecordCount.getLastCountDate());
            currentRecordCount.setDeleted(newRecordCount.getDeleted());
        }

        return currentRecordCount;
    }

    /**
     * Do full count starting at midnight on Saturday or any day at midnight if there is no previous full count in the last 24 hours
     *
     * @param calendar
     * @return boolean
     */
    public boolean isTimeForFullCount(Calendar calendar) {
        if(calendar.get(Calendar.HOUR_OF_DAY) == 0 && lastFullCount == null) {
            return true;
        }
        else if(calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                && (lastFullCount == null || calendar.getTimeInMillis() - lastFullCount.getTimeInMillis() > MIN_WAIT_TIME_FULL_COUNT)) {
            return true;
        }

        return false;
    }

    public boolean isTimeToRun(Calendar targetTime) {
        if(lastGenerationCalendar == null) {
            return true;
        }

        long timeDiff = (targetTime.getTimeInMillis() - lastGenerationCalendar.getTimeInMillis()) / 1000;
        return timeDiff > CYCLE_TIME;
    }

    public void generateCounts(boolean forceFullCount) throws IOException, DocumentException {
        Calendar generationStartTime = Calendar.getInstance();
        log.info("Generating Data Source counts" + (forceFullCount ? " forcing full count." : ""));



        HashMap<String, DataSourceContainer> dataSourceContainers = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().loadDataSourceContainers();
        HashMap<String, RecordCount> newRecordCounts = new HashMap<String, RecordCount>();

        for (DataSourceContainer dataSourceContainer : dataSourceContainers.values()) {
            try {
                log.debug("Starting count on Data Source: " + dataSourceContainer.getDataSource().getId());
                RecordCount recordCount = generateCount(dataSourceContainer.getDataSource().getId(), forceFullCount,true);
                newRecordCounts.put(dataSourceContainer.getDataSource().getId(), recordCount);
                log.debug("Finished count on Data Source: " + dataSourceContainer.getDataSource().getId() + " # records: " + recordCount.getCount());
            }
            catch (Exception e) {
                log.warn("Error generating Data Source record count", e);
            }
        }

        recordCounts = newRecordCounts;
        saveRecordCounts(recordCounts);
        lastGenerationCalendar = generationStartTime;
        if(forceFullCount) {
            lastFullCount = generationStartTime;
        }
    }

    public void updateDeletedRecordsCount(String dataSourceId, int deletedRecords) throws IOException {
        RecordCount recordCount = recordCounts.get(dataSourceId);
        if(recordCount == null) {
            return;
        }

        recordCount.setDeleted(recordCount.getDeleted() + deletedRecords);
        recordCounts.put(dataSourceId, recordCount);
        saveRecordCounts(recordCounts);
    }

    public void updateEraseRecordsCount(String dataSourceId, int eraseRecords, int deletedRecords) throws IOException {
        RecordCount recordCount = recordCounts.get(dataSourceId);
        if(recordCount == null) {
            return;
        }

        recordCount.setCount(recordCount.getCount() - eraseRecords);
        recordCount.setDeleted(recordCount.getDeleted() - deletedRecords);
        recordCounts.put(dataSourceId, recordCount);
        saveRecordCounts(recordCounts);
    }

    public void renameDataSourceCounts(String oldId, String newId) throws IOException {
        RecordCount oldDataSourceCount = recordCounts.remove(oldId);
        if(oldDataSourceCount != null){
            oldDataSourceCount.setDataSourceId(newId);
            recordCounts.put(newId, oldDataSourceCount);
        }
        saveRecordCounts(recordCounts);
    }

    public void removeDataSourceCounts(String dataSourceId) throws IOException {
        recordCounts.remove(dataSourceId);
        saveRecordCounts(recordCounts);

    }

    public RecordCount getRecordCount(String dataSourceId) throws IOException, DocumentException, SQLException {
        return getRecordCount(dataSourceId, false);
    }

    public RecordCount getRecordCount(String dataSourceId, boolean forceUpdate) throws IOException, DocumentException, SQLException {
        DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId).getDataSource();
        if(dataSource == null) {
            return new RecordCount(dataSourceId, 0, 0, 0, null, null);
        }

        if(forceUpdate) {
            RecordCount updatedRecordCount;

            if(recordCounts.containsKey(dataSourceId)) {
                RecordCount currentRecordCount = recordCounts.get(dataSourceId);
                updatedRecordCount = getUpdatedRecordCount(dataSource, currentRecordCount);
            }
            else {
                updatedRecordCount = generateCount(dataSourceId, true,true);
            }

            recordCounts.put(dataSourceId, updatedRecordCount);
            saveRecordCounts(recordCounts);

            return updatedRecordCount;
        }
        else {
            return recordCounts.get(dataSourceId);
        }
    }
}