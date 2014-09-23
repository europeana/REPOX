package harvesterUI.server;

import harvesterUI.client.servlets.RepoxService;
import harvesterUI.server.dataManagement.RepoxDataExchangeManager;
import harvesterUI.server.projects.ProjectManager;
import harvesterUI.server.projects.Light.LightManager;
import harvesterUI.server.projects.europeana.EuropeanaManager;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ProjectType;
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
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import pt.utl.ist.repox.RepoxManager;
import pt.utl.ist.repox.configuration.RepoxConfigurationEuropeana;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.PropertyUtil;
import pt.utl.ist.repox.util.TransformationResultLogger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RepoxServiceImpl extends RemoteServiceServlet implements RepoxService {

    private RepoxDataExchangeManager repoxDataExchangeManager = null;

    private static ProjectManager projectManager;

    public RepoxServiceImpl() {
        repoxDataExchangeManager = new RepoxDataExchangeManager();

        Properties properties = PropertyUtil.loadGuiConfiguration("gui.properties");
        ProjectType projectType = ProjectType.valueOf(properties.getProperty("project.type"));

        switch (projectType){
            case LIGHT:
                projectManager = new LightManager();
                break;
            case EUROPEANA:
                projectManager = new EuropeanaManager();
                break;
        }
    }

    public static ProjectManager getProjectManager(){
        return projectManager;
    }

    public static RepoxManager getRepoxManager() throws ServerSideException{
        try{
            return ConfigSingleton.getRepoxContextUtil().getRepoxManager();
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    /**
     *
     * Data Lists
     *
     */

    public Map<String,String> getFullCountryList() throws ServerSideException{
        try{
            return getProjectManager().getFullCountryList();
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<String> getFullCharacterEncodingList() throws ServerSideException{
        try{
            return repoxDataExchangeManager.getFullCharacterEncodingList();
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    /*
    * Administrator settings
    */
    public AdminInfo loadAdminFormInfo() throws ServerSideException{
        return getProjectManager().loadAdminFormInfo();
    }

    public void saveAdminFormInfo(AdminInfo results) throws ServerSideException{
        getProjectManager().saveAdminFormInfo(results);
    }

    public RepoxStatisticsUI getStatisticsInfo(StatisticsType statisticsType,String username) throws ServerSideException{
        return getProjectManager().getStatisticsInfo(statisticsType,username);
    }

    public String getRepoxVersion() throws ServerSideException{
        try{
            Properties properties = PropertyUtil.loadGuiConfiguration("gui.properties");
            return properties.getProperty("repox.version");
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public MainConfigurationInfo getInitialConfigData() throws ServerSideException{
        try{
            Properties properties = PropertyUtil.loadGuiConfiguration("gui.properties");

            ProjectType projectType = ProjectType.valueOf(properties.getProperty("project.type"));
            String repositoryFodlerPath = RepoxServiceImpl.getRepoxManager().getConfiguration().getRepositoryPath();
            MainConfigurationInfo mainConfigurationInfo = new MainConfigurationInfo(projectType,repositoryFodlerPath);

            if(getProjectManager() instanceof EuropeanaManager){
                String defaultExportFolder = ((RepoxConfigurationEuropeana)RepoxServiceImpl.getRepoxManager().getConfiguration()).getExportDefaultFolder();
                mainConfigurationInfo.setDefaultExportFolder(defaultExportFolder);
            }

            return mainConfigurationInfo;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public ExternalServiceResultUI getValidationState(String dataSetID) throws ServerSideException {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new URL(dataSetID));

            String reportFile = document.valueOf("//response/reportFile");

            if(reportFile == null || reportFile.equals("NOT_FOUND"))
                return null;

            String state = document.valueOf("//report/status");
            String htmlReport = document.valueOf("//report/warnings/@resultFile");

            return new ExternalServiceResultUI(state,htmlReport);
        }catch (DocumentException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Double getTimezoneOffset(String clientTimezone){
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("Z");
        String serverOffset = simpleDateFormat.format(now);

        double clientTimeOffset = getClientTimeInUTC(clientTimezone);
        double serverTimeOffset = getServerTimeInUTC(serverOffset);

        if(clientTimeOffset == serverTimeOffset)
            return 0.0;
        else
            return serverTimeOffset+clientTimeOffset;
    }

    public double getClientTimeInUTC(String time){
        if(time.contains("+")){
            return getSplitTime(time,"+");
        }else if(time.contains("-")){
            return getSplitTime(time,"-");
        }
        return 0;
    }

    private double getSplitTime(String value, String operator){
        double finalValue;
        String[] tokens = value.split("\\" + operator);
        String hourPart = tokens[1];
        if(hourPart.contains(":")){
            String[] minTokens = hourPart.split(":");
            finalValue = Double.valueOf(minTokens[0])+0.5;
        } else
            finalValue = Double.valueOf(hourPart);

        if(operator.equals("-"))
            return -finalValue;
        else
            return finalValue;
    }

    public double getServerTimeInUTC(String time){
        if(time.contains("+")){
            return getServerSplitTime(time, "+");
        }else if(time.contains("-")){
            return getServerSplitTime(time, "-");
        }
        return 0;
    }

    private double getServerSplitTime(String value, String operator){
        double finalValue;
        String[] tokens = value.split("\\" + operator);
        String hourAndMinPart = tokens[1];
        if(hourAndMinPart.endsWith("30")){
            int hours = Integer.valueOf(hourAndMinPart.substring(0,2));
            finalValue = Double.valueOf(hours)+0.5;
        } else{
            String serverTime = hourAndMinPart.replace("0","");
            if(serverTime.isEmpty())
                finalValue = 0.0;
            else
                finalValue = Double.valueOf(serverTime);
        }

        if(operator.equals("-"))
            return -finalValue;
        else
            return finalValue;
    }

    public Boolean transformationResultFileExists(String dataSetId, String transformationId){
        return TransformationResultLogger.hasTransformationResultFile(dataSetId,transformationId);
    }
}
