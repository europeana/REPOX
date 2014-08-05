package pt.utl.ist.repox.accessPoint.oai;

import org.apache.log4j.Logger;
import org.dom4j.Node;

import pt.utl.ist.repox.accessPoint.AccessPoint;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.recordPackage.RecordRepoxXpathId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 */
public class AccessPointSingleField extends AccessPoint {
    private static final Logger log = Logger.getLogger(AccessPointSingleField.class);
    private String              xPath;

    @SuppressWarnings("javadoc")
    public String getXPath() {
        return xPath;
    }

    @SuppressWarnings("javadoc")
    public void setXPath(String path) {
        xPath = path;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param id
     * @param xPath
     */
    public AccessPointSingleField(String id, String xPath) {
        super(id);
        this.xPath = xPath;
        indexDeletedRecords = true;
    }

    @Override
    public Collection<String> index(RecordRepox record) {
        HashSet<String> indexedValues = new HashSet<String>();
        RecordRepoxXpathId repoxRecord = (RecordRepoxXpathId)record;

        String correctedXpath = "//*[1]" + xPath + " | " + xPath;
        List nodesToIndex = repoxRecord.getDom().selectNodes(correctedXpath);
        for (Object currentNode : nodesToIndex) {
            String currentValue;
            if (currentNode instanceof Node) {
                currentValue = ((Node)currentNode).getText();
            } else { //String
                currentValue = (String)currentNode;
            }

            if (!indexedValues.contains(currentValue)) {
                indexedValues.add(currentValue);
                log.debug("current value to index: " + currentValue);
            }
        }

        return indexedValues;
    }

    @Override
    public List index(List<RecordRepox> records) {
        List indexedValues = new ArrayList();

        for (RecordRepox currentRecord : records) {
            RecordRepoxXpathId repoxRecord = (RecordRepoxXpathId)currentRecord;

            String correctedXpath = "//*[1]" + xPath + " | " + xPath;
            List nodesToIndex = repoxRecord.getDom().selectNodes(correctedXpath);
            List currentNodeIndexes = new ArrayList();
            for (Object currentNode : nodesToIndex) {
                String currentValue;
                if (currentNode instanceof Node) {
                    currentValue = ((Node)currentNode).getText();
                } else { //String
                    currentValue = (String)currentNode;
                }

                if (!currentNodeIndexes.contains(currentValue)) {
                    currentNodeIndexes.add(currentValue);
                    log.debug("current value to index: " + currentValue);
                }
            }
            indexedValues.add(currentNodeIndexes);
        }

        return indexedValues;
    }

    @Override
    public Class typeOfIndex() {
        return String.class;
    }

    @Override
    public String indexValue(String value) {
        return value;
    }
}
