package craftingSystem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import exceptions.EmptyHistoryException;
import exceptions.ItemNotFoundException;
import exceptions.NonCraftableItemException;
import inventory.Inventory;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;

class CraftingSystemTests {

	@Test
	void getCraftedItems() {
		// Arrange
		Item iron = new Item("iron");
		Item stick = new Item("stick");
		Item stone = new Item("stone");

		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> furnaceRecipeIngredients = List.of(new Ingredient(stone, 8));
		Recipe furnaceRecipe = new Recipe(woodCraftingTable, furnaceRecipeIngredients, 1250, 1);

		Item furnace = new Item("furnace", List.of(furnaceRecipe));

		List<Ingredient> swordRecipeIngredients = List.of(new Ingredient(iron, 4), new Ingredient(stick, 2));
		Recipe swordRecipe = new Recipe(woodCraftingTable, swordRecipeIngredients, 1750, 2);

		Item sword = new Item("sword", List.of(swordRecipe));

		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		inventoryItems.put(iron, 6);
		inventoryItems.put(stick, 2);
		inventoryItems.put(stone, 12);
		inventoryItems.put(woodCraftingTable, 2);

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		// Acts within assert
		assertDoesNotThrow(() -> {
			craftingSystem.setItemToCraft(sword, 2);
			craftingSystem.craftItem(1);

			craftingSystem.setItemToCraft(furnace, 1);
			craftingSystem.craftItem(1);
		}, "Should not throw an exception if it want to craft non-base and craftable items with the inventory");

		// Asserts
		// @formatter:off
		List<CraftedItem> expected = List.of(
			new CraftedItem(furnace, furnaceRecipe),
			new CraftedItem(sword, swordRecipe, 2)
		);
		// @formatter:on
		int expectedSize = expected.size();

		List<CraftedItem> received = craftingSystem.getCraftedItems();
		int receivedSize = received.size();

		assertTrue(receivedSize == expectedSize,
				"List of expected and received crafted items should have the same length");

		for (int i = 0; i < expectedSize; i++) {
			CraftedItem expectedCraftedItem = expected.get(i);
			CraftedItem receivedCraftedItem = received.get(i);

			assertTrue(receivedCraftedItem.softEquals(expectedCraftedItem),
					"Each expected crafted item should match the received one, allowing a diff on `date` attribute value");
		}
	}

	@Test
	void getCraftableUnits() {
		// Arrange
		Item iron = new Item("iron");
		Item coal = new Item("coal");
		Item wood = new Item("wood");
		Item charcoal = new Item("coal");

		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> stickRecipeIngredients = List.of(new Ingredient(wood, 1));
		Recipe stickRecipe = new Recipe(stickRecipeIngredients, 1350, 4);

		Item stick = new Item("stick", List.of(stickRecipe));

		List<Ingredient> swordRecipeIngredients = List.of(new Ingredient(iron, 2), new Ingredient(stick, 1));
		Recipe swordRecipe = new Recipe(woodCraftingTable, swordRecipeIngredients, 2100, 1);

		Item sword = new Item("sword", List.of(swordRecipe));

		List<Ingredient> torchRecipe01Ingredients = List.of(new Ingredient(coal, 2), new Ingredient(stick, 1));
		Recipe torchRecipe01 = new Recipe(torchRecipe01Ingredients, 750, 4);

		List<Ingredient> torchRecipe02Ingredients = List.of(new Ingredient(charcoal, 1), new Ingredient(stick, 1));
		Recipe torchRecipe02 = new Recipe(torchRecipe02Ingredients, 750, 4);

		Item torch = new Item("torch", List.of(torchRecipe01, torchRecipe02));

		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		inventoryItems.put(iron, 5);
		inventoryItems.put(coal, 2);
		inventoryItems.put(wood, 2);
		inventoryItems.put(charcoal, 3);
		inventoryItems.put(woodCraftingTable, 1);

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		// Acts within assert
		assertDoesNotThrow(() -> {
			craftingSystem.setItemToCraft(sword, 1);
			int swordCraftableUnits = craftingSystem.getCraftableUnits();

			craftingSystem.setItemToCraft(torch, 2);
			int torchCraftableUnits = craftingSystem.getCraftableUnits();

			// Asserts
			int expectedSwordCraftableUnits = 2;
			int receivedSwordCraftableUnits = swordCraftableUnits;

			int expectedTorchCraftableUnits = 3 * 4;
			int receivedTorchCraftableUnits = torchCraftableUnits;

			assertEquals(expectedSwordCraftableUnits, receivedSwordCraftableUnits,
					"Should return the maximum craftable swords");

			assertEquals(expectedTorchCraftableUnits, receivedTorchCraftableUnits,
					"Should return the maximum craftable torches");
		}, "Should not throw an exception if it want to craft non-base items");
	}

