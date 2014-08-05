/**
 * Copyright 2006 OCLC Online Computer Library Center Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or
 * agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oclc.oai.server.verb;

import org.oclc.oai.server.catalog.AbstractCatalog;
import org.oclc.oai.util.OAIUtil;
import pt.utl.ist.repox.util.ConfigSingleton;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

/**
 * This class represents a GetRecord response on either the server or the
 * client.
 * 
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class GetRecord extends ServerVerb {
    private static final boolean debug           = false;
    private static ArrayList     validParamNames = new ArrayList();
    static {
        validParamNames.add("verb");
        validParamNames.add("identifier");
        validParamNames.add("metadataPrefix");
    }

    /**
     * Construct the xml response on the server-side.
     * 
     * @param context
     *            the servlet context
     * @param request
     *            the servlet request
     * @param response 
     * @param serverTransformer 
     * @return a String containing the XML response
     * @throws OAIBadRequestException
     *                an http 400 status error occurred
     * @throws OAINotFoundException
     *                an http 404 status error occurred
     * @throws OAIInternalServerError
     *                an http 500 status error occurred
     * @throws TransformerException 
     */
    public static String construct(HashMap context, HttpServletRequest request, HttpServletResponse response, Transformer serverTransformer) throws OAIInternalServerError, TransformerException {
        Properties properties = (Properties)context.get("OAIHandler.properties");
        AbstractCatalog abstractCatalog = (AbstractCatalog)context.get("OAIHandler.catalog");
        String baseURL = properties.getProperty("OAIHandler.baseURL");
        if (baseURL == null) {
            try {
                baseURL = request.getRequestURL().toString();
            } catch (java.lang.NoSuchMethodError f) {
                baseURL = request.getRequestURL().toString();
            }
        }
        StringBuffer sb = new StringBuffer();
        String identifier = request.getParameter("identifier");
        String metadataPrefix = request.getParameter("metadataPrefix");

        if (debug) {
            System.out.println("GetRecord.constructGetRecord: identifier=" + identifier);
            System.out.println("GetRecord.constructGetRecord: metadataPrefix=" + metadataPrefix);
        }
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        String styleSheet = properties.getProperty("OAIHandler.styleSheet");
        if (styleSheet != null) {
            sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"");
            sb.append(styleSheet);
            sb.append("\"?>");
        }
        sb.append("<" + OAIUtil.getTag("OAI-PMH") + " xmlns" + (ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().isUseOAINamespace() ? ":oai" : "") + "=\"http://www.openarchives.org/OAI/2.0/\"");
        sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        String extraXmlns = properties.getProperty("OAIHandler.extraXmlns");
        if (extraXmlns != null) sb.append(" ").append(extraXmlns);
        sb.append(" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/");
        sb.append(" http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">");
        sb.append("<" + OAIUtil.getTag("responseDate") + ">");
        sb.append(createResponseDate(new Date()));
        sb.append("</" + OAIUtil.getTag("responseDate") + ">");
        // sb.append("<requestURL>");
        // sb.append(getRequestURL(request));
        // sb.append("</requestURL>");
        try {
            if (metadataPrefix == null || metadataPrefix.length() == 0 || identifier == null || identifier.length() == 0 || identifier.equalsIgnoreCase("null") || hasBadArguments(request, validParamNames.iterator(), validParamNames)) {
                throw new BadArgumentException();
            }
            /*
             * Crosswalks crosswalks = abstractCatalog.getCrosswalks(); else
             * if(!crosswalks.containsValue(metadataPrefix)) { throw new
             * CannotDisseminateFormatException(metadataPrefix); }
             */
            else {
                String record = abstractCatalog.getRecord(identifier, metadataPrefix);
                if (record != null) {
                    sb.append(getRequestElement(request, validParamNames, baseURL));
                    sb.append("<" + OAIUtil.getTag("GetRecord") + ">");
                    sb.append(record);
                    sb.append("</" + OAIUtil.getTag("GetRecord") + ">");
                } else {
                    throw new IdDoesNotExistException(identifier);
                }
            }
        } catch (BadArgumentException e) {
            sb.append("<" + OAIUtil.getTag("request") + " verb=\"GetRecord\">");
            sb.append(baseURL);
            sb.append("</" + OAIUtil.getTag("request") + ">");
            sb.append(e.getMessage());
        } catch (CannotDisseminateFormatException e) {
            sb.append(getRequestElement(request, validParamNames, baseURL));
            sb.append(e.getMessage());
        } catch (IdDoesNotExistException e) {
            sb.append(getRequestElement(request, validParamNames, baseURL));
            sb.append(e.getMessage());
        }
        sb.append("</" + OAIUtil.getTag("OAI-PMH") + ">");
        return render(response, "text/xml; charset=UTF-8", sb.toString(), serverTransformer);
    }
}
