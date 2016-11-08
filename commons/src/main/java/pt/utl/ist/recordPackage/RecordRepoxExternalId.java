/*
 * Created on 17/Mar/2006
 *
 */
package pt.utl.ist.recordPackage;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import pt.utl.ist.util.XmlUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 */
public class RecordRepoxExternalId implements RecordRepox {
    private static final Logger log              = Logger.getLogger(RecordRepoxExternalId.class);
    static final long           serialVersionUID = 1;

    protected Element           dom;
    protected Object            recordId;
    protected boolean           isDeleted        = false;
    protected boolean           isEmpty        = false;

    @Override
    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    @Override
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public Object getId() {
        return recordId;
    }

    @Override
    public Element getDom() {
        return dom;
    }

    /**
     * Creates a new instance of this class.
     */
    public RecordRepoxExternalId() {
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param dom
     * @param recordId
     */
    public RecordRepoxExternalId(Element dom, Object recordId) {
        this.dom = dom;
        this.recordId = recordId;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param dom
     * @param recordId
     * @param isDeleted
     */
    public RecordRepoxExternalId(Element dom, Object recordId, boolean isDeleted) {
        this(dom, recordId);
        this.isDeleted = isDeleted;
    }

    @Override
    public byte[] serialize() {
        try {
            if (dom == null) {
                return null;
            } else {
                byte[] domToBytes = dom.asXML().getBytes("UTF-8");
                return domToBytes;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deserialize(byte[] bytes) throws DocumentException, UnsupportedEncodingException {
        dom = XmlUtil.getRootElement(bytes);
    }

}
