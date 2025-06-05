package recipe;

import java.util.List;
import java.util.Optional;

import inventory.Item;

public class Recipe {
	private final List<Ingredient> ingredients;
	private final int timeToCraftInMilliseconds;
	private final int itemsToCraft;
	private final Optional<Item> craftingTable;

	public Recipe(List<Ingredient> ingredients, int timeToCraftInMilliseconds, int itemsToCraft) {
		this.craftingTable = Optional.empty();
		this.ingredients = ingredients;
		this.timeToCraftInMilliseconds = timeToCraftInMilliseconds;
		this.itemsToCraft = itemsToCraft;
	}

	public Recipe(Item craftingTable, List<Ingredient> ingredients, int timeInMilliseconds, int itemsToCraft) {
		this.craftingTable = Optional.ofNullable(craftingTable);
		this.ingredients = ingredients;
		this.timeToCraftInMilliseconds = timeInMilliseconds;
		this.itemsToCraft = itemsToCraft;
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
		// TODO

		return this.ingredients;
	}
}
