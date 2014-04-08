package pt.utl.ist.util.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: Gilberto Pedrosa
 * Date: 28-07-2011
 * Time: 9:46
 * To change this template use File | Settings | File Templates.
 */
public class InvalidArgumentsException extends Exception {

    public InvalidArgumentsException(String identifier) {
        super(identifier);
    }
}
