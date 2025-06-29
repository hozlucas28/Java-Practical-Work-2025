package exceptions;

public class EmptyHistoryException extends Exception {
	private static final long serialVersionUID = 3859683312751227433L;
	
	public EmptyHistoryException(String message) {
		super(message);
	}
}
