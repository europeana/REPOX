package pt.utl.ist.repox.externalServices;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 02-01-2012
 * Time: 13:22
 */
public class ExternalServiceStates {

    public enum ServiceRunningState {
        START,
        RUNNING,
        FINISHED
    }

    public enum ServiceExitState{
        SUCCESS,
        ERROR,
        NONE
    }

    public enum ContainerType{
        SEQUENTIAL,
        PARALLEL,
        SINGLE_SERVICE_EXECUTION
    }

}
