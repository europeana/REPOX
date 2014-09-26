package pt.utl.ist.configuration;

import pt.utl.ist.accessPoint.manager.AccessPointsManager;
import pt.utl.ist.dataProvider.DataManager;
import pt.utl.ist.dataProvider.dataSource.TagsManager;
import pt.utl.ist.externalServices.ExternalRestServicesManager;
import pt.utl.ist.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.util.EmailUtil;
import pt.utl.ist.statistics.RecordCountManager;
import pt.utl.ist.statistics.StatisticsManager;
import pt.utl.ist.task.TaskManager;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 31-03-2011
 * Time: 11:48
 * To change this template use File | Settings | File Templates.
 */
public interface RepoxManager {

    RepoxConfiguration getConfiguration();

	AccessPointsManager getAccessPointsManager();

	DataManager getDataManager();

	RecordCountManager getRecordCountManager();

	StatisticsManager getStatisticsManager();

	TaskManager getTaskManager();

	MetadataTransformationManager getMetadataTransformationManager();

	ExternalRestServicesManager getExternalRestServicesManager();

	MetadataSchemaManager getMetadataSchemaManager();

	TagsManager getTagsManager();

	Thread getTaskManagerThread();

    EmailUtil getEmailClient();
}
