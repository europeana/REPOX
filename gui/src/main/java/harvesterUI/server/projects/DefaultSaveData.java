package harvesterUI.server.projects;

import harvesterUI.server.dataManagement.dataSets.DataSetOperationsServiceImpl;
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
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxManager;
import pt.utl.ist.dataProvider.*;
import pt.utl.ist.dataProvider.dataSource.DataSourceTag;
import pt.utl.ist.dataProvider.dataSource.IdExtractedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdProvidedRecordIdPolicy;
import pt.utl.ist.externalServices.*;
import pt.utl.ist.ftp.FtpFileRetrieveStrategy;
import pt.utl.ist.http.HttpFileRetrieveStrategy;
import pt.utl.ist.marc.FolderFileRetrieveStrategy;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.util.FileUtil;
import pt.utl.ist.util.ProviderType;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.IncompatibleInstanceException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created to REPOX. User: Edmundo Date: 04-07-2011 Time: 13:35
 */
public class DefaultSaveData {
  private static final Logger log = Logger.getLogger(DefaultSaveData.class);

  public static String deleteDataProviders(List<DataProviderUI> dataProviderUIs) {
    for (DataProviderUI dataProvider : dataProviderUIs) {
      try {
        DefaultRepoxManager repoxManagerEuropeana =
            (DefaultRepoxManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager();
        repoxManagerEuropeana.getDataManager().deleteDataProvider(dataProvider.getId());
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
  public static SaveDataResponse saveDataSource(boolean update, DatasetType type,
      String originalDSset, DataSourceUI dataSourceUI, int pageSize) throws ServerSideException {
    SaveDataResponse saveDataResponse = new SaveDataResponse();
    try {
      DefaultRepoxManager defaultRepoxManager =
          (DefaultRepoxManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager();

      ResponseState urlStatus = Util.getUrlStatus(dataSourceUI);
      if (urlStatus != null) {
        saveDataResponse.setResponseState(urlStatus);
        return saveDataResponse;
      }

      // Save metadata transformations
      MetadataTransformationManager metadataTransformationManager =
          ConfigSingleton.getRepoxContextUtil().getRepoxManager()
              .getMetadataTransformationManager();
      Map<String, MetadataTransformation> metadataTransformations =
          new HashMap<String, MetadataTransformation>();
      for (TransformationUI transformationUI : dataSourceUI.getMetadataTransformations()) {
        MetadataTransformation loadedTransformation =
            metadataTransformationManager.loadMetadataTransformation(transformationUI
                .getIdentifier());
        metadataTransformations.put(transformationUI.getIdentifier(), loadedTransformation);
      }

      // Save external services
      List<ExternalRestService> externalRestServices = saveExternalServices(dataSourceUI);

      if (update) {
        // Check if the id already exists
        DataSourceContainer dataSourceContainer =
            defaultRepoxManager.getDataManager().getDataSourceContainer(
                dataSourceUI.getDataSourceSet());
        DataSourceContainer originalDSC =
            defaultRepoxManager.getDataManager().getDataSourceContainer(originalDSset);
        if (dataSourceContainer != null
            && !originalDSC.getDataSource().getId().equals(dataSourceUI.getDataSourceSet())) {
          saveDataResponse.setResponseState(ResponseState.ALREADY_EXISTS);
          return saveDataResponse;
        }

        DataSource createdDataSource = null;
        try {
          if (type == DatasetType.OAI) {
            createdDataSource =
                defaultRepoxManager.getDataManager().updateDataSourceOai(originalDSset,
                    dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                    dataSourceUI.getNameCode(), dataSourceUI.getName(),
                    dataSourceUI.getExportDirectory(), dataSourceUI.getSchema(),
                    dataSourceUI.getMetadataNamespace(), dataSourceUI.getSourceMDFormat(),
                    dataSourceUI.getOaiSource(), dataSourceUI.getOaiSet(), metadataTransformations,
                    externalRestServices, dataSourceUI.getMarcFormat(),
                    dataSourceUI.isUseLastUpdateDate());
          } else if (type == DatasetType.SRU) {
            createdDataSource =
                defaultRepoxManager.getDataManager().updateDataSourceSruRecordUpdate(originalDSset,
                    dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                    dataSourceUI.getNameCode(), dataSourceUI.getName(),
                    dataSourceUI.getExportDirectory(), dataSourceUI.getSchema(),
                    dataSourceUI.getMetadataNamespace(), dataSourceUI.getSourceMDFormat(),
                    metadataTransformations, externalRestServices, dataSourceUI.getMarcFormat(),
                    dataSourceUI.isUseLastUpdateDate());
          } else if (type == DatasetType.FOLDER) {
            if (dataSourceUI.getRetrieveStartegy().equals(
                FolderFileRetrieveStrategy.FOLDERFILERETRIEVESTRATEGY)) {
              Map<String, String> namespaces = new HashMap<String, String>();
              if (dataSourceUI.getRecordIdPolicy().equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
                for (int i = 0; i < dataSourceUI.getNamespaceList().size(); i += 2) {
                  namespaces.put(dataSourceUI.getNamespaceList().get(i), dataSourceUI
                      .getNamespaceList().get(i + 1));
                }
              }
              createdDataSource =
                  defaultRepoxManager.getDataManager().updateDataSourceFolder(originalDSset,
                      dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                      dataSourceUI.getNameCode(), dataSourceUI.getName(),
                      dataSourceUI.getExportDirectory(), dataSourceUI.getSchema(),
                      dataSourceUI.getMetadataNamespace(), dataSourceUI.getSourceMDFormat(),
                      dataSourceUI.getIsoVariant(), dataSourceUI.getCharacterEncoding(),
                      dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(), namespaces,
                      dataSourceUI.getRecordRootName(), dataSourceUI.getDirPath(),
                      metadataTransformations, externalRestServices, dataSourceUI.getMarcFormat(),
                      dataSourceUI.isUseLastUpdateDate());
            } else if (dataSourceUI.getRetrieveStartegy().equals(
                FtpFileRetrieveStrategy.FTPFILERETRIEVESTRATEGY)) {
              // Check FTP connection
              if (dataSourceUI.getUser() != null && !dataSourceUI.getUser().isEmpty()) {
                if (!FileUtil.checkFtpServer(dataSourceUI.getServer(), "Normal",
                    dataSourceUI.getFolderPath(), dataSourceUI.getUser(),
                    dataSourceUI.getPassword())) {
                  saveDataResponse.setResponseState(ResponseState.FTP_CONNECTION_FAILED);
                  return saveDataResponse;
                }
              }

              Map<String, String> namespaces = new HashMap<String, String>();
              if (dataSourceUI.getRecordIdPolicy().equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
                for (int i = 0; i < dataSourceUI.getNamespaceList().size(); i += 2) {
                  namespaces.put(dataSourceUI.getNamespaceList().get(i), dataSourceUI
                      .getNamespaceList().get(i + 1));
                }
              }
              createdDataSource =
                  defaultRepoxManager.getDataManager().updateDataSourceFtp(originalDSset,
                      dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                      dataSourceUI.getNameCode(), dataSourceUI.getName(),
                      dataSourceUI.getExportDirectory(), dataSourceUI.getSchema(),
                      dataSourceUI.getMetadataNamespace(), dataSourceUI.getSourceMDFormat(),
                      dataSourceUI.getIsoVariant(), dataSourceUI.getCharacterEncoding(),
                      dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(), namespaces,
                      dataSourceUI.getRecordRootName(), dataSourceUI.getServer(),
                      dataSourceUI.getUser(), dataSourceUI.getPassword(),
                      dataSourceUI.getFolderPath(), metadataTransformations, externalRestServices,
                      dataSourceUI.getMarcFormat(), dataSourceUI.isUseLastUpdateDate());
            } else if (dataSourceUI.getRetrieveStartegy().equals(
                HttpFileRetrieveStrategy.HTTPFILERETRIEVESTRATEGY)) {
              Map<String, String> namespaces = new HashMap<String, String>();
              if (dataSourceUI.getRecordIdPolicy().equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
                for (int i = 0; i < dataSourceUI.getNamespaceList().size(); i += 2) {
                  namespaces.put(dataSourceUI.getNamespaceList().get(i), dataSourceUI
                      .getNamespaceList().get(i + 1));
                }
              }
              createdDataSource =
                  defaultRepoxManager.getDataManager().updateDataSourceHttp(originalDSset,
                      dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                      dataSourceUI.getNameCode(), dataSourceUI.getName(),
                      dataSourceUI.getExportDirectory(), dataSourceUI.getSchema(),
                      dataSourceUI.getMetadataNamespace(), dataSourceUI.getSourceMDFormat(),
                      dataSourceUI.getIsoVariant(), dataSourceUI.getCharacterEncoding(),
                      dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(), namespaces,
                      dataSourceUI.getRecordRootName(), dataSourceUI.getHttpURL(),
                      metadataTransformations, externalRestServices, dataSourceUI.getMarcFormat(),
                      dataSourceUI.isUseLastUpdateDate());
            }
          } else if (type == DatasetType.Z39) {
            // Harvest Method differences
            if (dataSourceUI.getZ39HarvestMethod().equals("IdSequenceHarvester")) {
              Map<String, String> namespaces = new HashMap<String, String>();
              if (dataSourceUI.getRecordIdPolicy().equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
                for (int i = 0; i < dataSourceUI.getNamespaceList().size(); i += 2) {
                  namespaces.put(dataSourceUI.getNamespaceList().get(i), dataSourceUI
                      .getNamespaceList().get(i + 1));
                }
              }
              createdDataSource =
                  defaultRepoxManager.getDataManager().updateDataSourceZ3950IdSequence(
                      originalDSset, dataSourceUI.getDataSourceSet(),
                      dataSourceUI.getDescription(), dataSourceUI.getNameCode(),
                      dataSourceUI.getName(), dataSourceUI.getExportDirectory(),
                      dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                      dataSourceUI.getZ39Address(), dataSourceUI.getZ39Port(),
                      dataSourceUI.getZ39Database(), dataSourceUI.getZ39User(),
                      dataSourceUI.getZ39Password(), dataSourceUI.getZ39RecordSyntax(),
                      dataSourceUI.getCharacterEncoding(), dataSourceUI.getZ39MaximumId(),
                      dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(), namespaces,
                      metadataTransformations, externalRestServices,
                      dataSourceUI.isUseLastUpdateDate());
            } else if (dataSourceUI.getZ39HarvestMethod().equals("IdListHarvester")) {
              // check z3950 file upload
              File z3950 = null;
              if (!Z39FileUpload.ignoreUploadFile()) {
                z3950 = Z39FileUpload.getZ39TempFile();
                Z39FileUpload.deleteTempFile();
              }

              Map<String, String> namespaces = new HashMap<String, String>();
              if (dataSourceUI.getRecordIdPolicy().equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
                for (int i = 0; i < dataSourceUI.getNamespaceList().size(); i += 2) {
                  namespaces.put(dataSourceUI.getNamespaceList().get(i), dataSourceUI
                      .getNamespaceList().get(i + 1));
                }
              }
              createdDataSource =
                  defaultRepoxManager.getDataManager().updateDataSourceZ3950IdList(originalDSset,
                      dataSourceUI.getDataSourceSet(), dataSourceUI.getDescription(),
                      dataSourceUI.getNameCode(), dataSourceUI.getName(),
                      dataSourceUI.getExportDirectory(), dataSourceUI.getSchema(),
                      dataSourceUI.getMetadataNamespace(), dataSourceUI.getZ39Address(),
                      dataSourceUI.getZ39Port(), dataSourceUI.getZ39Database(),
                      dataSourceUI.getZ39User(), dataSourceUI.getZ39Password(),
                      dataSourceUI.getZ39RecordSyntax(), dataSourceUI.getCharacterEncoding(),
                      z3950 != null ? z3950.getAbsolutePath() : "",
                      dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(), namespaces,
                      metadataTransformations, externalRestServices,
                      dataSourceUI.isUseLastUpdateDate());
            } else if (dataSourceUI.getZ39HarvestMethod().equals("TimestampHarvester")) {
              Format formatter = new SimpleDateFormat("yyyyMMdd");
              String earliestDateString = formatter.format(dataSourceUI.getZ39EarlistDate());

              Map<String, String> namespaces = new HashMap<String, String>();
              if (dataSourceUI.getRecordIdPolicy().equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
                for (int i = 0; i < dataSourceUI.getNamespaceList().size(); i += 2) {
                  namespaces.put(dataSourceUI.getNamespaceList().get(i), dataSourceUI
                      .getNamespaceList().get(i + 1));
                }
              }
              createdDataSource =
                  defaultRepoxManager.getDataManager().updateDataSourceZ3950Timestamp(
                      originalDSset, dataSourceUI.getDataSourceSet(),
                      dataSourceUI.getDescription(), dataSourceUI.getNameCode(),
                      dataSourceUI.getName(), dataSourceUI.getExportDirectory(),
                      dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                      dataSourceUI.getZ39Address(), dataSourceUI.getZ39Port(),
                      dataSourceUI.getZ39Database(), dataSourceUI.getZ39User(),
                      dataSourceUI.getZ39Password(), dataSourceUI.getZ39RecordSyntax(),
                      dataSourceUI.getCharacterEncoding(), earliestDateString,
                      dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(), namespaces,
                      metadataTransformations, externalRestServices,
                      dataSourceUI.isUseLastUpdateDate());

            }
          }
          // External Services Run Type
          if (dataSourceUI.getExternalServicesRunType() != null)
            createdDataSource.setExternalServicesRunType(ExternalServiceStates.ContainerType
                .valueOf(dataSourceUI.getExternalServicesRunType()));

          replaceExportPathWithUpdatedId(originalDSset, dataSourceUI, createdDataSource);
          ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()
              .setDataSetSampleState(dataSourceUI.isSample(), createdDataSource);

          // Save Tags
          createdDataSource.getTags().clear();
          for (DataSetTagUI dataSetTagUI : dataSourceUI.getTags()) {
            createdDataSource.getTags().add(new DataSourceTag(dataSetTagUI.getName()));
          }

          ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
          saveDataResponse.setPage(PagingUtil.getDataPage(createdDataSource.getId(), pageSize));
          saveDataResponse.setResponseState(ResponseState.SUCCESS);
        } catch (ParseException e) {
          saveDataResponse.setResponseState(ResponseState.OTHER);
        } catch (ObjectNotFoundException e) {
          saveDataResponse.setResponseState(ResponseState.NOT_FOUND);
        } catch (InvalidArgumentsException e) {
          saveDataResponse.setResponseState(ResponseState.INVALID_ARGUMENTS);
        } catch (IncompatibleInstanceException e) {
          saveDataResponse.setResponseState(ResponseState.INCOMPATIBLE_TYPE);
        } catch (AlreadyExistsException e) {
          saveDataResponse.setResponseState(ResponseState.ALREADY_EXISTS);
        }
        return saveDataResponse;
      } else {
        // New Data Source
        // Check if the id already exists
        DataSourceContainer dataSourceContainerTest =
            defaultRepoxManager.getDataManager().getDataSourceContainer(
                dataSourceUI.getDataSourceSet());
        if (dataSourceContainerTest != null) {
          saveDataResponse.setResponseState(ResponseState.ALREADY_EXISTS);
          return saveDataResponse;
        }

        DataSource createdDataSource = null;
        try {
          if (type == DatasetType.OAI) {
            createdDataSource =
                defaultRepoxManager.getDataManager().createDataSourceOai(
                    dataSourceUI.getDataSetParent().getId(), dataSourceUI.getDataSourceSet(),
                    dataSourceUI.getDescription(), dataSourceUI.getNameCode(),
                    dataSourceUI.getName(), dataSourceUI.getExportDirectory(),
                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                    dataSourceUI.getSourceMDFormat(), dataSourceUI.getOaiSource(),
                    dataSourceUI.getOaiSet(), metadataTransformations, externalRestServices,
                    dataSourceUI.getMarcFormat());
          } else if (type == DatasetType.SRU) {
            createdDataSource =
                defaultRepoxManager.getDataManager().createDataSourceSruRecordUpdate(
                    dataSourceUI.getDataSetParent().getId(), dataSourceUI.getDataSourceSet(),
                    dataSourceUI.getDescription(), dataSourceUI.getNameCode(),
                    dataSourceUI.getName(), dataSourceUI.getExportDirectory(),
                    dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                    dataSourceUI.getSourceMDFormat(), metadataTransformations,
                    externalRestServices, dataSourceUI.getMarcFormat());
          } else if (type == DatasetType.FOLDER) {
            if (dataSourceUI.getRetrieveStartegy().equals(
                FolderFileRetrieveStrategy.FOLDERFILERETRIEVESTRATEGY)) {
              Map<String, String> namespaces = new HashMap<String, String>();
              if (dataSourceUI.getRecordIdPolicy().equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
                for (int i = 0; i < dataSourceUI.getNamespaceList().size(); i += 2) {
                  namespaces.put(dataSourceUI.getNamespaceList().get(i), dataSourceUI
                      .getNamespaceList().get(i + 1));
                }
              }
              createdDataSource =
                  defaultRepoxManager.getDataManager().createDataSourceFolder(
                      dataSourceUI.getDataSetParent().getId(), dataSourceUI.getDataSourceSet(),
                      dataSourceUI.getDescription(), dataSourceUI.getNameCode(),
                      dataSourceUI.getName(), dataSourceUI.getExportDirectory(),
                      dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                      dataSourceUI.getSourceMDFormat(), dataSourceUI.getIsoVariant(),
                      dataSourceUI.getCharacterEncoding(), dataSourceUI.getRecordIdPolicy(),
                      dataSourceUI.getIdXPath(), namespaces, dataSourceUI.getRecordRootName(),
                      dataSourceUI.getDirPath(), metadataTransformations, externalRestServices,
                      dataSourceUI.getMarcFormat());
            } else if (dataSourceUI.getRetrieveStartegy().equals(
                FtpFileRetrieveStrategy.FTPFILERETRIEVESTRATEGY)) {
              Map<String, String> namespaces = new HashMap<String, String>();
              if (dataSourceUI.getRecordIdPolicy().equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
                for (int i = 0; i < dataSourceUI.getNamespaceList().size(); i += 2) {
                  namespaces.put(dataSourceUI.getNamespaceList().get(i), dataSourceUI
                      .getNamespaceList().get(i + 1));
                }
              }
              // Check FTP connection
              if (dataSourceUI.getUser() != null && !dataSourceUI.getUser().isEmpty()) {
                if (!FileUtil.checkFtpServer(dataSourceUI.getServer(), "Normal",
                    dataSourceUI.getFolderPath(), dataSourceUI.getUser(),
                    dataSourceUI.getPassword())) {
                  saveDataResponse.setResponseState(ResponseState.FTP_CONNECTION_FAILED);
                  return saveDataResponse;
                }
              }

              createdDataSource =
                  defaultRepoxManager.getDataManager().createDataSourceFtp(
                      dataSourceUI.getDataSetParent().getId(), dataSourceUI.getDataSourceSet(),
                      dataSourceUI.getDescription(), dataSourceUI.getNameCode(),
                      dataSourceUI.getName(), dataSourceUI.getExportDirectory(),
                      dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                      dataSourceUI.getSourceMDFormat(), dataSourceUI.getIsoVariant(),
                      dataSourceUI.getCharacterEncoding(), dataSourceUI.getRecordIdPolicy(),
                      dataSourceUI.getIdXPath(), namespaces, dataSourceUI.getRecordRootName(),
                      dataSourceUI.getServer(), dataSourceUI.getUser(), dataSourceUI.getPassword(),
                      dataSourceUI.getFolderPath(), metadataTransformations, externalRestServices,
                      dataSourceUI.getMarcFormat());
            } else if (dataSourceUI.getRetrieveStartegy().equals(
                HttpFileRetrieveStrategy.HTTPFILERETRIEVESTRATEGY)) {
              Map<String, String> namespaces = new HashMap<String, String>();
              if (dataSourceUI.getRecordIdPolicy().equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
                for (int i = 0; i < dataSourceUI.getNamespaceList().size(); i += 2) {
                  namespaces.put(dataSourceUI.getNamespaceList().get(i), dataSourceUI
                      .getNamespaceList().get(i + 1));
                }
              }
              createdDataSource =
                  defaultRepoxManager.getDataManager().createDataSourceHttp(
                      dataSourceUI.getDataSetParent().getId(), dataSourceUI.getDataSourceSet(),
                      dataSourceUI.getDescription(), dataSourceUI.getNameCode(),
                      dataSourceUI.getName(), dataSourceUI.getExportDirectory(),
                      dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                      dataSourceUI.getSourceMDFormat(), dataSourceUI.getIsoVariant(),
                      dataSourceUI.getCharacterEncoding(), dataSourceUI.getRecordIdPolicy(),
                      dataSourceUI.getIdXPath(), namespaces, dataSourceUI.getRecordRootName(),
                      dataSourceUI.getHttpURL(), metadataTransformations, externalRestServices,
                      dataSourceUI.getMarcFormat());
            }
          } else if (type == DatasetType.Z39) {
            // Harvest Method differences
            if (dataSourceUI.getZ39HarvestMethod().equals("IdSequenceHarvester")) {
              Map<String, String> namespaces = new HashMap<String, String>();
              if (dataSourceUI.getRecordIdPolicy().equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
                for (int i = 0; i < dataSourceUI.getNamespaceList().size(); i += 2) {
                  namespaces.put(dataSourceUI.getNamespaceList().get(i), dataSourceUI
                      .getNamespaceList().get(i + 1));
                }
              }
              createdDataSource =
                  defaultRepoxManager.getDataManager().createDataSourceZ3950IdSequence(
                      dataSourceUI.getDataSetParent().getId(), dataSourceUI.getDataSourceSet(),
                      dataSourceUI.getDescription(), dataSourceUI.getNameCode(),
                      dataSourceUI.getName(), dataSourceUI.getExportDirectory(),
                      dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                      dataSourceUI.getZ39Address(), dataSourceUI.getZ39Port(),
                      dataSourceUI.getZ39Database(), dataSourceUI.getZ39User(),
                      dataSourceUI.getZ39Password(), dataSourceUI.getZ39RecordSyntax(),
                      dataSourceUI.getCharacterEncoding(), dataSourceUI.getZ39MaximumId(),
                      dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(), namespaces,
                      metadataTransformations, externalRestServices);
            } else if (dataSourceUI.getZ39HarvestMethod().equals("IdListHarvester")) {
              // check z3950 file upload
              File z3950 = null;
              if (!Z39FileUpload.ignoreUploadFile()) {
                z3950 = Z39FileUpload.getZ39TempFile();
                Z39FileUpload.deleteTempFile();
              }

              Map<String, String> namespaces = new HashMap<String, String>();
              if (dataSourceUI.getRecordIdPolicy().equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
                for (int i = 0; i < dataSourceUI.getNamespaceList().size(); i += 2) {
                  namespaces.put(dataSourceUI.getNamespaceList().get(i), dataSourceUI
                      .getNamespaceList().get(i + 1));
                }
              }
              createdDataSource =
                  defaultRepoxManager.getDataManager().createDataSourceZ3950IdList(
                      dataSourceUI.getDataSetParent().getId(), dataSourceUI.getDataSourceSet(),
                      dataSourceUI.getDescription(), dataSourceUI.getNameCode(),
                      dataSourceUI.getName(), dataSourceUI.getExportDirectory(),
                      dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                      dataSourceUI.getZ39Address(), dataSourceUI.getZ39Port(),
                      dataSourceUI.getZ39Database(), dataSourceUI.getZ39User(),
                      dataSourceUI.getZ39Password(), dataSourceUI.getZ39RecordSyntax(),
                      dataSourceUI.getCharacterEncoding(), z3950.getAbsolutePath(),
                      dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(), namespaces,
                      metadataTransformations, externalRestServices);
            } else if (dataSourceUI.getZ39HarvestMethod().equals("TimestampHarvester")) {
              Format formatter = new SimpleDateFormat("yyyyMMdd");
              String earliestDateString = formatter.format(dataSourceUI.getZ39EarlistDate());
              Map<String, String> namespaces = new HashMap<String, String>();
              if (dataSourceUI.getRecordIdPolicy().equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
                for (int i = 0; i < dataSourceUI.getNamespaceList().size(); i += 2) {
                  namespaces.put(dataSourceUI.getNamespaceList().get(i), dataSourceUI
                      .getNamespaceList().get(i + 1));
                }
              }
              createdDataSource =
                  defaultRepoxManager.getDataManager().createDataSourceZ3950Timestamp(
                      dataSourceUI.getDataSetParent().getId(), dataSourceUI.getDataSourceSet(),
                      dataSourceUI.getDescription(), dataSourceUI.getNameCode(),
                      dataSourceUI.getName(), dataSourceUI.getExportDirectory(),
                      dataSourceUI.getSchema(), dataSourceUI.getMetadataNamespace(),
                      dataSourceUI.getZ39Address(), dataSourceUI.getZ39Port(),
                      dataSourceUI.getZ39Database(), dataSourceUI.getZ39User(),
                      dataSourceUI.getZ39Password(), dataSourceUI.getZ39RecordSyntax(),
                      dataSourceUI.getCharacterEncoding(), earliestDateString,
                      dataSourceUI.getRecordIdPolicy(), dataSourceUI.getIdXPath(), namespaces,
                      metadataTransformations, externalRestServices);
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
        if (dataSourceUI.getExternalServicesRunType() != null)
          createdDataSource.setExternalServicesRunType(ExternalServiceStates.ContainerType
              .valueOf(dataSourceUI.getExternalServicesRunType()));

        createdDataSource.setExportDir(dataSourceUI.getExportDirectory());
        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()
            .setDataSetSampleState(dataSourceUI.isSample(), createdDataSource);

        // Save Tags
        createdDataSource.getTags().clear();
        for (DataSetTagUI dataSetTagUI : dataSourceUI.getTags()) {
          createdDataSource.getTags().add(new DataSourceTag(dataSetTagUI.getName()));
        }

        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
        saveDataResponse.setPage(PagingUtil.getDataPage(createdDataSource.getId(), pageSize));
        saveDataResponse.setResponseState(ResponseState.SUCCESS);
        return saveDataResponse;
      }
    } catch (DocumentException e) {
      saveDataResponse.setResponseState(ResponseState.OTHER);
      return saveDataResponse;
    } catch (IOException e) {
      saveDataResponse.setResponseState(ResponseState.OTHER);
      return saveDataResponse;
    }
  }

  public static SaveDataResponse saveDataProvider(boolean update, DataProviderUI dataProviderUI,
      int pageSize, String username) throws ServerSideException {

    DefaultDataManager defaultManager =
        (DefaultDataManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager()
            .getDataManager();
    // String dpId;
    String homepage = dataProviderUI.getHomepage();
    URL url = null;
    SaveDataResponse saveDataResponse = new SaveDataResponse();


    if (homepage != null && !homepage.isEmpty()) {
      String checkUrlResult = DataSetOperationsServiceImpl.checkURL(homepage);
      if (checkUrlResult != null) {
        if (checkUrlResult.equals("URL_MALFORMED")) {
          saveDataResponse.setResponseState(ResponseState.URL_MALFORMED);
          return saveDataResponse;
        } else if (checkUrlResult.equals("URL_NOT_EXISTS")) {
          saveDataResponse.setResponseState(ResponseState.URL_NOT_EXISTS);
          return saveDataResponse;
        } else if (checkUrlResult.equals("SUCCESS"))
            log.info("Homepage value: " + homepage + " is fine.");
      }
    }

    if (update) {
      DataProvider dataProvider = defaultManager.getDataProvider(dataProviderUI.getId());
      if (dataProvider != null) {
        dataProvider.setCountryCode(dataProviderUI.getCountryCode());
        dataProvider.setCountry(dataProviderUI.getCountryName());
        dataProvider.setName(dataProviderUI.getName());
        dataProvider.setDescription(dataProviderUI.getDescription());
        dataProvider.setProviderType(ProviderType.valueOf(dataProviderUI.getType()));
        dataProvider.setNameCode(dataProviderUI.getNameCode());
        dataProvider.setHomepage(homepage);

        try {
          dataProvider =
              defaultManager.updateDataProvider(null, dataProvider.getId(), null,
                  dataProviderUI.getName(), dataProviderUI.getCountryName(),
                  dataProviderUI.getCountryCode(), dataProviderUI.getDescription(),
                  dataProviderUI.getNameCode(), homepage, dataProviderUI.getType(), null);
          UserManagementServiceImpl.getInstance().addDPtoUser(username, dataProvider.getId());
          saveDataResponse.setPage(PagingUtil.getDataPage(dataProvider.getId(), pageSize));
          saveDataResponse.setResponseState(ResponseState.SUCCESS);
        } catch (ObjectNotFoundException e) {
          saveDataResponse.setResponseState(ResponseState.NOT_FOUND);
        } catch (InvalidArgumentsException e) {
          saveDataResponse.setResponseState(ResponseState.INVALID_ARGUMENTS);
        } catch (IOException e) {
          saveDataResponse.setResponseState(ResponseState.OTHER);
        } catch (AlreadyExistsException e) {
          saveDataResponse.setResponseState(ResponseState.ALREADY_EXISTS);
        }

      }
    } else {
      try {
        DataProvider dataProvider =
            defaultManager.createDataProvider(dataProviderUI.getParentAggregatorID(), null,
                dataProviderUI.getName(), dataProviderUI.getCountryName(),
                dataProviderUI.getCountryCode(), dataProviderUI.getDescription(),
                dataProviderUI.getNameCode(), homepage, dataProviderUI.getType(), null);
        UserManagementServiceImpl.getInstance().addDPtoUser(username, dataProvider.getId());
        saveDataResponse.setPage(PagingUtil.getDataPage(dataProvider.getId(), pageSize));
        saveDataResponse.setResponseState(ResponseState.SUCCESS);
      } catch (ObjectNotFoundException e) {
        saveDataResponse.setResponseState(ResponseState.NOT_FOUND);
      } catch (AlreadyExistsException e) {
        saveDataResponse.setResponseState(ResponseState.ALREADY_EXISTS);
      } catch (IOException e) {
        saveDataResponse.setResponseState(ResponseState.OTHER);
      } catch (InvalidArgumentsException e) {
        saveDataResponse.setResponseState(ResponseState.INVALID_ARGUMENTS);
      }
    }
    return saveDataResponse;
  }

  public static List<ExternalRestService> saveExternalServices(DataSourceUI dataSourceUI) {
    List<ExternalRestService> externalRestServices = new ArrayList<ExternalRestService>();
    // Rest Service Data
    if (dataSourceUI.getRestServiceUIList().size() > 0) {
      for (ExternalServiceUI externalServiceUI : dataSourceUI.getRestServiceUIList()) {
        ExternalRestService externalRestService = null;
        if (externalServiceUI.getExternalServiceType().equals("MONITORED")) {
          externalRestService =
              new ExternalRestService(externalServiceUI.getId(), externalServiceUI.getName(),
                  externalServiceUI.getUri(), externalServiceUI.getStatusUri(),
                  externalServiceUI.getType(), ExternalServiceType.valueOf(externalServiceUI
                      .getExternalServiceType()));
          externalRestService.setEnabled(externalServiceUI.isEnabled());
        } else if (externalServiceUI.getExternalServiceType().equals("NO_MONITOR")) {
          try {
            externalRestService =
                new ExternalServiceNoMonitor(externalServiceUI.getId(),
                    externalServiceUI.getName(), externalServiceUI.getUri(), ConfigSingleton
                        .getRepoxContextUtil().getRepoxManager().getDataManager()
                        .getDataSourceContainer(dataSourceUI.getDataSourceSet()).getDataSource());
          } catch (DocumentException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File
                                 // Templates.
          } catch (IOException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File
                                 // Templates.
          }
        }
        for (ServiceParameterUI serviceParameterUI : externalServiceUI.getServiceParameters()) {
          ServiceParameter serviceParameter =
              new ServiceParameter(serviceParameterUI.getName(), serviceParameterUI.getType(),
                  serviceParameterUI.getRequired(), serviceParameterUI.getExample(),
                  serviceParameterUI.getSemantics());
          serviceParameter.setValue(serviceParameterUI.getValue());
          externalRestService.getServiceParameters().add(serviceParameter);
        }

        if (externalServiceUI.getExternalResultUI() != null)
          externalRestService.setExternalResultsUri(externalServiceUI.getExternalResultUI());
        externalRestServices.add(externalRestService);
      }
    }

    return externalRestServices;
  }

  public static String deleteDataSources(List<DataSourceUI> dataSourceUIs) {
    Iterator<DataSourceUI> dataSourceUIIterator = dataSourceUIs.iterator();
    while (dataSourceUIIterator.hasNext()) {
      DefaultRepoxManager repoxManagerEuropeana =
          (DefaultRepoxManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager();
      // todo use result
      try {
        repoxManagerEuropeana.getDataManager().deleteDataSourceContainer(
            dataSourceUIIterator.next().getDataSourceSet());
      } catch (IOException e) {
        return MessageType.OTHER.name();
      } catch (ObjectNotFoundException e) {
        return MessageType.NOT_FOUND.name();
      }
    }
    return MessageType.OK.name();
  }

  public static void addAllOAIURL(String url, String dataProviderID, String dsSchema,
      String dsNamespace, String dsMTDFormat, Map<String, List<String>> map, String name,
      String nameCode, String exportPath) {
    try {
      String finalExportPath;
      if (exportPath == null) {
        DefaultRepoxManager europeanaManager =
            (DefaultRepoxManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager();
        finalExportPath = europeanaManager.getConfiguration().getExportDefaultFolder();
      } else
        finalExportPath = exportPath;

      List<String> sets = map.get("sets");
      List<String> setNames = map.get("setNames");

      DefaultDataManager europeanaManager =
          (DefaultDataManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager()
              .getDataManager();
      DataProvider dataProviderEuropeana = europeanaManager.getDataProvider(dataProviderID);

      for (int i = 0; i < sets.size(); i++) {
        String setSpec = sets.get(i);
        String setDescription = setNames.get(i);
        String setName = name + "_" + setSpec;
        String setNameCode = nameCode + "_" + setSpec;

        String setId = setSpec.replaceAll("[^a-zA-Z_0-9]", "_");

        OaiDataSource dataSourceOai =
            new OaiDataSource(dataProviderEuropeana, setId, setDescription, dsSchema, dsNamespace,
                dsMTDFormat, url, setSpec, new IdProvidedRecordIdPolicy(),
                new TreeMap<String, MetadataTransformation>());

        HashMap<String, DataSourceContainer> oldDataSourceContainers =
            dataProviderEuropeana.getDataSourceContainers();

        if (oldDataSourceContainers == null) {
          dataProviderEuropeana.setDataSourceContainers(new HashMap<String, DataSourceContainer>());
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
          while (ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()
              .getDataSourceContainer(dataSourceOai.getId()) != null) {
            dataSourceOai.setId(dataSourceOai.getId() + "_new");
          }

          dataSourceOai.initAccessPoints();

          DefaultDataSourceContainer dataSourceContainerE =
              new DefaultDataSourceContainer(dataSourceOai, setNameCode, setName, finalExportPath);
          dataProviderEuropeana.getDataSourceContainers().put(dataSourceOai.getId(),
              dataSourceContainerE);
        }
      }
      try {
        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()
            .updateDataProvider(dataProviderEuropeana, dataProviderEuropeana.getId());
      } catch (ObjectNotFoundException e) {
        e.printStackTrace(); // To change body of catch statement use File | Settings | File
                             // Templates.
      }
      ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager()
          .initialize(dataProviderEuropeana.getDataSourceContainers());
    } catch (DocumentException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // System.out.println("Done add all");
  }

  public static String getDirPathFtp(String dataSourceId) {
    return ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()
        .getDirPathFtp(dataSourceId);
  }

  public static void replaceExportPathWithUpdatedId(String originalDSset,
      DataSourceUI dataSourceUI, DataSource createdDataSource) {
    if (!originalDSset.equals(dataSourceUI.getDataSourceSet())) {
      createdDataSource.setExportDir(dataSourceUI.getExportDirectory().replace(originalDSset,
          dataSourceUI.getDataSourceSet()));
    } else
      createdDataSource.setExportDir(dataSourceUI.getExportDirectory());
  }
}
