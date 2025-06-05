package inventory;

import java.util.HashMap;

import exceptions.ItemNotFoundException;
import exceptions.OutOfRangeException;

public class Inventory {
	private HashMap<Item, Integer> items;

	public Inventory(HashMap<Item, Integer> items) {
		this.items = items;
	}

	public HashMap<Item, Integer> getItems() {
		return this.items;
	}

	public int getItemQuantity(Item item) {
		return this.items.getOrDefault(item, 0);
	}

	public void addItem(Item item, int quantity) throws OutOfRangeException {
		if (quantity < 1) {
			String errorMessage = String.format("Received %d as quantity, but expect it greather than or equal to %d",
					quantity, 1);
			throw new OutOfRangeException(errorMessage);
		}

		if (this.items.containsKey(item)) {
			this.items.compute(item, (key, value) -> value + quantity);
		} else {
			this.items.put(item, quantity);
		}
	}

	public void removeItem(Item item, int quantity) throws ItemNotFoundException, OutOfRangeException {
		String errorMessage;

		if (!this.items.containsKey(item)) {
			errorMessage = String.format("Item with `%s` id was not found inside the inventory", item.getId());
			throw new ItemNotFoundException(errorMessage);
		}

		int currentQuantity = this.items.get(item);

		if (quantity < 1 || quantity > currentQuantity) {
			errorMessage = String.format("Received %d as quantity, but expect it between %d and %d (inclusive)",
					quantity, 1, currentQuantity);
			throw new OutOfRangeException(errorMessage);
		}

		int newQuantity = currentQuantity - quantity;

		if (newQuantity == 0) {
			this.items.remove(item);
		} else {
			this.items.replace(item, newQuantity);
		}
	}

	public void storeOnJSON(String path) {
		// TODO
	}

	public static Inventory loadFromJSON(String path) {
		// TODO
		HashMap<Item, Integer> items = new HashMap<Item, Integer>();
		Inventory inventory = new Inventory(items);

		return inventory;
	}
}
