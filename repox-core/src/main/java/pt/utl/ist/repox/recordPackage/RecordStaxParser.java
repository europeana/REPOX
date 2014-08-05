package pt.utl.ist.repox.recordPackage;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 27/Mar/2010
 * Time: 17:58:41
 * To change this template use File | Settings | File Templates.
 */
public class RecordStaxParser {
    /**
     * @param args
     */
    public static void main(final String[] args) {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader streamReader = factory.createXMLStreamReader(new FileReader("C:\\Users\\GPedrosa\\Desktop\\teste_stax\\09428_Ag_DE_ELocal.xml"));

            int eventType = streamReader.getEventType();
            while (streamReader.hasNext()) {
                eventType = streamReader.next();
                //Get all "Book" elements as XMLEvent object
                if (eventType == XMLStreamConstants.START_ELEMENT && streamReader.getLocalName().equals("record") && streamReader.getNamespaceURI().equals("http://www.europeana.eu/schemas/ese/")) {
                    //get immutable XMLEvent
                    //StartElement event = getXMLEvent(streamReader).asStartElement();
                    System.out.println("EVENT: ");
                }
            }
            /*
            while(streamReader.hasNext()){
                streamReader.next();
                if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT){


                    System.out.println(streamReader.getLocalName());
                }
            }
            */
            streamReader.close();

        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
