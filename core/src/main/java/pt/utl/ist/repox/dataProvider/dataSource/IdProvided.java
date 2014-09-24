package pt.utl.ist.repox.dataProvider.dataSource;


import org.dom4j.Element;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.recordPackage.RecordRepoxExternalId;

/**
 */
public class IdProvided implements RecordIdPolicy {
	@Override
    public RecordRepox createRecordRepox(Element recordElement, String recordId, boolean forceId, boolean isDeleted) {
		return new RecordRepoxExternalId(recordElement, recordId, isDeleted);
	}
}
