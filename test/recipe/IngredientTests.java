package recipe;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import inventory.Item;

class IngredientTests {

	@Test
	void getItem() {
		// Arrange
		Item glass = new Item("glass");
		Ingredient ingredient = new Ingredient(glass, 2);

		// Act within assert
		Item expected = glass;
		Item received = ingredient.getItem();

		assertEquals(expected, received, "Should return the necessary glass item for the ingredient");
	}

	@Test
	void getQuantity() {
		// Arrange
		Item stick = new Item("stick");
		int stickQuantity = 5;

		Ingredient ingredient = new Ingredient(stick, stickQuantity);

		// Act within assert
		int expected = stickQuantity;
		int received = ingredient.getQuantity();

		assertEquals(expected, received, "Should return ingredient quantity");
	}
}
