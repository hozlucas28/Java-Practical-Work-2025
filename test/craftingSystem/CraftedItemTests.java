package craftingSystem;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;

class CraftedItemTests {

	@Test
	void getDate() {
		assertDoesNotThrow(() -> {
			// Arrange
			Item item = new Item("My item A");

			Ingredient ingredient = new Ingredient(item, 1);
			List<Ingredient> ingredients = new ArrayList<Ingredient>();

			ingredients.add(ingredient);

			Recipe craftedItemRecipe = new Recipe(ingredients, 1000, 1);
			List<Recipe> craftedItemRecipes = new ArrayList<Recipe>();

			craftedItemRecipes.add(craftedItemRecipe);

			CraftedItem craftedItem = new CraftedItem("My item B", craftedItemRecipes, craftedItemRecipes.get(0));

			// Act
			ZonedDateTime expected = ZonedDateTime.now();
			ZonedDateTime received = craftedItem.getDate();

			// Assert
			assertEquals(0, ChronoUnit.SECONDS.between(expected, received));
		});
	}

	@Test
	void getUsedRecipe() {
		assertDoesNotThrow(() -> {
			// Arrange
			Item item = new Item("My item A");

			Ingredient ingredient = new Ingredient(item, 1);
			List<Ingredient> ingredients = new ArrayList<Ingredient>();

			ingredients.add(ingredient);

			Recipe craftedItemRecipe = new Recipe(ingredients, 1000, 1);
			List<Recipe> craftedItemRecipes = new ArrayList<Recipe>();

			craftedItemRecipes.add(craftedItemRecipe);

			CraftedItem craftedItem = new CraftedItem("My item B", craftedItemRecipes, craftedItemRecipes.get(0));

			// Act
			Recipe expected = craftedItemRecipes.get(0);
			Recipe received = craftedItem.getUsedRecipe();

			// Assert
			assertEquals(expected, received);
		});
	}
}
