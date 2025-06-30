package inventory;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
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
		Item diamond = new Item("diamond");
		Item cobblestone = new Item("cobblestone");

		int diamondQuantity = 1;
		int cobblestoneQuantity = 3;

		HashMap<Item, Integer> items = new HashMap<Item, Integer>();

		items.put(diamond, diamondQuantity);
		items.put(cobblestone, cobblestoneQuantity);

		Inventory inventory = new Inventory(items);

		// Act within assert
		HashMap<Item, Integer> expected = new HashMap<Item, Integer>();

		expected.put(diamond, diamondQuantity);
		expected.put(cobblestone, cobblestoneQuantity);

		HashMap<Item, Integer> received = inventory.getItems();

		assertEquals(expected, received, "Should return the correct items and quantities within inventory");
	}

	@Test
	void getItemQuantity() {
		// Arrange
		Item gold = new Item("gold");

		int goldQuantity = 7;

		HashMap<Item, Integer> items = new HashMap<Item, Integer>();

		items.put(gold, goldQuantity);

		Inventory inventory = new Inventory(items);

		// Act within assert
		int expected = goldQuantity;
		int received = inventory.getItemQuantity(gold);

		assertEquals(expected, received, "Should return the correct quantity of gold items within inventory");
	}

	@Test
	void addItem() {
		// Arrange
		HashMap<Item, Integer> items = new HashMap<Item, Integer>();
		Inventory inventory = new Inventory(items);

		Item redstone = new Item("redstone");

		int redstoneQuantity = 7;

		// Act within assert
		assertDoesNotThrow(() -> {
			// Act
			inventory.addItem(redstone, redstoneQuantity);

			int expected = redstoneQuantity;
			int received = inventory.getItemQuantity(redstone);

			// Assert
			assertEquals(expected, received, "Should add the correct quantity of redstone to the inventory");
		}, "Should not throw an exception with a valid redstone quantity to add");
	}

	@Test
	void addItem__OutOfRangeException() {
		// Arrange
		HashMap<Item, Integer> items = new HashMap<Item, Integer>();
		Inventory inventory = new Inventory(items);

		Item compass = new Item("compass");

		int compassQuantity = 0;

		// Act within assert
		assertThrows(OutOfRangeException.class, () -> inventory.addItem(compass, compassQuantity),
				"Should throw `OutOfRangeException` on add zero compass items to the inventory");
	}

	@Test
	void removeItem() {
		// Arrange
		Item torch = new Item("torch");

		int torchQuantity = 7;

		HashMap<Item, Integer> items = new HashMap<Item, Integer>();

		items.put(torch, torchQuantity);

		Inventory inventory = new Inventory(items);

		// Act within assert
		int torchesToRemove = 1;
		assertDoesNotThrow(() -> inventory.removeItem(torch, torchesToRemove),
				"Should not throw an exception with a valid torches quantity to remove");

		int expected = torchQuantity - torchesToRemove;
		int received = inventory.getItemQuantity(torch);

		assertEquals(expected, received, "Should decrease the quantity of the stored torches");
	}

	@Test
	void removeItem__ItemNotFoundException() {
		// Arrange
		Item shovel = new Item("shovel");

		HashMap<Item, Integer> items = new HashMap<Item, Integer>();
		Inventory inventory = new Inventory(items);

		// Act within assert
		assertThrows(ItemNotFoundException.class, () -> inventory.removeItem(shovel, 1),
				"Should throw `ItemNotFoundException` on try to remove a missing shovel within inventory");
	}

	@Test
	void removeItem__OutOfRangeException() {
		// Arrange
		Item fish = new Item("fish");

		int fishQuantity = 7;

		HashMap<Item, Integer> items = new HashMap<Item, Integer>();

		items.put(fish, fishQuantity);

		Inventory inventory = new Inventory(items);

		// Act within assert
		assertThrows(OutOfRangeException.class, () -> inventory.removeItem(fish, 10),
				"Should throw `OutOfRangeException` on try to remove fish above the stored quantity");
	}

	@Test
	void storeOnJSON() {
		// Arrange
		Item diamond = new Item("diamond");
		Item redstone = new Item("redstone");
		Item gold = new Item("gold");
		Item iron = new Item("iron");

		int diamondQuantity = 2;
		int redstoneQuantity = 5;
		int goldQuantity = 9;
		int ironQuantity = 1;

		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		inventoryItems.put(diamond, diamondQuantity);
		inventoryItems.put(redstone, redstoneQuantity);
		inventoryItems.put(gold, goldQuantity);
		inventoryItems.put(iron, ironQuantity);

		Inventory inventory = new Inventory(inventoryItems);

		HashMap<String, Item> repositoryItems = new HashMap<String, Item>();

		repositoryItems.put(diamond.getName(), diamond);
		repositoryItems.put(redstone.getName(), redstone);
		repositoryItems.put(gold.getName(), gold);
		repositoryItems.put(iron.getName(), iron);

		ItemsRepository itemsRepository = new ItemsRepository(repositoryItems);

		// Act within assert
		assertDoesNotThrow(() -> {
			// Act
			File tempFile = File.createTempFile("inventoryTests__storeOnJSON", ".tmp.json");
			String tempFilePath = tempFile.getAbsolutePath().replace("\\", "/");

			inventory.storeOnJSON(tempFilePath);

			// Assert
			Inventory savedInventory = Inventory.loadFromJSON(tempFilePath, itemsRepository);

			assertEquals(inventory.getItems(), savedInventory.getItems(),
					"Stored inventory and loaded one from the generated json should be the same");

			// After
			tempFile.delete();
		}, "Should not throw an exception on store/load the inventory in/from a JSON file");
	}

	@Test
	void loadFromJSON() {
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
		String jsonPath = Paths.get("test", "assets", "inventory.test.json").toString();
		Inventory inventory = assertDoesNotThrow(() -> Inventory.loadFromJSON(jsonPath, itemsRepository),
				"Should not throw an exception for a valid JSON file");

		// Assert
		Map<Item, Integer> expected = Map.of(wood, 2, iron, 7, woodCraftingTable, 1);
		HashMap<Item, Integer> received = inventory.getItems();

		assertEquals(expected, received, "Expected inventory and loaded one from the json file should be the same");
	}
}
