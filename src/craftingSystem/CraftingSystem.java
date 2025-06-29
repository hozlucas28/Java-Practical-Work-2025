package craftingSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.EmptyHistoryException;
import exceptions.InvalidItemException;
import exceptions.ItemNotFoundException;
import exceptions.NonCraftableItemException;
import exceptions.OutOfRangeException;
import inventory.Inventory;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;

public class CraftingSystem {
	private Item itemToCraft;
	private int quantityToCraft;
	private final Inventory inventory;
	private final CraftingHistory history;

	public CraftingSystem(Inventory inventory) {
		this.itemToCraft = null;
		this.quantityToCraft = 0;
		this.inventory = inventory;
		this.history = new CraftingHistory();
	}

	public List<CraftedItem> getCraftedItems() {
		return this.history.getItems();
	}

	public int getCraftableUnits() {
		HashMap<Recipe, List<Ingredient>> ingredientsPerRecipe = this.getRequiredIngredients();

		int maxCraftableUnits = 0;

		for (Map.Entry<Recipe, List<Ingredient>> recipeEntry : ingredientsPerRecipe.entrySet()) {
			Recipe recipe = recipeEntry.getKey();
			List<Ingredient> ingredients = recipeEntry.getValue();

			Item recipeCraftingTable = recipe.getCraftingTable();
			int recipeQuantityToCraft = recipe.getQuantityToCraft();

			Integer minCraftableUnits = null;

			for (Ingredient ingredient : ingredients) {
				Item item = ingredient.getItem();
				int quantity = ingredient.getQuantity();
				int quantityInInventory = this.inventory.getItemQuantity(item);

				if (item == recipeCraftingTable) {
					if (quantityInInventory < 1) {
						minCraftableUnits = 0;
						break;
					}
				} else {
					int ingredientUnits = quantityInInventory / quantity;

					minCraftableUnits = minCraftableUnits == null ? ingredientUnits
							: Math.min(minCraftableUnits, ingredientUnits);
				}
			}

			int craftableRecipeUnits = minCraftableUnits == null ? 0 : minCraftableUnits * recipeQuantityToCraft;

			maxCraftableUnits = Math.max(maxCraftableUnits, craftableRecipeUnits);
		}

		return maxCraftableUnits;
	}

	public HashMap<Recipe, List<Ingredient>> getMissingIngredients() {
		return this.getMissingIngredients(this.getRequiredIngredients());
	}

	public HashMap<Recipe, List<Ingredient>> getMissingBaseIngredients() {
		// TODO: add parameter to select which path of recipes the algorithm should
		// follow, and if the inventory have all the required items return empty list
		return this.getMissingIngredients(this.getRequiredBaseIngredients());
	}

	private HashMap<Recipe, List<Ingredient>> getMissingIngredients(HashMap<Recipe, List<Ingredient>> recipes) {
		HashMap<Recipe, List<Ingredient>> missingIngredientsPerRecipe = new HashMap<Recipe, List<Ingredient>>();

		for (Map.Entry<Recipe, List<Ingredient>> recipeEntry : recipes.entrySet()) {
			Recipe recipe = recipeEntry.getKey();
			List<Ingredient> recipeIngredients = recipeEntry.getValue();

			List<Ingredient> missingRecipeIngredients = new ArrayList<Ingredient>();

			for (Ingredient ingredient : recipeIngredients) {
				Item ingredientItem = ingredient.getItem();
				int ingredientQuantity = ingredient.getQuantity();
				int missingQuantity = ingredientQuantity - this.inventory.getItemQuantity(ingredientItem);

				if (missingQuantity > 0) {
					Ingredient missingIngredient = new Ingredient(ingredientItem, missingQuantity);
					missingRecipeIngredients.add(missingIngredient);
				}
			}

			missingIngredientsPerRecipe.put(recipe, missingRecipeIngredients);
		}

		return missingIngredientsPerRecipe;
	}

	public HashMap<Recipe, List<Ingredient>> getRequiredIngredients() {
		HashMap<Recipe, List<Ingredient>> ingredientsPerRecipe = new HashMap<Recipe, List<Ingredient>>();

		if (this.quantityToCraft > 0) {
			List<Recipe> recipes = this.itemToCraft.getRecipes();

			for (Recipe recipe : recipes) {
				List<Ingredient> recipeIngredients = recipe.getIngredients();
				int recipeQuantityToCraft = recipe.getQuantityToCraft();

				List<Ingredient> realRecipeIngredients = new ArrayList<Ingredient>();

				if (recipe.needsCraftingTable()) {
					Ingredient table = new Ingredient(recipe.getCraftingTable(), 1);
					realRecipeIngredients.add(table);
				}

				for (Ingredient ingredient : recipeIngredients) {
					int realQuantity = (int) Math.ceil(this.quantityToCraft / (double) recipeQuantityToCraft)
							* ingredient.getQuantity();

					Ingredient realIngredient = new Ingredient(ingredient.getItem(), realQuantity);

					realRecipeIngredients.add(realIngredient);
				}

				ingredientsPerRecipe.put(recipe, realRecipeIngredients);
			}
		}

		return ingredientsPerRecipe;
	}

