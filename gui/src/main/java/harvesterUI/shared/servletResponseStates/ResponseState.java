package harvesterUI.shared.servletResponseStates;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 12/02/12
 * Time: 17:10
 */
public enum ResponseState implements IsSerializable {
    SUCCESS,
    ERROR,
    NO_INTERNET_CONNECTION,
    ALREADY_EXISTS,
    OTHER,
    NOT_FOUND,
    URL_MALFORMED,
    URL_NOT_EXISTS,
    INVALID_ARGUMENTS,

    // Data Sets
    HTTP_URL_MALFORMED,
    HTTP_URL_NOT_EXISTS,
    FTP_CONNECTION_FAILED,
    INCOMPATIBLE_TYPE,
    ERROR_DATABASE,


    // User management
    USER_ALREADY_EXISTS,
    EMAIL_AUTHENTICATION_ERROR,

    // Transformations
    MAPPING_SAME_SRC_AND_DEST,
    MAPPING_SAME_XSL,
    ERROR_SAVING_XSL
}
