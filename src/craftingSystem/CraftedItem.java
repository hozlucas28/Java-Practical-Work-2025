package craftingSystem;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Objects;

import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;
import utilities.StringTransformers;

public class CraftedItem extends Item {
	private final ZonedDateTime date;
	private final Recipe usedRecipe;
	private final int quantityCrafted;

	public CraftedItem(Item itemCrafted, Recipe usedRecipe) {
		super(itemCrafted.getName(), itemCrafted.getRecipes());

		this.date = ZonedDateTime.now();
		this.usedRecipe = usedRecipe;
		this.quantityCrafted = 1;
	}

	public CraftedItem(Item itemCrafted, Recipe usedRecipe, int quantityCrafted) {
		super(itemCrafted.getName(), itemCrafted.getRecipes());

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Formatter formatter = new Formatter(builder);

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' HH:mm:ss");
		formatter.format("%s (%s):\n", StringTransformers.toTitle(this.name), this.date.format(dateFormatter));

		int recipeNumber = this.recipes.indexOf(usedRecipe) + 1;
		formatter.format("  ◦ Recipe #%d (x%d): ", recipeNumber, this.quantityCrafted);

		List<Ingredient> usedIngredients = new ArrayList<Ingredient>();

		if (this.usedRecipe.needsCraftingTable()) {
			Ingredient craftingTable = new Ingredient(this.usedRecipe.getCraftingTable(), 1);
			usedIngredients.add(craftingTable);
		}

		usedIngredients.addAll(usedRecipe.getIngredients());

		for (int i = 0; i < usedIngredients.size(); i++) {
			Ingredient ingredient = usedIngredients.get(i);

			String ingredientName = StringTransformers.toTitle(ingredient.getItem().getName());
			int ingredientQuantity = ingredient.getQuantity();

			if (i == usedIngredients.size() - 1) {
				formatter.format("%s%s (x%d)", i == 0 ? "" : "and ", ingredientName, ingredientQuantity);
			} else {
				formatter.format("%s (x%d), ", ingredientName, ingredientQuantity);
			}
		}

		formatter.format(".");
		formatter.close();

		return builder.toString();
	}
}
