package pt.utl.ist.util.exceptions;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Gilberto Pedrosa
 * Date: 28-07-2011
 * Time: 9:46
 * To change this template use File | Settings | File Templates.
 */
public class AlreadyExistsException extends Exception implements Serializable{
    private static final long serialVersionUID = 44L;

    public AlreadyExistsException() {
        super();
    }

    public AlreadyExistsException(String msg) {
        super(msg);
    }

    public AlreadyExistsException(String msg, Exception e) {
        super(msg, e);
    }

}
