package recipe;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import inventory.Item;

class IngredientTests {

	@Test
	void getItem() {
		// Arrange
		Item item = new Item("My item A");
		int itemQuantity = 2;

		Ingredient ingredient = new Ingredient(item, itemQuantity);

		// Act
		Item expected = item;
		Item received = ingredient.getItem();

		// Assert
		assertEquals(expected, received);
	}

	@Test
	void getQuantity() {
		// Arrange
		Item item = new Item("My item A");
		int itemQuantity = 5;

		Ingredient ingredient = new Ingredient(item, itemQuantity);

		// Act
		int expected = itemQuantity;
		int received = ingredient.getQuantity();

		// Assert
		assertEquals(expected, received);
	}
}
