package pt.utl.ist.dataProvider.dataSource;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.dom4j.Element;

import com.wordnik.swagger.annotations.ApiModel;

import pt.utl.ist.recordPackage.RecordRepox;
import pt.utl.ist.recordPackage.RecordRepoxExternalId;

@XmlRootElement(name = "IdProvidedRecordIdPolicy")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "An IdProvidedRecordIdPolicy")
public class IdProvidedRecordIdPolicy implements RecordIdPolicy {
    public static final String IDPROVIDED = "IdProvided";
    
    /**
     * Required for JAXB
     */
    public IdProvidedRecordIdPolicy() {
        super();
    }
    
	@Override
    public RecordRepox createRecordRepox(Element recordElement, String recordId, boolean forceId, boolean isDeleted) {
		return new RecordRepoxExternalId(recordElement, recordId, isDeleted);
	}
}
