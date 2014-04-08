/*
 * Created on 2006/08/16
 *
 */
package pt.utl.ist.marc.xml;

import org.dom4j.Document;
import org.dom4j.Element;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.iso2709.IteratorIso2709;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class MarcWriterInXml {
	public static final String MARCXML_NS="http://www.loc.gov/MARC21/slim";
	public static final String MARCXCHANGE_NS="info:lc/xmlns/marcxchange-v1";
	public static final String MARCXMLBN_NS="http://www.bn.pt/standards/metadata/marcxml/1.0/";
	
	FileOutputStream fileWriter;
	Element collectionElement;
	String namespace;
	
	public MarcWriterInXml(File target) throws IOException{
		this(target, MARCXCHANGE_NS);
	}
	public MarcWriterInXml(File target, String namespace) throws IOException{
		if (namespace.equals(MARCXCHANGE_NS))
			this.namespace=MARCXCHANGE_NS;
		else if (namespace.equals(MARCXML_NS))
			this.namespace=MARCXML_NS;
		else if (namespace.equals(MARCXMLBN_NS))
			this.namespace=MARCXMLBN_NS;
		fileWriter=new FileOutputStream(target);
		fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes("UTF-8")); 
		fileWriter.write(("<mx:collection xmlns:mx=\""+namespace+"\" >\n").getBytes("UTF-8"));
//		fileWriter.write("<mx:collection xmlns:mx=\"info:lc/xmlns/marcxchange-v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.bs.dk/standards/MarcXchange http://www.bs.dk/standards/MarcXchange.xsd\">\n".getBytes("UTF-8"));
	}
	
	public int write(File isoFile) throws IOException{
		int counter=0;
		for(Record rec: new IteratorIso2709(isoFile)) {
			write(rec);
			counter++;
		}
		return counter;
	}
	
	
	public void write(Record rec) throws IOException{
		Document recDoc=MarcXChangeDom4jBuilder.record2Dom(rec, "");
		Element rootEl=recDoc.getRootElement();
		if(namespace!=MARCXCHANGE_NS) {
			rootEl.remove(rootEl.attribute("format"));
			rootEl.remove(rootEl.attribute("type"));
		}
		fileWriter.write(rootEl.asXML().replaceFirst(" xmlns:mx=\"info:lc/xmlns/marcxchange-v1\"", "").getBytes("UTF-8"));
	}
	

	public void write(Collection<Record> recs) throws IOException{
		for(Record rec: recs) {
			write(rec);
		}
	}

	public void close()  throws IOException{
		fileWriter.write("\n</mx:collection>".getBytes("UTF-8"));
		fileWriter.close();
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		MarcWriterInXml w=new MarcWriterInXml(new File("C:\\Desktop\\1EclipsePrj\\PORBASE-Repox\\test\\nmaf\\repox\\cooperantes\\tests\\lotes\\horizonFirstHarvest.xml"));
		w.write(new File("C:\\Desktop\\1EclipsePrj\\PORBASE-Repox\\test\\nmaf\\repox\\cooperantes\\tests\\lotes\\horizonFirstHarvest.iso"));
		w.close();

		System.out.println("");
		w=new MarcWriterInXml(new File("C:\\Desktop\\1EclipsePrj\\PORBASE-Repox\\test\\nmaf\\repox\\cooperantes\\tests\\lotes\\esel-horizonFirstHarvest.xml"));
		w.write(new File("C:\\Desktop\\1EclipsePrj\\PORBASE-Repox\\test\\nmaf\\repox\\cooperantes\\tests\\lotes\\esel-horizonFirstHarvest.iso"));
		w.close();


		w=new MarcWriterInXml(new File("C:\\Desktop\\1EclipsePrj\\PORBASE-Repox\\test\\nmaf\\repox\\cooperantes\\tests\\lotes\\esel.xml"));
		w.write(new File("C:\\Desktop\\1EclipsePrj\\PORBASE-Repox\\test\\nmaf\\repox\\cooperantes\\tests\\lotes\\esel.iso"));
		w.close();

		
		w=new MarcWriterInXml(new File("C:\\Desktop\\1EclipsePrj\\PORBASE-Repox\\test\\nmaf\\repox\\cooperantes\\tests\\lotes\\bn.xml"));
		w.write(new File("C:\\Desktop\\1EclipsePrj\\PORBASE-Repox\\test\\nmaf\\repox\\cooperantes\\tests\\lotes\\bn.iso"));
		w.close();
	}

}
