package exceptions;

public class ItemNotFoundException extends Exception {
	private static final long serialVersionUID = -7708452809775504558L;

	public ItemNotFoundException(String message) {
		super(message);
	}
}
