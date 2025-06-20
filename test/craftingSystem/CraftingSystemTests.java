package craftingSystem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import inventory.Inventory;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;

class CraftingSystemTests {

	// TODO: test getCraftableUnits()

	@Test
	void getRequiredIngredients() {
		assertDoesNotThrow(() -> {
			// Arrange
			HashMap<Item, Integer> itemsInInventory = new HashMap<Item, Integer>();
			Inventory inventory = new Inventory(itemsInInventory);
			CraftingSystem craftingSystem = new CraftingSystem(inventory);

			Item craftingTable = new Item("Crafting table A");

			Item itemBase01 = new Item("Item base 01");
			Item itemBase02 = new Item("Item base 02");
			Item itemBase03 = new Item("Item base 03");
			Item itemBase04 = new Item("Item base 04");

			Ingredient ingredient01 = new Ingredient(itemBase01, 1);
			Ingredient ingredient02 = new Ingredient(itemBase02, 2);

			Ingredient ingredient03 = new Ingredient(itemBase03, 3);
			Ingredient ingredient04 = new Ingredient(itemBase04, 5);

			List<Ingredient> ingredientsRecipe01 = new ArrayList<Ingredient>();

			ingredientsRecipe01.add(ingredient01);
			ingredientsRecipe01.add(ingredient02);

			List<Ingredient> ingredientsRecipe02 = new ArrayList<Ingredient>();

			ingredientsRecipe02.add(ingredient03);
			ingredientsRecipe02.add(ingredient04);

			Recipe recipe01 = new Recipe(ingredientsRecipe01, 1000, 2);
			Recipe recipe02 = new Recipe(ingredientsRecipe02, 1250, 4, craftingTable);

			List<Recipe> recipes01 = new ArrayList<Recipe>();

			recipes01.add(recipe01);

			List<Recipe> recipes02 = new ArrayList<Recipe>();

			recipes02.add(recipe02);

			Item itemToCraft01 = new Item("Item A01", recipes01);
			Item itemToCraft02 = new Item("Item A02", recipes02);

			List<Item> itemsToCraft = new ArrayList<Item>();

			itemsToCraft.add(itemToCraft01);
			itemsToCraft.add(itemToCraft02);

			craftingSystem.setItemsToCraft(itemsToCraft);

			// Act
			HashMap<Item, List<List<Ingredient>>> expected = new HashMap<Item, List<List<Ingredient>>>();

			List<List<Ingredient>> requiredIngredients01 = new ArrayList<List<Ingredient>>();

			requiredIngredients01.add(ingredientsRecipe01);

			List<List<Ingredient>> requiredIngredients02 = new ArrayList<List<Ingredient>>();

			List<Ingredient> expectedIngredientsRecipe02 = new ArrayList<Ingredient>();

			expectedIngredientsRecipe02.add(new Ingredient(craftingTable, 1));
			expectedIngredientsRecipe02.addAll(ingredientsRecipe02);

			requiredIngredients02.add(expectedIngredientsRecipe02);

			expected.put(itemToCraft01, requiredIngredients01);
			expected.put(itemToCraft02, requiredIngredients02);

			HashMap<Item, List<List<Ingredient>>> received = craftingSystem.getRequiredIngredients();

			// Assert
			assertEquals(expected, received);
		});
	}

	// TODO: test getRequiredBaseIngredients()
	// TODO: test getCraftedItems()
	// TODO: test canCraft()
	// TODO: test craftItems()
	// TODO: test undoLastCraft()
}
