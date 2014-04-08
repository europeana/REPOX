package pt.utl.ist.repox.externalServices;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ExternalRestService {
    private static final Logger log = Logger.getLogger(ExternalRestService.class);

    private String id;
    private String name;
    private String uri;
    private String statusUri;
    private String externalResultsUri;
    private String type;
    private boolean isEnabled;
    private ExternalServiceType externalServiceType = ExternalServiceType.MONITORED;
    private List<ServiceParameter> serviceParameters;

    public ExternalRestService(String id, String name, String uri, String statusUri, String type, ExternalServiceType externalServiceType) {
        super();
        this.id = id;
        this.uri = uri;
        this.type = type;
        this.name = name;
        this.statusUri = statusUri;
        this.externalServiceType = externalServiceType;
        isEnabled = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatusUri() {
        return statusUri;
    }

    public void setStatusUri(String statusUri) {
        this.statusUri = statusUri;
    }

    public List<ServiceParameter> getServiceParameters() {
        if(serviceParameters == null)
            serviceParameters = new ArrayList<ServiceParameter>();
        return serviceParameters;
    }

    public void setServiceParameters(List<ServiceParameter> serviceParameters) {
        this.serviceParameters = serviceParameters;
    }

    public ExternalServiceType getExternalServiceType() {
        return externalServiceType;
    }

    public void setExternalServiceType(ExternalServiceType externalServiceType) {
        this.externalServiceType = externalServiceType;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getExternalResultsUri() {
        return externalResultsUri;
    }

    public void setExternalResultsUri(String externalResultsUri) {
        this.externalResultsUri = externalResultsUri;
    }
}
