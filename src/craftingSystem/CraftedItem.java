package craftingSystem;

import java.time.ZonedDateTime;
import java.util.List;

import exceptions.OutOfRangeException;
import inventory.Item;
import recipe.Recipe;

public class CraftedItem extends Item {
	private final ZonedDateTime date;
	private final Recipe usedRecipe;
	private final int craftedItems;

	public CraftedItem(String name, List<Recipe> recipes, Recipe usedRecipe) {
		super(name, recipes);
		this.date = ZonedDateTime.now();
		this.usedRecipe = usedRecipe;
		this.craftedItems = 1;
	}

	public CraftedItem(String name, List<Recipe> recipes, Recipe usedRecipe, int craftedItems)
			throws OutOfRangeException {
		super(name, recipes);

		if (craftedItems < 1) {
			String errorMessage = String.format("Received %d as craftedItems, but expect it greater than or equal to %d",
					craftedItems, 1);
			throw new OutOfRangeException(errorMessage);
		}

		this.date = ZonedDateTime.now();
		this.usedRecipe = usedRecipe;
		this.craftedItems = craftedItems;
	}

	public ZonedDateTime getDate() {
		return this.date;
	}

	public Recipe getUsedRecipe() {
		return this.usedRecipe;
	}

	public int getCraftedItems() {
		return this.craftedItems;
	}
}
