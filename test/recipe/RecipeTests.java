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
		int itemsToCraft = 2;

		Recipe recipe = new Recipe(ingredients, timeToCraftInMilliseconds, itemsToCraft, craftingTable);

		// Act
		Item expected = craftingTable;
		Item received = recipe.getCraftingTable().get();

		// Assert
		assertEquals(expected, received);
	}

	@Test
	void getIngredients() {
		// Arrange
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		int timeToCraftInMilliseconds = 1500;
		int itemsToCraft = 7;

		Recipe recipe = new Recipe(ingredients, timeToCraftInMilliseconds, itemsToCraft);

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
		int itemsToCraft = 1;

		Recipe recipe = new Recipe(ingredients, timeToCraftInMilliseconds, itemsToCraft);

		// Act
		int expected = timeToCraftInMilliseconds;
		int received = recipe.getTimeToCraftInMilliseconds();

		// Assert
		assertEquals(expected, received);
	}

	@Test
	void getItemsToCraft() {
		// Arrange
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		int timeToCraftInMilliseconds = 3000;
		int itemsToCraft = 5;

		Recipe recipe = new Recipe(ingredients, timeToCraftInMilliseconds, itemsToCraft);

		// Act
		int expected = itemsToCraft;
		int received = recipe.getItemsToCraft();

		// Assert
		assertEquals(expected, received);
	}

	// TODO: test getIngredientsToBase()
}
