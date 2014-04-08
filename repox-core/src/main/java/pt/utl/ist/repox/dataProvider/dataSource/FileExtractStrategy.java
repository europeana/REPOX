package pt.utl.ist.repox.dataProvider.dataSource;

import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.marc.CharacterEncoding;
import pt.utl.ist.repox.recordPackage.RecordRepox;

import java.io.File;


public interface FileExtractStrategy {

    public interface RecordHandler{
        public void handleRecord(RecordRepox record);
    }

	/**
	 * True if this file extracting strategy only for XML files, false otherwise.
	 */
	public abstract boolean isXmlExclusive();

	/**
	 * Returns an Iterator of records.
	 */
	public abstract void iterateRecords(RecordHandler recordHandler, DataSource dataSource, File file, CharacterEncoding characterEncoding,
			File logFile) throws Exception;
}
