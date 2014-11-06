package pt.utl.ist.dataProvider.dataSource;


import org.dom4j.Element;

import pt.utl.ist.recordPackage.RecordRepox;
import pt.utl.ist.recordPackage.RecordRepoxExternalId;

/**
 */
public class IdProvidedRecordIdPolicy implements RecordIdPolicy {
    public static final String IDPROVIDED = "IdProvided";
    
	@Override
    public RecordRepox createRecordRepox(Element recordElement, String recordId, boolean forceId, boolean isDeleted) {
		return new RecordRepoxExternalId(recordElement, recordId, isDeleted);
	}
}
