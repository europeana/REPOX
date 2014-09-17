/* OperationStatus.java - created on 19 de Abr de 2013, Copyright (c) 2011 The European Library, all rights reserved */
package harvesterUI.server.sru;

/**
 * SRU record update operation status
 * 
 * @author Nuno Freire (nfreire@gmail.com)
 * @since 19 de Abr de 2013
 */
public enum OperationStatus {
    /** The server has completed the operation successfully */
    SUCCESS,
    /** The server could not complete the operation, additional information may be present in the diagnostics */
    FAIL,
    /** Part of the operation was successful, additional information may be present in the diagnostics */
    PARTIAL,
    /** The server has not yet finished the operation */
    DELAYED;
    
    public String toSruCode() {
        return name().toLowerCase();
    }
}
