package pt.utl.ist.repox.dataProvider.dataSource;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.RepoxContextUtilDefault;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.repox.util.XmlUtil;
import pt.utl.ist.util.date.DateUtil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

/**
 */
public class DataSourcesMonitor {
    private static final Logger       log                   = Logger.getLogger(DataSourcesMonitor.class);

    private static final long         MAX_TEST_TIME         = 120000;                                    // 120 seconds to consider failure
    private static final long         MIN_WAIT_TIME_MONITOR = 60 * 60 * 2 * 1000;                        // number of milliseconds in 2 hours

    private static DataSourcesMonitor monitorInstance;

    private File                      configurationFile;
    private Calendar                  lastMonitoringTime;
    private Map<String, Boolean>      dataSourcesState;                                                  //Map with key Data Source id and value working state.

    @SuppressWarnings("javadoc")
    public Calendar getLastMonitoringTime() {
        return lastMonitoringTime;
    }

    @SuppressWarnings("javadoc")
    public void setLastMonitoringTime(Calendar lastMonitoringTime) {
        this.lastMonitoringTime = lastMonitoringTime;
    }

    @SuppressWarnings("javadoc")
    public Map<String, Boolean> getDataSourcesState() {
        return dataSourcesState;
    }

    @SuppressWarnings("javadoc")
    public void setDataSourcesState(Map<String, Boolean> dataSourcesState) {
        this.dataSourcesState = dataSourcesState;
    }

    private DataSourcesMonitor() {
        configurationFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getXmlConfigPath(), RepoxContextUtilDefault.DATA_SOURCES_STATE_FILENAME);
    }

    /**
     * @return a monitor instance
     */
    public static DataSourcesMonitor getInstance() {
        if (monitorInstance == null) {
            monitorInstance = new DataSourcesMonitor();
            try {
                monitorInstance.loadXML();
            } catch (Exception e) {
                log.error("Error loading Data Source states from file " + RepoxContextUtilDefault.DATA_SOURCES_STATE_FILENAME, e);
            }
        }
        return monitorInstance;
    }

    private void loadXML() throws DocumentException, ParseException {
        if (!configurationFile.exists()) { return; }

        SAXReader reader = new SAXReader();
        Document document = reader.read(configurationFile);

        if (document.getRootElement().attributeValue("timestamp") != null) {
            lastMonitoringTime = Calendar.getInstance();
            lastMonitoringTime.setTime(DateUtil.string2Date(document.getRootElement().attributeValue("timestamp"), TimeUtil.LONG_DATE_FORMAT));
        }

        if (!document.getRootElement().elements("dataSourceState").isEmpty()) {
            dataSourcesState = new TreeMap<String, Boolean>();

            for (Element currentDataSourceState : (List<Element>)document.getRootElement().elements("dataSourceState")) {
                dataSourcesState.put(currentDataSourceState.elementText("dataSourceId"), Boolean.valueOf(currentDataSourceState.elementText("state")));
            }
        }
    }

    private void saveXML() throws IOException {
        if (lastMonitoringTime == null) { return; }

        Document document = DocumentHelper.createDocument();
        Element rootNode = document.addElement("dataSourcesState");
        rootNode.addAttribute("timestamp", DateUtil.date2String(lastMonitoringTime.getTime(), TimeUtil.LONG_DATE_FORMAT));

        for (Entry<String, Boolean> currentEntry : dataSourcesState.entrySet()) {
            Element stateElement = rootNode.addElement("dataSourceState");
            stateElement.addElement("dataSourceId").setText(currentEntry.getKey());
            stateElement.addElement("state").setText(currentEntry.getValue().toString());
        }

        XmlUtil.writePrettyPrint(configurationFile, document);
    }

    /**
     * True if calendar has 0 hours and there was no monitoring in the last 2
     * hours.
     * 
     * @param calendar
     * @return boolean
     */
    public boolean isTimeForMonitoring(Calendar calendar) {
        if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && (lastMonitoringTime == null || calendar.getTimeInMillis() - lastMonitoringTime.getTimeInMillis() > MIN_WAIT_TIME_MONITOR)) { return true; }

        return false;
    }

    /**
     * Receives a list of Data Sources and monitors the state of the Data
     * Sources, saving it in the property dataSourcesState.
     * 
     * @param dataSourceContainers
     */
    public void monitorDataSources(HashMap<String, DataSourceContainer> dataSourceContainers) {
        dataSourcesState = new TreeMap<String, Boolean>();

        for (DataSourceContainer dataSourceContainer : dataSourceContainers.values()) {
            try {
                boolean isWorking;

                isWorking = monitorDataSource(dataSourceContainer.getDataSource());

                dataSourcesState.put(dataSourceContainer.getDataSource().getId(), isWorking);
            } catch (Exception e) {
                log.error("Error checking if Data Source " + dataSourceContainer.getDataSource().getId() + " is working", e);
            }
        }

        lastMonitoringTime = Calendar.getInstance();

        try {
            saveXML();
        } catch (IOException e) {
            log.error("Error saving Data Sources state XML", e);
        }
    }

    /**
     * @param dataSource
     */
    public void updateDataSource(DataSource dataSource) {
        try {
            boolean isDataSourceWorking = monitorDataSource(dataSource);
            if (dataSourcesState == null) {
                dataSourcesState = new TreeMap<String, Boolean>();
            }
            dataSourcesState.put(dataSource.getId(), isDataSourceWorking);

            saveXML();
        } catch (InterruptedException e) {
            log.error("Error checking if Data Source " + dataSource.getId() + " is working", e);
        } catch (IOException e) {
            log.error("Error saving Data Sources state XML", e);
        }
    }

    private boolean monitorDataSource(DataSource dataSource) throws InterruptedException {
        boolean isWorking;
        DataSourceTester dataSourceTester = new DataSourceTester(dataSource);
        Thread testerThread = new Thread(dataSourceTester);
        testerThread.run();
        testerThread.join(MAX_TEST_TIME);

        if (testerThread.isAlive()) {
            log.warn("Data Source with id " + dataSource.getId() + " considered not working. Took longer than " + MAX_TEST_TIME + "ms");
            testerThread.interrupt();
            isWorking = false;
        } else {
            isWorking = dataSourceTester.isWorking();
        }
        return isWorking;
    }
}
