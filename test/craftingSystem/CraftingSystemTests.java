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
		Recipe furnaceRecipe = new Recipe(furnaceRecipeIngredients, 1250, 1, woodCraftingTable);

		Item furnace = new Item("furnace", List.of(furnaceRecipe));

		List<Ingredient> swordRecipeIngredients = List.of(new Ingredient(iron, 4), new Ingredient(stick, 2));
		Recipe swordRecipe = new Recipe(swordRecipeIngredients, 1750, 2, woodCraftingTable);

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
		assertDoesNotThrow(() -> craftingSystem.craftItems());

		// Assert
		// @formatter:off
		List<CraftedItem> expected = List.of(
			new CraftedItem(sword.getName(), sword.getRecipes(), swordRecipe, 2),
			new CraftedItem(furnace.getName(), furnace.getRecipes(), furnaceRecipe)
		);
		// @formatter:on

		List<CraftedItem> received = craftingSystem.getCraftedItems();

		assertTrue(expected.size() == received.size());

		for (int i = 0; i < expected.size(); i++) {
			CraftedItem expectedCraftedItem = expected.get(i);
			CraftedItem receivedCraftedItem = received.get(i);

			assertTrue(receivedCraftedItem.softEquals(expectedCraftedItem));
		}
	}

	// TODO: test getCraftableUnits()

	@Test
	void getMissingIngredients() {
		assertDoesNotThrow(() -> {
			// Arrange
			HashMap<Item, Integer> itemsInInventory = new HashMap<Item, Integer>();
			Inventory inventory = new Inventory(itemsInInventory);
			CraftingSystem craftingSystem = new CraftingSystem(inventory);

			Item itemBase01 = new Item("Item base 01");
			Item itemBase02 = new Item("Item base 02");
			Item itemBase03 = new Item("Item base 03");
			Item itemBase04 = new Item("Item base 04");

			Item craftingTable = new Item("Crating table");

			Ingredient ingredient01 = new Ingredient(itemBase01, 1);
			Ingredient ingredient02 = new Ingredient(itemBase02, 2);

			Ingredient ingredient03 = new Ingredient(itemBase03, 3);
			Ingredient ingredient04 = new Ingredient(itemBase04, 5);

			List<Ingredient> ingredientsRecipe01 = List.of(ingredient01, ingredient02);
			List<Ingredient> ingredientsRecipe02 = List.of(ingredient03, ingredient04);

			Recipe recipe01 = new Recipe(ingredientsRecipe01, 1000, 2);
			Recipe recipe02 = new Recipe(ingredientsRecipe02, 1250, 4, craftingTable);

			Item itemToCraft01 = new Item("Item A01", List.of(recipe01));
			Item itemToCraft02 = new Item("Item A02", List.of(recipe02));

			HashMap<Item, Integer> itemsToCraft = new HashMap<Item, Integer>();

			itemsToCraft.put(itemToCraft01, 1);
			itemsToCraft.put(itemToCraft02, 5);

			craftingSystem.setItemsToCraft(itemsToCraft);

			inventory.addItem(itemBase01, 1);
			inventory.addItem(itemBase02, 1);
			inventory.addItem(itemBase04, 4);

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
						new Ingredient(itemBase03, 3 * 2),
						new Ingredient(itemBase04, (5 * 2) - 4),
						new Ingredient(craftingTable, 1),
						new Ingredient(craftingTable, 1)
					)
				)
			);
			// @formatter:on

			HashMap<Item, HashMap<Recipe, List<Ingredient>>> received = craftingSystem.getMissingIngredients();

			// Assert
			assertEquals(expected, received);
		});
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
		Recipe recipe02 = new Recipe(ingredientsRecipe02, 1250, 4, craftingTable);

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
					new Ingredient(item03, 3 * 2),
					new Ingredient(item04, 5 * 2),
					new Ingredient(craftingTable, 1)
				)
			)
		);
		// @formatter:on

		HashMap<Item, HashMap<Recipe, List<Ingredient>>> received = craftingSystem.getRequiredIngredients();

		// Assert
		assertEquals(expected, received);
	}

	@Test
	void getRequiredBaseIngredients() {
		// Arrange
		Item wood = new Item("wood");
		Item iron = new Item("iron");

		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> stickRecipeIngredients = List.of(new Ingredient(wood, 2));
		Recipe stickRecipe = new Recipe(stickRecipeIngredients, 1400, 4, woodCraftingTable);
		List<Recipe> stickRecipes = List.of(stickRecipe);

		Item stick = new Item("stick", stickRecipes);

		List<Ingredient> swordRecipeIngredients = List.of(new Ingredient(stick, 1), new Ingredient(wood, 10),
				new Ingredient(iron, 3));
		Recipe swordRecipe = new Recipe(swordRecipeIngredients, 2100, 1, woodCraftingTable);
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
		assertEquals(expected, received);
	}

	@Test
	void canCraft__noMissingIngredients() {
		// Arrange
		Item stone = new Item("stone");
		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> furnaceRecipeIngredients = List.of(new Ingredient(stone, 8));
		Recipe furnaceRecipe = new Recipe(furnaceRecipeIngredients, 1250, 1, woodCraftingTable);

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
		assertTrue(craftingSystem.canCraft());
	}

	@Test
	void canCraft__missingIngredients() {
		// Arrange
		Item stone = new Item("stone");
		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> furnaceRecipeIngredients = List.of(new Ingredient(stone, 8));
		Recipe furnaceRecipe = new Recipe(furnaceRecipeIngredients, 1250, 1, woodCraftingTable);

		Item furnace = new Item("furnace", List.of(furnaceRecipe));

		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		inventoryItems.put(stone, 8);

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		HashMap<Item, Integer> itemsToCraft = new HashMap<Item, Integer>();

		itemsToCraft.put(furnace, 1);

		craftingSystem.setItemsToCraft(itemsToCraft);

		// Act within assert
		assertFalse(craftingSystem.canCraft());
	}

	@Test
	void craftItems() {
		// Arrange
		Item stone = new Item("stone");
		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> furnaceRecipeIngredients = List.of(new Ingredient(stone, 8));
		Recipe furnaceRecipe = new Recipe(furnaceRecipeIngredients, 1250, 1, woodCraftingTable);

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
		assertDoesNotThrow(() -> craftingSystem.craftItems());

		// Assert
		HashMap<Item, Integer> expectedInventoryItems = new HashMap<Item, Integer>();

		expectedInventoryItems.put(stone, 12 - 8);
		expectedInventoryItems.put(woodCraftingTable, 2);
		expectedInventoryItems.put(furnace, 1);

		Inventory expectedInventory = new Inventory(expectedInventoryItems);
		Inventory receivedInventory = inventory;

		assertEquals(expectedInventory, receivedInventory);
	}

	@Test
	void craftItems_NonCraftableItemException() {
		// Arrange
		Item stone = new Item("stone");
		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> furnaceRecipeIngredients = List.of(new Ingredient(stone, 8));
		Recipe furnaceRecipe = new Recipe(furnaceRecipeIngredients, 1250, 1, woodCraftingTable);

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
		assertThrows(NonCraftableItemException.class, () -> craftingSystem.craftItems());
	}

	@Test
	void undoLastCraft() {
		// Arrange
		Item iron = new Item("iron");
		Item coal = new Item("coal");
		Item stick = new Item("stick");

		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> swordRecipeIngredients = List.of(new Ingredient(iron, 2), new Ingredient(stick, 1));
		Recipe swordRecipe = new Recipe(swordRecipeIngredients, 2100, 1, woodCraftingTable);

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
		});

		// Assert
		HashMap<Item, Integer> expectedInventoryItems = new HashMap<Item, Integer>();

		expectedInventoryItems.put(sword, 1);

		expectedInventoryItems.put(iron, 3);
		expectedInventoryItems.put(coal, 3);
		expectedInventoryItems.put(stick, 2);
		expectedInventoryItems.put(woodCraftingTable, 2);

		Inventory expectedInventory = new Inventory(expectedInventoryItems);
		Inventory receivedInventory = inventory;

		assertEquals(expectedInventory, receivedInventory);
	}

	@Test
	void undoLastCraft_ItemNotFoundException() {
		// Arrange
		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		Inventory inventory = new Inventory(inventoryItems);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		// Act within assert
		assertThrows(ItemNotFoundException.class, () -> {
			craftingSystem.undoLastCraft();
		});
	}
}
