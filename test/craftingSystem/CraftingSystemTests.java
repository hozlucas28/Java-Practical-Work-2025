package craftingSystem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import inventory.Inventory;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;

class CraftingSystemTests {

	// TODO: test getCraftedItems()
	// TODO: test getCraftableUnits()
	// TODO: test getMissingIngredients() X
	// TODO: test getMissingBaseIngredients() X

	@Test
	void getRequiredIngredients() {
		// Arrange
		HashMap<Item, Integer> itemsInInventory = new HashMap<Item, Integer>();
		Inventory inventory = new Inventory(itemsInInventory);
		CraftingSystem craftingSystem = new CraftingSystem(inventory);

		Item itemBase01 = new Item("Item base 01");
		Item itemBase02 = new Item("Item base 02");
		Item itemBase03 = new Item("Item base 03");
		Item itemBase04 = new Item("Item base 04");

		Ingredient ingredient01 = new Ingredient(itemBase01, 1);
		Ingredient ingredient02 = new Ingredient(itemBase02, 2);

		Ingredient ingredient03 = new Ingredient(itemBase03, 3);
		Ingredient ingredient04 = new Ingredient(itemBase04, 5);

		List<Ingredient> ingredientsRecipe01 = List.of(ingredient01, ingredient02);
		List<Ingredient> ingredientsRecipe02 = List.of(ingredient03, ingredient04);

		Recipe recipe01 = new Recipe(ingredientsRecipe01, 1000, 2);
		Recipe recipe02 = new Recipe(ingredientsRecipe02, 1250, 4);

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
				List.of(new Ingredient(itemBase01, 1), new Ingredient(itemBase02, 2))
			),
			itemToCraft02,
			Map.of(
				recipe02,
				List.of(new Ingredient(itemBase03, 3 * 2), new Ingredient(itemBase04, 5 * 2))
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

		List<Ingredient> stickRecipeIngredients = List.of(new Ingredient(wood, 2));
		Recipe stickRecipe = new Recipe(stickRecipeIngredients, 1400, 4);
		List<Recipe> stickRecipes = List.of(stickRecipe);

		Item stick = new Item("stick", stickRecipes);

		List<Ingredient> swordRecipeIngredients = List.of(new Ingredient(stick, 1), new Ingredient(wood, 10), new Ingredient(iron, 3));
		Recipe swordRecipe = new Recipe(swordRecipeIngredients, 2100, 1);
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

	// TODO: test canCraft()
	// TODO: test craftItems()
	// TODO: test undoLastCraft()
}
