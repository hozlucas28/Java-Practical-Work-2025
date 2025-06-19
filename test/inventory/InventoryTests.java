package inventory;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import exceptions.ItemNotFoundException;
import exceptions.OutOfRangeException;
import repositories.ItemsRepository;

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
		HashMap<Item, Integer> expected = new HashMap<Item, Integer>();

		expected.put(item01, item01Quantity);
		expected.put(item02, item02Quantity);

		HashMap<Item, Integer> received = inventory.getItems();

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
		assertDoesNotThrow(() -> {
			String jsonPath = Paths.get("test", "assets", "inventory__storeOnJSON.temporal.json").toString();

			// Before
			Files.deleteIfExists(Paths.get(jsonPath));

			// Arrange
			Item itemA = new Item("Item A");
			Item itemB = new Item("Item B");
			Item itemC = new Item("Item C");
			Item itemD = new Item("Item D");

			int itemAQuantity = 2;
			int itemBQuantity = 5;
			int itemCQuantity = 9;
			int itemDQuantity = 1;

			HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

			inventoryItems.put(itemA, itemAQuantity);
			inventoryItems.put(itemB, itemBQuantity);
			inventoryItems.put(itemC, itemCQuantity);
			inventoryItems.put(itemD, itemDQuantity);

			Inventory inventory = new Inventory(inventoryItems);

			HashMap<String, Item> repositoryItems = new HashMap<String, Item>();

			repositoryItems.put(itemA.getName(), itemA);
			repositoryItems.put(itemB.getName(), itemB);
			repositoryItems.put(itemC.getName(), itemC);
			repositoryItems.put(itemD.getName(), itemD);

			ItemsRepository itemsRepository = new ItemsRepository(repositoryItems);

			// Act
			inventory.storeOnJSON(jsonPath);

			// Assert
			Inventory savedInventory = Inventory.loadFromJSON(jsonPath, itemsRepository);

			assertEquals(inventory.getItems(), savedInventory.getItems());

			// After
			Files.deleteIfExists(Paths.get(jsonPath));
		});
	}

	@Test
	void loadFromJSON() {
		assertDoesNotThrow(() -> {
			// Arrange
			Item coal = new Item("coal");
			Item wood = new Item("wood");
			Item iron = new Item("iron");
			Item stick = new Item("stick");
			Item woodCraftingTable = new Item("wood crafting table");

			HashMap<String, Item> repositoryItems = new HashMap<String, Item>();

			repositoryItems.put(coal.getName(), coal);
			repositoryItems.put(wood.getName(), wood);
			repositoryItems.put(iron.getName(), iron);
			repositoryItems.put(stick.getName(), stick);
			repositoryItems.put(woodCraftingTable.getName(), woodCraftingTable);

			ItemsRepository itemsRepository = new ItemsRepository(repositoryItems);

			// Act
			String jsonPath = Paths.get("test", "assets", "inventory.json").toString();
			Inventory inventory = Inventory.loadFromJSON(jsonPath, itemsRepository);

			// Assert
			Map<Item, Integer> expected = Map.of(wood, 2, iron, 7, woodCraftingTable, 1);
			HashMap<Item, Integer> received = inventory.getItems();

			assertEquals(expected, received);
		});
	}

}
