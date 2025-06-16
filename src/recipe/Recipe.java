package recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import exceptions.OutOfRangeException;
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
		return this.ingredients;
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

	public List<Ingredient> getIngredientsToBase() {
		List<Ingredient> ingredients = this.ingredients;
		List<Ingredient> baseIngredients = new ArrayList<Ingredient>();

		for (Ingredient ingredient : ingredients) {
			Item item = ingredient.getItem();
			List<Recipe> itemRecipes = item.getRecipes();

			if (itemRecipes.size() < 1) {
				int IBaseIngredient = baseIngredients.indexOf(ingredient);

				if (IBaseIngredient < 0) {
					baseIngredients.add(ingredient.copy());
					continue;
				}

				Ingredient $ingredient = baseIngredients.get(IBaseIngredient);
				int $ingredientQuantity = $ingredient.getQuantity();

				try {
					$ingredient.incrementQuantity($ingredientQuantity);
				} catch (OutOfRangeException e) {
					// With an ingredient quantity greater than 1, it's never throw an
					// OutOfRangeException.
				}

				continue;
			}

			for (Recipe itemRecipe : itemRecipes) {
				List<Ingredient> itemBaseIngredients = itemRecipe.getIngredientsToBase();

				for (Ingredient itemBaseIngredient : itemBaseIngredients) {
					int IItemBaseIngredient = baseIngredients.indexOf(itemBaseIngredient);

					if (IItemBaseIngredient < 0) {
						baseIngredients.add(itemBaseIngredient.copy());
						continue;
					}

					Ingredient $ingredient = baseIngredients.get(IItemBaseIngredient);
					int $ingredientQuantity = $ingredient.getQuantity();

					try {
						$ingredient.incrementQuantity($ingredientQuantity);
					} catch (OutOfRangeException e) {
						// With an ingredient quantity greater than 1, it's never throw an
						// OutOfRangeException.
					}
				}
			}
		}

		return baseIngredients;
	}
}
