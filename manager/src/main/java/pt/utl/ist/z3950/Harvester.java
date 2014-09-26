package pt.utl.ist.z3950;

import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.recordPackage.RecordRepox;

import java.io.File;
import java.util.Iterator;

/**
 * A Z39.50 harvesting Method that implements a full or incremental Harvest.
 * 
 * @see TimestampHarvester
 * @see IdListHarvester
 * @see IdSequenceHarvester
 */
public interface Harvester {

    /**
     * Startup processing and gathers required resources.
     */
    public void init();

    /**
     * Clean the resources. 
     */
    public void cleanup();

    /**
     * Returns an Iterator of records.
     */
    public abstract Iterator<RecordRepox> getIterator(DataSource dataSource, File logFile, boolean fullIngest);

    /**
     * Returns true if method only allows full Ingest, false if allows incremental Ingest.
     */
    public abstract boolean isFullIngestExclusive();

    /**
     * @return Target
     */
    public Target getTarget();

    @SuppressWarnings("javadoc")
    public void setTarget(Target target);
}