	@Test
	void getMissingIngredients() {
		// Arrange
		Inventory inventory = new Inventory(new HashMap<Item, Integer>());
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		Item wood = new Item("wood");
		Item paper = new Item("paper");
		Item iron = new Item("iron");
		Item redstone = new Item("redstone");

		Item craftingTable = new Item("crafting table");

		Recipe swordRecipe = new Recipe(craftingTable, List.of(new Ingredient(wood, 2), new Ingredient(iron, 6)), 1850,
				2);

		Recipe bookRecipe = new Recipe(List.of(new Ingredient(wood, 1), new Ingredient(paper, 2)), 1250, 2);

		Item sword = new Item("sword", List.of(swordRecipe));
		Item book = new Item("book", List.of(bookRecipe));

		assertDoesNotThrow(() -> {
			inventory.addItem(wood, 2);
			inventory.addItem(iron, 3);
			inventory.addItem(paper, 2);
			inventory.addItem(redstone, 4);
		}, "Should not throw an exception on add valid item quantities to the inventory");

		// Acts within asserts
		// @formatter:off
		Map<Recipe, List<Ingredient>> expectedSwordMissingIngredients = Map.of(
			swordRecipe,
			List.of(
				new Ingredient(craftingTable, 1),
				new Ingredient(iron, 3)
			)
		);

		Map<Recipe, List<Ingredient>> expectedBookMissingIngredients = Map.of(
			bookRecipe,
			List.of(
				new Ingredient(wood, 1),
				new Ingredient(paper, 4)
			)
		);
		// @formatter:on

		assertDoesNotThrow(() -> {
			craftingSystem.setItemToCraft(sword, 1);
			HashMap<Recipe, List<Ingredient>> receivedSwordMissingIngredients = craftingSystem.getMissingIngredients();

			craftingSystem.setItemToCraft(book, 6);
			HashMap<Recipe, List<Ingredient>> receivedBookMissingIngredients = craftingSystem.getMissingIngredients();

			// Asserts
			assertEquals(expectedSwordMissingIngredients, receivedSwordMissingIngredients,
					"Should return the missing ingredients to craft a sword per recipe");

			assertEquals(expectedBookMissingIngredients, receivedBookMissingIngredients,
					"Should return the missing ingredients to craft 6 books per recipe");
		}, "Should not throw an exception if it want to craft non-base items");
	}

	@Test
	void getMissingBaseIngredients() {
		// Arrange
		Inventory inventory = new Inventory(new HashMap<Item, Integer>());
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		Item lava = new Item("lava");
		Item water = new Item("water");
		Item paper = new Item("paper");
		Item diamond = new Item("diamond");
		Item cowhide = new Item("cowhide");

		Item woodCraftingTable = new Item("wood crafting table");

		// @formatter:off
		Recipe bookRecipe = new Recipe(
			woodCraftingTable,
			List.of(
				new Ingredient(paper, 1),
				new Ingredient(cowhide, 3)
			),
			1375,
			1
		);

		Item book = new Item("book", List.of(bookRecipe));

		Recipe obsidianBlockRecipe = new Recipe(
			List.of(
				new Ingredient(water, 1),
				new Ingredient(lava, 1)
			),
			750,
			1
		);

		Item obsidianBlock = new Item("obsidian block", List.of(obsidianBlockRecipe));

		Recipe enchantingTableRecipe = new Recipe(
			woodCraftingTable,
			List.of(
				new Ingredient(book, 1),
				new Ingredient(diamond, 2),
				new Ingredient(obsidianBlock, 4)
			),
			750,
			1
		);

		Item enchantingTable = new Item("enchanting table", List.of(enchantingTableRecipe));
		// @formatter:on

		assertDoesNotThrow(() -> {
			inventory.addItem(lava, 3);
			inventory.addItem(water, 2);
			inventory.addItem(paper, 2);
			inventory.addItem(cowhide, 3);
		}, "Should not throw an exception on add valid item quantities to the inventory");

		assertDoesNotThrow(() -> {
			craftingSystem.setItemToCraft(enchantingTable, 2);
		}, "Should not throw an exception if it want to craft non-base items");

		// Act within assert
		int branch = 0;

		// @formatter:off
		Map<Recipe, List<Ingredient>> expected = Map.of(
			enchantingTableRecipe,
			List.of(
				new Ingredient(woodCraftingTable, 1),
				new Ingredient(diamond, 4),
				new Ingredient(cowhide, 3),
				new Ingredient(water, 6),
				new Ingredient(lava, 5)
			)
		);
		// @formatter:on

		HashMap<Recipe, List<Ingredient>> received = craftingSystem.getMissingBaseIngredients(branch);

		assertEquals(expected, received,
				"Should return the missing base ingredients to craft 2 enchanting tables per recipe");
	}

