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

    public RepoxConfiguration getConfiguration();

	public AccessPointsManager getAccessPointsManager();

	public DataManager getDataManager();

	public RecordCountManager getRecordCountManager();

	public StatisticsManager getStatisticsManager();

	public TaskManager getTaskManager();

	public MetadataTransformationManager getMetadataTransformationManager();

	public ExternalRestServicesManager getExternalRestServicesManager();

	public MetadataSchemaManager getMetadataSchemaManager();

	public TagsManager getTagsManager();

	public Thread getTaskManagerThread();

    public EmailUtil getEmailClient();
}
