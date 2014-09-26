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
import pt.utl.ist.repox.util.date.DateUtil;

import java.io.File;
import java.util.*;

/**
 * Z39.50 Harvester by Date/time last modified
 */
public class TimestampHarvester extends AbstractHarvester {
    private static final Logger log                          = Logger.getLogger(TimestampHarvester.class);

    //	private String creationDateBibAttribute="1011";
    private String              modificationDateBibAttribute = "1012";
    private Date                earliestTimestamp;

    @SuppressWarnings("javadoc")
    public Date getEarliestTimestamp() {
        return earliestTimestamp;
    }

    @SuppressWarnings("javadoc")
    public void setEarliestTimestamp(Date earliestTimestamp) {
        this.earliestTimestamp = earliestTimestamp;
    }

    /**
     *
     * @param target
     * @param earliestTimestamp Date from which records will be harvested
     */
    public TimestampHarvester(Target target, Date earliestTimestamp) {
        super(target);
        this.earliestTimestamp = earliestTimestamp;
    }

    private class RecordIterator implements Iterator<RecordRepox> {
        private DataSource                       dataSource;
        private File                             logFile;
        private boolean                          fullIngest;
        private Date                             currentDay;
        private Date                             tomorrow;
        Record                                   nextRecord = null;

        private Enumeration<InformationFragment> currentInformationFragment;

        public RecordIterator(DataSource dataSource, File logFile, boolean fullIngest) {
            this.dataSource = dataSource;
            this.logFile = logFile;
            this.fullIngest = fullIngest;
            this.currentDay = earliestTimestamp;

            if (!this.fullIngest && dataSource.getLastUpdate() != null) {
                this.currentDay = dataSource.getLastUpdate();
            }
            this.tomorrow = DateUtil.add(new Date(), 1, Calendar.DAY_OF_MONTH);
        }

        private boolean getNextBatch() throws HarvestFailureException {
            int consecutiveErrors = 0;

            while (currentDay.before(tomorrow)) {

                StringUtil.simpleLog("Harvesting " + DateUtil.date2String(currentDay, "yyyyMMdd"), this.getClass(), logFile);
                String queryStr = "@attrset bib-1 ";
                queryStr += "@and @attr 2=4 @attr 4=5  @attr 6=1  @attr 1=" + modificationDateBibAttribute + " \"" + DateUtil.date2String(currentDay, "yyyyMMdd") + "\"";
                queryStr += " @attr 2=2 @attr 4=5  @attr 6=1  @attr 1=" + modificationDateBibAttribute + " \"" + DateUtil.date2String(currentDay, "yyyyMMdd") + "\"";

                log.debug("currentDay = " + currentDay);
                log.debug("... = " + DateUtil.date2String(currentDay, "yyyyMMdd"));

                IRResultSet results = runQuery(queryStr, logFile, dataSource.getId());
                if (results == null) {
                    consecutiveErrors++;
                    if (consecutiveErrors > 10) { throw new HarvestFailureException("Importing aborted - Too many consecutive errors"); }
                } else {
                    consecutiveErrors = 0;

                    StringUtil.simpleLog("Iterate over results (status=" + results.getStatus() + "), count=" + results.getFragmentCount(), this.getClass(), logFile);
                    currentInformationFragment = new org.jzkit.search.util.ResultSet.ReadAheadEnumeration(results);

                    try {
                        nextRecord = handleRecord(currentInformationFragment.nextElement());
                    } catch (ClassCastException e) {
                        StringUtil.simpleLog("ClassCastException: record with date: " + modificationDateBibAttribute, this.getClass(), logFile);
                    }
                    results.close();
                    currentDay = DateUtil.add(currentDay, 1, Calendar.DAY_OF_MONTH);
                    return true;

                    /*
                    int count = 0;
                    int repeated = 0;
                    HashMap<String, Record> list = new HashMap<String, Record>();

                    System.out.println("currentDay = " + currentDay);
                    while (currentInformationFragment.nextElement() != null){
                        try{

                            String nc = new Record((byte[]) currentInformationFragment.nextElement().getOriginalObject(), target.getCharacterEncoding().toString()).getNc();
                            System.out.println(nc);

                            if(list.get(nc) != null){
                                repeated++;
                            }
                            list.put(nc, new Record((byte[]) currentInformationFragment.nextElement().getOriginalObject(), target.getCharacterEncoding().toString()));

                            count++;

                            if(nc.equalsIgnoreCase("000000269659")){
                                System.out.println("stop");
                            }

                            if(count%100 == 0){
                                System.out.println("count = " + count);
                                System.out.println("list.size() = " + list.size());
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    */
                }

                //currentDay = DateUtil.add(currentDay, 1, Calendar.DAY_OF_MONTH);
            }

            return false;
        }

        @Override
        public boolean hasNext() {
            /*if(currentInformationFragment != null && currentInformationFragment.hasMoreElements()) {
                return true;
            }*/
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
                return null;//agregado
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
        return false;
    }

}
