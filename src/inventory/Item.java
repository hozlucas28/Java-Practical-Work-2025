package inventory;

import java.util.List;

import recipe.Recipe;

public class Item {
	private final String name;
	private final List<Recipe> recipes;

	public Item(String name, List<Recipe> recipes) {
		this.name = name;
		this.recipes = recipes;
	}

	public String getName() {
		return this.name;
	}

	public List<Recipe> getRecipes() {
		return this.recipes;
	}

	public List<Recipe> getCraftableRecipes(Inventory inventory) {
		// TODO

		return this.recipes;
	}

	public boolean isBase() {
		return this.recipes.size() == 0;
	}
}
