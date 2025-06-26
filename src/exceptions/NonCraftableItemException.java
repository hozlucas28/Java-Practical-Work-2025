package exceptions;

public class NonCraftableItemException extends Exception {
	private static final long serialVersionUID = 264434855143082574L;

	public NonCraftableItemException(String message) {
		super(message);
	};
}
