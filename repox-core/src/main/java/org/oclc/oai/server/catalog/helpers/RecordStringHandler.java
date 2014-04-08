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
package org.oclc.oai.server.catalog.helpers;

import org.oclc.oai.util.OAIUtil;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class RecordStringHandler extends DefaultHandler {
    private static final boolean debug = false;
    private static final String OAI_NS = "http://www.openarchives.org/OAI/2.0/";
    private static final String DATABASE_NS = "http://www.oclc.org/pears/";
//     private static final String OAI_DC_NS = "http://www.openarchives.org/OAI/2.0/oai_dc/";
//     private static final String MARC21_NS = "http://www.loc.gov/MARC21/slim";
//     private static final String REG_NS = "http://info-uri.info/registry";
//     private static final String MTX_NS = "http://www.w3.org/1999/xhtml";
//     private static final String PRO_NS = "info:ofi/pro";
//     private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";
//     private static final String XSL_NS = "http://www.w3.org/1999/XSL/Transform";
    private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
    private SortedMap nativeRecords = new TreeMap();
    private int recordFlag = 0;
    private int metadataFlag = 0;
    private StringWriter metadata = null;
    private int recordidFlag = 0;
    private StringBuffer recordid = null;
    private String schemaLocation = null;
    private int identifierFlag = 0;
    private StringBuffer identifier = null;
    private int datestampFlag = 0;
    private StringBuffer datestamp = null;
    private ArrayList setSpecs = null;
    private int setSpecFlag = 0;
    private StringBuffer setSpec = null;
//     private static Logger logger = Logger.getLogger(RecordStringHandler.class);
//     static {
//         BasicConfigurator.configure();
//     }

    public SortedMap getNativeRecords() { return nativeRecords; }
    
    public void startElement(String uri, String localName, String qName,
			     Attributes attrs) {
        if (debug) {
            System.out.println("startElement: " + uri + ", " + localName + ", "
                               + qName + ", ");
        }
	if (OAI_NS.equals(uri) && "record".equals(localName)) {
	    setSpecs = new ArrayList();
	    recordFlag++;
	}
	if (metadataFlag > 0) {
	    metadata.write("<" + getName(localName, qName));
	    if (attrs != null) {
		for (int i=0; i<attrs.getLength(); ++i) {
		    String attributeName = getName(attrs.getLocalName(i),
						   attrs.getQName(i));
		    
		    // modified by Colin DOig, 6 September 2006
		    // xmlEncode ",&,< etc within attributes
		    // previously invalid XML was being produced.
		    metadata.write(" " + attributeName + "=\"" +
				  OAIUtil.xmlEncode(attrs.getValue(i)) + "\"");
		}
	    }
	    metadata.write(">");
	}
	if (schemaLocation == null
            && metadataFlag == 1) {
// 	    && ((OAI_DC_NS.equals(uri) && "dc".equals(localName))
//             || (XSD_NS.equals(uri) && "schema".equals(localName))
//             || (XSL_NS.equals(uri) && "stylesheet".equals(localName))
//             || (MARC21_NS.equals(uri) && "record".equals(localName))
//             || (REG_NS.equals(uri) && "info-registry-entry".equals(localName))
//             || (MTX_NS.equals(uri) && "html".equals(localName))
//             || (PRO_NS.equals(uri) && "profile".equals(localName)))) {
            schemaLocation = attrs.getValue(XSI_NS, "schemaLocation");
	}
	if (OAI_NS.equals(uri) && "metadata".equals(localName)) {
	    if (metadata == null) {
		metadata = new StringWriter();
	    }
	    metadataFlag++;
	}
	if (OAI_NS.equals(uri) && "identifier".equals(localName)) {
	    if (identifier == null) {
		identifier = new StringBuffer();
	    }
	    identifierFlag++;
	}
	if (DATABASE_NS.equals(uri) && "recordid".equals(localName)) {
	    if (recordid == null) {
		recordid = new StringBuffer();
	    }
	    recordidFlag++;
	}
	if (OAI_NS.equals(uri) && "datestamp".equals(localName)) {
	    if (datestamp == null) {
		datestamp = new StringBuffer();
	    }
	    datestampFlag++;
	}
	if (OAI_NS.equals(uri) && "setSpec".equals(localName)) {
	    if (setSpec == null) {
		setSpec = new StringBuffer();
	    }
	    setSpecFlag++;
	}
    }

    public void endElement(String uri, String localName, String qName) {
	if (OAI_NS.equals(uri) && "identifier".equals(localName)) {
	    identifierFlag--;
	}
	if (DATABASE_NS.equals(uri) && "recordid".equals(localName)) {
	    recordidFlag--;
	}
	if (OAI_NS.equals(uri) && "datestamp".equals(localName)) {
	    datestampFlag--;
	}
	if (OAI_NS.equals(uri) && "setSpec".equals(localName)) {
	    setSpecs.add(setSpec.toString());
	    setSpec = null;
	    setSpecFlag--;
	}
	if (OAI_NS.equals(uri) && "record".equals(localName)) {
	    recordFlag--;
	    if (recordFlag == 0) {
		HashMap nativeRecord = new HashMap();
		nativeRecord.put("recordString", metadata.toString());
//                 logger.debug(metadata.toString());
                if (debug) {
                    System.out.println("metadata: " + metadata.toString());
                }
                nativeRecord.put("localIdentifier", identifier.toString());
                if (debug) {
                    System.out.println("localIdentifier=" + identifier.toString());
                }
                nativeRecord.put("recordid", recordid.toString());
                if (debug) {
                    System.out.println("recordid=" + recordid.toString());
                }
                nativeRecord.put("schemaLocation", schemaLocation);
                if (debug) {
                    System.out.println("schemaLocation=" + schemaLocation);
                }
                nativeRecord.put("datestamp", datestamp.toString());
                if (debug) {
                    System.out.println("datestamp=" + datestamp.toString());
                }
 		nativeRecord.put("setSpecs", setSpecs);
                nativeRecords.put(recordid.toString().toLowerCase(), nativeRecord);
		setSpecs = null;
                identifier = null;
                metadata = null;
                recordid = null;
                schemaLocation = null;
                datestamp = null;
	    }
	}
	if (OAI_NS.equals(uri) && "metadata".equals(localName)) {
	    metadataFlag--;
	}
	if (metadataFlag > 0) {
	    metadata.write("</" + getName(localName, qName) + ">");
	}
    }

    public void characters(char[] ch, int start, int length) {
        String s = new String(ch, start, length);
	if (metadataFlag > 0) {
	    metadata.write(OAIUtil.xmlEncode(s));
	}
	if (identifierFlag > 0) {
	    identifier.append(s);
	}
	if (recordidFlag > 0) {
	    recordid.append(s);
	}
	if (datestampFlag > 0) {
	    datestamp.append(s);
	}
	if (setSpecFlag > 0) {
	    setSpec.append(s);
	}
    }

    private String getName(String s1, String s2) {
        if (s2==null || "".equals(s2))
            return s1;
        else
            return s2;
    }

//     public InputSource resolveEntity(String publicId, String systemId) {
//         System.out.println("RecordStringHandler.resolveEntity:publicId=" + publicId);
//         System.out.println("RecordStringHandler.resolveEntity:systemId=" + systemId);
//         return null;
//     }

//     public void skippedEntity(String name) {
//         System.out.println("RecordStringHandler.skippedEntity:name=" + name);
//     }

//     public void unparsedEntityDecl(String name, String publicId, String systemId,
//                                    String notationName) {
//         System.out.println("RecordStringHandler.unparsedEntityDecl: name=" + name);
//         System.out.println("RecordStringHandler.unparsedEntityDecl: publicId=" + publicId);
//         System.out.println("RecordStringHandler.unparsedEntityDecl: systemId=" + systemId);
//         System.out.println("RecordStringHandler.unparsedEntityDecl: notationName=" + notationName);
//     }
}
