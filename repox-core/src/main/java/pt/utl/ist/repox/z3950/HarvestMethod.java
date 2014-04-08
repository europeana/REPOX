package pt.utl.ist.repox.z3950;

import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.recordPackage.RecordRepox;

import java.io.File;
import java.util.Iterator;

/**
 * A Z39.50 harvesting Method that implements a full or incremental Harvest.
 * 
 * @see TimestampHarvester
 * @see IdListHarvester
 * @see IdSequenceHarvester
 */
public interface HarvestMethod {
	
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
	
	public Target getTarget();
	public void setTarget(Target target);
}
