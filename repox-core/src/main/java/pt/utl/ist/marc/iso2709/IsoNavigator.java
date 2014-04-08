/*
 * ISONavigator.java
 *
 * Created on 4 de Janeiro de 2002, 17:46
 */

package pt.utl.ist.marc.iso2709;

import org.apache.log4j.Logger;
import pt.utl.ist.characters.CharacterConverterI;
import pt.utl.ist.marc.Record;
import pt.utl.ist.util.NUtil;
import pt.utl.ist.util.structure.Tuple;

import java.io.*;
import java.util.List;
/**
 *
 * @author  Nuno Freire
 */
public class IsoNavigator {
	private static final Logger log = Logger.getLogger(IsoNavigator.class);

    private ISOHandler handler;
    private MARCPartialReader marcReader;
    private boolean started;
//    private String filename;
    private InputStream inputStream;
    private int inc=50;
    private String charset=null;
    
    /** Creates a new instance of ISONavigator */
//    public IsoNavigator(String filename, MarcObjectFactory factory) {
//        this.factory=factory;
//        init(filename);
//    }
//
//    public IsoNavigator(String filename, int increment, MarcObjectFactory factory) {
//        this.factory=factory;
//        inc=increment;
//        init(filename);
//    }
    
    public IsoNavigator(String filename,int increment) {
        inc=increment;
        init(filename, null);
    }

    public IsoNavigator(String filename) {
        
        if (!NUtil.getSystemCharset().equals("Cp1252"))       
        	charset="Cp1252";
        
        init(filename, null);
    }
    
//    public IsoNavigator(String filename, String charset) {
//    	this.charset=charset;
//        factory=new pt.utl.ist.marc.impl.MarcObjectFactoryImpl();
//        init(filename);
//        
//    }
    
    public IsoNavigator(String filename, MARCPartialReader marcReader) {
    	this.marcReader=marcReader;
    	init(filename, null);
    }
    public IsoNavigator(String filename, CharacterConverterI converter) {
    	init(filename, converter);
    	
    }
    
    private void init(String filename, CharacterConverterI converter){
    	if(marcReader==null) {
	    	if(charset==null)
	    		marcReader = new MARCPartialReader();
	    	else
	    		marcReader = new MARCPartialReader(charset);
    	}
    	if(converter!=null)
    		handler=new ISOHandler(converter);
    	else
    		handler=new ISOHandler();
        marcReader.setMARCHandler(handler);
        started=false;
        try {
			this.inputStream=new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
    }
    
    public void setIncrement(int inc){
        this.inc=inc;
    }
    
    public List<Record> getNextRecords(){
        try {
            if (!started){
                marcReader.parse(inputStream,inc);
                started=true;
            }else
                marcReader.continueParse();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
        return handler.records;
    }
    
    public void close(){
        marcReader.close();
    }

    public static boolean canRead(File iso) {
    	try {
			IteratorIso2709 it=new IteratorIso2709(iso);
			int read=0;
			while (it.next()!= null && read<20) {
				read++;
			}
    	}catch(Exception e) {
    		return false;
    	}
    	return true;
    }
    
    
    public static int countRecordsInIso(String filename){
        return countRecordsInIso(new File(filename));
    }

    
    public static int countRecordsInIso(File file){
        try {
            BufferedReader reader=new BufferedReader(new FileReader(file));
            int counter=0;
            int i;
            while((i = reader.read()) != -1){
                if(i == Record.RT) 
                    counter++;
            }                 
            return counter;
        } catch (IOException e) {
            return 0;
        }
    }
    

    /**
     * @param file The iso2709 file
     * @return Tuple - recordsCount, itemsCount
     */
    public static Tuple<Integer,Integer> countRecordsAndItemsInIso(File file){
        try {
            BufferedReader reader=new BufferedReader(new FileReader(file));
            int recCounter=0;
            int itemCounter=0;
            int i;

            int leaderPosition=0;
            int dirPosition=-1;
            String field="";
            while((i = reader.read()) != -1){
                if(i == Record.RT) { 
                	recCounter++;
                	leaderPosition=0;
                }else if(i == Record.FT && dirPosition!=-1) {
                	dirPosition=-1;
                }else if(leaderPosition<24) {
                	leaderPosition++;
                	if(leaderPosition==24)
                		dirPosition=0;
                }else if(dirPosition>=0) {
                	dirPosition++;                        	
                	if(dirPosition < 3) {
                		field += (char)i;
                	}else if(dirPosition==3) {
                		field += (char)i;
                		if (field.equals("966"))
                			itemCounter++;
                		field="";
                	}else if(dirPosition==12) {
                		dirPosition=0;
                	}
                }
            }                 
            return new Tuple<Integer,Integer>(recCounter,itemCounter);
        } catch (IOException e) {
            return new Tuple<Integer,Integer>(0,0);
        }
    }
    
    
    public static void main(String[] args) {
    	System.err.println(IsoNavigator.countRecordsAndItemsInIso(new File("c:\\desktop\\teste.iso")));
    }
}
