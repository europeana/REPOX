/*
 * Created on 29/Abr/2005
 *
 */
package pt.utl.ist.marc.iso2709;

import pt.utl.ist.characters.CharacterConverterI;
import pt.utl.ist.characters.CharacterConverters;
import pt.utl.ist.marc.Record;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

public class IteratorIso2709 implements Iterator<Record>, Iterable<Record> {
	File isoFile;
	IsoNavigator isoNavig;
	List<Record> records=null;
	int currentIndex;
	
	

	public IteratorIso2709(File isoFile) {
		this.isoFile=isoFile;
		isoNavig=new IsoNavigator(isoFile.getAbsolutePath());
		currentIndex=0;
	}
	
	public IteratorIso2709(File isoFile, String charset) {
		this.isoFile=isoFile;
		CharacterConverterI converter=null;
		if(charset!=null)
			converter=CharacterConverters.getInstance(charset);
		if(converter==null) 
			isoNavig=new IsoNavigator(isoFile.getAbsolutePath(), new MARCPartialReader(charset));
		else
			isoNavig=new IsoNavigator(isoFile.getAbsolutePath(), converter);
		currentIndex=0;
	}
	
	public IteratorIso2709(FileInputStream fileInputStream) {
		// TODO Auto-generated constructor stub
	}

	public boolean hasNext() {
		if (records==null || currentIndex == records.size()) {
			records=isoNavig.getNextRecords();		
			currentIndex=0;
		}
		if (records!=null && currentIndex < records.size())
			return true;
		
		isoNavig.close();
		return false;
	}
	
	public void remove() {
		throw new RuntimeException("not implemented");		
	}
	
	public Record next() {
		Record ret=null;
		if (hasNext()) {						
			ret=records.get(currentIndex);
			currentIndex++;
			processRecord(ret);
		}
	    return ret;
	}

	public Iterator<Record> iterator() {		
		return this;
	}
	
	protected void processRecord(Record rec) {		
	}
	
	
}
