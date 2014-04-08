package harvesterUI.server.ldap;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 29-05-2012
 * Time: 18:24
 */

/*
 * $name:         GetAuthenticated.java
 * $description:  GetAuthenticated shows different kinds of bind.
 *                   -- anonymous bind
 *                   -- simple bind
 *                   -- simple bind with connection method
 *                   -- SSL bind
 ******************************************************************************/

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import harvesterUI.server.util.Util;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;

public class LDAPAuthenticator{

    private static Logger logger = Logger.getLogger(LDAPAuthenticator.class);

    public static void main( String[] args ) {
        String ldapHost   = "digmap2.ist.utl.pt";
        String loginDN    = "uid=rfarinha,ou=People,dc=digmap,dc=eu";
        String password   = "rfarinha";

        Util.addLogEntry("RESULT = " + checkLDAPAuthentication(ldapHost, loginDN, password),logger);
        System.exit(0);
    }

    public static boolean checkLDAPAuthentication(String ldapHost, String loginDN, String password){
//        int ldapVersion   = LDAPConnection.LDAP_V3;
        int ldapPort      = LDAPConnection.DEFAULT_PORT;
//        int ldapSSLPort   = LDAPConnection.DEFAULT_SSL_PORT;
//        String ldapHost   = args[0];
//        String loginDN    = args[1];
//        String password   = args[2];


        LDAPConnection conn = new LDAPConnection();
//        anonymousBind( conn, ldapHost, ldapPort );

        return simpleBind1(conn, ldapHost, ldapPort, loginDN, password);
    }


    private static boolean simpleBind1(LDAPConnection conn, String host,int port, String dn, String passwd) {
        try {
            Util.addLogEntry("Simple bind...",logger);
            // connect to the server
            conn.connect( host, port );
            // authenticate to the server
            try {
                conn.bind( LDAPConnection.LDAP_V3, dn, passwd.getBytes("UTF8") );
            } catch (UnsupportedEncodingException u){
                throw new LDAPException( "UTF8 Invalid Encoding",
                        LDAPException.LOCAL_ERROR,(String)null, u);
            }

            Util.addLogEntry("    User DN: " + dn ,logger);
            Util.addLogEntry((conn.isBound()) ?
                    "\tAuthenticated to the server ( simple )\n":
                    "\n\tNot authenticated to the server\n",logger);
            // disconnect with the server
            conn.disconnect();
        }catch( LDAPException e ) {
            Util.addLogEntry( "Error: " + e.toString(),logger);
            try {
                conn.disconnect();
            } catch (LDAPException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return false;
            }
            return false;
        }
        return true;
    }

//    private static void simpleBind2(int version, LDAPConnection conn,
//                                      String host, int port,String dn, String passwd ) {
//        try {
//            System.out.println("Simple bind with connection method...");
//            // connect to the server
//            conn.connect( host, port );
//            // authenticate to the server with the connection method
//            try {
//                conn.bind( version, dn, passwd.getBytes("UTF8") );
//            } catch (UnsupportedEncodingException u){
//                throw new LDAPException( "UTF8 Invalid Encoding",
//                        LDAPException.LOCAL_ERROR,
//                        (String)null, u);
//            }
//            System.out.println((conn.isBound()) ?
//                    "\n\tAuthenticated to the server ( simple )\n":
//                    "\n\tNot authenticated to the server\n");
//            // disconnect with the server
//            conn.disconnect();
//        }catch( LDAPException e ) {
//            System.out.println( "Error: " + e.toString() );
//        }
//        return;
//    }

//    private static void SSLBind( int version, String host, int SSLPort,String dn, String passwd ) {
//        // Set the socket factory for this connection only
//        LDAPJSSESecureSocketFactory ssf = new LDAPJSSESecureSocketFactory();
//        LDAPConnection  conn = new LDAPConnection(ssf);
//        try {
//            System.out.println("SSL bind...");
//            // connect to the server
//            conn.connect( host, SSLPort);
//            // authenticate to the server with the connection method
//            try {
//                conn.bind( version, dn, passwd.getBytes("UTF8") );
//            } catch (UnsupportedEncodingException u){
//                throw new LDAPException( "UTF8 Invalid Encoding",LDAPException.LOCAL_ERROR,(String)null, u);
//            }
//            System.out.println((conn.isBound()) ?
//                    "\n\tAuthenticated to the server ( ssl )\n":
//                    "\n\tNot authenticated to the server\n");
//            // disconnect with the server
//            conn.disconnect();
//        }
//        catch( LDAPException e ) {
//            System.out.println( "Error: " + e.toString() );
//        }
//        return;
//    }