	@Test
	void getRequiredIngredients() {
		// Arrange
		Inventory inventory = new Inventory(new HashMap<Item, Integer>());
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		Item stick = new Item("stick");
		Item iron = new Item("iron");
		Item sand = new Item("sand");

		Item furnace = new Item("furnace");

		Recipe shovelRecipe = new Recipe(List.of(new Ingredient(stick, 2), new Ingredient(iron, 1)), 1000, 1);
		Recipe glassRecipe = new Recipe(furnace, List.of(new Ingredient(sand, 4)), 1250, 4);

		Item shovel = new Item("shovel", List.of(shovelRecipe));
		Item glass = new Item("glass", List.of(glassRecipe));

		// Acts within asserts
		// @formatter:off
		Map<Recipe, List<Ingredient>> expectedShovelRequiredIngredients = Map.of(
			shovelRecipe,
			List.of(
				new Ingredient(stick, 2),
				new Ingredient(iron, 1)
			)
		);

		Map<Recipe, List<Ingredient>> expectedGlassRequiredIngredients = Map.of(
			glassRecipe,
			List.of(
				new Ingredient(furnace, 1),
				new Ingredient(sand, 12)
			)
		);
		// @formatter:on

		assertDoesNotThrow(() -> {
			craftingSystem.setItemToCraft(shovel, 1);
			HashMap<Recipe, List<Ingredient>> receivedShovelRequiredIngredients = craftingSystem
					.getRequiredIngredients();

			craftingSystem.setItemToCraft(glass, 10);
			HashMap<Recipe, List<Ingredient>> receivedGlassRequiredIngredients = craftingSystem
					.getRequiredIngredients();

			// Asserts
			assertEquals(expectedShovelRequiredIngredients, receivedShovelRequiredIngredients,
					"Should return the required ingredients to craft a shovel per recipe");

			assertEquals(expectedGlassRequiredIngredients, receivedGlassRequiredIngredients,
					"Should return the required ingredients to craft 10 glasses per recipe");
		}, "Should not throw an exception if it want to craft non-base items");
	}

	@Test
	void getRequiredBaseIngredients() {
		// Arrange
		Item wood = new Item("wood");
		Item iron = new Item("iron");
		Item whiteWood = new Item("white wood");

		Item woodCraftingTable = new Item("wood crafting table");

		// @formatter:off
		Recipe stickRecipe01 = new Recipe(
			woodCraftingTable,
			List.of(
				new Ingredient(wood, 2)
			),
			1400,
			4
		);

		Recipe stickRecipe02 = new Recipe(
			woodCraftingTable,
			List.of(
				new Ingredient(whiteWood, 2)
			),
			1400,
			4
		);
		// @formatter:on

		Item stick = new Item("stick", List.of(stickRecipe01, stickRecipe02));

		// @formatter:off
		Recipe swordRecipe = new Recipe(
			woodCraftingTable,
			List.of(
				new Ingredient(stick, 1),
				new Ingredient(wood, 10),
				new Ingredient(iron, 3)
			),
			2100,
			1);
		// @formatter:on

		Item sword = new Item("sword", List.of(swordRecipe));

		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		assertDoesNotThrow(() -> {
			craftingSystem.setItemToCraft(sword, 2);
		}, "Should not throw an exception if it want to craft a non-base item");

		// Acts within asserts
		int branch = 0;

		// @formatter:off
		Map<Recipe, List<Ingredient>> expected = Map.of(
			swordRecipe,
			List.of(
				new Ingredient(woodCraftingTable, 1),
				new Ingredient(iron, 6),
				new Ingredient(wood, 22)
			)
		);
		// @formatter:on

		HashMap<Recipe, List<Ingredient>> received = craftingSystem.getRequiredBaseIngredients(branch);

		assertEquals(expected, received,
				"Should return the required base ingredients to craft 2 swords per recipe, following the first recipe path");

		branch = 1;

		// @formatter:off
		expected = Map.of(
			swordRecipe,
			List.of(
				new Ingredient(whiteWood, 2),
				new Ingredient(woodCraftingTable, 1),
				new Ingredient(iron, 6),
				new Ingredient(wood, 20)
			)
		);
		// @formatter:on

		received = craftingSystem.getRequiredBaseIngredients(branch);

		assertEquals(expected, received,
				"Should return the required base ingredients to craft 2 swords per recipe, following the second recipe path");
	}

