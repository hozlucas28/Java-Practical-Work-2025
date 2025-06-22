package repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import exceptions.ItemNotFoundException;
import exceptions.OutOfRangeException;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;

class JSONRecipe {
	private HashMap<String, Integer> ingredients;
	private int timeToCraftInMilliseconds;
	private int itemsToCraft;
	private Optional<Item> craftingTable;

	public JSONRecipe(HashMap<String, Integer> ingredients, int timeToCraftInMilliseconds, int itemsToCraft) {
		this.ingredients = ingredients;
		this.timeToCraftInMilliseconds = timeToCraftInMilliseconds;
		this.itemsToCraft = itemsToCraft;
		this.craftingTable = Optional.empty();
	}

	public JSONRecipe(HashMap<String, Integer> ingredients, int timeInMilliseconds, int itemsToCraft,
			Item craftingTable) {
		this.ingredients = ingredients;
		this.timeToCraftInMilliseconds = timeInMilliseconds;
		this.itemsToCraft = itemsToCraft;
		this.craftingTable = Optional.ofNullable(craftingTable);
	}

	public static void linkItemsAndRecipes(HashMap<String, Item> items,
			HashMap<String, List<JSONRecipe>> recipesPerItem) throws ItemNotFoundException, OutOfRangeException {
		String errorMessage;

		// Map each item recipes (key)
		for (Map.Entry<String, List<JSONRecipe>> recipeEntry : recipesPerItem.entrySet()) {
			String itemName = recipeEntry.getKey();
			List<JSONRecipe> itemJSONRecipes = recipeEntry.getValue();

			Item item = items.get(itemName);
			if (item == null) {
				errorMessage = String.format("Item with \"%s\" name was not found inside the items hash map", itemName);
				throw new ItemNotFoundException(errorMessage);
			}

			List<Recipe> itemRecipes = new ArrayList<Recipe>();

			// Map each recipe (value)
			for (JSONRecipe jsonRecipe : itemJSONRecipes) {
				List<Ingredient> ingredients = new ArrayList<Ingredient>();

				// Migrate ingredients type
				for (Map.Entry<String, Integer> ingredientEntry : jsonRecipe.ingredients.entrySet()) {
					String ingredientName = ingredientEntry.getKey();
					int ingredientQuantity = ingredientEntry.getValue();

					Item ingredientItem = items.get(ingredientName);
					if (ingredientItem == null) {
						errorMessage = String.format(
								"Ingredient (item) with \"%s\" name was not found inside the items hash map",
								ingredientName);
						throw new ItemNotFoundException(errorMessage);
					}

					if (ingredientQuantity < 1) {
						errorMessage = String.format(
								"Received %d as ingredient quantity, but expect it greater than or equal to %d",
								ingredientQuantity, 1);
						throw new OutOfRangeException(errorMessage);
					}

					Ingredient ingredient = new Ingredient(ingredientItem, ingredientQuantity);
					ingredients.add(ingredient);
				}

				// Append recipe to item
				Recipe recipe = jsonRecipe.craftingTable.isPresent()
						? new Recipe(ingredients, jsonRecipe.timeToCraftInMilliseconds, jsonRecipe.itemsToCraft,
								jsonRecipe.craftingTable.get())
						: new Recipe(ingredients, jsonRecipe.timeToCraftInMilliseconds, jsonRecipe.itemsToCraft);

				itemRecipes.add(recipe);
			}

			Item itemWithRecipes = new Item(item.getName(), itemRecipes);
			items.replace(itemName, itemWithRecipes);
		}
	}
}
