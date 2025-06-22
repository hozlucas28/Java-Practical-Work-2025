package recipe;

import java.util.Objects;

import inventory.Item;

public class Ingredient {
	private final Item item;
	private int quantity;

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

	@Override
	public int hashCode() {
		return Objects.hash(item);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		Ingredient other = (Ingredient) obj;

		return Objects.equals(this.item, other.item) && this.quantity == other.quantity;
	}
}
