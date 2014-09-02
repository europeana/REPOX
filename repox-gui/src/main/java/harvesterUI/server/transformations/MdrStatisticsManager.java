package harvesterUI.server.transformations;

import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.dataSet.SimpleDataSetInfo;
import harvesterUI.shared.mdr.MdrDataStatistics;
import harvesterUI.shared.mdr.SchemaMdrDataStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.metadataSchemas.MetadataSchema;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaVersion;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.util.ConfigSingleton;

/**
 * Created to Project REPOX-EUDML
 * User: Edmundo
 * Date: 28-05-2012
 * Time: 16:44
 */
public class MdrStatisticsManager {

    private Map<String,MdrDataStatistics> transformationStatisticsMap;
    private Map<String,SchemaMdrDataStatistics> schemaVersionsStatisticsMap;

    public MdrStatisticsManager() {

    }

    public void processMatches() throws ServerSideException{
        List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
        List<DataSource> dataSourceList = new ArrayList<DataSource>();
        for(Object object : allDataList){
            if(object instanceof DataSourceContainer){
                dataSourceList.add(((DataSourceContainer) object).getDataSource());
            }
        }

        Map<String,List<MetadataTransformation>> transformations =
                RepoxServiceImpl.getRepoxManager().getMetadataTransformationManager().getMetadataTransformations();
        Iterator iterator=transformations.entrySet().iterator();

        while(iterator.hasNext()){
            Map.Entry mapEntry=(Map.Entry)iterator.next();
            for (MetadataTransformation metadataTransformation : (List<MetadataTransformation>)mapEntry.getValue()) {
                int used = 0;
                List<SimpleDataSetInfo> simpleDataSetInfoList = new ArrayList<SimpleDataSetInfo>();
                for(DataSource dataSource : dataSourceList){
                    Map<String,MetadataTransformation> dataSetTransformations = dataSource.getMetadataTransformations();
                    for(MetadataTransformation dataSetTransformation :dataSetTransformations.values()){
                        if(metadataTransformation.getId().equals(dataSetTransformation.getId())){
                            SimpleDataSetInfo simpleDataSetInfo = new SimpleDataSetInfo(dataSource.getId(),dataSource.getDescription());
                            simpleDataSetInfoList.add(simpleDataSetInfo);
                            used++;
                        }
                    }
                }
                MdrDataStatistics mdrDataStatistics = new MdrDataStatistics(used,simpleDataSetInfoList);
                getTransformationStatisticsMap().put(metadataTransformation.getId(), mdrDataStatistics);
            }
        }
    }

    public void processSchemaStatistics() throws ServerSideException{
        List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
        List<DataSource> dataSourceList = new ArrayList<DataSource>();
        for(Object object : allDataList){
            if(object instanceof DataSourceContainer){
                dataSourceList.add(((DataSourceContainer) object).getDataSource());
            }
        }

        MetadataSchemaManager metadataSchemaManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager();
        MetadataTransformationManager mtdTransfManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager();

        for (MetadataSchema metadataSchema : metadataSchemaManager.getMetadataSchemas()) {
            for (MetadataSchemaVersion metadataSchemaVersion : metadataSchema.getMetadataSchemaVersions()) {
                int usedDS = 0;
                List<SimpleDataSetInfo> simpleDataSetInfoList = new ArrayList<SimpleDataSetInfo>();
                String versionLink = metadataSchemaVersion.getXsdLink();
                for(DataSource dataSource : dataSourceList){
                    if(dataSource.getSchema().equals(versionLink)){
                        SimpleDataSetInfo simpleDataSetInfo = new SimpleDataSetInfo(dataSource.getId(),dataSource.getDescription());
                        simpleDataSetInfoList.add(simpleDataSetInfo);
                        usedDS++;
                    }
                }

                int usedT = 0;
                List<MetadataTransformation> transfList = new ArrayList<MetadataTransformation>();
                for(String source : mtdTransfManager.getMetadataTransformations().keySet()) {
                    for(MetadataTransformation t : mtdTransfManager.getMetadataTransformations().get(source)) {
                        if(t.isMDRCompliant())
                            if(t.getSourceSchema().equals(versionLink) || t.getDestSchema().equals(versionLink)) {
                                usedT++;
                                transfList.add(t);
                            }
                    }
                }
                SchemaMdrDataStatistics mdrDataStatistics = new SchemaMdrDataStatistics(usedDS,simpleDataSetInfoList, usedT, null); //todo
                getSchemaVersionsStatisticsMap().put(metadataSchema.getShortDesignation()+metadataSchemaVersion.getVersion(), mdrDataStatistics);
            }
        }
    }

    public Map<String, MdrDataStatistics> getTransformationStatisticsMap() {
        if(transformationStatisticsMap == null){
            transformationStatisticsMap = new HashMap<String, MdrDataStatistics>();
        }
        return transformationStatisticsMap;
    }

    public Map<String, SchemaMdrDataStatistics> getSchemaVersionsStatisticsMap() {
        if(schemaVersionsStatisticsMap == null){
            schemaVersionsStatisticsMap = new HashMap<String, SchemaMdrDataStatistics>();
        }
        return schemaVersionsStatisticsMap;
    }
}
