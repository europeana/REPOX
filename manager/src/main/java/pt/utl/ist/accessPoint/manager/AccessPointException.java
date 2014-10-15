/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.utl.ist.accessPoint.manager;

/**
 * This is a general exception for all problems concerning access points.
 *
 * @author Markus Muhr (markus.muhr@theeuropeanlibrary.org)
 * @since 15.10.2014
 */
public class AccessPointException extends Exception {
    /**
     * For inheritance reasons, pipes through to the super constructor.
     *
     * @param message description of the error
     */
    public AccessPointException(String message) {
        super(message);
    }

    /**
     * For inheritance reasons, pipes through to the super constructor.
     *
     * @param message description of the error
     * @param cause root cause of the error
     */
    public AccessPointException(String message, Throwable cause) {
        super(message, cause);
    }
}
