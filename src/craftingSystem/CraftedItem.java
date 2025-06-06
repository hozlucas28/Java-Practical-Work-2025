package craftingSystem;

import java.time.ZonedDateTime;
import java.util.List;

import inventory.Item;
import recipe.Recipe;

public class CraftedItem extends Item {
	private final ZonedDateTime date;
	private final Recipe usedRecipe;

	public CraftedItem(String id, String name, List<Recipe> recipes, Recipe usedRecipe) {
		super(id, name, recipes);
		this.date = ZonedDateTime.now();
		this.usedRecipe = usedRecipe;
	}

	public ZonedDateTime getDate() {
		return this.date;
	}

	public Recipe getUsedRecipe() {
		return this.usedRecipe;
	}
}
