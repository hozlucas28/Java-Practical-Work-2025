package repositories;

import java.util.HashMap;

import inventory.Item;

public class ItemsRepository {
	private final HashMap<String, Item> items;

	public ItemsRepository(HashMap<String, Item> items) {
		this.items = items;
	};

	public Item getItem(String itemName) {
		return this.items.get(itemName);
	}
	
	public static ItemsRepository loadFromJSON(String path) {
		// TODO
		HashMap<String, Item> allItems = new HashMap<String, Item>();
		ItemsRepository itemsRepository = new ItemsRepository(allItems);

		return itemsRepository;
	}
}
