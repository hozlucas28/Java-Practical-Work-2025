package repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import exceptions.ItemNotFoundException;
import inventory.Item;

class ItemsRepositoryTests {

	@Test
	void getItem() {
		// Arrange
		Item item01 = new Item("My item A01");
		Item item02 = new Item("My item A02");
		Item item03 = new Item("My item A03");
		Item item04 = new Item("My item A04");

		HashMap<String, Item> items = new HashMap<String, Item>();

		items.put(item01.getName(), item01);
		items.put(item02.getName(), item02);
		items.put(item03.getName(), item03);
		items.put(item04.getName(), item04);

		ItemsRepository itemsRepository = new ItemsRepository(items);

		assertDoesNotThrow(() -> {
			// Act
			Item expected = item03;
			Item received = itemsRepository.getItem(item03.getName());

			// Assert
			assertEquals(expected, received);
		});
	}

	@Test
	void getItem_ItemNotFoundException() {
		// Arrange
		Item item01 = new Item("My item A01");
		Item item02 = new Item("My item A02");

		HashMap<String, Item> items = new HashMap<String, Item>();

		items.put(item01.getName(), item01);
		items.put(item02.getName(), item02);

		ItemsRepository itemsRepository = new ItemsRepository(items);

		// Act within assert
		assertThrows(ItemNotFoundException.class, () -> itemsRepository.getItem("A03"));
	}

	// TODO: loadFromJSON()
}
