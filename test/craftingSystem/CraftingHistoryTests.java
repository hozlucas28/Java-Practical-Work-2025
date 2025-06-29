package craftingSystem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import exceptions.EmptyHistoryException;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;

class CraftingHistoryTests {

	@Test
	void addItems() {
		// Arrange
		Item stone = new Item("stone");
		Item stick = new Item("stick");

		Recipe refinedStoneCraftedRecipe = new Recipe(List.of(new Ingredient(stone, 5)), 2250, 3);
		Recipe sturdyWoodCraftedRecipe = new Recipe(List.of(new Ingredient(stick, 2)), 1500, 1);

		Item refinedStone = new Item("refined stone", List.of(refinedStoneCraftedRecipe));
		Item sturdyWood = new Item("sturdy wood", List.of(sturdyWoodCraftedRecipe));

		CraftingHistory craftingHistory = new CraftingHistory();

		craftingHistory.addItem(refinedStone, refinedStoneCraftedRecipe, 1);
		craftingHistory.addItem(sturdyWood, sturdyWoodCraftedRecipe, 1);

		// Act
		List<CraftedItem> expected = List.of(new CraftedItem(sturdyWood, refinedStoneCraftedRecipe),
				new CraftedItem(refinedStone, sturdyWoodCraftedRecipe));

		List<CraftedItem> received = craftingHistory.getItems();

		// Assert
		assertEquals(expected, received, "Should add crafted items (refined stone and sturdy wood) to the crafting history");
	}

	@Test
	void removeLastItem() {
		// Arrange
		Item stone = new Item("stone");
		Item stick = new Item("stick");

		Recipe refinedStoneCraftedRecipe = new Recipe(List.of(new Ingredient(stone, 5)), 2250, 3);
		Recipe sturdyWoodCraftedRecipe = new Recipe(List.of(new Ingredient(stick, 2)), 1500, 1);

		Item refinedStone = new Item("refined stone", List.of(refinedStoneCraftedRecipe));
		Item sturdyWood = new Item("sturdy wood", List.of(sturdyWoodCraftedRecipe));

		CraftingHistory craftingHistory = new CraftingHistory();

		craftingHistory.addItem(refinedStone, refinedStoneCraftedRecipe, 1);
		craftingHistory.addItem(sturdyWood, sturdyWoodCraftedRecipe, 1);

		// Act
		CraftedItem expected = new CraftedItem(sturdyWood, sturdyWoodCraftedRecipe);
		CraftedItem received = assertDoesNotThrow(() -> craftingHistory.removeLastItem());

		// Assert
		assertEquals(expected, received, "Should remove and return the last crafted item (sturdy wood)");
	}

	@Test
	void removeLastItem__EmptyHistoryException() {
		// Arrange
		CraftingHistory craftingHistory = new CraftingHistory();

		// Act within assert
		assertThrows(EmptyHistoryException.class, () -> craftingHistory.removeLastItem(),
				"Should throw `EmptyHistoryException` when the crafting history is empty");
	}
}
