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

	@Test
	void quantityCrafted() {
		// Arrange
		Item stick = new Item("stick");

		List<Ingredient> ingredients = List.of(new Ingredient(stick, 2));

		Recipe longStickRecipe = new Recipe(ingredients, 1900, 1);
		Item longStick = new Item("long stick", List.of(longStickRecipe));

		int quantityCrafted = 13;
		CraftedItem craftedLongStick = new CraftedItem(longStick, longStickRecipe, quantityCrafted);

		// Act within assert
		int expected = quantityCrafted;
		int received = craftedLongStick.getQuantityCrafted();

		assertEquals(expected, received, "Should return the number of crafted long sticks");
	}

	@Test
	void getCraftingTimeInMilliseconds() {
		// Arrange
		Item refinedWood = new Item("refined wood");

		List<Ingredient> ingredients = List.of(new Ingredient(refinedWood, 2));

		Recipe tableRecipe = new Recipe(ingredients, 2250, 1);
		Item table = new Item("long stick", List.of(tableRecipe));

		int quantityCrafted = 3;
		CraftedItem craftedTable = new CraftedItem(table, tableRecipe, quantityCrafted);

		// Act within assert
		int expected = (quantityCrafted / tableRecipe.getQuantityToCraft())
				* tableRecipe.getTimeToCraftInMilliseconds();
		int received = craftedTable.getCraftingTimeInMilliseconds();

		assertEquals(expected, received, "Should return the crafting time in milliseconds for craft 3 tables");
	}
}
