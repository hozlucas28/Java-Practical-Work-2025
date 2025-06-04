package inventory;

import java.util.HashMap;

public class Inventory {
	private HashMap<Item, Integer> items;

	public Inventory(HashMap<Item, Integer> items) {
		this.items = items;
	}

	public HashMap<Item, Integer> getItems() {
		return this.items;
	}

	public int getItemQuantity(Item item) {
		return this.items.get(item);
	}

	public void addItem(Item item, Integer quantity) {
		this.items.put(item, quantity);
	}

	public void removeItem(Item item, Integer quantity) {
		// TODO
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
