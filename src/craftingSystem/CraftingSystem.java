package craftingSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import exceptions.ItemNotFoundException;
import exceptions.OutOfRangeException;
import exceptions.ThereAreNoCraftedItemsException;
import inventory.Inventory;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;

public class CraftingSystem {
	private final Inventory inventory;
	private List<Item> itemsToCraft;
	private final CraftingHistory history;

	public CraftingSystem(Inventory inventory) {
		this.inventory = inventory;
		this.history = new CraftingHistory();
	}

	public int getCraftableUnits() {
		// TODO

		return 0;
	}

	public HashMap<Item, List<List<Ingredient>>> getRequiredIngredients() {
		HashMap<Item, List<List<Ingredient>>> requiredIngredients = new HashMap<Item, List<List<Ingredient>>>();

		for (Item item : this.itemsToCraft) {
			List<Recipe> recipes = item.getRecipes();
			List<List<Ingredient>> recipesIngredients = new ArrayList<List<Ingredient>>();

			for (Recipe recipe : recipes) {
				List<Ingredient> ingredients = new ArrayList<Ingredient>();

				Optional<Item> craftingTable = recipe.getCraftingTable();

				if (craftingTable.isPresent()) {
					try {
						Ingredient craftingTableIngredient = new Ingredient(craftingTable.get(), 1);
						ingredients.add(craftingTableIngredient);
					} catch (OutOfRangeException e) {
						// With a quantity of 1 ingredient, it's never throw an OutOfRangeException.
					}
				}

				ingredients.addAll(recipe.getIngredients());
				recipesIngredients.add(ingredients);
			}

			requiredIngredients.put(item, recipesIngredients);
		}

		return requiredIngredients;
	}

	public HashMap<Item, List<List<Ingredient>>> getRequiredIngredientsToBase() {
		// TODO
		HashMap<Item, List<List<Ingredient>>> requiredIngredients = new HashMap<Item, List<List<Ingredient>>>();

		return requiredIngredients;
	}

	public List<CraftedItem> getCraftedItems() {
		return this.history.getItems();
	}

	public boolean canCraft() {
		// TODO

		return false;
	};

	public CraftingSystem setItemsToCraft(List<Item> items) {
		this.itemsToCraft = items;
		return this;
	}

	public void craftItems() {
		// TODO
	}

	public void undoLastCraft() throws ThereAreNoCraftedItemsException, ItemNotFoundException {
		CraftedItem lastCraftedItem = this.history.getLastItem();
		int craftedItems = lastCraftedItem.getCraftedItems();

		try {
			this.inventory.removeItem(lastCraftedItem, craftedItems);
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException("The last crafted item was not found in the inventory");
		} catch (OutOfRangeException e) {
			// With a crafted items quantity greater than 1, it's never throw an
			// OutOfRangeException.
		}

		lastCraftedItem = this.history.removeLastItem();
		Recipe usedRecipe = lastCraftedItem.getUsedRecipe();
		List<Ingredient> usedIngredients = usedRecipe.getIngredients();

		for (Ingredient ingredient : usedIngredients) {
			Item itemIngredient = ingredient.getItem();
			int itemIngredientQuantity = ingredient.getQuantity();

			try {
				this.inventory.addItem(itemIngredient, itemIngredientQuantity);
			} catch (OutOfRangeException e) {
				// With an item quantity greater than 1, it's never throw an
				// OutOfRangeException.
			}
		}
	}
}
