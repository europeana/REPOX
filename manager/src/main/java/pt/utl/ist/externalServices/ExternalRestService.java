package pt.utl.ist.externalServices;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ExternalRestService {
    private static final Logger    log                 = Logger.getLogger(ExternalRestService.class);

    private String                 id;
    private String                 name;
    private String                 uri;
    private String                 statusUri;
    private String                 externalResultsUri;
    private String                 type;
    private boolean                isEnabled;
    private ExternalServiceType    externalServiceType = ExternalServiceType.MONITORED;
    private List<ServiceParameter> serviceParameters;

    /**
     * Creates a new instance of this class.
     * 
     * @param id
     * @param name
     * @param uri
     * @param statusUri
     * @param type
     * @param externalServiceType
     */
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

    @SuppressWarnings("javadoc")
    public String getId() {
        return id;
    }

    @SuppressWarnings("javadoc")
    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("javadoc")
    public String getName() {
        return name;
    }

    @SuppressWarnings("javadoc")
    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("javadoc")
    public String getUri() {
        return uri;
    }

    @SuppressWarnings("javadoc")
    public void setUri(String uri) {
        this.uri = uri;
    }

    @SuppressWarnings("javadoc")
    public String getType() {
        return type;
    }

    @SuppressWarnings("javadoc")
    public void setType(String type) {
        this.type = type;
    }

    @SuppressWarnings("javadoc")
    public String getStatusUri() {
        return statusUri;
    }

    @SuppressWarnings("javadoc")
    public void setStatusUri(String statusUri) {
        this.statusUri = statusUri;
    }

    @SuppressWarnings("javadoc")
    public List<ServiceParameter> getServiceParameters() {
        if (serviceParameters == null) serviceParameters = new ArrayList<ServiceParameter>();
        return serviceParameters;
    }

    @SuppressWarnings("javadoc")
    public void setServiceParameters(List<ServiceParameter> serviceParameters) {
        this.serviceParameters = serviceParameters;
    }

    @SuppressWarnings("javadoc")
    public ExternalServiceType getExternalServiceType() {
        return externalServiceType;
    }

    @SuppressWarnings("javadoc")
    public void setExternalServiceType(ExternalServiceType externalServiceType) {
        this.externalServiceType = externalServiceType;
    }

    @SuppressWarnings("javadoc")
    public boolean isEnabled() {
        return isEnabled;
    }

    @SuppressWarnings("javadoc")
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @SuppressWarnings("javadoc")
    public String getExternalResultsUri() {
        return externalResultsUri;
    }

    @SuppressWarnings("javadoc")
    public void setExternalResultsUri(String externalResultsUri) {
        this.externalResultsUri = externalResultsUri;
    }
}
