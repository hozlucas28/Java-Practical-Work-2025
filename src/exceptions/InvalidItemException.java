package exceptions;

public class InvalidItemException extends Exception {
	private static final long serialVersionUID = -2553761766601848852L;
	
	public InvalidItemException(String message) {
		super(message);
	}
}
