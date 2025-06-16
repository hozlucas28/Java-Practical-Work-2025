package recipe;

import java.util.Objects;

import exceptions.OutOfRangeException;
import inventory.Item;

public class Ingredient {
	private final Item item;
	private int quantity;

	public Ingredient(Item item, int quantity) throws OutOfRangeException {
		if (quantity < 1) {
			String errorMessage = String.format("Received %d as quantity, but expect it greater than or equal to %d",
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

	public void incrementQuantity(int quantity) throws OutOfRangeException {
		if (quantity < 1) {
			String errorMessage = String.format("Received %d as quantity, but expect it greater than or equal to %d",
					quantity, 1);
			throw new OutOfRangeException(errorMessage);
		}

		this.quantity += quantity;
	}

	public Ingredient copy() {
		Ingredient copy = null;

		try {
			copy = new Ingredient(this.item.copy(), this.quantity);
		} catch (OutOfRangeException e) {
			// With a quantity greater than 1, it's never throw an OutOfRangeException.
		}

		return copy;
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

		return Objects.equals(this.item, other.item);
	}
}
