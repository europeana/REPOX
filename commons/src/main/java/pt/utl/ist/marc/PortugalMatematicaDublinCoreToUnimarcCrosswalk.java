/*
 * Created on 2/Jan/2006
 *
 */
package pt.utl.ist.marc;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import pt.utl.ist.repox.util.Dom4jUtil;
import pt.utl.ist.util.date.DateUtil;

/**
 */
public class PortugalMatematicaDublinCoreToUnimarcCrosswalk {

	/**
	 * @param dc
	 * @return the converted Record
	 */
	public static Record toUnimarc(Element dc) {
		Record rec=new Record();
		rec.setLeader("00000naa  2200000   450 ");
		Namespace dcNS=new Namespace("dc","http://purl.org/dc/elements/1.1/");
		Element resourceDc=(Element)dc.elements(new QName("resource",new Namespace("bnd","http://bnd.bn.pt/"))).get(0);

		String yearOfPublication=null;
		for(Iterator<Element> i=resourceDc.elementIterator(new QName("date",dcNS)) ; i.hasNext() ; ) {
			Element node=i.next();
        	Pattern yearPattern=Pattern.compile("(\\d\\d\\d\\d)");
        	Matcher yearMatcher=yearPattern.matcher(node.getText());
        	if (yearMatcher.find()) {
        		yearOfPublication = yearMatcher.group(1);
        		break;
            }
        }

        String f100a=DateUtil.date2String(new Date(),"yyyyMMdd");
        
        //lizString f100a=DateUtil.date2String(new Date(),"dd-MM-yyyy");
        f100a += "c" + (yearOfPublication==null ? "    " : yearOfPublication) + "    ";
        f100a += "m  y0pora0103    ba";
        
        Field f100=rec.addField(100);
        f100.setInd1(' ');
        f100.setInd2(' ');
        f100.addSubfield('a', f100a);

        Field f001=null;
		for(Iterator<Element> i=resourceDc.elementIterator(new QName("identifier",dcNS)) ; i.hasNext() ; ) {
			Element node=i.next();
            String id=node.getText();
            
            if(id.toLowerCase().startsWith("urn:purl.pt:") && f001==null) {
            	f001=rec.addField(1);
	            f001.setValue(id);
            }
            
            if(node.getText().toLowerCase().startsWith("http://")) {
                Field f856=rec.addField(856);
                f856.setInd1(' ');
                f856.setInd2(' ');
                f856.addSubfield('u', node.getText());
            }
        }

		boolean first=true;
		for(Iterator<Element> i=resourceDc.elementIterator(new QName("title",dcNS)) ; i.hasNext() ; ) {
			Element node=i.next();
            if(first) {
                Field f200=rec.addField(200);
                f200.setInd1('1');
                f200.setInd2(' ');
                f200.addSubfield('a',node.getText());   
                first=false;
            }else {
                Field f517=rec.addField(517);
                f517.setInd1('1');
                f517.setInd2(' ');
                f517.addSubfield('a',node.getText());  
            }
        }
        

		 first=true;
		for(Iterator<Element> i=resourceDc.elementIterator(new QName("creator",dcNS)) ; i.hasNext() ; ) {
			Element node=i.next();
            if(first) {
            	String name=node.getText();
            	if(name.contains(",")) {                	
                    Field f700=rec.addField(700);
                    f700.setInd1(' ');
                    f700.setInd2('1');
                    f700.addSubfield('a',name.substring(0,name.indexOf(',')).trim());
                    f700.addSubfield('b',name.substring(name.indexOf(',')+1).trim());    
            	}else {              	
                    Field f700=rec.addField(700);
                    f700.setInd1(' ');
                    f700.setInd2('0');
                    f700.addSubfield('a',node.getText());             		            		
            	}
            	first=false;
            }else {
            	String name=node.getText();
            	if(name.contains(",")) {                	
                    Field f701=rec.addField(701);
                    f701.setInd1(' ');
                    f701.setInd2('1');
                    f701.addSubfield('a',name.substring(0,name.indexOf(',')).trim());
                    f701.addSubfield('b',name.substring(name.indexOf(',')+1).trim());    
            	}else {              	
                    Field f701=rec.addField(701);
                    f701.setInd1(' ');
                    f701.setInd2('0');
                    f701.addSubfield('a',node.getText());             		            		
            	}
            }
        }
        


		for(Iterator<Element> i=resourceDc.elementIterator(new QName("contributer",dcNS)) ; i.hasNext() ; ) {
			Element node=i.next();
        	String name=node.getText();
        	if(name.contains(",")) {                	
                Field f702=rec.addField(702);
                f702.setInd1(' ');
                f702.setInd2('1');
                f702.addSubfield('a',name.substring(0,name.indexOf(',')).trim());
                f702.addSubfield('b',name.substring(name.indexOf(',')+1).trim());    
        	}else {              	
                Field f702=rec.addField(702);
                f702.setInd1(' ');
                f702.setInd2('0');
                f702.addSubfield('a',node.getText());             		            		
        	}
        }


		for(Iterator<Element> i=resourceDc.elementIterator(new QName("publisher",dcNS)) ; i.hasNext() ; ) {
			Element node=i.next();
        	String name=node.getText();      	
            Field f210=rec.addField(210);
            f210.setInd1(' ');
            f210.setInd2(' ');
            f210.addSubfield('c',node.getText());
            if(yearOfPublication!=null)
                f210.addSubfield('d',yearOfPublication);
            break;
        }
        
        
        
        
        
//		NodeList titles=dc.getElementsByTagNameNS("http://purl.org/dc/elements/1.1/","title");
//		
//        int sz=titles.getLength();
//        for (int idx=0 ; idx<sz ; idx++) {
//            Node node = titles.item(idx);
//            if (node.getNodeName().equals("leader"))
//                rec.setLeader(node.getFirstChild().getNodeValue());
////            else if (node.getNodeName().equals("controlfield"))
////                parseControlField(node);
////            else if (node.getNodeName().equals("datafield"))
////                parseDataField(node);
//        }
////        recs.add(rec);
		return rec;
	}
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		Document dc=Dom4jUtil.parseDomFromFile(new File("c:\\desktop\\registos\\j-5293-b-vol1-fasc1-art1.xml"),"UTF-8");
		System.out.println(toUnimarc((Element)dc.getRootElement()));
		System.out.println(toUnimarc((Element)dc.getRootElement()).toIso2709());
	}
}
