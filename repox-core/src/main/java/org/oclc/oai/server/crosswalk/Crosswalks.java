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
package org.oclc.oai.server.crosswalk;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Crosswalks manages all the Crosswalk objects this repository supports.
 * 
 * @author Jeffrey A. Young
 */
public class Crosswalks {
    private static final boolean debug         = false;

    // map of metadataPrefix/CrosswalkItem
    private Map                  crosswalksMap = new HashMap();

    /**
     * Find out which metadataFormats this repository supports and create the
     * corresponding Crosswalk objects for each.
     * 
     * @param properties
     *            a properties object containing Crosswalks entries
     */
    public Crosswalks(Properties properties) {
        String propertyPrefix = "Crosswalks.";
        Enumeration propNames = properties.propertyNames();
        while (propNames.hasMoreElements()) {
            String propertyName = (String)propNames.nextElement();
            if (propertyName.startsWith(propertyPrefix)) {
                String schemaLabel = propertyName.substring(propertyPrefix.length());
                String formatClassName = (String)properties.get(propertyName);
                try {
                    Class crosswalkClass = Class.forName(formatClassName);
                    Crosswalk crosswalk = null;
                    try {
                        Constructor crosswalkConstructor = crosswalkClass.getConstructor(new Class[] { String.class, Properties.class });
                        crosswalk = (Crosswalk)crosswalkConstructor.newInstance(new Object[] { schemaLabel, properties });
                    } catch (NoSuchMethodException e) {
                        Constructor crosswalkConstructor = crosswalkClass.getConstructor(new Class[] { Properties.class });
                        crosswalk = (Crosswalk)crosswalkConstructor.newInstance(new Object[] { properties });
                    }
                    CrosswalkItem crosswalkItem = new CrosswalkItem(schemaLabel, crosswalk.getSchemaURL(), crosswalk.getNamespaceURL(), crosswalk);
                    crosswalksMap.put(schemaLabel, crosswalkItem);
                    if (debug) {
                        System.out.println("Crosswalks.Crosswalks: " + schemaLabel + "=" + crosswalk);
                    }
                } catch (Exception e) {
                    System.err.println("Crosswalks: couldn't construct: " + formatClassName);
                    e.printStackTrace();
                }
            }
        }
        if (crosswalksMap.size() == 0) {
            //			System.err.println("Crosswalks entries are missing from properties file");
        }
    }

