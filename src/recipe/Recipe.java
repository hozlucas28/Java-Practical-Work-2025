package recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inventory.Item;

public class Recipe {
	private final Item craftingTable;
	private final List<Ingredient> ingredients;
	private final int timeToCraftInMilliseconds;
	private final int quantityToCraft;

	public Recipe(List<Ingredient> ingredients, int timeToCraftInMilliseconds, int quantityToCraft) {
		this.craftingTable = null;
		this.ingredients = ingredients;
		this.timeToCraftInMilliseconds = timeToCraftInMilliseconds;
		this.quantityToCraft = quantityToCraft;
	}

	public Recipe(Item craftingTable, List<Ingredient> ingredients, int timeInMilliseconds, int quantityToCraft) {
		this.craftingTable = craftingTable;
		this.ingredients = ingredients;
		this.timeToCraftInMilliseconds = timeInMilliseconds;
		this.quantityToCraft = quantityToCraft;
	}

	public List<Ingredient> getIngredients() {
		return Collections.unmodifiableList(this.ingredients);
	}

	public int getTimeToCraftInMilliseconds() {
		return this.timeToCraftInMilliseconds;
	}

	public int getQuantityToCraft() {
		return this.quantityToCraft;
	}

	public Item getCraftingTable() {
		return this.craftingTable;
	}

	public boolean needsCraftingTable() {
		return this.craftingTable != null;
	}

	public List<Ingredient> getBaseIngredients() {
		HashMap<Ingredient, Integer> baseIngredientsMap = new HashMap<Ingredient, Integer>();

		for (Ingredient ingredient : this.ingredients) {
			Item item = ingredient.getItem();

			if (item.isBase()) {
				baseIngredientsMap.put(ingredient, baseIngredientsMap.getOrDefault(ingredient, 0) + 1);
				break;
			}

			List<Recipe> ingredientRecipes = item.getRecipes();

			for (Recipe itemRecipe : ingredientRecipes) {
				List<Ingredient> baseIngredients = itemRecipe.getBaseIngredients();

				for (Ingredient baseIngredient : baseIngredients) {
					baseIngredientsMap.put(baseIngredient, baseIngredientsMap.getOrDefault(baseIngredient, 0) + 1);
				}
			}
		}

		List<Ingredient> baseIngredients = new ArrayList<Ingredient>();

		for (Map.Entry<Ingredient, Integer> entry : baseIngredientsMap.entrySet()) {
			Ingredient baseIngredient = entry.getKey();
			int realBaseIngredientQuantity = baseIngredient.getQuantity() * entry.getValue();

			Ingredient realBaseIngredient = new Ingredient(baseIngredient.getItem(), realBaseIngredientQuantity);

			baseIngredients.add(realBaseIngredient);
		}

		return baseIngredients;
	}
}
