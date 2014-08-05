package pt.utl.ist.repox.task;

/**
 * @author Diogo
 */
public abstract class TaskFactory {
    /**
     */
    public enum TaskType {
        /** TaskType DATA_SOURCE_INGEST */
        DATA_SOURCE_INGEST, 
        /** TaskType DATA_SOURCE_EXPORT */
        DATA_SOURCE_EXPORT, 
        /** TaskType SCHEDULED */
        SCHEDULED
    }

    /**
     * @param type
     * @return Task depending on the type
     */
    public static Task getInstance(TaskType type) {
        switch (type) {
        case DATA_SOURCE_INGEST:
            return new DataSourceIngestTask();
        case DATA_SOURCE_EXPORT:
            return new DataSourceExportTask();
        case SCHEDULED:
            return new ScheduledTask();

        default:
            return null;
        }
    }

    /**
     * @param clazz
     * @return TaskType of the clazz
     */
    public static TaskType getType(Class<? extends Task> clazz) {
        if (clazz.equals(DataSourceIngestTask.class)) { return TaskType.DATA_SOURCE_INGEST; }
        if (clazz.equals(DataSourceExportTask.class)) { return TaskType.DATA_SOURCE_EXPORT; }
        if (clazz.equals(ScheduledTask.class)) {
            return TaskType.SCHEDULED;
        } else {
            return null;
        }
    }
}
