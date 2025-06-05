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
		Item item01 = new Item("A01", "My item", null);
		Item item02 = new Item("A02", "My item", null);
		Item item03 = new Item("A03", "My item", null);
		Item item04 = new Item("A04", "My item", null);

		HashMap<String, Item> items = new HashMap<String, Item>();

		items.put(item01.getId(), item01);
		items.put(item02.getId(), item02);
		items.put(item03.getId(), item03);
		items.put(item04.getId(), item04);

		ItemsRepository itemsRepository = new ItemsRepository(items);

		assertDoesNotThrow(() -> {
			// Act
			Item expected = item03;
			Item received = itemsRepository.getItem(item03.getId());

			// Assert
			assertEquals(expected, received);
		});
	}

	@Test
	void getItem_ItemNotFoundException() {
		// Arrange
		Item item01 = new Item("A01", "My item", null);
		Item item02 = new Item("A02", "My item", null);

		HashMap<String, Item> items = new HashMap<String, Item>();

		items.put(item01.getId(), item01);
		items.put(item02.getId(), item02);

		ItemsRepository itemsRepository = new ItemsRepository(items);

		// Act within assert
		assertThrows(ItemNotFoundException.class, () -> itemsRepository.getItem("A03"));
	}

	// TODO: loadFromJSON()
}
