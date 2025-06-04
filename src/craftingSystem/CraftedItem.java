package craftingSystem;

import java.util.Date;
import java.util.List;

import inventory.Item;
import recipe.Recipe;

public class CraftedItem extends Item {
	private final Date date;
	private final Recipe usedRecipe;

	public CraftedItem(String name, List<Recipe> recipes, Recipe usedRecipe) {
		super(name, recipes);
		this.date = new Date();
		this.usedRecipe = usedRecipe;
	}

	public Date getDate() {
		return this.date;
	}

	public Recipe getUsedRecipe() {
		return this.usedRecipe;
	}
}
