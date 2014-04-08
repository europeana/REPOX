package pt.utl.ist.repox.z3950;

public class HarvestFailureException extends Exception {
	private static final long serialVersionUID = 1L;

	public HarvestFailureException() {
		super();
	}
	
	public HarvestFailureException(Exception e) {
		super(e);
	}

	public HarvestFailureException(String message, Exception e) {
		super(message, e);
	}

	public HarvestFailureException(String message) {
		super(message);
	}
}