	@Test
	void canCraft__noMissingIngredients() {
		// Arrange
		Item stone = new Item("stone");
		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> furnaceRecipeIngredients = List.of(new Ingredient(stone, 8));
		Recipe furnaceRecipe = new Recipe(woodCraftingTable, furnaceRecipeIngredients, 1250, 1);

		Item furnace = new Item("furnace", List.of(furnaceRecipe));

		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		inventoryItems.put(stone, 8);
		inventoryItems.put(woodCraftingTable, 1);

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		// Act within asserts
		assertDoesNotThrow(() -> {
			craftingSystem.setItemToCraft(furnace, 1);

			// Assert
			assertTrue(craftingSystem.canCraft(), "Should be true on can craft a furnace");
		}, "Should not throw an exception if it want to craft non-base items");
	}

	@Test
	void canCraft__missingIngredients() {
		// Arrange
		Item stone = new Item("stone");
		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> furnaceRecipeIngredients = List.of(new Ingredient(stone, 8));
		Recipe furnaceRecipe = new Recipe(woodCraftingTable, furnaceRecipeIngredients, 1250, 1);

		Item furnace = new Item("furnace", List.of(furnaceRecipe));

		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		inventoryItems.put(stone, 8);

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		// Act within asserts
		assertDoesNotThrow(() -> {
			craftingSystem.setItemToCraft(furnace, 1);

			// Assert
			assertFalse(craftingSystem.canCraft(),
					"Should be false, if crafting system can not craft a furnace because it have missing ingredients within inventory");
		}, "Should not throw an exception if it want to craft non-base items");
	}

	@Test
	void craftItem() {
		// Arrange
		Item stone = new Item("stone");
		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> furnaceRecipeIngredients = List.of(new Ingredient(stone, 8));
		Recipe furnaceRecipe = new Recipe(woodCraftingTable, furnaceRecipeIngredients, 1250, 1);

		Item furnace = new Item("furnace", List.of(furnaceRecipe));

		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		inventoryItems.put(stone, 12);
		inventoryItems.put(woodCraftingTable, 2);

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		// Act within assert
		assertDoesNotThrow(() -> {
			// Act
			craftingSystem.setItemToCraft(furnace, 1);
			CraftedItem craftedItem = craftingSystem.craftItem(1);

			// Asserts
			HashMap<Item, Integer> expectedInventoryItems = new HashMap<Item, Integer>();

			expectedInventoryItems.put(stone, 12 - 8);
			expectedInventoryItems.put(woodCraftingTable, 2);
			expectedInventoryItems.put(furnace, 1);

			Inventory expectedInventory = new Inventory(expectedInventoryItems);
			Inventory receivedInventory = inventory;

			CraftedItem expectedCraftedItem = new CraftedItem(furnace, furnaceRecipe, 1);
			CraftedItem receivedCraftedItem = craftedItem;

			assertEquals(expectedInventory, receivedInventory,
					"Inventory should have the quantity of crafted item, minus used ingredients to craft it");
			assertEquals(expectedCraftedItem, receivedCraftedItem,
					"Crafted item should a furnace crafted with 8 stones in 1250 milliseconds");
		}, "Should not throw an exception if it want to craft a non-base and craftable item with the inventory");
	}

