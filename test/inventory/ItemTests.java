package inventory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import recipe.Ingredient;
import recipe.Recipe;

class ItemTests {

	@Test
	void getName() {
		// Arrange
		String itemName = "My item";

		Item item = new Item(itemName);

		// Act
		String expected = itemName;
		String received = item.getName();

		// Assert
		assertEquals(expected, received, "Should return the item name");
	}

	@Test
	void getRecipes() {
		// Arrange
		Item item01 = new Item("My item A01");
		Item item02 = new Item("My item A02");
		Item item03 = new Item("My item A03");
		Item item04 = new Item("My item A04");

		Ingredient ingredient01 = new Ingredient(item01, 1);
		Ingredient ingredient02 = new Ingredient(item02, 3);
		Ingredient ingredient03 = new Ingredient(item03, 7);
		Ingredient ingredient04 = new Ingredient(item04, 5);

		List<Ingredient> ingredients01 = new ArrayList<Ingredient>();
		List<Ingredient> ingredients02 = new ArrayList<Ingredient>();

		ingredients01.add(ingredient01);
		ingredients01.add(ingredient02);

		ingredients02.add(ingredient03);
		ingredients02.add(ingredient04);

		Recipe recipe01 = new Recipe(ingredients01, 1000, 1);
		Recipe recipe02 = new Recipe(ingredients02, 1250, 3);

		List<Recipe> recipes = new ArrayList<Recipe>();

		recipes.add(recipe01);
		recipes.add(recipe02);

		Item item = new Item("My item B", recipes);

		// Act
		List<Recipe> expected = recipes;
		List<Recipe> received = item.getRecipes();

		// Assert
		assertEquals(expected, received, "Should return item recipes");
	}

	@Test
	void isBase() {
		// Arrange
		String itemName = "My item";

		Item item = new Item(itemName);

		// Act
		boolean received = item.isBase();

		// Assert
		assertTrue(received, "Should return true if it is a base item");
	}
}
