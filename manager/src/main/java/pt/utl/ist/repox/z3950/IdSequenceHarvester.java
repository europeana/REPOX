package pt.utl.ist.repox.z3950;

import org.apache.log4j.Logger;
import org.jzkit.search.util.RecordModel.InformationFragment;
import org.jzkit.search.util.ResultSet.IRResultSet;

import pt.utl.ist.repox.characters.RecordCharactersConverter;
import pt.utl.ist.repox.characters.UnderCode32Remover;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.marc.Record;
import pt.utl.ist.repox.marc.RecordRepoxMarc;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.util.StringUtil;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Z39.50 Harvester by control number-local (sequential number)
 * maximumId MUST be higher than current server MAX to allow future records with higher control number
 */
public class IdSequenceHarvester extends AbstractHarvester {
    private static final Logger log                         = Logger.getLogger(IdSequenceHarvester.class);
    private static final int    stopAfterNotFoundCount      = 5000;
    private static final int    stopAfterFailureCount       = 50;
    private static final int    sleepTimeAfterFailureInSecs = 300;
    private static final int    maxSleepsBeforeFailure      = 50;

    private String              idBibAttribute              = "12";
    public Long                 maximumId;

    @SuppressWarnings("javadoc")
    public Long getMaximumId() {
        return maximumId;
    }

    @SuppressWarnings("javadoc")
    public void setMaximumId(Long maximumId) {
        this.maximumId = maximumId;
    }

    /**
     * @param target
     * @param maximumId must be higher than current server MAX to allow future records with higher control number
     */
    public IdSequenceHarvester(Target target, Long maximumId) {
        super(target);
        this.maximumId = maximumId;
    }

    private class RecordIterator implements Iterator<RecordRepox> {
        private DataSource                       dataSource;
        private File                             logFile;
        int                                      currentId  = 1;
        Record                                   nextRecord = null;

        private Enumeration<InformationFragment> currentInformationFragment;

        public RecordIterator(DataSource dataSource, File logFile, boolean fullIngest) {
            this.dataSource = dataSource;
            this.logFile = logFile;
        }

        private boolean getNextBatch() throws HarvestFailureException {
            if (maximumId != null && currentId > maximumId) return false;

            if (currentId == 1 || currentId % 1000 == 0) StringUtil.simpleLog("Harvesting ID: " + currentId, this.getClass(), logFile);

            int consecutiveErrors = 0;
            int consecutiveSleeps = 0;
            int consecutiveNotFound = 0;

            while (true) {
                String queryStr = "@attrset bib-1 " + "@attr 1=" + idBibAttribute + " \"" + currentId + "\"";
                currentId++;
                IRResultSet results = runQuery(queryStr, logFile, dataSource.getId());
                if (results == null) {
                    consecutiveErrors++;
                    if (consecutiveErrors > stopAfterFailureCount) {
                        if (consecutiveSleeps > maxSleepsBeforeFailure) throw new HarvestFailureException("Importing aborted - Too many consecutive errors");
                        consecutiveSleeps++;
                        currentId -= stopAfterFailureCount;
                        consecutiveErrors = 0;
                        try {
                            StringUtil.simpleLog("Server unavailable: will wait " + sleepTimeAfterFailureInSecs + " seconds.", this.getClass(), logFile);
                            Thread.sleep(sleepTimeAfterFailureInSecs * 1000);
                        } catch (InterruptedException e) {
                        }
                    }
                } else if (results.getFragmentCount() == 0) {
                    results.close();
                    consecutiveNotFound++;
                    if (consecutiveNotFound > stopAfterNotFoundCount) return false;
                } else {
                    try {
                        //StringUtil.simpleLog("Iterate over results (status=" + results.getStatus() + "), count=" + results.getFragmentCount(),
                        //this.getClass(), logFile);
                        currentInformationFragment = new org.jzkit.search.util.ResultSet.ReadAheadEnumeration(results);
                        try {
                            nextRecord = handleRecord(currentInformationFragment.nextElement());
                        } catch (ClassCastException e) {
                            StringUtil.simpleLog("ClassCastException: record id: " + currentId, this.getClass(), logFile);
                        }
                        results.close();
                        return true;
                    } catch (Exception e) {
                        consecutiveErrors++;
                        if (consecutiveErrors > stopAfterFailureCount) {
                            if (consecutiveSleeps > maxSleepsBeforeFailure) throw new HarvestFailureException("Importing aborted - Too many consecutive errors");
                            consecutiveSleeps++;
                            currentId -= stopAfterFailureCount;
                            consecutiveErrors = 0;
                            try {
                                StringUtil.simpleLog("Server unavailable: will wait " + sleepTimeAfterFailureInSecs + " seconds.", this.getClass(), logFile);
                                Thread.sleep(sleepTimeAfterFailureInSecs * 1000);
                            } catch (InterruptedException e2) {
                            }
                        }
                    }

                }
                if (maximumId != null && currentId > maximumId) return false;
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
                StringUtil.simpleLog("Error importing record: " + e.getMessage(), e, this.getClass(), logFile);
                log.error("Error importing record", e);
                //nextRecord=null;
                //return next();
                return null;

            } finally {
                nextRecord = null;
            }

        }

        @Override
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
}
