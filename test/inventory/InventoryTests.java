package inventory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import exceptions.ItemNotFoundException;
import exceptions.OutOfRangeException;

class InventoryTests {

	@Test
	void getItems() {
		// Arrange
		Item item01 = new Item("My item A01");
		Item item02 = new Item("My item A02");

		int item01Quantity = 1;
		int item02Quantity = 3;

		HashMap<Item, Integer> items = new HashMap<Item, Integer>();

		items.put(item01, item01Quantity);
		items.put(item02, item02Quantity);

		Inventory inventory = new Inventory(items);

		// Act
		int expected = items.size();
		int received = inventory.getItems().size();

		// Assert
		assertEquals(expected, received);
	}

	@Test
	void getItemQuantity() {
		// Arrange
		Item item = new Item("My item A");

		int itemQuantity = 7;

		HashMap<Item, Integer> items = new HashMap<Item, Integer>();

		items.put(item, itemQuantity);

		Inventory inventory = new Inventory(items);

		// Act
		int expected = itemQuantity;
		int received = inventory.getItemQuantity(item);

		// Assert
		assertEquals(expected, received);
	}

	@Test
	void addItem() {
		// Arrange
		HashMap<Item, Integer> items = new HashMap<Item, Integer>();
		Inventory inventory = new Inventory(items);

		Item item = new Item("My item A");

		int itemQuantity = 7;

		assertDoesNotThrow(() -> {
			// Act
			inventory.addItem(item, itemQuantity);

			int expected = itemQuantity;
			int received = inventory.getItemQuantity(item);

			// Assert
			assertEquals(expected, received);
		});
	}

	@Test
	void addItem_OutOfRangeException() {
		// Arrange
		HashMap<Item, Integer> items = new HashMap<Item, Integer>();
		Inventory inventory = new Inventory(items);

		Item item = new Item("My item A");

		int itemQuantity = 0;

		// Act within assert
		assertThrows(OutOfRangeException.class, () -> inventory.addItem(item, itemQuantity));
	}

	@Test
	void removeItem() {
		// Arrange
		Item item = new Item("My item A");

		int itemQuantity = 7;

		HashMap<Item, Integer> items = new HashMap<Item, Integer>();

		items.put(item, itemQuantity);

		Inventory inventory = new Inventory(items);

		assertDoesNotThrow(() -> {
			// Act
			int quantityToRemove = 1;
			inventory.removeItem(item, quantityToRemove);

			int expected = itemQuantity - quantityToRemove;
			int received = inventory.getItemQuantity(item);

			// Assert
			assertEquals(expected, received);
		});
	}

	@Test
	void removeItem_ItemNotFoundException() {
		// Arrange
		Item item = new Item("My item A");

		HashMap<Item, Integer> items = new HashMap<Item, Integer>();
		Inventory inventory = new Inventory(items);

		// Assert
		assertThrows(ItemNotFoundException.class, () -> {
			// Act
			int quantityToRemove = 1;
			inventory.removeItem(item, quantityToRemove);
		});
	}

	@Test
	void removeItem_OutOfRangeException() {
		// Arrange
		Item item = new Item("My item A");

		int itemQuantity = 7;

		HashMap<Item, Integer> items = new HashMap<Item, Integer>();

		items.put(item, itemQuantity);

		Inventory inventory = new Inventory(items);

		// Assert
		assertThrows(OutOfRangeException.class, () -> {
			// Act
			int quantityToRemove = 10;
			inventory.removeItem(item, quantityToRemove);
		});
	}

	@Test
	void storeOnJSON() {
		// TODO
		fail("Not yet implemented");
	}

	@Test
	void loadFromJSON() {
		// TODO
		fail("Not yet implemented");
	}
}
