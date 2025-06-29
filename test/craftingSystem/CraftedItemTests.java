package craftingSystem;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;

import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;

class CraftedItemTests {

	@Test
	void getDate() {
		// Arrange
		Item woodenBlocks = new Item("wooden blocks");

		List<Ingredient> stairIngredients = List.of(new Ingredient(woodenBlocks, 1));

		Recipe craftedStairRecipe = new Recipe(stairIngredients, 1000, 1);
		List<Recipe> stairRecipes = List.of(craftedStairRecipe);

		Item stair = new Item("stair", stairRecipes);

		CraftedItem craftedStair = new CraftedItem(stair, craftedStairRecipe);

		// Act within assert
		ZonedDateTime expected = ZonedDateTime.now();
		ZonedDateTime received = craftedStair.getDate();

		assertEquals(0, ChronoUnit.SECONDS.between(expected, received), "Should return the current time");
	}

	@Test
	void getUsedRecipe() {
		// Arrange
		Item iron = new Item("iron");
		Item stick = new Item("stick");

		List<Ingredient> ingredients = List.of(new Ingredient(iron, 2), new Ingredient(stick, 1));

		Recipe craftedKnifeRecipe = new Recipe(ingredients, 1365, 1);
		List<Recipe> knifeRecipes = List.of(craftedKnifeRecipe);

		Item knife = new Item("knife", knifeRecipes);

		CraftedItem craftedKnife = new CraftedItem(knife, craftedKnifeRecipe);

		// Act within assert
		Recipe expected = knifeRecipes.get(0);
		Recipe received = craftedKnife.getUsedRecipe();

		assertEquals(expected, received, "Should return the recipe used to craft the knife");
	}
}
