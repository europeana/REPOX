package harvesterUI.server;

import harvesterUI.client.servlets.RepoxService;
import harvesterUI.server.dataManagement.RepoxDataExchangeManager;
import harvesterUI.server.projects.DefaultProjectManager;
import harvesterUI.server.projects.ProjectManager;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.admin.AdminInfo;
import harvesterUI.shared.dataTypes.admin.MainConfigurationInfo;
import harvesterUI.shared.externalServices.ExternalServiceResultUI;
import harvesterUI.shared.statistics.RepoxStatisticsUI;
import harvesterUI.shared.statistics.StatisticsType;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxConfiguration;
import pt.utl.ist.configuration.RepoxManager;
import pt.utl.ist.util.PropertyUtil;
import pt.utl.ist.util.TransformationResultLogger;
import pt.utl.ist.util.shared.ProjectType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RepoxServiceImpl extends RemoteServiceServlet implements RepoxService {

    private RepoxDataExchangeManager repoxDataExchangeManager = null;

    private static ProjectManager    projectManager;

    public RepoxServiceImpl() {
        repoxDataExchangeManager = new RepoxDataExchangeManager();

        PropertiesConfigurationLayout propertiesConfigrationLayout = PropertyUtil.loadGuiConfiguration("gui.properties");
        PropertiesConfiguration configuration = propertiesConfigrationLayout.getConfiguration();
        ProjectType projectType = ProjectType.valueOf(configuration.getProperty("project.type").toString());

        switch (projectType) {
        //            case LIGHT:
        //                projectManager = new LightManager();
        //                break;
        case DEFAULT:
        default:
            projectManager = new DefaultProjectManager();
            break;
        }
    }

    public static ProjectManager getProjectManager() {
        return projectManager;
    }

    public static RepoxManager getRepoxManager() throws ServerSideException {
        try {
            return ConfigSingleton.getRepoxContextUtil().getRepoxManager();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    @Override
    public Map<String, String> getFullCountryList() throws ServerSideException {
        try {
            return getProjectManager().getFullCountryList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    @Override
    public List<String> getFullCharacterEncodingList() throws ServerSideException {
        try {
            return repoxDataExchangeManager.getFullCharacterEncodingList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    @Override
    public AdminInfo loadAdminFormInfo() throws ServerSideException {
        return getProjectManager().loadAdminFormInfo();
    }

    @Override
    public void saveAdminFormInfo(AdminInfo results) throws ServerSideException {
        getProjectManager().saveAdminFormInfo(results);
    }

    @Override
    public RepoxStatisticsUI getStatisticsInfo(StatisticsType statisticsType, String username) throws ServerSideException {
        return getProjectManager().getStatisticsInfo(statisticsType, username);
    }

    @Override
    public String getRepoxVersion() throws ServerSideException {
        try {
            PropertiesConfigurationLayout propertiesConfigrationLayout = PropertyUtil.loadGuiConfiguration("gui.properties");
            PropertiesConfiguration configuration = propertiesConfigrationLayout.getConfiguration();
            return configuration.getProperty("repox.version").toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    @Override
    public MainConfigurationInfo getInitialConfigData() throws ServerSideException {
        try {
            PropertiesConfigurationLayout propertiesConfigrationLayout = PropertyUtil.loadGuiConfiguration("gui.properties");
            PropertiesConfiguration configuration = propertiesConfigrationLayout.getConfiguration();

            ProjectType projectType = ProjectType.valueOf(configuration.getProperty("project.type").toString());
            String repositoryFodlerPath = RepoxServiceImpl.getRepoxManager().getConfiguration().getRepositoryPath();
            MainConfigurationInfo mainConfigurationInfo = new MainConfigurationInfo(projectType, repositoryFodlerPath);

            if (getProjectManager() instanceof DefaultProjectManager) {
                String defaultExportFolder = ((DefaultRepoxConfiguration)RepoxServiceImpl.getRepoxManager().getConfiguration()).getExportDefaultFolder();
                mainConfigurationInfo.setDefaultExportFolder(defaultExportFolder);
            }

            return mainConfigurationInfo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    @Override
    public ExternalServiceResultUI getValidationState(String dataSetID) throws ServerSideException {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new URL(dataSetID));

            String reportFile = document.valueOf("//response/reportFile");

            if (reportFile == null || reportFile.equals("NOT_FOUND"))
                return null;

            String state = document.valueOf("//report/status");
            String htmlReport = document.valueOf("//report/warnings/@resultFile");

            return new ExternalServiceResultUI(state, htmlReport);
        } catch (DocumentException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    @Override
    public Double getTimezoneOffset(String clientTimezone) {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("Z");
        String serverOffset = simpleDateFormat.format(now);

        double clientTimeOffset = getClientTimeInUTC(clientTimezone);
        double serverTimeOffset = getServerTimeInUTC(serverOffset);

        if (clientTimeOffset == serverTimeOffset)
            return 0.0;
        else
            return serverTimeOffset + clientTimeOffset;
    }

    public double getClientTimeInUTC(String time) {
        if (time.contains("+")) {
            return getSplitTime(time, "+");
        } else if (time.contains("-")) {
            return getSplitTime(time, "-");
        }
        return 0;
    }

    private double getSplitTime(String value, String operator) {
        double finalValue;
        String[] tokens = value.split("\\" + operator);
        String hourPart = tokens[1];
        if (hourPart.contains(":")) {
            String[] minTokens = hourPart.split(":");
            finalValue = Double.valueOf(minTokens[0]) + 0.5;
        } else
            finalValue = Double.valueOf(hourPart);

        if (operator.equals("-"))
            return -finalValue;
        else
            return finalValue;
    }

    public double getServerTimeInUTC(String time) {
        if (time.contains("+")) {
            return getServerSplitTime(time, "+");
        } else if (time.contains("-")) {
            return getServerSplitTime(time, "-");
        }
        return 0;
    }

    private double getServerSplitTime(String value, String operator) {
        double finalValue;
        String[] tokens = value.split("\\" + operator);
        String hourAndMinPart = tokens[1];
        if (hourAndMinPart.endsWith("30")) {
            int hours = Integer.valueOf(hourAndMinPart.substring(0, 2));
            finalValue = Double.valueOf(hours) + 0.5;
        } else {
            String serverTime = hourAndMinPart.replace("0", "");
            if (serverTime.isEmpty())
                finalValue = 0.0;
            else
                finalValue = Double.valueOf(serverTime);
        }

        if (operator.equals("-"))
            return -finalValue;
        else
            return finalValue;
    }

    @Override
    public Boolean transformationResultFileExists(String dataSetId, String transformationId) {
        return TransformationResultLogger.hasTransformationResultFile(dataSetId, transformationId);
    }
}
