package pt.utl.ist.z3950;

import org.apache.log4j.Logger;
import org.jzkit.search.util.RecordModel.InformationFragment;
import org.jzkit.search.util.ResultSet.IRResultSet;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.marc.RecordRepoxMarc;
import pt.utl.ist.repox.characters.RecordCharactersConverter;
import pt.utl.ist.repox.characters.UnderCode32Remover;
import pt.utl.ist.repox.marc.Record;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.util.StringUtil;

import java.io.*;
import java.util.*;

/**
 * Z39.50 Harvester by control number-local (from provided list)
 * Ids are extracted from a file (1 per line)
 */
public class IdListHarvester extends AbstractHarvester {
    private static final Logger log                         = Logger.getLogger(IdListHarvester.class);
    private static final int    stopAfterFailureCount       = 3;                                       //50;
    private static final int    sleepTimeAfterFailureInSecs = 5;                                       //300;
    private static final int    maxSleepsBeforeFailure      = 50;
    private String              idBibAttribute              = "12";
    private File                idListFile;

    @SuppressWarnings("javadoc")
    public String getIdBibAttribute() {
        return idBibAttribute;
    }

    @SuppressWarnings("javadoc")
    public void setIdBibAttribute(String idBibAttribute) {
        this.idBibAttribute = idBibAttribute;
    }

    @SuppressWarnings("javadoc")
    public File getIdListFile() {
        return idListFile;
    }

    @SuppressWarnings("javadoc")
    public void setIdListFile(File idListFile) {
        this.idListFile = idListFile;
    }

    /**
     *
     * @param target
     * @param idListFile file with one id per line
     */
    public IdListHarvester(Target target, File idListFile) {
        super(target);
        this.idListFile = idListFile;
    }

    private class RecordIterator implements Iterator<RecordRepox> {
        private DataSource                       dataSource;
        private File                             logFile;
        private BufferedReader                   reader;
        Record                                   nextRecord = null;
        //
        String                                   recordId;
        //liz
        int                                      currentId  = 0;
        //
        private Enumeration<InformationFragment> currentInformationFragment;

        public RecordIterator(DataSource dataSource, File logFile, boolean fullIngest) {
            this.dataSource = dataSource;
            this.logFile = logFile;

            try {
                FileReader fr = new FileReader(idListFile);
                this.reader = new BufferedReader(fr);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found: " + idListFile.getAbsolutePath(), e);
            } catch (IOException e) {
                e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
            }
        }

        private boolean getNextBatch() throws HarvestFailureException {
            int consecutiveErrors = 0;
            int consecutiveSleeps = 0;
            try {
                recordId = reader.readLine();

                if (recordId == null) {
                    reader.close();
                    return false;
                }

                recordId = recordId.replace('\"', ' ');

                while (true) {
                    String queryStr = "@attrset bib-1 " + "@attr 1=" + idBibAttribute + " \"" + recordId + "\"";

                    currentId++;
                    log.debug(currentId);

                    IRResultSet results = runQuery(queryStr, logFile, dataSource.getId());
                    if (results == null) {
                        consecutiveErrors++;
                        if (consecutiveErrors > stopAfterFailureCount) {

                            StringUtil.simpleLog("Server unavailable: consecutive errors: " + consecutiveErrors, this.getClass(), logFile);

                            return true;

                            /*
                            if(consecutiveSleeps > maxSleepsBeforeFailure)
                                throw new HarvestFailureException("Importing aborted - Too many consecutive errors");
                            consecutiveSleeps++;
                            //consecutiveErrors = 0;
                            try {
                                StringUtil.simpleLog("Server unavailable: will wait "+sleepTimeAfterFailureInSecs+ " seconds." , this.getClass(), logFile);
                                Thread.sleep(sleepTimeAfterFailureInSecs*1000);
                            } catch (InterruptedException e2){
                            }
                            */
                        }
                    } else if (results.getFragmentCount() == 0) {
                        results.close();
                        return getNextBatch();
                    } else {
                        consecutiveErrors = 0;
                        // StringUtil.simpleLog("Iterate over results (status=" + results.getStatus() + "), count=" + results.getFragmentCount(),
                        //this.getClass(), logFile);
                        currentInformationFragment = new org.jzkit.search.util.ResultSet.ReadAheadEnumeration(results);

                        try {
                            nextRecord = handleRecord(currentInformationFragment.nextElement());
                        } catch (ClassCastException e) {
                            StringUtil.simpleLog("ClassCastException: record id: " + recordId, this.getClass(), logFile);
                        }

                        results.close();
                        return true;
                    }
                }
            } catch (IOException e) {
                throw new HarvestFailureException("Error reading file: " + idListFile.getAbsolutePath(), e);
            }
        }

        @Override
        public boolean hasNext() {
            if (nextRecord != null) {
                return true;
            } else {
                try {
                    return getNextBatch();
                } catch (HarvestFailureException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public RecordRepox next() {
            try {
                if (!hasNext()) { throw new NoSuchElementException(); }

                RecordCharactersConverter.convertRecord(nextRecord, new UnderCode32Remover());
                boolean isRecordDeleted = (nextRecord.getLeader().charAt(5) == 'd');
                RecordRepoxMarc recordMarc = new RecordRepoxMarc(nextRecord);
                RecordRepox recordRepox = dataSource.getRecordIdPolicy().createRecordRepox(recordMarc.getDom(), recordMarc.getId(), false, isRecordDeleted);
                return recordRepox;

            } catch (Exception e) {
                StringUtil.simpleLog("Error importing record (ID - " + recordId + "): " + e.getMessage(), e, this.getClass(), logFile);
                log.error("Error importing record", e);
                //nextRecord=null;
                //return next();
                return null;//agregado
            } finally {
                nextRecord = null;
            }
        }

        public void remove() {
        }

    }

    @Override
    public Iterator<RecordRepox> getIterator(DataSource dataSource, File logFile, boolean fullIngest) {
        return new RecordIterator(dataSource, logFile, fullIngest);
    }

    @Override
    public boolean isFullIngestExclusive() {
        return true;
    }

    /**
     * @return File
     */
    public static File getIdListFilePermanent() {
        File baseDir = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getXmlConfigPath(), "z3950");
        baseDir.mkdir();
        Random generator = new Random(new Date().getTime());
        String finalFilename = Math.abs(generator.nextInt()) + "z3950idList.txt";

        return new File(baseDir, finalFilename);
    }
}
