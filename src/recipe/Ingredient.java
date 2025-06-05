package recipe;

import exceptions.OutOfRangeException;
import inventory.Item;

public class Ingredient {
	private final Item item;
	private final int quantity;

	public Ingredient(Item item, int quantity) throws OutOfRangeException {
		if (quantity < 1) {
			String errorMessage = String.format("Received %d as quantity, but expect it greather than or equal to %d",
					quantity, 1);
			throw new OutOfRangeException(errorMessage);
		}

		this.item = item;
		this.quantity = quantity;
	}

	public Item getItem() {
		return this.item;
	}

	public int getQuantity() {
		return this.quantity;
	}
}
