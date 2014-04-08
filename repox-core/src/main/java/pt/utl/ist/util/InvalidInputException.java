/*
 * Created on 2006/12/02
 *
 */
package pt.utl.ist.util;

public class InvalidInputException extends Exception {
    public InvalidInputException(String message){
        super(message);
    }

    public InvalidInputException(Throwable thr){
        super(thr);
    }

    public InvalidInputException(String message, Throwable thr){
        super(message,thr);
    }  
}
