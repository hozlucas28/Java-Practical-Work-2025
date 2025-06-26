package repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;

class ItemsRepositoryTests {

	@Test
	void getItem() {
		// Arrange
		Item diamond = new Item("diamond");
		Item shovel = new Item("shovel");
		Item meat = new Item("meat");
		Item refinedStone = new Item("refined stone");

		HashMap<String, Item> items = new HashMap<String, Item>();

		items.put(diamond.getName(), diamond);
		items.put(shovel.getName(), shovel);
		items.put(meat.getName(), meat);
		items.put(refinedStone.getName(), refinedStone);

		ItemsRepository itemsRepository = new ItemsRepository(items);

		// Act
		Item expected = meat;
		Item received = itemsRepository.getItem(meat.getName());

		// Assert
		assertEquals(expected, received, "Should return the meat item from the repository");
	}

	@Test
	void loadFromJSON() {
		// Arrange
		Item coal = new Item("coal");
		Item wood = new Item("wood");
		Item stick = new Item("stick");
		Item furnace = new Item("furnace");
		Item woodCraftingTable = new Item("wood crafting table");

		List<Ingredient> charcoalRecipeIngredients = List.of(new Ingredient(wood, 1));
		Recipe charcoalRecipe = new Recipe(furnace, charcoalRecipeIngredients, 1, 1600);

		Item charcoal = new Item("charcoal", List.of(charcoalRecipe));

		List<Ingredient> torchRecipe01Ingredients = List.of(new Ingredient(coal, 1), new Ingredient(stick, 1));
		Recipe torchRecipe01 = new Recipe(woodCraftingTable, torchRecipe01Ingredients, 3, 1300);

		List<Ingredient> torchRecipe02Ingredients = List.of(new Ingredient(charcoal, 1), new Ingredient(stick, 1));
		Recipe torchRecipe02 = new Recipe(torchRecipe02Ingredients, 3, 1400);

		Item torch = new Item("torch", List.of(torchRecipe01, torchRecipe02));

		HashMap<String, Item> repositoryItems = new HashMap<String, Item>();

		repositoryItems.put(coal.getName(), coal);
		repositoryItems.put(wood.getName(), wood);
		repositoryItems.put(stick.getName(), stick);
		repositoryItems.put(furnace.getName(), furnace);
		repositoryItems.put(woodCraftingTable.getName(), woodCraftingTable);

		repositoryItems.put(charcoal.getName(), charcoal);
		repositoryItems.put(torch.getName(), torch);

		// Act
		String jsonPath = Paths.get("test", "assets", "recipes.json").toString();
		ItemsRepository itemsRepository = assertDoesNotThrow(() -> ItemsRepository.loadFromJSON(jsonPath),
				"Should not throw an exception for a valid JSON file");

		// Assert
		ItemsRepository expected = new ItemsRepository(repositoryItems);
		ItemsRepository received = itemsRepository;

		assertEquals(expected, received, "Expected items and loaded one from the json file should be the same");
	}
}