    // GetBindInfo() checks bind restrictions and returns 'true' if


    // there is no any bind restriction. Otherwise 'false' is returned.


//    public static boolean GetBindInfo( LDAPConnection lc,
//
//                                       String userDN, String userPWD)
//
//    {
//
//        int     i;
//
//        byte    timeMap[] = new byte [0];
//
//        boolean checkResult = true, res;
//
//        String  login = null, loginExpTime = null, pwdExpTime = null;
//
//        String  locked = null, attrName;
//
//        String value;
//
//        LDAPAttribute attribute;
//
//        // return those attributes only
//
//
//        String returnAttrs[] = { "LoginDisabled",
//
//                "loginExpirationTime",
//
//                "passwordExpirationTime",
//
//                "loginAllowedTimeMap",
//
//                "lockedByIntruder"};
//
//
//
//        try {
//
//            // check user's password
//
//
//            System.out.print("        user's password: ");
//
//            attribute = new LDAPAttribute( "userPassword", userPWD );
//
//            if ( lc.compare( userDN, attribute ) )
//
//                System.out.println("password is correct");
//
//            else {
//
//                System.out.println("password is incorrect");
//
//                checkResult = false;
//
//            }
//
//
//
//            // read the entry to get attributes
//
//
//            LDAPEntry entry = lc.read( userDN, returnAttrs );
//
//            LDAPAttributeSet attributeSet = entry.getAttributeSet();
//
//            Iterator allAttributes = attributeSet.iterator();
//
//
//
//            // save the attribute values
//
//
//            while(allAttributes.hasNext()) {
//
//                attribute = (LDAPAttribute)allAttributes.next();
//
//                attrName  = attribute.getName();
//
//
//
//                if (attrName.equalsIgnoreCase( "loginAllowedTimeMap" )) {
//
//                    timeMap = attribute.getByteValueArray()[0];
//
//                }
//
//                else if (attrName.equalsIgnoreCase( "LoginDisabled" )) {
//
//                    if ( (value = attribute.getStringValue()) != null)
//
//                        login = value;
//
//                }
//
//                else if (attrName.equalsIgnoreCase("loginExpirationTime")) {
//
//                    if ( (value = attribute.getStringValue()) != null )
//
//                        loginExpTime = value;
//
//                }
//
//                else if (attrName.equalsIgnoreCase("passwordExpirationTime")) {
//
//                    if ( (value = attribute.getStringValue()) != null )
//
//                        pwdExpTime = value;
//
//                }
//
//                else if (attrName.equalsIgnoreCase( "lockedByIntruder" )) {
//
//                    if ( (value = attribute.getStringValue()) != null )
//
//                        locked = value;
//
//                }
//
//            }
//
//
//
//            // check 'Logindisabled'
//
//
//            System.out.print("        Logindisabled: ");
//
//            if ( (login != null) && (login.length() != 0) ) {
//
//                if ( login.equalsIgnoreCase( "FALSE" ) )
//
//                    System.out.println( login + " (enabled)" );
//
//                else {
//
//                    System.out.println( login + " (disabled)" );
//
//                    checkResult = false;
//
//                }
//
//            }
//
//            else
//
//                System.out.println("not present (no logindisabled set)");
//
//
//
//            // check 'LoginExpirationTime'
//
//
//            System.out.print("        loginExpirationTime: ");
//
//            if ( (loginExpTime != null) && (loginExpTime.length() != 0) ) {
//
//                PrintLocalTime(loginExpTime);
//
//                res = CompareTime(loginExpTime);
//
//                if ( res )
//
//                    System.out.println( "" );
//
//                else {
//
//                    System.out.println( " (login expired)" );
//
//                    checkResult = false;
//
//                }
//
//            }
//
//            else
//
//                System.out.println("not present (no expiration set)");
//
//
//
//            // check 'passwordExpirationTime'
//
//
//            System.out.print("        passwordExpirationTime: ");
//
//            if ( (pwdExpTime != null)  && (pwdExpTime.length() != 0) ) {
//
//                PrintLocalTime(pwdExpTime);
//
//                res = CompareTime(pwdExpTime);
//
//                if ( res )
//
//                    System.out.println( "" );
//
//                else {
//
//                    System.out.println( " (password expired)" );
//
//                    checkResult = false;
//
//                }
//
//            }
//
//            else
//
//                System.out.println("not present (no expiration set)");
//
//
//
//            // check 'LoginAllowedTimeMap'
//
//
//            System.out.print("        LoginAllowedTimeMap: ");
//
//            if ( (timeMap != null) &&
//
//                    (timeMap.length != 0) &&
//
//                    (timeMap.length == LENGTH)) {
//
//                res = getTimeRestriction( timeMap, locale);
//
//                if ( res ) {
//
//                    System.out.println( res + " (login time is restricted)");
//
//                    checkResult = false;
//
//                }
//
//                else {
//
//                    System.out.println( res + " (no restriction)");
//
//                }
//
//            }
//
//            else
//
//                System.out.println("not present (no time restriction set)");
//
//
//
//            // Check 'lockedByIntruder'
//
//
//            System.out.print("        lockedByIntruder: ");
//
//            if ( (locked != null) && (locked.length() != 0) ) {
//
//                if ( locked.equalsIgnoreCase( "FALSE" ) )
//
//                    System.out.println( locked + " (not locked)" );
//
//                else {
//
//                    System.out.println( locked + " (locked)");
//
//                    checkResult = false;
//
//                }
//
//            }
//
//            else
//
//                System.out.println("not present (no lock set)");
//
//        }
//
//        catch( LDAPException e ) {
//
//            System.out.println( "\n    Error: " + e.toString() );
//
//            System.exit(1);
//
//        }
//
//
//
//        return checkResult;
//
//    }
//
//
//
//    // PrintLocalTime() turns UTCTime into local
//
//
//    // time and then prints it out in text format
//
//
//    public static void PrintLocalTime( String UTCTime )
//
//    {
//
//        Date date = null;
//
//        // setup x.208 generalized time formatter
//
//
//        DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
//
//        // LDAP time in UTC - must set formatter
//
//
//        TimeZone tz = TimeZone.getTimeZone("UTC");
//
//        formatter.setTimeZone(tz);
//
//
//
//        try {
//
//            // parse into Date - converted to locale time zone
//
//
//            date = formatter.parse( UTCTime );
//
//        }
//
//        catch(ParseException pe) {
//
//            System.out.println( "\n    Error: " + pe.toString() );
//
//        }
//
//        System.out.print(date);
//
//    }
//
//
//
//    // CompareTime() parses UTCTime into locale Date, and then
//
//
//    // compare it with syetem Date. It returns true if UTCTime
//
//
//    // is after system time. Otherwise false is returned.
//
//
//    public static boolean CompareTime( String UTCTime )
//
//    {
//
//        Date date = null, currentDate = new Date();
//
//        // setup x.208 generalized time formatter
//
//
//        DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
//
//        // all time in UTC - must set formatter
//
//
//        TimeZone tz = TimeZone.getTimeZone("UTC");
//
//        formatter.setTimeZone(tz);
//
//
//
//        try {
//
//            // parse into Date - converted to locale time zone
//
//
//            date = formatter.parse( UTCTime );
//
//        }
//
//        catch(ParseException pe) {
//
//            System.out.println( "\n   Error: " + pe.toString() );
//
//        }
//
//
//
//        return date.after(currentDate);
//
//    }
//
//
//
//    // getTimeRestriction() returns 'true' if there is a time restriction
//
//
//    // for the current 30-minute period. Otherwise 'false' is returned
//
//
//    public static boolean getTimeRestriction( byte[] tm, String localTime )
//
//    {
//
//        int i, temp, index = 0;
//
//        boolean flags[] = new boolean[BITS];
//
//        boolean temp1[] = new boolean [BITSOFFSET];
//
//
//
//        // tm has 336 bits(42 bytes). The following operations must performed
//
//
//        // in order to interpret loginAllowedTimeMap value:
//
//
//        //     1. swap the bit order in all the bytes;
//
//
//        //     2. shift the 336 bits left 14 times;
//
//
//        //     3. replace the vacated bits at right
//
//
//        //        with the 14 bits shifted off the left;
//
//
//        // Then the 336 bits represent 336 thirty-minute time intervals in
//
//
//        // a week, starting from the period of Sun 12:00AM-12:30AM.
//
//
//        // Bit value '1' indicates time restriction while bit value '0'
//
//
//        // means no restriction.
//
//
//
//
//        // reverse the bits order and set flags
//
//
//        for ( i = 0; i < LENGTH; i++ ) {
//
//            if ( (tm[i] & 0x01) != 0 )
//
//                flags[ i * 8 + 0 ]= true;
//
//            if ( (tm[i] & 0x02) != 0 )
//
//                flags[ i * 8 + 1 ]= true;
//
//            if ( (tm[i] & 0x04) != 0 )
//
//                flags[ i * 8 + 2 ]= true;
//
//            if ( (tm[i] & 0x08) != 0 )
//
//                flags[ i * 8 + 3 ]= true;
//
//            if ( (tm[i] & 0x10) != 0 )
//
//                flags[ i * 8 + 4 ]= true;
//
//            if ( (tm[i] & 0x20) != 0 )
//
//                flags[ i * 8 + 5 ]= true;
//
//            if ( (tm[i] & 0x40) != 0 )
//
//                flags[ i * 8 + 6 ]= true;
//
//            if ( (tm[i] & 0x80) != 0 )
//
//                flags[ i * 8 + 7 ]= true;
//
//        }
//
//        // shift the first 14 elements to the end of flags
//
//
//        for ( i = 0; i < BITSOFFSET; i++ )
//
//            temp1[i] = flags[i];
//
//        for ( i = BITSOFFSET; i < BITS; i++ )
//
//            flags[i-BITSOFFSET] = flags[i];
//
//        for ( i = 0; i < BITSOFFSET; i++ )
//
//            flags[BITS-BITSOFFSET+i] = temp1[i];
//
//
//
//        // get day, hour, and minute from localTime which is
//
//
//        // in the format of
//
//
//        //     "Tue Jul 24 12:34:56 MDT 2001"
//
//
//        String weekDay = localTime.substring( 0, 3 );
//
//        String clock   = localTime.substring(
//
//                localTime.indexOf((int)':') - 2,
//
//                localTime.indexOf((int)':') + 3 );
//
//        int hour   = Integer.parseInt( clock.substring( 0, 2 ));
//
//        int minute = Integer.parseInt( clock.substring( 3 ));
//
//
//
//        // calculate index
//
//
//        int clockIndex = hour * 2 + minute / 30;
//
//        if (      weekDay.equalsIgnoreCase( "Sun" ))
//
//            index = 0 * 48 + clockIndex;
//
//        else if ( weekDay.equalsIgnoreCase( "Mon" ))
//
//            index = 1 * 48 + clockIndex;
//
//        else if ( weekDay.equalsIgnoreCase( "Tue" ))
//
//            index = 2 * 48 + clockIndex;
//
//        else if ( weekDay.equalsIgnoreCase( "Wed" ))
//
//            index = 3 * 48 + clockIndex;
//
//        else if ( weekDay.equalsIgnoreCase( "Thu" ))
//
//            index = 4 * 48 + clockIndex;
//
//        else if ( weekDay.equalsIgnoreCase( "Fri" ))
//
//            index = 5 * 48 + clockIndex;
//
//        else if ( weekDay.equalsIgnoreCase( "Sat" ))
//
//            index = 6 * 48 + clockIndex;
//
//
//
//        return !flags[index];
//
//    }
}
