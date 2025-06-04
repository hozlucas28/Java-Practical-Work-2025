package recipe;

import inventory.Item;

public class Ingredient {
	private final Item item;
	private final int quantity;

	public Ingredient(Item item, int quantity) {
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
