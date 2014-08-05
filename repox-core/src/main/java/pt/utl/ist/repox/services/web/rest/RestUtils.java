package pt.utl.ist.repox.services.web.rest;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import pt.utl.ist.repox.util.XmlUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The RestUtils class contains useful methods to deal with web services REST requests.
 *
 * @author dreis
 * req
 */
public class RestUtils {
    private static final Logger log = Logger.getLogger(RestUtils.class);

    /**
     * @param baseURI
     * @param request
     * @return RestRequest
     * @throws InvalidRequestException
     * @throws UnsupportedEncodingException
     */
    public static RestRequest processRequest(String baseURI, HttpServletRequest request) throws InvalidRequestException, UnsupportedEncodingException {
        if (request.getRequestURI().indexOf(baseURI) < 0) { throw new InvalidRequestException("Requested URI '" + request.getRequestURI() + "' does not contain baseURI: '" + baseURI + "'"); }

        String contextURL = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf(request.getContextPath())) + request.getContextPath();

        String parsedRequestURI = request.getRequestURL().toString().substring(request.getRequestURL().toString().indexOf(request.getContextPath()) + request.getContextPath().length());

        String correctedQueryURI = (request.getQueryString() == null ? "" : "?" + request.getQueryString());

        String relevantURI = request.getRequestURI().substring(request.getContextPath().length() + baseURI.length());

        String[] rawUriHierarchy = relevantURI.split("/");
        List<String> uriHierarchy = new ArrayList<String>();

        for (String currentURIDir : rawUriHierarchy) {
            if (!currentURIDir.trim().isEmpty()) {
                uriHierarchy.add(URLDecoder.decode(currentURIDir.trim(), "UTF-8"));
                log.debug("current UriHierarchy[" + (uriHierarchy.size() - 1) + "]: " + uriHierarchy.get(uriHierarchy.size() - 1));
            }
        }

        Map<String, String> requestParameters = processParameters(request);

        //		log.debug("contextURL:" + contextURL);
        //		log.debug("parsedRequestURI: " + parsedRequestURI);
        //		log.debug("correctedQueryURI: " + correctedQueryURI);
        //		log.debug("uriHierarchy: " + uriHierarchy);
        //		log.debug("requestParameters: " + requestParameters);

        return new RestRequest(contextURL, parsedRequestURI, correctedQueryURI, uriHierarchy, requestParameters);
    }

    /**
     * @param request
     * @return Map<String, String> of the mapping values in the request
     */
    public static Map<String, String> processParameters(HttpServletRequest request) {
        Map<String, String> requestParameters = new LinkedHashMap<String, String>();

        for (Object currentEntry : request.getParameterMap().keySet()) {
            String currentKey = (String)currentEntry;
            String[] currentValues = (String[])request.getParameterMap().get(currentEntry);
            requestParameters.put(currentKey, currentValues[0]);
        }

        return requestParameters;
    }

    /**
     * Writes a DOMElement as root to an OuputStream.
     * @param out 
     * @param response 
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static void writeRestResponse(OutputStream out, Element response) throws IOException {
        Element rootElement = DocumentHelper.createElement("response");
        rootElement.add(response);
        XmlUtil.writePrettyPrint(out, rootElement);
    }

    /**
     * @param requestURI
     * @param out
     * @throws IOException
     */
    public static void writeInvalidRequest(String requestURI, OutputStream out) throws IOException {
        writeInvalidRequest(requestURI, null, out);
    }

    /**
     * @param requestURI
     * @param cause
     * @param out
     * @throws IOException
     */
    public static void writeInvalidRequest(String requestURI, String cause, OutputStream out) throws IOException {

        Element errorElement = DocumentHelper.createElement("error");
        errorElement.addAttribute("type", "invalidRequest");
        errorElement.addAttribute("requestURI", requestURI);
        if (cause != null && cause.trim().length() > 0) {
            errorElement.addAttribute("cause", cause.trim());
        }
        writeRestResponse(out, errorElement);
    }

    /**
     * @param args
     * @throws UnsupportedEncodingException
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        String[] stringArray = new String[] { "teste", "/asd\\esd:isd", "oai:louisdl.louislibraries.org:LHC/10" };
        for (String currentString : stringArray) {
            String encodedString = URLEncoder.encode(currentString, "UTF-8");
            String decodedString = URLDecoder.decode(encodedString, "UTF-8");

            log.debug("orginalString: " + currentString);
            log.debug("encodedString: " + encodedString);
            log.debug("decodedString: " + decodedString);
        }

    }
}
