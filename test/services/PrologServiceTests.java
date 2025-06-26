package services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import inventory.Inventory;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;
import repositories.ItemsRepository;

class PrologServiceTests {

	@Test
	void craftableItems() {
		// Arrange
		Item wood = new Item("wood");
		Item iron = new Item("iron");
		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> stickRecipeIngredients = List.of(new Ingredient(wood, 2));
		Recipe stickRecipe = new Recipe(stickRecipeIngredients, 1400, 1);
		List<Recipe> stickRecipes = List.of(stickRecipe);

		Item stick = new Item("stick", stickRecipes);

		List<Ingredient> swordRecipeIngredients = List.of(new Ingredient(stick, 1), new Ingredient(iron, 3));
		Recipe swordRecipe = new Recipe(woodCraftingTable, swordRecipeIngredients, 2100, 1);
		List<Recipe> swordRecipes = List.of(swordRecipe);

		Item sword = new Item("sword", swordRecipes);

		HashMap<String, Item> repositoryItems = new HashMap<String, Item>();

		repositoryItems.put(wood.getName(), wood);
		repositoryItems.put(iron.getName(), iron);
		repositoryItems.put(woodCraftingTable.getName(), woodCraftingTable);

		repositoryItems.put(stick.getName(), stick);
		repositoryItems.put(sword.getName(), sword);

		ItemsRepository itemsRepository = new ItemsRepository(repositoryItems);

		HashMap<Item, Integer> inventoryItems = new HashMap<Item, Integer>();

		inventoryItems.put(wood, 2);
		inventoryItems.put(iron, 6);
		inventoryItems.put(woodCraftingTable, 1);

		Inventory inventory = new Inventory(inventoryItems);

		PrologServiceBuilder prologServiceBuilder = new PrologServiceBuilder();

		PrologService prologService = prologServiceBuilder.setBaseItemFactName("base_item")
				.setIngredientFactName("ingredient").setItemInInventoryFactName("have")
				.setItemsRepository(itemsRepository).setInventory(inventory).build();

		// Act
		HashMap<Item, Integer> craftableItems = assertDoesNotThrow(() -> prologService.craftableItems());

		// Assert
		HashMap<Item, Integer> expected = new HashMap<Item, Integer>();

		expected.put(stick, 1);
		expected.put(sword, 1);

		HashMap<Item, Integer> received = craftableItems;

		assertEquals(expected, received);
	}
}
