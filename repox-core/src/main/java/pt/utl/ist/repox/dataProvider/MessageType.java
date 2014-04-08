package pt.utl.ist.repox.dataProvider;

/**
 * Created by IntelliJ IDEA.
 * User: Gilberto Pedrosa
 * Date: 08-07-2011
 * Time: 19:54
 * To change this template use File | Settings | File Templates.
 */


public enum MessageType {
    OK,
    INVALID_REQUEST,
    INCOMPATIBLE_TYPE,
    ERROR_DATABASE,
    ALREADY_EXISTS,
    INVALID_ARGUMENTS,
    NOT_FOUND,
    OTHER;

    public static MessageType get(String string) {
        for (MessageType t : values()) {
            if (t.toString().equalsIgnoreCase(string)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Did not recognize MessageType: [" + string + "]");
    }
}

