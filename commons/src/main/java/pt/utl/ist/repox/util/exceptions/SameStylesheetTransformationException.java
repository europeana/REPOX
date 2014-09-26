package pt.utl.ist.repox.util.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: Gilberto Pedrosa
 * Date: 28-07-2011
 * Time: 9:46
 * To change this template use File | Settings | File Templates.
 */
public class SameStylesheetTransformationException extends Exception {

    /**
     * Creates a new instance of this class.
     * @param identifier
     */
    public SameStylesheetTransformationException(String identifier) {
        super(identifier);
    }
}
