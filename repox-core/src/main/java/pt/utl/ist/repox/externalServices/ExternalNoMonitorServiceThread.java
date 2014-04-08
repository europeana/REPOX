package pt.utl.ist.repox.externalServices;

import org.apache.log4j.Logger;
import pt.utl.ist.repox.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 27-12-2011
 * Time: 14:30
 */
public class ExternalNoMonitorServiceThread extends Thread {
    private static final Logger log = Logger.getLogger(ExternalNoMonitorServiceThread.class);

    private ExternalServiceNoMonitor externalRestService = null;
    private File logFile;

    private String finalUri;

    public ExternalNoMonitorServiceThread(ExternalServiceNoMonitor externalRestService,File logFile) {
        this.externalRestService = externalRestService;
        this.logFile = logFile;
    }

    public void run() {

        buildFinalUri();

        ExternalServiceStates.ServiceExitState result = runExternalService();

        // Check status until ingest task is finished
        writeOutputMessage("SERVICE ENDED. SERVICE ID = " + externalRestService.getId() + "EXIT_STATE = " + result,logFile);
    }

    public ExternalServiceStates.ServiceExitState runExternalService() {
        try {
            writeOutputMessage("Running External No Monitor Service with ID " +
                    externalRestService.getId() + " with FINAL COMMAND = " + finalUri,logFile);
            new URL(finalUri).openConnection();

            writeOutputMessage("External service ended  -- with state = " + ExternalServiceStates.ServiceExitState.SUCCESS, logFile);
            return ExternalServiceStates.ServiceExitState.SUCCESS;
        } catch (IOException e) {
            writeOutputErrorMessage("IOException while starting service with id -- " + externalRestService.getId(),logFile,e);
            return ExternalServiceStates.ServiceExitState.ERROR;
        }
    }

    private void buildFinalUri(){
        String uri = externalRestService.getUri() + "?" + externalRestService.getServiceParameters().get(0).getName() +
                "=" + externalRestService.getServiceParameters().get(0).getSemantics();

        for(int i = 1; i<externalRestService.getServiceParameters().size(); i++){
            uri = uri + "&" + externalRestService.getServiceParameters().get(i).getName() + "=" +
                    externalRestService.getServiceParameters().get(i).getSemantics();
        }

        finalUri = setDynamicFieldsInUri(uri);
    }

    private String setDynamicFieldsInUri(String uri){
        String pass1 = uri.replace("DATA_SET_ID",externalRestService.getDataSource().getId());
        String pass2 = pass1.replace("DATA_SET_STATUS",externalRestService.getDataSource().getStatus().name());
        return pass2;
    }

    private void writeOutputErrorMessage(String message, File logFile, Exception e){
        if(logFile != null)
            StringUtil.simpleLog(message, e, this.getClass(), logFile);
        e.printStackTrace();
    }

    private void writeOutputMessage(String message, File logFile){
        if(logFile != null)
            StringUtil.simpleLog(message, this.getClass(), logFile);

        log.error(message);
    }
}
