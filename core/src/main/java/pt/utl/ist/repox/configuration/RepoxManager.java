package pt.utl.ist.repox.configuration;

import pt.utl.ist.repox.accessPoint.manager.AccessPointsManager;
import pt.utl.ist.repox.dataProvider.DataManager;
import pt.utl.ist.repox.dataProvider.dataSource.TagsManager;
import pt.utl.ist.repox.externalServices.ExternalRestServicesManager;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.statistics.RecordCountManager;
import pt.utl.ist.repox.statistics.StatisticsManager;
import pt.utl.ist.repox.task.TaskManager;
import pt.utl.ist.repox.util.EmailUtil;

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
