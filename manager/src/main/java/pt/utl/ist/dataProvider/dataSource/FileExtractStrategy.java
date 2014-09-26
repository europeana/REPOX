package pt.utl.ist.dataProvider.dataSource;

import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.repox.marc.CharacterEncoding;
import pt.utl.ist.repox.recordPackage.RecordRepox;

import java.io.File;

/**
 */
public interface FileExtractStrategy {

    /**
     */
    public interface RecordHandler {
        /**
         * @param record
         */
        void handleRecord(RecordRepox record);
    }

    /**
     * True if this file extracting strategy only for XML files, false
     * otherwise.
     * 
     * @return boolean
     */
    boolean isXmlExclusive();

    /**
     * Returns an Iterator of records.
     * @param recordHandler 
     * @param dataSource 
     * @param file 
     * @param characterEncoding 
     * @param logFile 
     * @throws Exception 
     */
    void iterateRecords(RecordHandler recordHandler, DataSource dataSource, File file, CharacterEncoding characterEncoding, File logFile) throws Exception;
}
