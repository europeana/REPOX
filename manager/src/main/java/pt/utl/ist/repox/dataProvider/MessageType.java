package pt.utl.ist.repox.dataProvider;

/**
 * Created by IntelliJ IDEA. User: Gilberto Pedrosa Date: 08-07-2011 Time: 19:54
 * To change this template use File | Settings | File Templates.
 */

public enum MessageType {
    /** MessageType OK */
    OK,
    /** MessageType INVALID_REQUEST */
    INVALID_REQUEST,
    /** MessageType INCOMPATIBLE_TYPE */
    INCOMPATIBLE_TYPE,
    /** MessageType ERROR_DATABASE */
    ERROR_DATABASE,
    /** MessageType ALREADY_EXISTS */
    ALREADY_EXISTS,
    /** MessageType INVALID_ARGUMENTS */
    INVALID_ARGUMENTS,
    /** MessageType NOT_FOUND */
    NOT_FOUND,
    /** MessageType OTHER */
    OTHER;

    /**
     * Return the MessageType of the provided string
     * 
     * @param string
     * @return the MessageType
     */
    public static MessageType get(String string) {
        for (MessageType t : values()) {
            if (t.toString().equalsIgnoreCase(string)) { return t; }
        }
        throw new IllegalArgumentException("Did not recognize MessageType: [" + string + "]");
    }
}
