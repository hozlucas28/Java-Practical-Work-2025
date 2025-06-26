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
		Item gold = new Item("gold");

		// Act
		String expected = "gold";
		String received = gold.getName();

		// Assert
		assertEquals(expected, received, "Should return \"gold\" as name");
	}

	@Test
	void getRecipes() {
		// Arrange
		Item diamond = new Item("diamond");
		Item obsidian = new Item("obsidian");
		Item book = new Item("book");
		Item glass = new Item("glass");

		List<Ingredient> enchantingTableRecipe01Ingredients = List.of(new Ingredient(book, 1), new Ingredient(diamond, 2),
				new Ingredient(obsidian, 4));
		
		List<Ingredient> enchantingTableRecipe02Ingredients = List.of(new Ingredient(book, 1), new Ingredient(glass, 2),
				new Ingredient(diamond, 4));

		Recipe enchantingTableRecipe01 = new Recipe(enchantingTableRecipe01Ingredients, 1000, 1);
		Recipe enchantingTableRecipe02 = new Recipe(enchantingTableRecipe02Ingredients, 1250, 3);

		List<Recipe> enchantingTableRecipes = List.of(enchantingTableRecipe01, enchantingTableRecipe02);

		Item enchantingTable = new Item("enchanting table", enchantingTableRecipes);

		// Act
		List<Recipe> expected = enchantingTableRecipes;
		List<Recipe> received = enchantingTable.getRecipes();

		// Assert
		assertEquals(expected, received, "Should return enchanting table recipes");
	}

	@Test
	void isBase() {
		// Arrange
		Item iron = new Item("iron");

		// Act
		boolean received = iron.isBase();

		// Assert
		assertTrue(received, "Should return true if iron is a base item");
	}
}