	public HashMap<Recipe, List<Ingredient>> getRequiredBaseIngredients() {
		HashMap<Recipe, List<Ingredient>> ingredientsPerRecipe = new HashMap<Recipe, List<Ingredient>>();

		if (this.quantityToCraft > 0) {
			List<Recipe> recipes = this.itemToCraft.getRecipes();

			for (Recipe recipe : recipes) {
				List<Ingredient> baseIngredients = getBaseIngredientsRecursive(recipe, this.quantityToCraft);
				ingredientsPerRecipe.put(recipe, baseIngredients);
			}
		}

		return ingredientsPerRecipe;
	}

	private List<Ingredient> getBaseIngredientsRecursive(Recipe recipe, int totalToCraft) {
		List<Ingredient> ingredients = recipe.getIngredients();

		Map<Item, Integer> baseCount = new HashMap<Item, Integer>();
		List<Ingredient> baseIngredients = new ArrayList<Ingredient>();

		int craftsNeeded = (int) Math.ceil(totalToCraft / (double) recipe.getQuantityToCraft());

		Item craftingTable = recipe.getCraftingTable();

		if (recipe.needsCraftingTable()) {
			baseCount.put(craftingTable, 1);
		}

		for (Ingredient ingredient : ingredients) {
			Item item = ingredient.getItem();
			int itemQuantityNeeded = ingredient.getQuantity() * craftsNeeded;

			if (item.isBase()) {
				baseCount.put(item, baseCount.getOrDefault(item, 0) + itemQuantityNeeded);
			} else {
				Recipe firstRecipe = item.getRecipes().get(0);
				List<Ingredient> firstRecipeBaseIngredients = getBaseIngredientsRecursive(firstRecipe,
						itemQuantityNeeded);

				for (Ingredient baseIngredient : firstRecipeBaseIngredients) {
					Item baseItem = baseIngredient.getItem();
					int baseItemQuantity = baseIngredient.getQuantity();

					if (baseItem != craftingTable) {
						baseCount.put(baseItem, baseCount.getOrDefault(baseItem, 0) + baseItemQuantity);
					}
				}
			}
		}

		for (Map.Entry<Item, Integer> entry : baseCount.entrySet()) {
			Item item = entry.getKey();
			Integer itemQuantity = entry.getValue();

			Ingredient baseIngredient = new Ingredient(item, itemQuantity);

			baseIngredients.add(baseIngredient);
		}

		return baseIngredients;
	}

	public boolean canCraft() {
		HashMap<Recipe, List<Ingredient>> missingIngredientsPerRecipe = this.getMissingIngredients();

		for (List<Ingredient> missingIngredients : missingIngredientsPerRecipe.values()) {
			if (missingIngredients.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	public void setItemToCraft(Item item, int quantity) throws InvalidItemException {
		if (item.isBase()) {
			throw new InvalidItemException("Item to craft should not be a base one");
		}

		this.itemToCraft = item;
		this.quantityToCraft = quantity;
	}

	public int craftItem() throws NonCraftableItemException {
		Recipe firstEmptyRecipe = null;
		HashMap<Recipe, List<Ingredient>> missingIngredientsPerRecipe = this.getMissingIngredients();

		for (Map.Entry<Recipe, List<Ingredient>> recipeEntry : missingIngredientsPerRecipe.entrySet()) {
			Recipe recipe = recipeEntry.getKey();
			List<Ingredient> missingIngredients = recipeEntry.getValue();

			if (missingIngredients.isEmpty()) {
				firstEmptyRecipe = recipe;
				break;
			}
		}

		if (firstEmptyRecipe == null) {
			String errorMessage = String.format(
					"Inventory does not have the necessary ingredients to craft %d of \"%s\" items with any recipe.",
					this.quantityToCraft, this.itemToCraft.getName());

			throw new NonCraftableItemException(errorMessage);
		}

		List<Ingredient> ingredientsToRemove = firstEmptyRecipe.getIngredients();

		for (Ingredient ingredient : ingredientsToRemove) {
			Item itemToRemove = ingredient.getItem();
			int quantityToRemove = ingredient.getQuantity();

			try {
				this.inventory.removeItem(itemToRemove, quantityToRemove);
			} catch (ItemNotFoundException | OutOfRangeException e) {
				// With an required ingredient quantity below the available one, and knowing
				// that it exists inside the inventory, it's never throw an
				// ItemNotFoundException or OutOfRangeException.
			}
		}

		int itemsCrafted = firstEmptyRecipe.getQuantityToCraft()
				* (int) Math.ceil(this.quantityToCraft / (double) firstEmptyRecipe.getQuantityToCraft());

		try {
			this.inventory.addItem(this.itemToCraft, itemsCrafted);
			this.history.addItem(this.itemToCraft, firstEmptyRecipe, itemsCrafted);
		} catch (OutOfRangeException e) {
			// With a crafted item quantity greater than 1, it's never throw an
			// OutOfRangeException.
		}

		return itemsCrafted;
	}

	public CraftedItem undoLastCraft() throws EmptyHistoryException, ItemNotFoundException {
		CraftedItem lastCraftedItem = this.history.getLastItem();
		int quantityCrafted = lastCraftedItem.getQuantityCrafted();

		try {
			this.inventory.removeItem(lastCraftedItem, quantityCrafted);
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
		
		return lastCraftedItem;
	}
}
