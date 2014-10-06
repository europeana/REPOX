package harvesterUI.server.projects.Light;

import harvesterUI.server.dataManagement.dataSets.Z39FileUpload;
import harvesterUI.server.userManagement.UserManagementServiceImpl;
import harvesterUI.server.util.PagingUtil;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;
import harvesterUI.shared.dataTypes.dataSet.DataSetTagUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.DatasetType;
import harvesterUI.shared.externalServices.ExternalServiceUI;
import harvesterUI.shared.externalServices.ServiceParameterUI;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dom4j.DocumentException;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.LightRepoxManager;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.LightDataSourceContainer;
import pt.utl.ist.dataProvider.MessageType;
import pt.utl.ist.dataProvider.dataSource.DataSourceTag;
import pt.utl.ist.dataProvider.dataSource.IdProvidedRecordIdPolicy;
import pt.utl.ist.externalServices.ExternalRestService;
import pt.utl.ist.externalServices.ExternalServiceNoMonitor;
import pt.utl.ist.externalServices.ExternalServiceStates;
import pt.utl.ist.externalServices.ExternalServiceType;
import pt.utl.ist.externalServices.ServiceParameter;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.util.FileUtilSecond;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.IncompatibleInstanceException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 04-07-2011
 * Time: 13:36
 */
public class LightSaveData {

    public static SaveDataResponse saveDataProvider(boolean update,DataProviderUI dataProviderUI, int pageSize, String username) {
        SaveDataResponse saveDataResponse = new SaveDataResponse();
        if(update) {
            DataProvider dataProvider = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProvider(dataProviderUI.getId());
            if(dataProvider != null) {
                try {
                    dataProvider = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().updateDataProvider(dataProvider.getId(),
                            dataProviderUI.getName(), dataProviderUI.getCountry(), dataProviderUI.getDescription());
                    UserManagementServiceImpl.getInstance().addDPtoUser(username,dataProvider.getId());
                    saveDataResponse.setPage(PagingUtil.getDataPage(dataProvider.getId(),pageSize));
                    saveDataResponse.setResponseState(ResponseState.SUCCESS);
                } catch (ObjectNotFoundException e) {
                    saveDataResponse.setResponseState(ResponseState.NOT_FOUND);
                } catch (IOException e) {
                    saveDataResponse.setResponseState(ResponseState.OTHER);
                }
            }
        } else {
            try {
                DataProvider dataProvider = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().createDataProvider(dataProviderUI.getName(),
                        dataProviderUI.getCountry(), dataProviderUI.getDescription());
                UserManagementServiceImpl.getInstance().addDPtoUser(username,dataProvider.getId());
                saveDataResponse.setPage(PagingUtil.getDataPage(dataProvider.getId(),pageSize));
                saveDataResponse.setResponseState(ResponseState.SUCCESS);
            } catch (IOException e) {
                saveDataResponse.setResponseState(ResponseState.OTHER);
            } catch (AlreadyExistsException e) {
                saveDataResponse.setResponseState(ResponseState.ALREADY_EXISTS);
            }
        }
        return saveDataResponse;
    }

    public static String deleteDataProviders(List<DataProviderUI> dataProviderUIs) {
        for (DataProviderUI dataProvider : dataProviderUIs) {
            try {
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().deleteDataProvider(dataProvider.getId());
            } catch (IOException e) {
                return MessageType.OTHER.name();
            } catch (ObjectNotFoundException e) {
                return MessageType.NOT_FOUND.name();
            }
            UserManagementServiceImpl.getInstance().removeDPFromUsers(dataProvider.getId());
        }
        return MessageType.OK.name();
    }

