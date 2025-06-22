package recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import inventory.Item;

public class Recipe {
	private final List<Ingredient> ingredients;
	private final int timeToCraftInMilliseconds;
	private final int itemsToCraft;
	private final Optional<Item> craftingTable;

	public Recipe(List<Ingredient> ingredients, int timeToCraftInMilliseconds, int itemsToCraft) {
		this.ingredients = ingredients;
		this.timeToCraftInMilliseconds = timeToCraftInMilliseconds;
		this.itemsToCraft = itemsToCraft;
		this.craftingTable = Optional.empty();
	}

	public Recipe(List<Ingredient> ingredients, int timeInMilliseconds, int itemsToCraft, Item craftingTable) {
		this.ingredients = ingredients;
		this.timeToCraftInMilliseconds = timeInMilliseconds;
		this.itemsToCraft = itemsToCraft;
		this.craftingTable = Optional.ofNullable(craftingTable);
	}

	public List<Ingredient> getIngredients() {
		return Collections.unmodifiableList(this.ingredients);
	}

	public int getTimeToCraftInMilliseconds() {
		return this.timeToCraftInMilliseconds;
	}

	public int getItemsToCraft() {
		return this.itemsToCraft;
	}

	public Optional<Item> getCraftingTable() {
		return this.craftingTable;
	}

	public List<Ingredient> getBaseIngredients() {
		HashMap<Ingredient, Integer> baseIngredientsMap = new HashMap<Ingredient, Integer>();

		for (Ingredient ingredient : this.ingredients) {
			Item item = ingredient.getItem();
			List<Recipe> ingredientRecipes = item.getRecipes();

			if (item.isBase()) {
				baseIngredientsMap.put(ingredient, baseIngredientsMap.getOrDefault(ingredient, 0) + 1);
			}

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
