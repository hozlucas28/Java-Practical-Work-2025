package recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inventory.Item;

public class Recipe {
	private Item craftingTable;
	private List<Ingredient> ingredients;
	private final int timeToCraftInMilliseconds;
	private final int quantityToCraft;

	/**
	 * Constructs a Recipe with the specified ingredients, crafting time, and
	 * quantity.
	 *
	 * @param ingredients               list of ingredients
	 * @param timeToCraftInMilliseconds time required to craft, in milliseconds
	 * @param quantityToCraft           quantity produced when it is crafted
	 */
	public Recipe(List<Ingredient> ingredients, int timeToCraftInMilliseconds, int quantityToCraft) {
		this.ingredients = ingredients;
		this.timeToCraftInMilliseconds = timeToCraftInMilliseconds;
		this.quantityToCraft = quantityToCraft;
	}

	/**
	 * Constructs a Recipe with a crafting table, ingredients, crafting time, and
	 * quantity.
	 *
	 * @param craftingTable      crafting table required
	 * @param ingredients        list of ingredients required
	 * @param timeInMilliseconds time required to craft, in milliseconds
	 * @param quantityToCraft    quantity produced when it is crafted
	 */
	public Recipe(Item craftingTable, List<Ingredient> ingredients, int timeInMilliseconds, int quantityToCraft) {
		this.craftingTable = craftingTable;
		this.ingredients = ingredients;
		this.timeToCraftInMilliseconds = timeInMilliseconds;
		this.quantityToCraft = quantityToCraft;
	}

	/**
	 * @return the crafting table for this recipe, or null if not required.
	 */
	public Item getCraftingTable() {
		return this.craftingTable;
	}

	/**
	 *
	 * @return an unmodifiable {@link List} of the recipe ingredients
	 */
	public List<Ingredient> getIngredients() {
		return Collections.unmodifiableList(this.ingredients);
	}

	/**
	 * @return the crafting time in milliseconds to craft this recipe
	 */
	public int getTimeToCraftInMilliseconds() {
		return this.timeToCraftInMilliseconds;
	}

	/**
	 * @return the quantity produced when this recipe is crafted
	 */
	public int getQuantityToCraft() {
		return this.quantityToCraft;
	}

	/**
	 * Returns a {@link List} of base ingredients required for this recipe, resolving any
	 * non-base ingredients recursively.
	 *
	 * @return the {@link List} of base ingredients
	 */
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

		for (Map.Entry<Ingredient, Integer> baseIngredientEntry : baseIngredientsMap.entrySet()) {
			Ingredient baseIngredient = baseIngredientEntry.getKey();
			int realBaseIngredientQuantity = baseIngredient.getQuantity() * baseIngredientEntry.getValue();

			Ingredient realBaseIngredient = new Ingredient(baseIngredient.getItem(), realBaseIngredientQuantity);

			baseIngredients.add(realBaseIngredient);
		}

		return baseIngredients;
	}

	/**
	 * Returns whether this recipe requires a crafting table.
	 *
	 * @return true if a crafting table is required, false otherwise
	 */
	public boolean needsCraftingTable() {
		return this.craftingTable != null;
	}

	public void setCraftingTable(Item craftingTable) {
		this.craftingTable = craftingTable;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}
}
