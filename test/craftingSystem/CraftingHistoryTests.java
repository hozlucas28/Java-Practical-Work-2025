package craftingSystem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import exceptions.ThereAreNoCraftedItemsException;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;

class CraftingHistoryTests {

	@Test
	void addItems() {
		assertDoesNotThrow(() -> {
			// Arrange
			Item item01 = new Item("My item A01");
			Item item02 = new Item("My item A02");

			Ingredient ingredient01 = new Ingredient(item01, 5);
			Ingredient ingredient02 = new Ingredient(item02, 2);

			List<Ingredient> ingredients01 = new ArrayList<Ingredient>();
			List<Ingredient> ingredients02 = new ArrayList<Ingredient>();

			ingredients01.add(ingredient01);
			ingredients02.add(ingredient02);

			Recipe recipeItem03 = new Recipe(ingredients01, 2250, 3);
			Recipe recipeItem04 = new Recipe(ingredients02, 1500, 1);

			List<Recipe> recipesCraftedItem01 = new ArrayList<Recipe>();
			List<Recipe> recipesCraftedItem02 = new ArrayList<Recipe>();

			recipesCraftedItem01.add(recipeItem03);
			recipesCraftedItem02.add(recipeItem04);

			Item craftedItem01 = new Item("My item B01", recipesCraftedItem01);
			Item craftedItem02 = new Item("My item B02", recipesCraftedItem02);

			CraftingHistory craftingHistory = new CraftingHistory();

			Recipe usedRecipeCraftedItem01 = recipesCraftedItem01.get(0);
			Recipe usedRecipeCraftedItem02 = recipesCraftedItem02.get(0);

			craftingHistory.addItem(craftedItem01, usedRecipeCraftedItem01);
			craftingHistory.addItem(craftedItem02, usedRecipeCraftedItem02);

			// Act
			List<CraftedItem> expected = new ArrayList<CraftedItem>();

			expected.add(new CraftedItem(craftedItem01.getName(), craftedItem01.getRecipes(),
					usedRecipeCraftedItem01));

			expected.add(new CraftedItem(craftedItem02.getName(), craftedItem02.getRecipes(),
					usedRecipeCraftedItem02));

			List<CraftedItem> received = craftingHistory.getItems();

			// Assert
			assertEquals(expected, received);
		});
	}

	@Test
	void removeLastItem() {
		assertDoesNotThrow(() -> {
			// Arrange
			Item item01 = new Item("My item A01");
			Item item02 = new Item("My item A02");

			Ingredient ingredient01 = new Ingredient(item01, 5);
			Ingredient ingredient02 = new Ingredient(item02, 2);

			List<Ingredient> ingredients01 = new ArrayList<Ingredient>();
			List<Ingredient> ingredients02 = new ArrayList<Ingredient>();

			ingredients01.add(ingredient01);
			ingredients02.add(ingredient02);

			Recipe recipeItem03 = new Recipe(ingredients01, 2250, 3);
			Recipe recipeItem04 = new Recipe(ingredients02, 1500, 1);

			List<Recipe> recipesCraftedItem01 = new ArrayList<Recipe>();
			List<Recipe> recipesCraftedItem02 = new ArrayList<Recipe>();

			recipesCraftedItem01.add(recipeItem03);
			recipesCraftedItem02.add(recipeItem04);

			Item craftedItem01 = new Item("My item B01", recipesCraftedItem01);
			Item craftedItem02 = new Item("My item B02", recipesCraftedItem02);

			CraftingHistory craftingHistory = new CraftingHistory();

			Recipe usedRecipeCraftedItem01 = recipesCraftedItem01.get(0);
			Recipe usedRecipeCraftedItem02 = recipesCraftedItem02.get(0);

			craftingHistory.addItem(craftedItem01, usedRecipeCraftedItem01);
			craftingHistory.addItem(craftedItem02, usedRecipeCraftedItem02);

			// Act
			CraftedItem expected = new CraftedItem(craftedItem02.getName(),
					craftedItem02.getRecipes(), usedRecipeCraftedItem02);
			CraftedItem received = craftingHistory.removeLastItem();

			// Assert
			assertEquals(expected, received);
		});
	}

	@Test
	void removeLastItem_ThereAreNoCraftedItemsException() {
		// Assert
		assertThrows(ThereAreNoCraftedItemsException.class, () -> {
			// Arrange
			CraftingHistory craftingHistory = new CraftingHistory();

			// Act
			craftingHistory.removeLastItem();
		});
	}
}
