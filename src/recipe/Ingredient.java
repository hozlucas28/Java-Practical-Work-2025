package recipe;

import java.util.Formatter;
import java.util.Map;
import java.util.Objects;

import inventory.Item;
import utilities.StringTransformers;

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

	public String toString(String itemMarker, int lPadding) {
		StringBuilder builder = new StringBuilder();
		Formatter formatter = new Formatter(builder);

		formatter.format("%" + lPadding + "s%s ", " ", itemMarker);
		formatter.format("%s (x%d)", StringTransformers.toTitle(this.item.getName()), this.quantity);

		formatter.close();

		return builder.toString();
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
