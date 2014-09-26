package pt.utl.ist.externalServices;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import pt.utl.ist.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created to REPOX. User: Edmundo Date: 27-12-2011 Time: 14:30
 */
public class ExternalRestServiceThread extends Thread {
    private static final Logger                       log                 = Logger.getLogger(ExternalRestServiceThread.class);

    private ExternalRestServiceContainer              externalRestServiceContainer;
    private ExternalRestService                       externalRestService = null;
    private File                                      logFile;
    private ExternalServiceStates.ServiceRunningState runningState        = ExternalServiceStates.ServiceRunningState.START;
    private ExternalServiceStates.ServiceExitState    exitState           = ExternalServiceStates.ServiceExitState.NONE;

    private String                                    finalUri;
    private String                                    threadId;

    /**
     * Creates a new instance of this class.
     * 
     * @param externalRestService
     * @param externalRestServiceContainer
     * @param logFile
     */
    public ExternalRestServiceThread(ExternalRestService externalRestService, ExternalRestServiceContainer externalRestServiceContainer, File logFile) {
        this.externalRestServiceContainer = externalRestServiceContainer;
        this.externalRestService = externalRestService;
        this.logFile = logFile;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param externalRestService
     * @param externalRestServiceContainer
     */
    public ExternalRestServiceThread(ExternalRestService externalRestService, ExternalRestServiceContainer externalRestServiceContainer) {
        this.externalRestServiceContainer = externalRestServiceContainer;
        this.externalRestService = externalRestService;
    }

    @Override
    public void run() {
        log.info("Running Rest Service with ID" + externalRestService.getId());
        if (logFile != null) StringUtil.simpleLog("Running Rest Service with ID" + externalRestService.getId(), this.getClass(), logFile);
        buildFinalUri();

        startService();
        //
        // Check status until ingest task is finished
        while (runningState.equals(ExternalServiceStates.ServiceRunningState.RUNNING)) {
            try {
                sleep(3000);

                String result = checkServiceEnded();
                if (result.equals("SUCCESS")) {
                    exitState = ExternalServiceStates.ServiceExitState.SUCCESS;
                    runningState = ExternalServiceStates.ServiceRunningState.FINISHED;
                    externalRestServiceContainer.updateContainerState();
                    if (logFile != null) StringUtil.simpleLog("SERVICE SUCESSFULL ON --" + externalRestService.getId(), this.getClass(), logFile);
                    log.info("SERVICE SUCESSFULL ON --" + externalRestService.getId());
                } else if (result.equals("WARNING")) {
                    exitState = ExternalServiceStates.ServiceExitState.SUCCESS;
                    runningState = ExternalServiceStates.ServiceRunningState.FINISHED;
                    externalRestServiceContainer.updateContainerState();
                    if (logFile != null) StringUtil.simpleLog("SERVICE WITH WARNING ON --" + externalRestService.getId(), this.getClass(), logFile);
                    log.info("SERVICE WITH WARNING ON --" + externalRestService.getId());
                } else if (result.equals("ERROR")) {
                    exitState = ExternalServiceStates.ServiceExitState.ERROR;
                    runningState = ExternalServiceStates.ServiceRunningState.FINISHED;
                    externalRestServiceContainer.updateContainerState();
                    if (logFile != null) StringUtil.simpleLog("SERVICE FAILED ON --" + externalRestService.getId(), this.getClass(), logFile);
                    log.error("SERVICE FAILED ON --" + externalRestService.getId());
                }
            } catch (InterruptedException e) {
                sendError();
            }
        }
    }

    /**
     * 
     */
    public void startService() {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new URL(finalUri));

            runningState = ExternalServiceStates.ServiceRunningState.RUNNING;

            if (checkError(document)) return;

            threadId = document.valueOf("//response/thread-id");
            if (logFile != null) StringUtil.simpleLog("Starting service with Thread ID --" + threadId, this.getClass(), logFile);
            log.info("Starting service with Thread ID --" + threadId);
        } catch (IOException e) {
            if (logFile != null) StringUtil.simpleLog("IOException while starting service with id -- " + externalRestService.getId(), e, this.getClass(), logFile);
            sendError();
        } catch (DocumentException e) {
            if (logFile != null) StringUtil.simpleLog("DocumentException while starting service with id -- " + externalRestService.getId(), e, this.getClass(), logFile);
            sendError();
        }
    }

    private boolean checkError(Document document) {
        String error = document.valueOf("//response/error/@type");
        if (!error.isEmpty()) {
            sendError();
            return true;
        }
        return false;
    }

    private void sendError() {
        exitState = ExternalServiceStates.ServiceExitState.ERROR;
        runningState = ExternalServiceStates.ServiceRunningState.FINISHED;
        externalRestServiceContainer.updateContainerState();
        if (logFile != null) StringUtil.simpleLog("Service Failed --" + externalRestService.getId(), this.getClass(), logFile);
        log.error("Service Failed --" + externalRestService.getId());
    }

    /**
     * @return String
     */
    protected String checkServiceEnded() {
        try {
            SAXReader reader = new SAXReader();

            String statusUri = externalRestService.getStatusUri() + "?threadId=" + threadId;
            Document document = reader.read(new URL(statusUri));

            if (checkError(document)) return "ERROR";

            if (document.valueOf("//response/status").isEmpty()) {
                if (logFile != null) StringUtil.simpleLog("service with id -- " + externalRestService.getId() + " Doesnt support getStatus method", this.getClass(), logFile);
                return "ERROR";
            } else
                return document.valueOf("//response/status");
        } catch (IOException e) {
            if (logFile != null) StringUtil.simpleLog("IOException while monitoring service with id -- " + externalRestService.getId(), e, this.getClass(), logFile);
            return "ERROR";
        } catch (DocumentException e) {
            // todo: exception thrown when thread on service has finished and it return no xml response
            //            StringUtil.simpleLog("DocumentException while monitoring service with id -- " + externalRestService.getId(),e, this.getClass(), logFile);
            return "SUCCESS";
        }
    }

    /**
     * @return ExternalServiceStates.ServiceRunningState
     */
    public ExternalServiceStates.ServiceRunningState getRunningState() {
        return runningState;
    }

    /**
     * @return ExternalServiceStates.ServiceExitState
     */
    public ExternalServiceStates.ServiceExitState getExitState() {
        return exitState;
    }

    private void buildFinalUri() {
        String uri = externalRestService.getUri() + "?" + externalRestService.getServiceParameters().get(0).getName() + "=" + externalRestService.getServiceParameters().get(0).getValue();

        for (int i = 1; i < externalRestService.getServiceParameters().size(); i++) {
            uri = uri + "&" + externalRestService.getServiceParameters().get(i).getName() + "=" + externalRestService.getServiceParameters().get(i).getValue();
        }

        finalUri = uri;
    }
}
