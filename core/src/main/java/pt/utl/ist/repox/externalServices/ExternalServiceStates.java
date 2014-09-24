package pt.utl.ist.repox.externalServices;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 02-01-2012
 * Time: 13:22
 */
public class ExternalServiceStates {

    /**
     */
    public enum ServiceRunningState {
        /** ServiceRunningState START */
        START,
        /** ServiceRunningState RUNNING */
        RUNNING,
        /** ServiceRunningState FINISHED */
        FINISHED
    }

    /**
     */
    public enum ServiceExitState{
        /** ServiceExitState SUCCESS */
        SUCCESS,
        /** ServiceExitState ERROR */
        ERROR,
        /** ServiceExitState NONE */
        NONE
    }

    /**
     */
    public enum ContainerType{
        /** ContainerType SEQUENTIAL */
        SEQUENTIAL,
        /** ContainerType PARALLEL */
        PARALLEL,
        /** ContainerType SINGLE_SERVICE_EXECUTION */
        SINGLE_SERVICE_EXECUTION
    }

}
