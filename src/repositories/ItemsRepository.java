package repositories;

import java.util.HashMap;

import exceptions.ItemNotFoundException;
import inventory.Item;

public class ItemsRepository {
	private final HashMap<String, Item> items;

	public ItemsRepository(HashMap<String, Item> items) {
		this.items = items;
	};

	public Item getItem(String itemId) throws ItemNotFoundException {
		Item item = this.items.get(itemId);
		
		if (item == null) {
			String errorMessage = String.format("Item with `%s` id was not found inside items repository", itemId);
			throw new ItemNotFoundException(errorMessage);
		}
		
		return item;
	}
	
	public static ItemsRepository loadFromJSON(String path) {
		// TODO
		HashMap<String, Item> allItems = new HashMap<String, Item>();
		ItemsRepository itemsRepository = new ItemsRepository(allItems);

		return itemsRepository;
	}
}
