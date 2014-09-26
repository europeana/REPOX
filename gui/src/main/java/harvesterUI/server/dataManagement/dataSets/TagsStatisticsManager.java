package harvesterUI.server.dataManagement.dataSets;

import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.dataSet.SimpleDataSetInfo;
import harvesterUI.shared.mdr.MdrDataStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.dataSource.DataSourceTag;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 28-05-2012
 * Time: 16:44
 */
public class TagsStatisticsManager {

    private Map<String,MdrDataStatistics> tagsStatisticsMap;

    public TagsStatisticsManager() {}

    public void processMatches() throws ServerSideException{
        List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
        List<DataSource> dataSourceList = new ArrayList<DataSource>();
        for(Object object : allDataList){
            if(object instanceof DataSourceContainer){
                dataSourceList.add(((DataSourceContainer) object).getDataSource());
            }
        }

        for(DataSourceTag dataSourceTag : ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTagsManager().getTags()){
            int used = 0;
            List<SimpleDataSetInfo> simpleDataSetInfoList = new ArrayList<SimpleDataSetInfo>();
            for(DataSource dataSource : dataSourceList){
                for(DataSourceTag currentDataSourceTag : dataSource.getTags()){
                    if(dataSourceTag.getName().equals(currentDataSourceTag.getName())){
                        SimpleDataSetInfo simpleDataSetInfo = new SimpleDataSetInfo(dataSource.getId(),dataSource.getDescription());
                        simpleDataSetInfoList.add(simpleDataSetInfo);
                        used++;
                    }
                }
            }
            MdrDataStatistics mdrDataStatistics = new MdrDataStatistics(used,simpleDataSetInfoList);
            getTagsStatisticsMap().put(dataSourceTag.getName(), mdrDataStatistics);
        }
    }

    public Map<String, MdrDataStatistics> getTagsStatisticsMap() {
        if(tagsStatisticsMap == null){
            tagsStatisticsMap = new HashMap<String, MdrDataStatistics>();
        }
        return tagsStatisticsMap;
    }
}
