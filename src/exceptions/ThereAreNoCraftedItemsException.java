package exceptions;

public class ThereAreNoCraftedItemsException extends Exception {

	public ThereAreNoCraftedItemsException() {
		super("There are no crafted items within the `CraftingHistory` instance");
	}
}
