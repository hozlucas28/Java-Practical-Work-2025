package inventory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import recipe.Ingredient;
import recipe.Recipe;

class ItemTests {

	@Test
	void getId() {
		// Arrange
		String itemId = "A";
		String itemName = "My item";

		Item item = new Item(itemId, itemName);

		// Act
		String expected = itemId;
		String received = item.getId();

		// Assert
		assertEquals(expected, received);
	}

	@Test
	void getName() {
		// Arrange
		String itemId = "A";
		String itemName = "My item";

		Item item = new Item(itemId, itemName);

		// Act
		String expected = itemName;
		String received = item.getName();

		// Assert
		assertEquals(expected, received);
	}

	@Test
	void getRecipes() {
		assertDoesNotThrow(() -> {
			// Arrange
			Item item01 = new Item("A01", "My item 01");
			Item item02 = new Item("A02", "My item 02");
			Item item03 = new Item("A03", "My item 03");
			Item item04 = new Item("A04", "My item 04");

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

			Item item = new Item("B", "My item", recipes);

			// Act
			List<Recipe> expected = recipes;
			List<Recipe> received = item.getRecipes();

			// Assert
			assertEquals(expected, received);
		});
	}

	// TODO: test getCraftableRecipes()

	@Test
	void isBase() {
		// Arrange
		String itemId = "A";
		String itemName = "My item";

		Item item = new Item(itemId, itemName);

		// Act
		boolean received = item.isBase();

		// Assert
		assertTrue(received);
	}
}
