package pt.utl.ist.repox.util;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 02-02-2012
 * Time: 15:00
 */
public class ServerSideException extends Exception{

    private String message;

    public ServerSideException() {
    }

    public ServerSideException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