	@Test
	void craftItem__NonCraftableItemException() {
		// Arrange
		Item stone = new Item("stone");
		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> furnaceRecipeIngredients = List.of(new Ingredient(stone, 8));
		Recipe furnaceRecipe = new Recipe(woodCraftingTable, furnaceRecipeIngredients, 1250, 1);

		Item furnace = new Item("furnace", List.of(furnaceRecipe));

		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		inventoryItems.put(stone, 6);
		inventoryItems.put(woodCraftingTable, 2);

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		// Act within asserts
		assertDoesNotThrow(() -> {
			craftingSystem.setItemToCraft(furnace, 1);

			assertThrows(NonCraftableItemException.class, () -> craftingSystem.craftItem(1),
					"Should throw `NonCraftableItemException` on try to craft a furnace without the required ingredients within inventory");
		}, "Should not throw an exception if it want to craft non-base items");
	}

	@Test
	void undoLastCraft() {
		// Arrange
		Item iron = new Item("iron");
		Item coal = new Item("coal");
		Item stick = new Item("stick");

		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> swordRecipeIngredients = List.of(new Ingredient(iron, 2), new Ingredient(stick, 1));
		Recipe swordRecipe = new Recipe(woodCraftingTable, swordRecipeIngredients, 2100, 1);

		Item sword = new Item("sword", List.of(swordRecipe));

		List<Ingredient> torchRecipeIngredients = List.of(new Ingredient(coal, 1), new Ingredient(stick, 1));
		Recipe torchRecipe = new Recipe(torchRecipeIngredients, 750, 4);

		Item torch = new Item("torch", List.of(torchRecipe));

		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		inventoryItems.put(iron, 5); // 3
		inventoryItems.put(coal, 3); // 3
		inventoryItems.put(stick, 3); // 2
		inventoryItems.put(woodCraftingTable, 2); // 2

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		// Act within assert
		assertDoesNotThrow(() -> {
			craftingSystem.setItemToCraft(sword, 1);
			craftingSystem.craftItem(1);

			craftingSystem.setItemToCraft(torch, 2);
			craftingSystem.craftItem(1);

			craftingSystem.undoLastCraft();
		}, "Should not throw an exception if it craft items and undo last one");

		// Assert
		HashMap<Item, Integer> expectedInventoryItems = new HashMap<Item, Integer>();

		expectedInventoryItems.put(sword, 1);

		expectedInventoryItems.put(iron, 3);
		expectedInventoryItems.put(coal, 3);
		expectedInventoryItems.put(stick, 2);
		expectedInventoryItems.put(woodCraftingTable, 2);

		Inventory expectedInventory = new Inventory(expectedInventoryItems);
		Inventory receivedInventory = inventory;

		assertEquals(expectedInventory, receivedInventory,
				"Inventory should have the ingredients of the 2 crafted torches, minus the crafted ones");
	}

	@Test
	void undoLastCraft__EmptyHistoryException() {
		// Arrange
		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		// Act within assert
		assertThrows(EmptyHistoryException.class, () -> craftingSystem.undoLastCraft(),
				"If the crafted history is empty, it should throw `EmptyHistoryException` on try to undo the last craft");
	}

	@Test
	void undoLastCraft__ItemNotFoundException() {
		// Arrange
		Item stone = new Item("stone");
		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> furnaceRecipeIngredients = List.of(new Ingredient(stone, 8));
		Recipe furnaceRecipe = new Recipe(woodCraftingTable, furnaceRecipeIngredients, 1250, 1);

		Item furnace = new Item("furnace", List.of(furnaceRecipe));

		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		inventoryItems.put(stone, 12);
		inventoryItems.put(woodCraftingTable, 2);

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		assertDoesNotThrow(() -> {
			craftingSystem.setItemToCraft(furnace, 1);
			craftingSystem.craftItem(1);
			inventory.removeItem(furnace, 1);
		}, "Should not throw an exception if it want to craft a non-base and craftable item with the inventory");

		// Act within assert
		assertThrows(ItemNotFoundException.class, () -> craftingSystem.undoLastCraft(),
				"Should throw `ItemNotFoundException` on try to undo the last crafted item without have crafted items");
	}
}
