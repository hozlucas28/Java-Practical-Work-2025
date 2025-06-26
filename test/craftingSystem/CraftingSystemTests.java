package craftingSystem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

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

		// Acts within asserts
		craftingSystem.setItemToCraft(sword, 2);

		assertDoesNotThrow(() -> {
			craftingSystem.craftItem();
		}, "Should not throw an exception if it craft craftable items");

		craftingSystem.setItemToCraft(furnace, 1);

		assertDoesNotThrow(() -> {
			craftingSystem.craftItem();
		}, "Should not throw an exception if it craft craftable items");

		// Assert
		// @formatter:off
		List<CraftedItem> expected = List.of(
			new CraftedItem(furnace, furnaceRecipe),
			new CraftedItem(sword, swordRecipe, 2)
		);
		// @formatter:on

		List<CraftedItem> received = craftingSystem.getCraftedItems();

		assertTrue(expected.size() == received.size(),
				"List of expected and received crafted items should have the same length");

		for (int i = 0; i < expected.size(); i++) {
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
		Item stick = new Item("stick");
		Item charcoal = new Item("coal");

		Item woodCraftingTable = new Item("wood crafting table");

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
		inventoryItems.put(stick, 3);
		inventoryItems.put(charcoal, 3);
		inventoryItems.put(woodCraftingTable, 1);

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		// Acts
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
	}

	@Test
	void getMissingIngredients() {
		// Arrange
		HashMap<Item, Integer> itemsInInventory = new HashMap<Item, Integer>();
		Inventory inventory = new Inventory(itemsInInventory);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		Item itemBase01 = new Item("Item base 01");
		Item itemBase02 = new Item("Item base 02");
		Item itemBase03 = new Item("Item base 03");
		Item itemBase04 = new Item("Item base 04");

		Item craftingTable = new Item("Crafting table");

		Ingredient ingredient01 = new Ingredient(itemBase01, 1);
		Ingredient ingredient02 = new Ingredient(itemBase02, 2);

		Ingredient ingredient03 = new Ingredient(itemBase03, 3);
		Ingredient ingredient04 = new Ingredient(itemBase04, 5);

		List<Ingredient> ingredientsRecipe01 = List.of(ingredient01, ingredient02);
		List<Ingredient> ingredientsRecipe02 = List.of(ingredient03, ingredient04);

		Recipe recipe01 = new Recipe(ingredientsRecipe01, 1000, 2);
		Recipe recipe02 = new Recipe(craftingTable, ingredientsRecipe02, 1250, 4);

		Item itemToCraft01 = new Item("Item A01", List.of(recipe01));
		Item itemToCraft02 = new Item("Item A02", List.of(recipe02));

		assertDoesNotThrow(() -> {
			inventory.addItem(itemBase01, 1);
			inventory.addItem(itemBase02, 1);
			inventory.addItem(itemBase04, 4);
		}, "Should not throw an exception if it craft craftable items");

		// Act
		// @formatter:off
		Map<Recipe, List<Ingredient>> expectedMissingIngredients01 = Map.of(
			recipe01,
			List.of(
				new Ingredient(itemBase02, 1)
			)
		);

		Map<Recipe, List<Ingredient>> expectedMissingIngredients02 = Map.of(
			recipe02,
			List.of(
				new Ingredient(craftingTable, 1),
				new Ingredient(itemBase03, 3 * 2),
				new Ingredient(itemBase04, (5 * 2) - 4)
			)
		);
		// @formatter:on

		craftingSystem.setItemToCraft(itemToCraft01, 1);
		HashMap<Recipe, List<Ingredient>> receivedMissingIngredients01 = craftingSystem.getMissingIngredients();

		craftingSystem.setItemToCraft(itemToCraft02, 5);
		HashMap<Recipe, List<Ingredient>> receivedMissingIngredients02 = craftingSystem.getMissingIngredients();

		// Assert
		assertEquals(expectedMissingIngredients01, receivedMissingIngredients01,
				"Should return the missing ingredients to craft item 01 per recipe");

		assertEquals(expectedMissingIngredients02, receivedMissingIngredients02,
				"Should return the missing ingredients to craft item 02 per recipe");
	}

	// TODO: test getMissingBaseIngredients() X

	@Test
	void getRequiredIngredients() {
		// Arrange
		HashMap<Item, Integer> itemsInInventory = new HashMap<Item, Integer>();
		Inventory inventory = new Inventory(itemsInInventory);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		Item item01 = new Item("Item base 01");
		Item item02 = new Item("Item base 02");
		Item item03 = new Item("Item base 03");
		Item item04 = new Item("Item base 04");

		Item craftingTable = new Item("Crafting table");

		Ingredient ingredient01 = new Ingredient(item01, 1);
		Ingredient ingredient02 = new Ingredient(item02, 2);

		Ingredient ingredient03 = new Ingredient(item03, 3);
		Ingredient ingredient04 = new Ingredient(item04, 5);

		List<Ingredient> ingredientsRecipe01 = List.of(ingredient01, ingredient02);
		List<Ingredient> ingredientsRecipe02 = List.of(ingredient03, ingredient04);

		Recipe recipe01 = new Recipe(ingredientsRecipe01, 1000, 2);
		Recipe recipe02 = new Recipe(craftingTable, ingredientsRecipe02, 1250, 4);

		Item itemToCraft01 = new Item("Item A01", List.of(recipe01));
		Item itemToCraft02 = new Item("Item A02", List.of(recipe02));

		// Act
		// @formatter:off
		Map<Recipe, List<Ingredient>> expectedRequiredIngredients01 = Map.of(
			recipe01,
			List.of(
				new Ingredient(item01, 1),
				new Ingredient(item02, 2)
			)
		);

		Map<Recipe, List<Ingredient>> expectedRequiredIngredients02 = Map.of(
			recipe02,
			List.of(
				new Ingredient(craftingTable, 1),
				new Ingredient(item03, 3 * 2),
				new Ingredient(item04, 5 * 2)
			)
		);
		// @formatter:on

		craftingSystem.setItemToCraft(itemToCraft01, 1);
		HashMap<Recipe, List<Ingredient>> receivedRequiredIngredients01 = craftingSystem.getRequiredIngredients();

		craftingSystem.setItemToCraft(itemToCraft02, 5);
		HashMap<Recipe, List<Ingredient>> receivedRequiredIngredients02 = craftingSystem.getRequiredIngredients();

		// Assert
		assertEquals(expectedRequiredIngredients01, receivedRequiredIngredients01,
				"Should return the required ingredients to craft item 01 per recipe");

		assertEquals(expectedRequiredIngredients02, receivedRequiredIngredients02,
				"Should return the required ingredients to craft item 02 per recipe");
	}

	@Test
	void getRequiredBaseIngredients() {
		// Arrange
		Item wood = new Item("wood");
		Item iron = new Item("iron");

		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> stickRecipeIngredients = List.of(new Ingredient(wood, 2));
		Recipe stickRecipe = new Recipe(woodCraftingTable, stickRecipeIngredients, 1400, 4);
		List<Recipe> stickRecipes = List.of(stickRecipe);

		Item stick = new Item("stick", stickRecipes);

		List<Ingredient> swordRecipeIngredients = List.of(new Ingredient(stick, 1), new Ingredient(wood, 10),
				new Ingredient(iron, 3));
		Recipe swordRecipe = new Recipe(woodCraftingTable, swordRecipeIngredients, 2100, 1);
		List<Recipe> swordRecipes = List.of(swordRecipe);

		Item sword = new Item("sword", swordRecipes);

		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		craftingSystem.setItemToCraft(sword, 2);

		// Act
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

		HashMap<Recipe, List<Ingredient>> received = craftingSystem.getRequiredBaseIngredients();

		// Assert
		assertEquals(expected, received, "Should return the required base ingredients to craft 2 swords per recipe");
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

		craftingSystem.setItemToCraft(furnace, 1);

		// Act within assert
		assertTrue(craftingSystem.canCraft(), "Should be true on can craft 1 furnace");
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

		craftingSystem.setItemToCraft(furnace, 1);

		// Act within assert
		assertFalse(craftingSystem.canCraft(),
				"Should be false, if crafting system can not craft 1 furnace because it have missing ingredients within inventory");
	}

	@Test
	void craftItems() {
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

		craftingSystem.setItemToCraft(furnace, 1);

		// Act within assert
		assertDoesNotThrow(() -> {
			craftingSystem.craftItem();
		}, "Should not throw an exception if it craft a craftable item");

		// Assert
		HashMap<Item, Integer> expectedInventoryItems = new HashMap<Item, Integer>();

		expectedInventoryItems.put(stone, 12 - 8);
		expectedInventoryItems.put(woodCraftingTable, 2);
		expectedInventoryItems.put(furnace, 1);

		Inventory expectedInventory = new Inventory(expectedInventoryItems);
		Inventory receivedInventory = inventory;

		assertEquals(expectedInventory, receivedInventory,
				"Inventory should have the quantity of crafted item, minus used ingredients to craft it");
	}

	@Test
	void craftItems__NonCraftableItemException() {
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

		craftingSystem.setItemToCraft(furnace, 1);

		// Act within assert
		assertThrows(NonCraftableItemException.class, () -> craftingSystem.craftItem(),
				"Should throw `NonCraftableItemException` on try to craft 1 furnace without the required ingredients within inventory");
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
			craftingSystem.craftItem();

			craftingSystem.setItemToCraft(torch, 2);
			craftingSystem.craftItem();

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
	void undoLastCraft__ItemNotFoundException() {
		// Arrange
		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		// Act within assert
		assertThrows(ItemNotFoundException.class, () -> craftingSystem.undoLastCraft(),
				"Should throw `ItemNotFoundException` on try to undo the last crafted item without have crafted items");
	}
}
