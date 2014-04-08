package pt.utl.ist.repox.dataProvider.dataSource;


import org.dom4j.Element;
import pt.utl.ist.repox.recordPackage.RecordRepox;

public interface RecordIdPolicy {

	/**
	 * Create a RecordRepox from a dom representation and an id (id may be null if it can be extracted) If forceId is set to
	 * true, the id of the Record is recordId. Otherwise, if the id is extracted from the Record, the parameter is ignored.
	 *
	 * @param dom
	 * @param id
	 * @param forceId
	 * @param isDeleted
	 * @return
	 * @throws Exception
	 */
	public abstract RecordRepox createRecordRepox(Element recordElement, String recordId, boolean forceId, boolean isDeleted) throws Exception;
}
