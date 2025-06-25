package craftingSystem;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import exceptions.OutOfRangeException;
import inventory.Item;
import recipe.Recipe;

public class CraftedItem extends Item {
	private final ZonedDateTime date;
	private final Recipe usedRecipe;
	private final int quantityCrafted;

	public CraftedItem(String name, List<Recipe> recipes, Recipe usedRecipe) {
		super(name, recipes);

		this.date = ZonedDateTime.now();
		this.usedRecipe = usedRecipe;
		this.quantityCrafted = 1;
	}

	public CraftedItem(String name, List<Recipe> recipes, Recipe usedRecipe, int quantityCrafted) {
		super(name, recipes);

		this.date = ZonedDateTime.now();
		this.usedRecipe = usedRecipe;
		this.quantityCrafted = quantityCrafted;
	}

	public ZonedDateTime getDate() {
		return this.date;
	}

	public Recipe getUsedRecipe() {
		return this.usedRecipe;
	}

	public int getQuantityCrafted() {
		return this.quantityCrafted;
	}

	public boolean softEquals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!super.equals(obj)) {
			return false;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		CraftedItem other = (CraftedItem) obj;

		return Objects.equals(this.usedRecipe, other.usedRecipe) && this.quantityCrafted == other.quantityCrafted;
	}
}
