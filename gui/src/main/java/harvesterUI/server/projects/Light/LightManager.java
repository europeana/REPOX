package harvesterUI.server.projects.Light;

import harvesterUI.server.dataManagement.dataSets.DataSetOperationsServiceImpl;
import harvesterUI.server.projects.EuDMLAndLightManager;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.DatasetType;

import java.util.List;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.configuration.LightRepoxManager;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 30-04-2012
 * Time: 11:29
 */
public class LightManager extends EuDMLAndLightManager {

    public LightManager() {
        super();
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
    }

    public SaveDataResponse saveDataProvider(boolean update, DataProviderUI dataProviderUI, int pageSize, String username) throws ServerSideException {
        try{
            return LightSaveData.saveDataProvider(update, dataProviderUI, pageSize, username);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String deleteDataProviders(List<DataProviderUI> dataProviderUIs) throws ServerSideException{
        try{
            return LightSaveData.deleteDataProviders(dataProviderUIs);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public SaveDataResponse saveDataSource(boolean update, DatasetType type, String originalDSset, DataSourceUI dataSourceUI, int pageSize) throws ServerSideException {
        try{
            return LightSaveData.saveDataSource(update, type, originalDSset, dataSourceUI,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String addAllOAIURL(String url,String dataProviderID,String dsSchema,String dsNamespace,
                               String dsMTDFormat, String name, String nameCode, String exportPath,DataSetOperationsServiceImpl dataSetOperationsService) throws ServerSideException{
        try{
            // Check http URLs
            String checkUrlResult = DataSetOperationsServiceImpl.checkURL(url);
            if(checkUrlResult.equals("URL_MALFORMED"))
                return "URL_MALFORMED";
            else if(checkUrlResult.equals("URL_NOT_EXISTS"))
                return "URL_NOT_EXISTS";

            LightSaveData.addAllOAIURL(url.trim(), dataProviderID, dsSchema, dsNamespace, dsMTDFormat, dataSetOperationsService.checkOAIURL(url.trim()));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return "SUCCESS";
    }

    public String deleteDataSources(List<DataSourceUI> dataSourceUIs) throws ServerSideException{
        try{
            return LightSaveData.deleteDataSources(dataSourceUIs);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Boolean dataSourceExport(DataSourceUI dataSourceUI) throws ServerSideException{
        try {
            LightRepoxManager repoxManagerDefault = (LightRepoxManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager();
            DataSourceContainer dataSourceContainer = repoxManagerDefault.getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet());

            DataSource dataSource = dataSourceContainer.getDataSource();
            dataSource.setExportDir(dataSourceUI.getExportDirectory());

            String recordsPerFile;
            if(dataSourceUI.getRecordsPerFile().equals("All"))
                recordsPerFile = "-1";
            else
                recordsPerFile = dataSourceUI.getRecordsPerFile();

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                    startExportDataSource(dataSourceUI.getDataSourceSet(), recordsPerFile, dataSourceUI.getExportFormat());
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return true;
    }

}