    // DATA SOURCES
    public static SaveDataResponse saveDataSource(boolean update, DatasetType type, String originalDSset, DataSourceUI dataSourceUI, int pageSize) throws ServerSideException {
        SaveDataResponse saveDataResponse = new SaveDataResponse();
        try {
            LightRepoxManager repoxManagerDefault = (LightRepoxManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager();

            ResponseState urlStatus = Util.getUrlStatus(dataSourceUI);
            if(urlStatus != null){
                saveDataResponse.setResponseState(urlStatus);
                return saveDataResponse;
            }

            MetadataTransformationManager metadataTransformationManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager();
            Map<String, MetadataTransformation> metadataTransformations = new HashMap<String, MetadataTransformation>();
            for(TransformationUI transformationUI: dataSourceUI.getMetadataTransformations()) {
                MetadataTransformation loadedTransformation = metadataTransformationManager.loadMetadataTransformation(transformationUI.getIdentifier());
                metadataTransformations.put(transformationUI.getIdentifier(),loadedTransformation);
            }

            // Save external services
            List<ExternalRestService> externalRestServices = saveExternalServices(dataSourceUI);

            if(update) {
                // Check if the id already exists
                DataSourceContainer dataSourceContainer = repoxManagerDefault.getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet());
                DataSourceContainer originalDSC = repoxManagerDefault.getDataManager().getDataSourceContainer(originalDSset);
                if(dataSourceContainer != null && !originalDSC.getDataSource().getId().equals(dataSourceUI.getDataSourceSet())){
                    saveDataResponse.setResponseState(ResponseState.ALREADY_EXISTS);
                    return saveDataResponse;
                }

                DataSource createdDataSource = null;
                try {
                    if(type == DatasetType.OAI) {
                        createdDataSource = repoxManagerDefault.getDataManager().updateDataSourceOai(originalDSset, dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                                dataSourceUI.getSourceMDFormat(), dataSourceUI.getOaiSource(),
                                dataSourceUI.getOaiSet(), metadataTransformations,
                                externalRestServices,dataSourceUI.getMarcFormat(),dataSourceUI.isUseLastUpdateDate());
                    } else if(type == DatasetType.SRU) {
                        createdDataSource = repoxManagerDefault.getDataManager().updateDataSourceSruRecordUpdate(originalDSset,
                                dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                                dataSourceUI.getSourceMDFormat(), metadataTransformations,externalRestServices,
                                dataSourceUI.getMarcFormat(),dataSourceUI.isUseLastUpdateDate());
                    } else if(type == DatasetType.FOLDER) {
                        if(dataSourceUI.getRetrieveStartegy().equals("pt.utl.ist.repox.marc.DataSourceFolder")) {
                            Map<String, String> namespaces = new HashMap<String, String>();
                            if(dataSourceUI.getRecordIdPolicy().equals("IdExtracted")) {
                                for(int i=0; i<dataSourceUI.getNamespaceList().size(); i+=2) {
                                    namespaces.put(dataSourceUI.getNamespaceList().get(i),
                                            dataSourceUI.getNamespaceList().get(i+1));
                                }
                            }
                            createdDataSource = repoxManagerDefault.getDataManager().updateDataSourceFolder(originalDSset, dataSourceUI.getDataSourceSet(),
                                    dataSourceUI.getDescription(),
                                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(), dataSourceUI.getSourceMDFormat(),
                                    dataSourceUI.getIsoVariant(), dataSourceUI.getCharacterEncoding(),
                                    dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(),
                                    namespaces, dataSourceUI.getRecordRootName(), dataSourceUI.getDirPath(), metadataTransformations
                                    ,externalRestServices,dataSourceUI.getMarcFormat(),dataSourceUI.isUseLastUpdateDate());
                        } else if(dataSourceUI.getRetrieveStartegy().equals("pt.utl.ist.repox.ftp.DataSourceFtp")) {
                            Map<String, String> namespaces = new HashMap<String, String>();
                            if(dataSourceUI.getRecordIdPolicy().equals("IdExtracted")) {
                                for(int i=0; i<dataSourceUI.getNamespaceList().size(); i+=2) {
                                    namespaces.put(dataSourceUI.getNamespaceList().get(i),
                                            dataSourceUI.getNamespaceList().get(i+1));
                                }
                            }
                            // Check FTP connection
                            if(dataSourceUI.getUser() != null && !dataSourceUI.getUser().isEmpty()) {
                                if(!FileUtilSecond.checkFtpServer(dataSourceUI.getServer(), "Normal", dataSourceUI.getFolderPath(),
                                        dataSourceUI.getUser(), dataSourceUI.getPassword())){
                                    saveDataResponse.setResponseState(ResponseState.FTP_CONNECTION_FAILED);
                                    return saveDataResponse;
                                }
                            }

                            createdDataSource = repoxManagerDefault.getDataManager().updateDataSourceFtp(originalDSset,
                                    dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(), dataSourceUI.getSourceMDFormat(),
                                    dataSourceUI.getIsoVariant(), dataSourceUI.getCharacterEncoding(),
                                    dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(),
                                    namespaces, dataSourceUI.getRecordRootName(), dataSourceUI.getServer(),
                                    dataSourceUI.getUser(), dataSourceUI.getPassword(), dataSourceUI.getFolderPath(),
                                    metadataTransformations,externalRestServices,dataSourceUI.getMarcFormat(),dataSourceUI.isUseLastUpdateDate());
                        } else if(dataSourceUI.getRetrieveStartegy().equals("pt.utl.ist.repox.ftp.DataSourceHTTP")) {
                            Map<String, String> namespaces = new HashMap<String, String>();
                            if(dataSourceUI.getRecordIdPolicy().equals("IdExtracted")) {
                                for(int i=0; i<dataSourceUI.getNamespaceList().size(); i+=2) {
                                    namespaces.put(dataSourceUI.getNamespaceList().get(i),
                                            dataSourceUI.getNamespaceList().get(i+1));
                                }
                            }
                            createdDataSource = repoxManagerDefault.getDataManager().updateDataSourceHttp(originalDSset,
                                    dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(), dataSourceUI.getSourceMDFormat(),
                                    dataSourceUI.getIsoVariant(), dataSourceUI.getCharacterEncoding(),
                                    dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(),
                                    namespaces, dataSourceUI.getRecordRootName(), dataSourceUI.getHttpURL(),
                                    metadataTransformations,externalRestServices,dataSourceUI.getMarcFormat(),dataSourceUI.isUseLastUpdateDate());
                        }
                    } else if(type == DatasetType.Z39) {
                        // Harvest Method differences
                        if(dataSourceUI.getZ39HarvestMethod().equals("IdSequenceHarvester")) {
                            Map<String, String> namespaces = new HashMap<String, String>();
                            if(dataSourceUI.getRecordIdPolicy().equals("IdExtracted")) {
                                for(int i=0; i<dataSourceUI.getNamespaceList().size(); i+=2) {
                                    namespaces.put(dataSourceUI.getNamespaceList().get(i),
                                            dataSourceUI.getNamespaceList().get(i+1));
                                }
                            }
                            createdDataSource = repoxManagerDefault.getDataManager().updateDataSourceZ3950IdSequence(originalDSset,
                                    dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                                    dataSourceUI.getZ39Address(), dataSourceUI.getZ39Port(), dataSourceUI.getZ39Database(),
                                    dataSourceUI.getZ39User(), dataSourceUI.getZ39Password(), dataSourceUI.getZ39RecordSyntax(),
                                    dataSourceUI.getCharacterEncoding(), dataSourceUI.getZ39MaximumId(),
                                    dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(),
                                    namespaces, metadataTransformations,externalRestServices,dataSourceUI.isUseLastUpdateDate());

                        } else if(dataSourceUI.getZ39HarvestMethod().equals("IdListHarvester")) {
                            // check z3950 file upload
                            File z3950 = null;
                            if(!Z39FileUpload.ignoreUploadFile()) {
                                z3950 = Z39FileUpload.getZ39TempFile();
                                Z39FileUpload.deleteTempFile();
                            }

                            Map<String, String> namespaces = new HashMap<String, String>();
                            if(dataSourceUI.getRecordIdPolicy().equals("IdExtracted")) {
                                for(int i=0; i<dataSourceUI.getNamespaceList().size(); i+=2) {
                                    namespaces.put(dataSourceUI.getNamespaceList().get(i),
                                            dataSourceUI.getNamespaceList().get(i+1));
                                }
                            }
                            createdDataSource = repoxManagerDefault.getDataManager().updateDataSourceZ3950IdList(originalDSset,
                                    dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                                    dataSourceUI.getZ39Address(), dataSourceUI.getZ39Port(), dataSourceUI.getZ39Database(),
                                    dataSourceUI.getZ39User(), dataSourceUI.getZ39Password(), dataSourceUI.getZ39RecordSyntax(),
                                    dataSourceUI.getCharacterEncoding(), z3950 != null ? z3950.getAbsolutePath() : "",
                                    dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(),
                                    namespaces, metadataTransformations,externalRestServices,dataSourceUI.isUseLastUpdateDate());
                        } else if(dataSourceUI.getZ39HarvestMethod().equals("TimestampHarvester")) {
                            Format formatter = new SimpleDateFormat("yyyyMMdd");
                            String earliestDateString = formatter.format(dataSourceUI.getZ39EarlistDate());

                            Map<String, String> namespaces = new HashMap<String, String>();
                            if(dataSourceUI.getRecordIdPolicy().equals("IdExtracted")) {
                                for(int i=0; i<dataSourceUI.getNamespaceList().size(); i+=2) {
                                    namespaces.put(dataSourceUI.getNamespaceList().get(i),
                                            dataSourceUI.getNamespaceList().get(i+1));
                                }
                            }
                            createdDataSource = repoxManagerDefault.getDataManager().updateDataSourceZ3950Timestamp(originalDSset,
                                    dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                                    dataSourceUI.getZ39Address(), dataSourceUI.getZ39Port(), dataSourceUI.getZ39Database(),
                                    dataSourceUI.getZ39User(), dataSourceUI.getZ39Password(), dataSourceUI.getZ39RecordSyntax(),
                                    dataSourceUI.getCharacterEncoding(), earliestDateString,
                                    dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(),
                                    namespaces, metadataTransformations,externalRestServices,dataSourceUI.isUseLastUpdateDate());
                        }
                    }
                } catch (ParseException e) {
                    saveDataResponse.setResponseState(ResponseState.OTHER);
                    return saveDataResponse;
                } catch (ObjectNotFoundException e) {
                    saveDataResponse.setResponseState(ResponseState.NOT_FOUND);
                    return saveDataResponse;
                } catch (InvalidArgumentsException e) {
                    saveDataResponse.setResponseState(ResponseState.INVALID_ARGUMENTS);
                    return saveDataResponse;
                } catch (IncompatibleInstanceException e) {
                    saveDataResponse.setResponseState(ResponseState.INCOMPATIBLE_TYPE);
                    return saveDataResponse;
                }

                // External Services Run Type
                if(dataSourceUI.getExternalServicesRunType() != null)
                    createdDataSource.setExternalServicesRunType(
                            ExternalServiceStates.ContainerType.valueOf(dataSourceUI.getExternalServicesRunType()));

                replaceExportPathWithUpdatedId(originalDSset,dataSourceUI,createdDataSource);
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().setDataSetSampleState(dataSourceUI.isSample(),createdDataSource);

                // Save Tags
                createdDataSource.getTags().clear();
                for(DataSetTagUI dataSetTagUI : dataSourceUI.getTags()){
                    createdDataSource.getTags().add(new DataSourceTag(dataSetTagUI.getName()));
                }

                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
                saveDataResponse.setPage(PagingUtil.getDataPage(createdDataSource.getId(),pageSize));
                saveDataResponse.setResponseState(ResponseState.SUCCESS);
                return saveDataResponse;
            } else {
                // Check if the id already exists
                DataSourceContainer dataSourceContainerTest = repoxManagerDefault.getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet());
                if(dataSourceContainerTest != null){
                    saveDataResponse.setResponseState(ResponseState.ALREADY_EXISTS);
                    return saveDataResponse;
                }

                DataSource createdDataSource = null;
                try {
                    if(type == DatasetType.OAI) {
                        createdDataSource = repoxManagerDefault.getDataManager().createDataSourceOai(dataSourceUI.getDataSetParent().getId(),
                                dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                                dataSourceUI.getSourceMDFormat(), dataSourceUI.getOaiSource(),
                                dataSourceUI.getOaiSet(), metadataTransformations,externalRestServices,dataSourceUI.getMarcFormat());
                    }else if(type == DatasetType.SRU) {
                        createdDataSource = repoxManagerDefault.getDataManager().createDataSourceSruRecordUpdate(dataSourceUI.getDataSetParent().getId(),
                                dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                                dataSourceUI.getSourceMDFormat(), metadataTransformations,externalRestServices,
                                dataSourceUI.getMarcFormat());
                    } else if(type == DatasetType.FOLDER) {
                        if(dataSourceUI.getRetrieveStartegy().equals("pt.utl.ist.repox.marc.DataSourceFolder")) {
                            Map<String, String> namespaces = new HashMap<String, String>();
                            if(dataSourceUI.getRecordIdPolicy().equals("IdExtracted")) {
                                for(int i=0; i<dataSourceUI.getNamespaceList().size(); i+=2) {
                                    namespaces.put(dataSourceUI.getNamespaceList().get(i),
                                            dataSourceUI.getNamespaceList().get(i+1));
                                }
                            }
                            createdDataSource = repoxManagerDefault.getDataManager().createDataSourceFolder(dataSourceUI.getDataSetParent().getId(),
                                    dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(), dataSourceUI.getSourceMDFormat(),
                                    dataSourceUI.getIsoVariant(), dataSourceUI.getCharacterEncoding(),
                                    dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(),
                                    namespaces, dataSourceUI.getRecordRootName(), dataSourceUI.getDirPath(),
                                    metadataTransformations,externalRestServices,dataSourceUI.getMarcFormat());
                        } else if(dataSourceUI.getRetrieveStartegy().equals("pt.utl.ist.repox.ftp.DataSourceFtp")) {
                            Map<String, String> namespaces = new HashMap<String, String>();
                            if(dataSourceUI.getRecordIdPolicy().equals("IdExtracted")) {
                                for(int i=0; i<dataSourceUI.getNamespaceList().size(); i+=2) {
                                    namespaces.put(dataSourceUI.getNamespaceList().get(i),
                                            dataSourceUI.getNamespaceList().get(i+1));
                                }
                            }
                            // Check FTP connection
                            if(dataSourceUI.getUser() != null && !dataSourceUI.getUser().isEmpty()) {
                                if(!FileUtilSecond.checkFtpServer(dataSourceUI.getServer(), "Normal", dataSourceUI.getFolderPath(),
                                        dataSourceUI.getUser(), dataSourceUI.getPassword())){
                                    saveDataResponse.setResponseState(ResponseState.FTP_CONNECTION_FAILED);
                                    return saveDataResponse;
                                }
                            }

                            createdDataSource = repoxManagerDefault.getDataManager().createDataSourceFtp(dataSourceUI.getDataSetParent().getId(),
                                    dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(), dataSourceUI.getSourceMDFormat(),
                                    dataSourceUI.getIsoVariant(), dataSourceUI.getCharacterEncoding(),
                                    dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(),
                                    namespaces, dataSourceUI.getRecordRootName(), dataSourceUI.getServer(),
                                    dataSourceUI.getUser(), dataSourceUI.getPassword(), dataSourceUI.getFolderPath(),
                                    metadataTransformations,externalRestServices,dataSourceUI.getMarcFormat());
                        } else if(dataSourceUI.getRetrieveStartegy().equals("pt.utl.ist.repox.ftp.DataSourceHTTP")) {
                            Map<String, String> namespaces = new HashMap<String, String>();
                            if(dataSourceUI.getRecordIdPolicy().equals("IdExtracted")) {
                                for(int i=0; i<dataSourceUI.getNamespaceList().size(); i+=2) {
                                    namespaces.put(dataSourceUI.getNamespaceList().get(i),
                                            dataSourceUI.getNamespaceList().get(i+1));
                                }
                            }
                            createdDataSource = repoxManagerDefault.getDataManager().createDataSourceHttp(dataSourceUI.getDataSetParent().getId(),
                                    dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(), dataSourceUI.getSourceMDFormat(),
                                    dataSourceUI.getIsoVariant(), dataSourceUI.getCharacterEncoding(),
                                    dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(),
                                    namespaces, dataSourceUI.getRecordRootName(), dataSourceUI.getHttpURL(),
                                    metadataTransformations,externalRestServices,dataSourceUI.getMarcFormat());
                        }
                    } else if(type == DatasetType.Z39) {
                        // Harvest Method differences
                        if(dataSourceUI.getZ39HarvestMethod().equals("IdSequenceHarvester")) {
                            Map<String, String> namespaces = new HashMap<String, String>();
                            if(dataSourceUI.getRecordIdPolicy().equals("IdExtracted")) {
                                for(int i=0; i<dataSourceUI.getNamespaceList().size(); i+=2) {
                                    namespaces.put(dataSourceUI.getNamespaceList().get(i),
                                            dataSourceUI.getNamespaceList().get(i+1));
                                }
                            }
                            createdDataSource = repoxManagerDefault.getDataManager().createDataSourceZ3950IdSequence(dataSourceUI.getDataSetParent().getId(),
                                    dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                                    dataSourceUI.getZ39Address(), dataSourceUI.getZ39Port(), dataSourceUI.getZ39Database(),
                                    dataSourceUI.getZ39User(), dataSourceUI.getZ39Password(), dataSourceUI.getZ39RecordSyntax(),
                                    dataSourceUI.getCharacterEncoding(), dataSourceUI.getZ39MaximumId(),
                                    dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(),
                                    namespaces, metadataTransformations,externalRestServices);
                        } else if(dataSourceUI.getZ39HarvestMethod().equals("IdListHarvester")) {
                            // check z3950 file upload
                            File z3950 = null;
                            if(!Z39FileUpload.ignoreUploadFile()) {
                                z3950 = Z39FileUpload.getZ39TempFile();
                                Z39FileUpload.deleteTempFile();
                            }

                            Map<String, String> namespaces = new HashMap<String, String>();
                            if(dataSourceUI.getRecordIdPolicy().equals("IdExtracted")) {
                                for(int i=0; i<dataSourceUI.getNamespaceList().size(); i+=2) {
                                    namespaces.put(dataSourceUI.getNamespaceList().get(i),
                                            dataSourceUI.getNamespaceList().get(i+1));
                                }
                            }
                            createdDataSource = repoxManagerDefault.getDataManager().createDataSourceZ3950IdList(dataSourceUI.getDataSetParent().getId(),
                                    dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                                    dataSourceUI.getZ39Address(), dataSourceUI.getZ39Port(), dataSourceUI.getZ39Database(),
                                    dataSourceUI.getZ39User(), dataSourceUI.getZ39Password(), dataSourceUI.getZ39RecordSyntax(),
                                    dataSourceUI.getCharacterEncoding(), z3950.getAbsolutePath(),
                                    dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(),
                                    namespaces, metadataTransformations,externalRestServices);
                        } else if(dataSourceUI.getZ39HarvestMethod().equals("TimestampHarvester")) {
                            Format formatter = new SimpleDateFormat("yyyyMMdd");
                            String earliestDateString = formatter.format(dataSourceUI.getZ39EarlistDate());

                            Map<String, String> namespaces = new HashMap<String, String>();
                            if(dataSourceUI.getRecordIdPolicy().equals("IdExtracted")) {
                                for(int i=0; i<dataSourceUI.getNamespaceList().size(); i+=2) {
                                    namespaces.put(dataSourceUI.getNamespaceList().get(i),
                                            dataSourceUI.getNamespaceList().get(i+1));
                                }
                            }
                            createdDataSource = repoxManagerDefault.getDataManager().createDataSourceZ3950Timestamp(dataSourceUI.getDataSetParent().getId(),
                                    dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                                    dataSourceUI.getZ39Address(), dataSourceUI.getZ39Port(), dataSourceUI.getZ39Database(),
                                    dataSourceUI.getZ39User(), dataSourceUI.getZ39Password(), dataSourceUI.getZ39RecordSyntax(),
                                    dataSourceUI.getCharacterEncoding(), earliestDateString,
                                    dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(),
                                    namespaces, metadataTransformations,externalRestServices);
                        }
                    }
                } catch (SQLException e) {
                    saveDataResponse.setResponseState(ResponseState.ERROR_DATABASE);
                    return saveDataResponse;
                } catch (ObjectNotFoundException e) {
                    saveDataResponse.setResponseState(ResponseState.NOT_FOUND);
                    return saveDataResponse;
                } catch (AlreadyExistsException e) {
                    saveDataResponse.setResponseState(ResponseState.ALREADY_EXISTS);
                    return saveDataResponse;
                } catch (InvalidArgumentsException e) {
                    saveDataResponse.setResponseState(ResponseState.INVALID_ARGUMENTS);
                    return saveDataResponse;
                } catch (ParseException e) {
                    saveDataResponse.setResponseState(ResponseState.OTHER);
                    return saveDataResponse;
                }
                // External Services Run Type
                if(dataSourceUI.getExternalServicesRunType() != null)
                    createdDataSource.setExternalServicesRunType(
                            ExternalServiceStates.ContainerType.valueOf(dataSourceUI.getExternalServicesRunType()));

                createdDataSource.setExportDir(dataSourceUI.getExportDirectory());
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().setDataSetSampleState(dataSourceUI.isSample(),createdDataSource);

                // Save Tags
                createdDataSource.getTags().clear();
                for(DataSetTagUI dataSetTagUI : dataSourceUI.getTags()){
                    createdDataSource.getTags().add(new DataSourceTag(dataSetTagUI.getName()));
                }

                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
                saveDataResponse.setPage(PagingUtil.getDataPage(createdDataSource.getId(),pageSize));
                saveDataResponse.setResponseState(ResponseState.SUCCESS);
                return saveDataResponse;
            }
        }catch (DocumentException e) {
            saveDataResponse.setResponseState(ResponseState.OTHER);
            return saveDataResponse;
        } catch (IOException e) {
            saveDataResponse.setResponseState(ResponseState.OTHER);
            return saveDataResponse;
        }
    }

    public static String deleteDataSources(List<DataSourceUI> dataSourceUIs) {
        Iterator<DataSourceUI> dataSourceUIIterator = dataSourceUIs.iterator();
        while (dataSourceUIIterator.hasNext()) {
            try {
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().deleteDataSourceContainer(dataSourceUIIterator.next().getDataSourceSet());
            }catch (IOException e) {
                return MessageType.OTHER.name();
            } catch (ObjectNotFoundException e) {
                return MessageType.NOT_FOUND.name();
            }
        }
//        System.out.println("Done dss removed");
        return MessageType.OK.name();
    }

    public static void addAllOAIURL(String url,String dataProviderID,String dsSchema,String dsNamespace,
                                    String dsMTDFormat, Map<String,List<String>> map) {
        try {
            List<String> sets = map.get("sets");
            List<String> setNames = map.get("setNames");

            DataProvider dataProvider = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                    getDataProvider(dataProviderID);

            for (int i=0; i<sets.size(); i++) {
                String setSpec = sets.get(i);
                String setDescription = setNames.get(i);

                String setId = setSpec.replaceAll("[^a-zA-Z_0-9]", "_");

                OaiDataSource dataSourceOai = new OaiDataSource(dataProvider, setId, setDescription,
                        dsSchema, dsNamespace, dsMTDFormat,
                        url, setSpec, new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());

                HashMap<String, DataSourceContainer> oldDataSourceContainers = dataProvider.getDataSourceContainers();

                if(oldDataSourceContainers == null) {
                    dataProvider.setDataSourceContainers(new HashMap<String, DataSourceContainer>());
                }

                boolean isDuplicate = false;
                if (oldDataSourceContainers != null) {
                    for (DataSourceContainer dataSourceContainer : oldDataSourceContainers.values()) {
                        DataSource oldDataSource = dataSourceContainer.getDataSource();
                        if (oldDataSource instanceof OaiDataSource
                                && ((OaiDataSource) oldDataSource).isSameDataSource(dataSourceOai)) {
                            isDuplicate = true;
                        }
                    }
                }

                if (!isDuplicate) {
                    while (ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceOai.getId()) != null) {
                        dataSourceOai.setId(dataSourceOai.getId() + "_new");
                    }
                    LightDataSourceContainer dataSourceContainer = new LightDataSourceContainer(dataSourceOai);
                    dataProvider.getDataSourceContainers().put(dataSourceOai.getId(),dataSourceContainer);

                    dataSourceOai.initAccessPoints();
                }
            }
            try {
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().updateDataProvider(dataProvider, dataProvider.getId());
            } catch (ObjectNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().initialize(dataProvider.getDataSourceContainers());
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        System.out.println("Done add all");
    }

    public static String getDirPathFtp(String dataSourceId){
        return ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDirPathFtp(dataSourceId);
    }

    public static List<ExternalRestService> saveExternalServices(DataSourceUI dataSourceUI){
        List<ExternalRestService> externalRestServices = new ArrayList<ExternalRestService>();
        // Rest Service Data
        if(dataSourceUI.getRestServiceUIList().size() > 0) {
            for(ExternalServiceUI externalServiceUI : dataSourceUI.getRestServiceUIList()){
                ExternalRestService externalRestService = null;
                if(externalServiceUI.getExternalServiceType().equals("MONITORED")){
                    externalRestService = new ExternalRestService(externalServiceUI.getId(),
                            externalServiceUI.getName(),externalServiceUI.getUri(),
                            externalServiceUI.getStatusUri(),externalServiceUI.getType(), ExternalServiceType.valueOf(externalServiceUI.getExternalServiceType()));
                    externalRestService.setEnabled(externalServiceUI.isEnabled());
                } else if(externalServiceUI.getExternalServiceType().equals("NO_MONITOR")){
                    try {
                        externalRestService = new ExternalServiceNoMonitor(externalServiceUI.getId(),
                                externalServiceUI.getName(),externalServiceUI.getUri(),
                                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                                        getDataSourceContainer(dataSourceUI.getDataSourceSet()).getDataSource());
                    } catch (DocumentException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                for(ServiceParameterUI serviceParameterUI : externalServiceUI.getServiceParameters()){
                    ServiceParameter serviceParameter = new ServiceParameter(serviceParameterUI.getName(),
                            serviceParameterUI.getType(),serviceParameterUI.getRequired(),serviceParameterUI.getExample(),
                            serviceParameterUI.getSemantics());
                    serviceParameter.setValue(serviceParameterUI.getValue());
                    externalRestService.getServiceParameters().add(serviceParameter);
                }

                if(externalServiceUI.getExternalResultUI() != null)
                    externalRestService.setExternalResultsUri(externalServiceUI.getExternalResultUI());
                externalRestServices.add(externalRestService);
            }
        }

        return externalRestServices;
    }

    public static void replaceExportPathWithUpdatedId(String originalDSset, DataSourceUI dataSourceUI, DataSource createdDataSource){
        if(!originalDSset.equals(dataSourceUI.getDataSourceSet())){
            createdDataSource.setExportDir(dataSourceUI.getExportDirectory().replace(originalDSset,dataSourceUI.getDataSourceSet()));
        }else
            createdDataSource.setExportDir(dataSourceUI.getExportDirectory());
    }
}
