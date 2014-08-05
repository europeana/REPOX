/*
 * Created on 15/Mar/2006
 *
 */
package pt.utl.ist.repox;

import org.dom4j.DocumentException;

import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.util.InvalidInputException;

import java.io.IOException;

/**
 * Used to identify the following entities in REPOX: DataSource, Record,
 * Timestamp URNs in REPOX have the following structure:
 * <RepositoryURNPrefix>:<DataSourceId>:<RecordId>;<Timestamp>
 * 
 * Examples: urn:bn:repox:PORBASE urn:bn:repox:PORBASE:1203923
 * urn:bn:repox:PORBASE:1203923;2007-01-23T17:06:56.125Z
 * urn:bn:repox:PORBASE:1203923;2007-02-20T10:16:50.125Z
 * 
 * @author Nuno Freire
 * 
 */
public class Urn {
    /** Urn URN_SEPARATOR */
    public static String URN_SEPARATOR           = ":";
    String               URN_TIMESTAMP_SEPARATOR = ";";

    String               dataSourceId;
    Object               recordId;
    //The timestamp that identifies a specific version of a record (RecordNode)
    String               timestamp;

    /**
     * Creates a new instance of this class.
     * 
     * @param urn
     * @throws InvalidInputException
     */
    public Urn(String urn) throws InvalidInputException {
        super();
        RepoxManager repoxManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager();

        try {
            if (!urn.startsWith(repoxManager.getConfiguration().getBaseUrn())) throw new InvalidInputException(urn);

            String[] parts = urn.substring(repoxManager.getConfiguration().getBaseUrn().length()).split(URN_SEPARATOR, 2);
            this.dataSourceId = parts[0];
            if (parts.length > 1) {
                String[] recordParts = parts[1].split(URN_TIMESTAMP_SEPARATOR, 2);
                this.recordId = recordParts[0];
                if (recordParts.length > 1) {
                    this.timestamp = recordParts[1];
                }
            }
        } catch (Exception e) {
            throw new InvalidInputException(urn, e);
        }
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param dataSourceId
     * @param recordId
     */
    public Urn(String dataSourceId, Object recordId) {
        super();
        this.dataSourceId = dataSourceId;
        this.recordId = recordId;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param dataSourceId
     * @param recordId
     * @param timestamp
     */
    public Urn(String dataSourceId, Object recordId, String timestamp) {
        super();
        this.dataSourceId = dataSourceId;
        this.recordId = recordId;
        this.timestamp = timestamp;
    }

    @Override
    public Urn clone() {
        Urn ret = new Urn(dataSourceId, recordId, timestamp);
        return ret;
    }

    @Override
    public String toString() {
        RepoxManager repoxManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager();
        String baseUrn = repoxManager.getConfiguration().getBaseUrn();

        return baseUrn + dataSourceId + URN_SEPARATOR + recordId + (timestamp == null ? "" : URN_TIMESTAMP_SEPARATOR + timestamp);
    }

    @SuppressWarnings("javadoc")
    public String getDataSourceId() {
        return dataSourceId;
    }

    @SuppressWarnings("javadoc")
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @SuppressWarnings("javadoc")
    public Object getRecordId() throws DocumentException, IOException {
        if (recordId == null) { return null; }

        DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId).getDataSource();
        Class idClass = dataSource.getClassOfLocalId();

        if (recordId.getClass().equals(idClass)) { return recordId; }

        if (recordId.getClass().equals(Integer.class)) {
            if (idClass.equals(Long.class)) {
                //return new Long((Integer)recordId);
                return Long.parseLong((String)recordId);
            } else {//String
                return recordId.toString();
            }
        } else if (recordId.getClass().equals(Long.class)) {
            if (idClass.equals(Integer.class)) {
                //return new Integer(((Long)recordId).intValue());
                return ((Long)recordId).intValue();
            } else {//String
                return recordId.toString();
            }
        } else {//String
            if (idClass.equals(Integer.class)) {
                return Integer.parseInt((String)recordId);
            } else {//Long
                return Long.parseLong((String)recordId);
            }
        }
    }

    @SuppressWarnings("javadoc")
    public void setRecordId(Object recordId) {
        this.recordId = recordId;
    }

    @SuppressWarnings("javadoc")
    public String getTimestamp() {
        return timestamp;
    }

    @SuppressWarnings("javadoc")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @param args
     * @throws DocumentException
     * @throws InvalidInputException
     * @throws IOException
     */
    public static void main(String[] args) throws DocumentException, InvalidInputException, IOException {
        //		Urn urn = new Urn("urn:repox.ist.utl.pt:");
        //		Urn urn = new Urn("urn:repox.ist.utl.pt:a1");
        //		Urn urn = new Urn("urn:repox.ist.utl.pt:a1:ASD3");
        Urn urn = new Urn("oai:repox.ist.utl.pt:a1:ASD3;2008-01-29;asdasd:234324");
        System.out.println(urn.getDataSourceId());
        System.out.println(urn.getRecordId());
        System.out.println(urn.getTimestamp());

    }
}
