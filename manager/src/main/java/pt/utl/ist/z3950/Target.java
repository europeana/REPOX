package pt.utl.ist.z3950;

import org.apache.log4j.Logger;

import pt.utl.ist.marc.CharacterEncoding;

import java.util.Properties;

/**
 * Target
 *
 * @author Nuno Freire
 */
public class Target {
    private static final Logger log = Logger.getLogger(Target.class);

    protected String            address;
    protected int               port;
    protected String            database;
    protected String            user;
    protected String            password;
    protected CharacterEncoding characterEncoding;

    protected String            recordSyntax;                        // "unimarc" or "usmarc"

    @SuppressWarnings("javadoc")
    public String getAddress() {
        return address;
    }

    @SuppressWarnings("javadoc")
    public void setAddress(String address) {
        this.address = address;
    }

    @SuppressWarnings("javadoc")
    public int getPort() {
        return port;
    }

    @SuppressWarnings("javadoc")
    public void setPort(int port) {
        this.port = port;
    }

    @SuppressWarnings("javadoc")
    public String getDatabase() {
        return database;
    }

    @SuppressWarnings("javadoc")
    public void setDatabase(String database) {
        this.database = database;
    }

    @SuppressWarnings("javadoc")
    public String getUser() {
        return user;
    }

    @SuppressWarnings("javadoc")
    public void setUser(String user) {
        this.user = user;
    }

    @SuppressWarnings("javadoc")
    public String getPassword() {
        return password;
    }

    @SuppressWarnings("javadoc")
    public void setPassword(String password) {
        this.password = password;
    }

    @SuppressWarnings("javadoc")
    public String getRecordSyntax() {
        return recordSyntax;
    }

    @SuppressWarnings("javadoc")
    public void setRecordSyntax(String recordSyntax) {
        this.recordSyntax = recordSyntax;
    }

    @SuppressWarnings("javadoc")
    public CharacterEncoding getCharacterEncoding() {
        return characterEncoding;
    }

    @SuppressWarnings("javadoc")
    public void setCharacterEncoding(CharacterEncoding characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    /**
     * Creates a new instance of this class.
     */
    public Target() {
    }

    /**
     * Creates a new instance of this class.
     * @param address
     * @param port
     * @param database
     * @param user
     * @param password
     * @param characterEncoding
     * @param recordSyntax
     */
    public Target(String address, int port, String database, String user, String password, CharacterEncoding characterEncoding, String recordSyntax) {
        this.address = address;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.characterEncoding = characterEncoding;
        this.recordSyntax = recordSyntax;
    }

    /**
     * @return Properties of the connection
     */
    public Properties connectionProperties() {
        Properties p = new Properties();
        p.put("host", address);
        p.put("port", String.valueOf(port));
        p.put("serviceName", "Long Name");
        p.put("defaultRecordSyntax", recordSyntax);
        p.put("defaultElementSetName", "B");
        p.setProperty("serviceUserPrincipal", user);
        p.setProperty("serviceUserGroup", "");
        p.setProperty("serviceUserCredentials", password);
        //        p.setProperty("charsetEncoding","ANSEL"); 
        //p.setProperty("charsetEncoding","ISO8859-1");
        p.setProperty("charsetEncoding", "charsetEncoding");
        //       p.setProperty("charsetEncoding","ISO8859-2");
        p.setProperty("prefMessageSize", "1048576");
        p.setProperty("exceptionalMessageSize", "1048576");
        //      private int pref_message_size; 
        //      private int exceptional_message_size;    
        //        private String default_element_set_name; 
        //      private int pref_message_size; 
        //      private int exceptional_message_size; 
        //        private boolean use_reference_id; 
        //        private String service_name;
        //        private String service_id;
        //        private int auth_type=0;

        return p;
    }

    /**
      * Forms the string containing values of member variables in the Bean.
      *
      * @return String containing member variable values.
      */
    @Override
    public String toString() {
        StringBuffer b = new StringBuffer("******* Target - Begin *******").append("\n address = " + address).append("\n port = " + port).append("\n database = " + database).append("\n user = " + user).append("\n password = " + password).append("\n characterEncoding = " + characterEncoding).append("\n recordSyntax = " + recordSyntax)

        .append("\n******* Target - End *******\n");
        return b.toString();
    }

}