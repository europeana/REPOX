package pt.utl.ist.dataProvider.dataSource;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.wordnik.swagger.annotations.ApiModel;

import pt.utl.ist.recordPackage.RecordRepox;
import pt.utl.ist.recordPackage.RecordRepoxExternalId;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "IdGeneratedRecordIdPolicy")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "An IdGeneratedRecordIdPolicy")
public class IdGeneratedRecordIdPolicy implements RecordIdPolicy {
    private static final Logger log = Logger.getLogger(IdGeneratedRecordIdPolicy.class);
    public static final String IDGENERATED = "IdGenerated";

    /**
     * Creates a new instance of this class.
     */
    public IdGeneratedRecordIdPolicy() {
        super();
    }

    private String getNewRecordId() {
        String newId = UUID.randomUUID().toString();
        log.debug("New ID for IdGenerated: " + newId);

        return newId;
    }

    @Override
    public RecordRepox createRecordRepox(Element recordElement, String recordId, boolean forceId, boolean isDeleted) {
        if (forceId) {
            return new RecordRepoxExternalId(recordElement, recordId, isDeleted);
        } else {
            return new RecordRepoxExternalId(recordElement, getNewRecordId(), isDeleted);
        }
    }
}
