package pt.utl.ist.repox.services.web.rest;

import java.util.List;
import java.util.Map;

/**
 * The RestRequest class has five components of a request for a REST web service:
 * <p>The contextURL is the requested URL with the app context (example: http://www.server.com/repox)
 * <p>The parsedRequestURI is a String that contains the URI of the request after the contextURL path has been removed
 * <p>The correctedQueryURI is either an empty String if there are no query elements or the exact query string otherwise (ex: ?id=10)
 * <p>The uriHierarchy is the list of Strings extracted from the parsedRequestURI, ordered from left to right, after the '/' have been removed
 * <p>The requestParameters is the Map of pairs: request parameter key, request parameter value
 * 
 */
public class RestRequest {
    private String              contextURL;
    private String              parsedRequestURI;
    private String              correctedQueryURI;
    private List<String>        uriHierarchy;
    private Map<String, String> requestParameters;

    @SuppressWarnings("javadoc")
    public String getContextURL() {
        return contextURL;
    }

    @SuppressWarnings("javadoc")
    public void setContextURL(String contextURL) {
        this.contextURL = contextURL;
    }

    @SuppressWarnings("javadoc")
    public String getParsedRequestURI() {
        return parsedRequestURI;
    }

    @SuppressWarnings("javadoc")
    public void setParsedRequestURI(String parsedRequestURI) {
        this.parsedRequestURI = parsedRequestURI;
    }

    @SuppressWarnings("javadoc")
    public String getCorrectedQueryURI() {
        return correctedQueryURI;
    }

    @SuppressWarnings("javadoc")
    public void setCorrectedQueryURI(String correctedQueryURI) {
        this.correctedQueryURI = correctedQueryURI;
    }

    @SuppressWarnings("javadoc")
    public List<String> getUriHierarchy() {
        return uriHierarchy;
    }

    @SuppressWarnings("javadoc")
    public void setUriHierarchy(List<String> uriHierarchy) {
        this.uriHierarchy = uriHierarchy;
    }

    @SuppressWarnings("javadoc")
    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    @SuppressWarnings("javadoc")
    public void setRequestParameters(Map<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }

    @SuppressWarnings("javadoc")
    public String getFullRequestURI() {
        return contextURL + parsedRequestURI + correctedQueryURI;
    }

    /**
     * Creates a new instance of this class.
     */
    public RestRequest() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * @param contextURL
     * @param parsedRequestURI
     * @param correctedQueryURI
     * @param uriHierarchy
     * @param requestParameters
     */
    public RestRequest(String contextURL, String parsedRequestURI, String correctedQueryURI, List<String> uriHierarchy, Map<String, String> requestParameters) {
        super();
        this.contextURL = contextURL;
        this.parsedRequestURI = parsedRequestURI;
        this.correctedQueryURI = correctedQueryURI;
        this.uriHierarchy = uriHierarchy;
        this.requestParameters = requestParameters;
    }
}
