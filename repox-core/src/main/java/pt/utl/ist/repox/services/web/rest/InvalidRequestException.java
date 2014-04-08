package pt.utl.ist.repox.services.web.rest;

public class InvalidRequestException extends Exception {

	public InvalidRequestException() {
		super();
	}

	public InvalidRequestException(String message) {
		super(message);
	}

	public InvalidRequestException(Throwable cause) {
		super(cause);
	}

	public InvalidRequestException(String message, Throwable cause) {
		super(message, cause);
	}

}
