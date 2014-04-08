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
import java.util.*;
//import org.xml.sax.SAXException;

/**
 * This class represents an Identify response on either the server or
 * on the client
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class Identify extends ServerVerb {
    private static ArrayList validParamNames = new ArrayList();
    static {
        validParamNames.add("verb");
    }
    
    /**
     * Construct the xml response on the server side.
     *
     * @param context the servlet context
     * @param request the servlet request
     * @return a String containing the xml response
     */
    public static String construct(HashMap context,
            HttpServletRequest request,
            HttpServletResponse response,
            Transformer serverTransformer)
    throws TransformerException {
        String version = (String)context.get("OAIHandler.version");
        AbstractCatalog abstractCatalog =
            (AbstractCatalog)context.get("OAIHandler.catalog");
        Properties properties =
            (Properties)context.get("OAIHandler.properties");
        String baseURL = properties.getProperty("OAIHandler.baseURL");

        if (baseURL == null) {
            try {
                baseURL = request.getRequestURL().toString();
            } catch (java.lang.NoSuchMethodError f) {
                baseURL = request.getRequestURL().toString();
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        String styleSheet = properties.getProperty("OAIHandler.styleSheet");
        if (styleSheet != null) {
            sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"");
            sb.append(styleSheet);
            sb.append("\"?>");
        }

        sb.append("<" + OAIUtil.getTag("OAI-PMH") + " xmlns" + (ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().isUseOAINamespace() ? ":oai" : "") + "=\"http://www.openarchives.org/OAI/2.0/\"");
        sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        sb.append(" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/");
        sb.append(" http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">");
        sb.append("<" + OAIUtil.getTag("responseDate") + ">");
        sb.append(createResponseDate(new Date()));
        sb.append("</" + OAIUtil.getTag("responseDate") + ">");
        sb.append(getRequestElement(request, validParamNames, baseURL));
        if (hasBadArguments(request, validParamNames.iterator(), validParamNames)) {
            sb.append(new BadArgumentException().getMessage());
        } else {
            sb.append("<" + OAIUtil.getTag("Identify") + ">");
            sb.append("<" + OAIUtil.getTag("repositoryName") + ">");
            sb.append(properties.getProperty("Identify.repositoryName", "undefined"));

            sb.append("</" + OAIUtil.getTag("repositoryName") + ">");
            //sb.append("</" + OAIUtil.getTag("Identify") + ">");

            sb.append("<" + OAIUtil.getTag("baseURL") + ">");
            sb.append(baseURL);
            sb.append("</" + OAIUtil.getTag("baseURL") + ">");

            sb.append("<" + OAIUtil.getTag("protocolVersion") + ">2.0</" + OAIUtil.getTag("protocolVersion") + ">");

            sb.append("<" + OAIUtil.getTag("adminEmail") + ">");
            String repoxAdmin = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getAdministratorEmail();
            sb.append("mailto:" + repoxAdmin == null || repoxAdmin.isEmpty() ? "undefined" : repoxAdmin);
            sb.append("</" + OAIUtil.getTag("adminEmail") + ">");

            sb.append("<" + OAIUtil.getTag("earliestDatestamp") + ">");
            sb.append(properties.getProperty("Identify.earliestDatestamp", "undefined"));
            sb.append("</" + OAIUtil.getTag("earliestDatestamp") + ">");
            sb.append("<" + OAIUtil.getTag("deletedRecord") + ">");
            sb.append(properties.getProperty("Identify.deletedRecord", "undefined"));
            sb.append("</" + OAIUtil.getTag("deletedRecord") + ">");
            String granularity = properties.getProperty("AbstractCatalog.granularity");
            if (granularity != null) {
                sb.append("<" + OAIUtil.getTag("granularity") + ">");
                sb.append(granularity);
                sb.append("</" + OAIUtil.getTag("granularity") + ">");
            }
            // 	String compression = properties.getProperty("Identify.compression");
            // 	if (compression != null) {
            sb.append("<" + OAIUtil.getTag("compression") + ">gzip</" + OAIUtil.getTag("compression") + ">");
//          sb.append("<compression>compress</compression>");
            sb.append("<" + OAIUtil.getTag("compression") + ">deflate</" + OAIUtil.getTag("compression") + ">");
            // 	}
            String repositoryIdentifier = properties.getProperty("Identify.repositoryIdentifier");
            String sampleIdentifier = properties.getProperty("Identify.sampleIdentifier");
            if (repositoryIdentifier != null && sampleIdentifier != null) {
                sb.append("<" + OAIUtil.getTag("description") + ">");
                sb.append("<oai-identifier xmlns=\"http://www.openarchives.org/OAI/2.0/oai-identifier\"");
                sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
                sb.append(" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai-identifier http://www.openarchives.org/OAI/2.0/oai-identifier.xsd\">");
                sb.append("<scheme>oai</scheme>");
                sb.append("<repositoryIdentifier>");
                sb.append(repositoryIdentifier);
                sb.append("</repositoryIdentifier>");
                sb.append("<delimiter>:</delimiter>");
                sb.append("<sampleIdentifier>");
                sb.append(sampleIdentifier);
                sb.append("</sampleIdentifier>");
                sb.append("</oai-identifier>");
                sb.append("</" + OAIUtil.getTag("description") + ">");
            }
            String propertyPrefix = "Identify.description";
            Enumeration propNames = properties.propertyNames();
            while (propNames.hasMoreElements()) {
                String propertyName = (String)propNames.nextElement();
                if (propertyName.startsWith(propertyPrefix)) {
                    sb.append("<" + OAIUtil.getTag("description") + ">");
                    sb.append((String)properties.get(propertyName));
                    sb.append("</" + OAIUtil.getTag("description") + ">");
                    sb.append("\n");
                }
            }

            sb.append("<" + OAIUtil.getTag("description") + "><toolkit xsi:schemaLocation=\"http://oai.dlib.vt.edu/OAI/metadata/toolkit http://alcme.oclc.org/oaicat/toolkit.xsd\" xmlns=\"http://oai.dlib.vt.edu/OAI/metadata/toolkit\"><title>OCLC's OAICat Repository Framework</title><author><name>Jeffrey A. Young</name><email>jyoung@oclc.org</email><institution>OCLC</institution></author><version>");
            sb.append(version);
            sb.append("</version><toolkitIcon>http://alcme.oclc.org/oaicat/oaicat_icon.gif</toolkitIcon><URL>http://www.oclc.org/research/software/oai/cat.shtm</URL></toolkit>" + "</" + OAIUtil.getTag("description") + ">");
            String descriptions = abstractCatalog.getDescriptions();
            if (descriptions != null) {
                sb.append(descriptions);
            }
            sb.append("</" + OAIUtil.getTag("Identify") + ">");
        }
        sb.append("</" + OAIUtil.getTag("OAI-PMH") + ">");
        return render(response, "text/xml; charset=UTF-8", sb.toString(), serverTransformer);
    }
}
