package pt.utl.ist.repox.dataProvider.dataSource;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.recordPackage.RecordRepoxExternalId;

import java.util.UUID;

public class IdGenerated implements RecordIdPolicy {
	private static final Logger log = Logger.getLogger(IdGenerated.class);

	public IdGenerated() {
		super();
	}

	private String getNewRecordId() {
		String newId = UUID.randomUUID().toString();
		log.debug("New ID for IdGenerated: " + newId);

		return newId;
	}

	public RecordRepox createRecordRepox(Element recordElement, String recordId, boolean forceId, boolean isDeleted){
		if(forceId) {
			return new RecordRepoxExternalId(recordElement, recordId, isDeleted);
		}
		else {
			return new RecordRepoxExternalId(recordElement, getNewRecordId(), isDeleted);
		}
	}
}