    /**
     * Creates a new instance of this class.
     * @param crosswalkItemMap
     */
    public Crosswalks(Map crosswalkItemMap) {
        for (Object o : crosswalkItemMap.values()) {
            CrosswalkItem crosswalkItem = (CrosswalkItem)o;
            String schemaLabel = crosswalkItem.getMetadataPrefix();
            // Crosswalk crosswalk = crosswalkItem.getCrosswalk();
            crosswalksMap.put(schemaLabel, crosswalkItem);
        }

        if (crosswalksMap.size() == 0) {
            //			System.err.println("Crosswalks entries are missing from properties file");
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator iterator = iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            sb.append(entry.getKey());
            sb.append("=");
            CrosswalkItem crosswalkItem = (CrosswalkItem)entry.getValue();
            sb.append(crosswalkItem.getCrosswalk().toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Get the metadataPrefix associated with the specified namespace/schema
     * 
     * @param namespaceURI
     *            the namespaceURI portion of the format specifier
     * @param schemaURL
     *            the schemaURL portion of the format specifier
     * @return a String containing the metadataPrefix value associated with this
     *         pair
     */
    public String getMetadataPrefix(String namespaceURI, String schemaURL) {
        StringBuffer sb = new StringBuffer();
        if (namespaceURI != null) {
            sb.append(namespaceURI);
        }
        sb.append(" ").append(schemaURL);
        Iterator iterator = crosswalksMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            if (((CrosswalkItem)entry.getValue()).getCrosswalk().getSchemaLocation().equals(sb.toString())) { return (String)entry.getKey(); }
        }
        return null;
    }

    /**
     * Get the schemaURL associated with the specified metadataPrefix
     * 
     * @param metadataPrefix
     *            the prefix desired
     * @return a String containing the schemaURL associated with the
     *         metadataPrefix
     */
    public String getSchemaURL(String metadataPrefix) {
        String schemaLocation = getSchemaLocation(metadataPrefix);
        StringTokenizer tokenizer = new StringTokenizer(schemaLocation);
        String temp = tokenizer.nextToken();
        try {
            return tokenizer.nextToken();
        } catch (NoSuchElementException e) {
            return temp;
        }
    }

    /**
     * Get the namespaceURI associated with the specified metadataPrefix
     * 
     * @param metadataPrefix
     *            the prefix desired
     * @return a String containing the namespaceURI associated with the
     *         metadataPrefix
     */
    public String getNamespaceURI(String metadataPrefix) {
        String schemaLocation = getSchemaLocation(metadataPrefix);
        StringTokenizer tokenizer = new StringTokenizer(schemaLocation);
        return tokenizer.nextToken();
    }

    /**
     * @param metadataPrefix
     * @return native record schema
     */
    public String getNativeRecordSchema(String metadataPrefix) {
        CrosswalkItem crosswalkItem = (CrosswalkItem)crosswalksMap.get(metadataPrefix);
        if (crosswalkItem == null) {
            return null;
        } else {
            return crosswalkItem.getNativeRecordSchema();
        }
    }

    /**
     * Get the namespaceURI/schemaURL associated with the specified
     * metadataPrefix
     * 
     * @param metadataPrefix
     *            the prefix desired
     * @return a String containing the namespaceURI/schemaURL associated with
     *         the metadataPrefix
     */
    public String getSchemaLocation(String metadataPrefix) {
        CrosswalkItem crosswalkItem = (CrosswalkItem)crosswalksMap.get(metadataPrefix);
        if (crosswalkItem != null) {
            return crosswalkItem.getCrosswalk().getSchemaLocation();
        } else {
            return null;
        }
    }

    /**
     * Get the namespaceURI/schemaURL associated with the specified
     * metadataPrefix
     * 
     * @param metadataPrefix
     *            the prefix desired
     * @return a String containing the namespaceURI/schemaURL associated with
     *         the metadataPrefix
     */
    public String getContentType(String metadataPrefix) {
        CrosswalkItem crosswalkItem = (CrosswalkItem)crosswalksMap.get(metadataPrefix);
        if (crosswalkItem != null) {
            return crosswalkItem.getCrosswalk().getContentType();
        } else {
            return null;
        }
    }

    /**
     * Get the DOCTYPE associated with the specified metadataPrefix
     * 
     * @param metadataPrefix
     *            the prefix desired
     * @return a String containing the DOCTYPE associated with the
     *         metadataPrefix
     */
    public String getDocType(String metadataPrefix) {
        CrosswalkItem crosswalkItem = (CrosswalkItem)crosswalksMap.get(metadataPrefix);
        if (crosswalkItem != null) {
            return crosswalkItem.getCrosswalk().getDocType();
        } else {
            return null;
        }
    }

    /**
     * @param metadataPrefix
     * @return the encoding
     */
    public String getEncoding(String metadataPrefix) {
        CrosswalkItem crosswalkItem = (CrosswalkItem)crosswalksMap.get(metadataPrefix);
        if (crosswalkItem != null) {
            return crosswalkItem.getCrosswalk().getEncoding();
        } else {
            return null;
        }
    }

    /**
     * Does the specified metadataPrefix appears in the list of
     * supportedFormats?
     * 
     * @param metadataPrefix
     *            the prefix desired
     * @return true if prefix is supported, false otherwise.
     */
    public boolean containsValue(String metadataPrefix) {
        return (crosswalksMap.get(metadataPrefix) != null);
    }

    /**
     * Get an iterator containing Map.Entry's for the supported formats.
     * 
     * @return an Iterator containing Map.Entry's for each supported format.
     */
    public Iterator iterator() {
        return crosswalksMap.entrySet().iterator();
    }
}
