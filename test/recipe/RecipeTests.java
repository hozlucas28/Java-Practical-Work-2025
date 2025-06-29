package recipe;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import inventory.Item;

class RecipeTests {

	@Test
	void getCraftingTable() {
		// Arrange
		Item craftingTable = new Item("Crafting table");

		Recipe recipe = new Recipe(craftingTable, List.of(), 1000, 2);

		// Act within assert
		Item expected = craftingTable;
		Item received = recipe.getCraftingTable();

		assertEquals(expected, received, "Should return the crafting table");
	}

	@Test
	void getIngredients() {
		// Arrange
		Item wood = new Item("wood");
		List<Ingredient> ingredients = List.of(new Ingredient(wood, 5));

		Recipe recipe = new Recipe(ingredients, 1500, 7);

		// Act within assert
		List<Ingredient> expected = ingredients;
		List<Ingredient> received = recipe.getIngredients();

		assertEquals(expected, received, "Should return the list of ingredients");
	}

	@Test
	void getTimeToCraftInMilliseconds() {
		// Arrange
		int timeToCraftInMilliseconds = 2000;

		Recipe recipe = new Recipe(List.of(), timeToCraftInMilliseconds, 1);

		// Act within assert
		int expected = timeToCraftInMilliseconds;
		int received = recipe.getTimeToCraftInMilliseconds();

		assertEquals(expected, received, "Should return the crafting time in milliseconds");
	}

	@Test
	void getQuantityToCraft() {
		// Arrange
		int quantityToCraft = 5;

		Recipe recipe = new Recipe(List.of(), 3000, quantityToCraft);

		// Act within assert
		int expected = quantityToCraft;
		int received = recipe.getQuantityToCraft();

		assertEquals(expected, received, "Should return the quantity to craft");
	}

	@Test
	void getBaseIngredients() {
		// Arrange
		Item wood = new Item("wood");
		Item iron = new Item("iron");

		List<Ingredient> stickRecipeIngredients = List.of(new Ingredient(wood, 2));
		Recipe stickRecipe = new Recipe(stickRecipeIngredients, 1400, 1);
		List<Recipe> stickRecipes = List.of(stickRecipe);

		Item stick = new Item("stick", stickRecipes);

		List<Ingredient> swordRecipeIngredients = List.of(new Ingredient(stick, 1), new Ingredient(iron, 3));
		Recipe swordRecipe = new Recipe(swordRecipeIngredients, 2100, 1);

		// Act
		List<Ingredient> baseIngredients = swordRecipe.getBaseIngredients();

		// Assert
		List<Ingredient> expected = List.of(new Ingredient(wood, 2), new Ingredient(iron, 3));
		int expectedSize = expected.size();

		List<Ingredient> received = baseIngredients;
		int receivedSize = received.size();

		assertTrue(receivedSize == expectedSize,
				"List of expected and received base ingredients should have the same length");
		assertTrue(received.containsAll(expected),
				"Received base ingredients should contain all expected base ingredients");
	}
}
