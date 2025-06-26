package recipe;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import inventory.Item;

class RecipeTests {

	@Test
	void getCraftingTable() {
		// Arrange
		Item craftingTable = new Item("Crafting table");
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		int timeToCraftInMilliseconds = 1000;
		int quantityToCraft = 2;

		Recipe recipe = new Recipe(craftingTable, ingredients, timeToCraftInMilliseconds, quantityToCraft);

		// Act
		Item expected = craftingTable;
		Item received = recipe.getCraftingTable();

		// Assert
		assertEquals(expected, received);
	}

	@Test
	void getIngredients() {
		// Arrange
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		int timeToCraftInMilliseconds = 1500;
		int quantityToCraft = 7;

		Recipe recipe = new Recipe(ingredients, timeToCraftInMilliseconds, quantityToCraft);

		// Act
		List<Ingredient> expected = ingredients;
		List<Ingredient> received = recipe.getIngredients();

		// Assert
		assertEquals(expected, received);
	}

	@Test
	void getTimeToCraftInMilliseconds() {
		// Arrange
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		int timeToCraftInMilliseconds = 2000;
		int quantityToCraft = 1;

		Recipe recipe = new Recipe(ingredients, timeToCraftInMilliseconds, quantityToCraft);

		// Act
		int expected = timeToCraftInMilliseconds;
		int received = recipe.getTimeToCraftInMilliseconds();

		// Assert
		assertEquals(expected, received);
	}

	@Test
	void getQuantityToCraft() {
		// Arrange
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		int timeToCraftInMilliseconds = 3000;
		int quantityToCraft = 5;

		Recipe recipe = new Recipe(ingredients, timeToCraftInMilliseconds, quantityToCraft);

		// Act
		int expected = quantityToCraft;
		int received = recipe.getQuantityToCraft();

		// Assert
		assertEquals(expected, received);
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
		List<Ingredient> received = baseIngredients;

		assertTrue(received.size() == expected.size());
		assertTrue(received.containsAll(expected));
	}
}
