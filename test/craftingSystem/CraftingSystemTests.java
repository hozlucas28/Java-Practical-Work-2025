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

		HashMap<Item, Integer> itemsToCraft = new HashMap<Item, Integer>();

		itemsToCraft.put(sword, 2);
		itemsToCraft.put(furnace, 1);

		craftingSystem.setItemsToCraft(itemsToCraft);

		// Act within assert
		assertDoesNotThrow(() -> craftingSystem.craftItems(),
				"Should not throw an exception if it craft craftable items");

		// Assert
		// @formatter:off
		List<CraftedItem> expected = List.of(
			new CraftedItem(sword, swordRecipe, 2),
			new CraftedItem(furnace, furnaceRecipe)
		);
		// @formatter:on

		List<CraftedItem> received = craftingSystem.getCraftedItems();

		assertTrue(expected.size() == received.size(),
				"List of expected and received crafted items should have the same length");

		for (int i = 0; i < expected.size(); i++) {
			CraftedItem expectedCraftedItem = expected.get(i);
			CraftedItem receivedCraftedItem = received.get(i);

			assertTrue(receivedCraftedItem.softEquals(expectedCraftedItem),
					"Each expected crafted item should match the received one");
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

		HashMap<Item, Integer> itemsToCraft = new HashMap<Item, Integer>();

		itemsToCraft.put(sword, 1);
		itemsToCraft.put(torch, 2);

		craftingSystem.setItemsToCraft(itemsToCraft);

		// Act
		HashMap<Item, Integer> craftableUnits = craftingSystem.getCraftableUnits();

		// Assert
		Map<Item, Integer> expected = Map.of(sword, 2, torch, 12);
		HashMap<Item, Integer> received = craftableUnits;

		assertEquals(expected, received, "Should return the correct craftable units for each item");
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

		HashMap<Item, Integer> itemsToCraft = new HashMap<Item, Integer>();

		itemsToCraft.put(itemToCraft01, 1);
		itemsToCraft.put(itemToCraft02, 5);

		craftingSystem.setItemsToCraft(itemsToCraft);

		assertDoesNotThrow(() -> {
			inventory.addItem(itemBase01, 1);
			inventory.addItem(itemBase02, 1);
			inventory.addItem(itemBase04, 4);
		}, "Should not throw an exception if it craft craftable items");

		// Act
		// @formatter:off
			Map<Item, Map<Recipe, List<Ingredient>>> expected = Map.of(
				itemToCraft01,
				Map.of(
					recipe01,
					List.of(
						new Ingredient(itemBase02, 1)
					)
				),
				itemToCraft02,
				Map.of(
					recipe02,
					List.of(
						new Ingredient(craftingTable, 1),
						new Ingredient(itemBase03, 3 * 2),
						new Ingredient(itemBase04, (5 * 2) - 4)
					)
				)
			);
			// @formatter:on

		HashMap<Item, HashMap<Recipe, List<Ingredient>>> received = craftingSystem.getMissingIngredients();

		// Assert
		assertEquals(expected, received, "Should return the missing ingredients to craft the requested ones");
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

		HashMap<Item, Integer> itemsToCraft = new HashMap<Item, Integer>();

		itemsToCraft.put(itemToCraft01, 1);
		itemsToCraft.put(itemToCraft02, 5);

		craftingSystem.setItemsToCraft(itemsToCraft);

		// Act
		// @formatter:off
		Map<Item, Map<Recipe, List<Ingredient>>> expected = Map.of(
			itemToCraft01,
			Map.of(
				recipe01,
				List.of(
					new Ingredient(item01, 1),
					new Ingredient(item02, 2)
				)
			),
			itemToCraft02,
			Map.of(
				recipe02,
				List.of(
					new Ingredient(craftingTable, 1),
					new Ingredient(item03, 3 * 2),
					new Ingredient(item04, 5 * 2)
				)
			)
		);
		// @formatter:on

		HashMap<Item, HashMap<Recipe, List<Ingredient>>> received = craftingSystem.getRequiredIngredients();

		// Assert
		assertEquals(expected, received, "Should return the required ingredients to craft the requested ones");
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

		HashMap<Item, Integer> itemsToCraft = new HashMap<Item, Integer>();

		itemsToCraft.put(sword, 2);

		craftingSystem.setItemsToCraft(itemsToCraft);

		// Act
		// @formatter:off
		Map<Item, Map<Recipe, List<Ingredient>>> expected = Map.of(
			sword,
			Map.of(
				swordRecipe,
				List.of(
					new Ingredient(woodCraftingTable, 1),
					new Ingredient(iron, 6),
					new Ingredient(wood, 22)
				)
			)
		);
		// @formatter:on

		HashMap<Item, HashMap<Recipe, List<Ingredient>>> received = craftingSystem.getRequiredBaseIngredients();

		// Assert
		assertEquals(expected, received, "Should return the required base ingredients to craft the requested ones");
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

		HashMap<Item, Integer> itemsToCraft = new HashMap<Item, Integer>();

		itemsToCraft.put(furnace, 1);

		craftingSystem.setItemsToCraft(itemsToCraft);

		// Act within assert
		assertTrue(craftingSystem.canCraft(), "Should be true on can craft requested items");
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

		HashMap<Item, Integer> itemsToCraft = new HashMap<Item, Integer>();

		itemsToCraft.put(furnace, 1);

		craftingSystem.setItemsToCraft(itemsToCraft);

		// Act within assert
		assertFalse(craftingSystem.canCraft(),
				"Should be false, if crafting system can not craft requested items because it missing ingredients within inventory");
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

		HashMap<Item, Integer> itemsToCraft = new HashMap<Item, Integer>();

		itemsToCraft.put(furnace, 1);

		craftingSystem.setItemsToCraft(itemsToCraft);

		// Act within assert
		assertDoesNotThrow(() -> craftingSystem.craftItems(), "craftItems() should not throw with valid items");

		// Assert
		HashMap<Item, Integer> expectedInventoryItems = new HashMap<Item, Integer>();

		expectedInventoryItems.put(stone, 12 - 8);
		expectedInventoryItems.put(woodCraftingTable, 2);
		expectedInventoryItems.put(furnace, 1);

		Inventory expectedInventory = new Inventory(expectedInventoryItems);
		Inventory receivedInventory = inventory;

		assertEquals(expectedInventory, receivedInventory,
				"Inventory should have the crafted items, minus used ingredients to craft those");
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

		HashMap<Item, Integer> itemsToCraft = new HashMap<Item, Integer>();

		itemsToCraft.put(furnace, 1);

		craftingSystem.setItemsToCraft(itemsToCraft);

		// Act within assert
		assertThrows(NonCraftableItemException.class, () -> craftingSystem.craftItems(),
				"Should throw `NonCraftableItemException` on try to craft an item without the required ingredients within inventory");
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

		inventoryItems.put(iron, 5);
		inventoryItems.put(coal, 3);
		inventoryItems.put(stick, 3);
		inventoryItems.put(woodCraftingTable, 2);

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		HashMap<Item, Integer> itemsToCraft = new HashMap<Item, Integer>();

		itemsToCraft.put(sword, 1);
		itemsToCraft.put(torch, 2);

		craftingSystem.setItemsToCraft(itemsToCraft);

		// Act within assert
		assertDoesNotThrow(() -> {
			craftingSystem.craftItems();
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
				"Inventory should have the ingredients of last crafted item, minus crafted one");
	}

	@Test
	void undoLastCraft__ItemNotFoundException() {
		// Arrange
		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		// Act within assert
		assertThrows(ItemNotFoundException.class, () -> craftingSystem.undoLastCraft(),
				"Should throw `ItemNotFoundException` on try to undo last crafted without crafted items");
	}
}
