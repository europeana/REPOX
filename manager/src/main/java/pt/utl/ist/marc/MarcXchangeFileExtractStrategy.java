/*
 * Created on 2007/01/23
 *
 */
package pt.utl.ist.marc;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import pt.utl.ist.characters.RecordCharactersConverter;
import pt.utl.ist.characters.UnderCode32Remover;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.dataSource.FileExtractStrategy;
import pt.utl.ist.marc.CharacterEncoding;
import pt.utl.ist.marc.MarcRecord;
import pt.utl.ist.marc.xml.IteratorMarcXChange;
import pt.utl.ist.marc.xml.MarcSaxParser;
import pt.utl.ist.recordPackage.RecordRepox;
import pt.utl.ist.util.StringUtil;
import pt.utl.ist.util.TimeUtil;
import pt.utl.ist.util.XmlUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

/**
 */
public class MarcXchangeFileExtractStrategy implements FileExtractStrategy {
  public static final String OLDCLASS = "MarcXchangeFileExtract";
    //	MetadataFormat.MarcXchange;

    private static final Logger log = Logger.getLogger(MarcXchangeFileExtractStrategy.class);

    @Override
    public void iterateRecords(RecordHandler recordHandler, DataSource dataSource, File file, CharacterEncoding characterEncoding, File logFile) {
        Iterator<RecordRepox> it = new RecordIterator(dataSource, file, characterEncoding, logFile);
        while (it.hasNext()) {
            recordHandler.handleRecord(it.next());
        }
    }

    @Override
    public boolean isXmlExclusive() {
        return true;
    }

    private class RecordIterator implements Iterator<RecordRepox> {
        private DataSource        dataSource;
        private File              file;
        private CharacterEncoding characterEncoding;
        private File              logFile;

        private Iterator<MarcRecord>  recordIterator;

        public RecordIterator(DataSource dataSource, File file, CharacterEncoding characterEncoding, File logFile) {
            this.dataSource = dataSource;
            this.file = file;
            this.characterEncoding = characterEncoding;
            this.logFile = logFile;

            try {
                //				StringUtil.simpleLog("Extracting records from file: " + file.getName(), this.getClass(), logFile);
                TimeUtil.getTimeSinceLastTimerArray(2);
                if (file.length() > 1048576) { // File larger than 1MB - Use asynchronous iteration
                    recordIterator = new IteratorMarcXChange(file);
                } else {
                    recordIterator = MarcSaxParser.parse(new FileInputStream(file)).iterator();
                }
                log.debug("Parsed MarcXChange: " + TimeUtil.getTimeSinceLastTimerArray(2));
            } catch (Exception e) {
                recordIterator = null;
                StringUtil.simpleLog("Error parsing records from file: " + file.getName() + " ERROR: " + e.getMessage(), this.getClass(), logFile);
            }
        }

        @Override
        public boolean hasNext() {
            return recordIterator != null && recordIterator.hasNext();
        }

        @Override
        public RecordRepox next() {
            try {
                TimeUtil.getTimeSinceLastTimerArray(2);
                MarcRecord currentRecord = recordIterator.next();
                log.debug("Iterate MarcXChange: " + TimeUtil.getTimeSinceLastTimerArray(2));
                RecordCharactersConverter.convertRecord(currentRecord, new UnderCode32Remover());

                boolean isRecordDeleted = (currentRecord.getLeader().charAt(5) == 'd');
                RecordRepoxMarc recordMarc = new RecordRepoxMarc(currentRecord);
                recordMarc.setMarcFormat(dataSource.getMarcFormat());
                RecordRepox record = dataSource.getRecordIdPolicy().createRecordRepox(recordMarc.getDom(), recordMarc.getId(), false, isRecordDeleted);
                log.debug("Adding to import list record with id:" + record.getId());

                return record;

            } catch (Exception e) {
                StringUtil.simpleLog("Error importing record from file: " + file.getName() + " ERROR: " + e.getMessage(), this.getClass(), logFile);
                log.error(file.getName() + ": " + e.getMessage(), e);
                return null;
            }
        }

        @Override
        public void remove() {
        }

    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Iterator<MarcRecord> recordIterator = MarcSaxParser.parse(new FileInputStream(new File("f:/temp/2marcxchange/999.xml"))).iterator();
        MarcRecord currentRecord = recordIterator.next();
        Element marcRootElement = XmlUtil.getRootElement(currentRecord.toXmlbytes());
        Element recordElement = marcRootElement;
        if (marcRootElement.elements().size() == 1 && marcRootElement.getName().equals("collection")) {
            recordElement = (Element)marcRootElement.elements().get(0);
        }

        System.out.println(recordElement.asXML());
    }
}
