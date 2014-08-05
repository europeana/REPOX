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

import org.oclc.oai.server.verb.OAIInternalServerError;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 */
public class CrosswalkItem {
    private String          contentType             = null;
    private String          docType                 = null;
    private String          encoding                = null;
    private String          nativeRecordSchema      = null;
    private String          metadataPrefix          = null;
    private String          schema                  = null;
    private String          metadataNamespace       = null;
    private String          xsltName                = null;
    private Crosswalk       crosswalk               = null;
    private int             rank                    = -1;

    /** CrosswalkItem RANK_DIRECTLY_AVAILABLE */
    public static final int RANK_DIRECTLY_AVAILABLE = 0;
    /** CrosswalkItem RANK_DERIVED */
    public static final int RANK_DERIVED            = 1;

    /**
     * Creates a new instance of this class.
     * 
     * @param nativeRecordSchema
     * @param metadataPrefix
     * @param schema
     * @param metadataNamespace
     * @param crosswalk
     * @param rank
     */
    public CrosswalkItem(String nativeRecordSchema, String metadataPrefix, String schema, String metadataNamespace, Crosswalk crosswalk, int rank) {
        this(nativeRecordSchema, metadataPrefix, schema, metadataNamespace, rank);
        this.crosswalk = crosswalk;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param nativeRecordSchema
     * @param metadataPrefix
     * @param schema
     * @param metadataNamespace
     * @param rank
     */
    public CrosswalkItem(String nativeRecordSchema, String metadataPrefix, String schema, String metadataNamespace, int rank) {
        this.nativeRecordSchema = nativeRecordSchema;
        this.metadataPrefix = metadataPrefix;
        this.schema = schema;
        this.metadataNamespace = metadataNamespace;
        this.rank = rank;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param metadataPrefix
     * @param schema
     * @param metadataNamespace
     * @param crosswalk
     */
    public CrosswalkItem(String metadataPrefix, String schema, String metadataNamespace, Crosswalk crosswalk) {
        this(metadataPrefix, metadataPrefix, schema, metadataNamespace, RANK_DIRECTLY_AVAILABLE);
        this.crosswalk = crosswalk;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param metadataPrefix
     * @param schema
     * @param metadataNamespace
     * @param crosswalkClass
     * @throws OAIInternalServerError
     */
    public CrosswalkItem(String metadataPrefix, String schema, String metadataNamespace, Class crosswalkClass) throws OAIInternalServerError {
        this(metadataPrefix, metadataPrefix, schema, metadataNamespace, RANK_DIRECTLY_AVAILABLE);
        try {
            this.crosswalk = getCrosswalk(crosswalkClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        }
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param nativeRecordSchema
     * @param metadataPrefix
     * @param schema
     * @param metadataNamespace
     * @param crosswalkClass
     * @param xsltName
     * @throws OAIInternalServerError
     */
    public CrosswalkItem(String nativeRecordSchema, String metadataPrefix, String schema, String metadataNamespace, Class crosswalkClass, String xsltName) throws OAIInternalServerError {
        this(nativeRecordSchema, metadataPrefix, schema, metadataNamespace, RANK_DERIVED);
        this.xsltName = xsltName;
        try {
            this.crosswalk = getCrosswalk(crosswalkClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        }
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param nativeRecordSchema
     * @param metadataPrefix
     * @param schema
     * @param metadataNamespace
     * @param crosswalkClass
     * @throws OAIInternalServerError
     */
    public CrosswalkItem(String nativeRecordSchema, String metadataPrefix, String schema, String metadataNamespace, Class crosswalkClass) throws OAIInternalServerError {
        this(nativeRecordSchema, metadataPrefix, schema, metadataNamespace, RANK_DIRECTLY_AVAILABLE);
        try {
            this.crosswalk = getCrosswalk(crosswalkClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OAIInternalServerError(e.getMessage());
        }
    }

    private Crosswalk getCrosswalk(Class crosswalkClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor constructor = crosswalkClass.getConstructor(new Class[] { CrosswalkItem.class });
        return (Crosswalk)constructor.newInstance(new Object[] { this });
    }

    @SuppressWarnings("javadoc")
    public String getNativeRecordSchema() {
        return nativeRecordSchema;
    }

    @SuppressWarnings("javadoc")
    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    @SuppressWarnings("javadoc")
    public String getMetadataNamespace() {
        return metadataNamespace;
    }

    @SuppressWarnings("javadoc")
    public String getSchema() {
        return schema;
    }

    @SuppressWarnings("javadoc")
    public Crosswalk getCrosswalk() {
        return crosswalk;
    }

    @SuppressWarnings("javadoc")
    public String getContentType() {
        return contentType;
    }

    @SuppressWarnings("javadoc")
    public String getDocType() {
        return docType;
    }

    @SuppressWarnings("javadoc")
    public String getEncoding() {
        return encoding;
    }

    @SuppressWarnings("javadoc")
    public String getXSLTName() {
        return xsltName;
    }
    
    @SuppressWarnings("javadoc")
    public int getRank() {
        return rank;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CrosswalkItem: ");
        sb.append(getNativeRecordSchema());
        sb.append(":");
        sb.append(getMetadataPrefix());
        sb.append(":");
        sb.append(getMetadataNamespace());
        sb.append(":");
        sb.append(getSchema());
        sb.append(":");
        sb.append(getCrosswalk());
        sb.append(":");
        sb.append(getRank());
        return sb.toString();
    }
}