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
		this.inventory = inventory;
		this.history = new CraftingHistory();
	}

	public List<CraftedItem> getCraftedItems() {
		return this.history.getItems();
	}

	public int getCraftableUnits() {
		int maxCraftableUnits = 0;

		if (this.quantityToCraft > 0) {
			for (Recipe recipe : this.itemToCraft.getRecipes()) {
				int craftableByRecipe = getCraftableUnits(recipe);
				maxCraftableUnits = Math.max(maxCraftableUnits, craftableByRecipe);
			}
		}

		return maxCraftableUnits;
	}

	private int getCraftableUnits(Recipe recipe) {
		Integer minCraftableUnits = null;
		List<Ingredient> ingredients = recipe.getIngredients();

		if (recipe.needsCraftingTable()) {
			Item craftingTable = recipe.getCraftingTable();
			int quantityInInventory = this.inventory.getItemQuantity(craftingTable);

			if (quantityInInventory < 1) {
				return 0;
			}
		}

		for (Ingredient ingredient : ingredients) {
			Item item = ingredient.getItem();
			int requiredQuantity = ingredient.getQuantity();
			int quantityInInventory = this.inventory.getItemQuantity(item);

			int totalAvailable = quantityInInventory;

			if (!item.isBase() && quantityInInventory < requiredQuantity) {
				int maxCraftable = 0;

				for (Recipe subRecipe : item.getRecipes()) {
					maxCraftable = Math.max(maxCraftable, getCraftableUnits(subRecipe));
				}

				totalAvailable += maxCraftable;
			}

			int ingredientUnits = totalAvailable / requiredQuantity;
			minCraftableUnits = minCraftableUnits == null ? ingredientUnits
					: Math.min(minCraftableUnits, ingredientUnits);

			if (minCraftableUnits < 1) {
				return 0;
			}
		}

		return minCraftableUnits == null ? 0 : minCraftableUnits * recipe.getQuantityToCraft();
	}

	public HashMap<Recipe, List<Ingredient>> getMissingIngredients() {
		return this.getMissingIngredients(this.getRequiredIngredients());
	}

	public HashMap<Recipe, List<Ingredient>> getMissingBaseIngredients(int branch) {
		return this.getMissingIngredients(this.getRequiredBaseIngredients(branch));
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

	public HashMap<Recipe, List<Ingredient>> getRequiredBaseIngredients(int branch) {
		HashMap<Recipe, List<Ingredient>> ingredientsPerRecipe = new HashMap<Recipe, List<Ingredient>>();

		if (this.quantityToCraft > 0) {
			List<Recipe> recipes = this.itemToCraft.getRecipes();

			for (Recipe recipe : recipes) {
				List<Ingredient> baseIngredients = getBaseIngredientsRecursive(recipe, this.quantityToCraft, branch);
				ingredientsPerRecipe.put(recipe, baseIngredients);
			}
		}

		return ingredientsPerRecipe;
	}

	private List<Ingredient> getBaseIngredientsRecursive(Recipe recipe, int totalToCraft, int branch) {
		List<Ingredient> ingredients = recipe.getIngredients();

		Map<Item, Integer> quantitiesPerItem = new HashMap<Item, Integer>();
		List<Ingredient> baseIngredients = new ArrayList<Ingredient>();

		int craftsNeeded = (int) Math.ceil(totalToCraft / (double) recipe.getQuantityToCraft());

		Item craftingTable = recipe.getCraftingTable();

		if (recipe.needsCraftingTable()) {
			quantitiesPerItem.put(craftingTable, 1);
		}

		for (Ingredient ingredient : ingredients) {
			Item item = ingredient.getItem();
			int itemQuantityNeeded = ingredient.getQuantity() * craftsNeeded;

			if (item.isBase()) {
				quantitiesPerItem.put(item, quantitiesPerItem.getOrDefault(item, 0) + itemQuantityNeeded);
			} else {
				List<Recipe> itemRecipes = item.getRecipes();

				int recipeIndex = (branch + itemRecipes.size()) % itemRecipes.size();
				Recipe desiredRecipe = itemRecipes.get(recipeIndex);

				List<Ingredient> desiredRecipeBaseIngredients = getBaseIngredientsRecursive(desiredRecipe,
						itemQuantityNeeded, branch);

				for (Ingredient baseIngredient : desiredRecipeBaseIngredients) {
					Item baseItem = baseIngredient.getItem();
					int baseItemQuantity = baseIngredient.getQuantity();

					if (baseItem != craftingTable) {
						quantitiesPerItem.put(baseItem, quantitiesPerItem.getOrDefault(baseItem, 0) + baseItemQuantity);
					}
				}
			}
		}

		for (Map.Entry<Item, Integer> itemEntry : quantitiesPerItem.entrySet()) {
			Item item = itemEntry.getKey();
			Integer itemQuantity = itemEntry.getValue();

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

	public CraftedItem craftItem(int recipe) throws NonCraftableItemException {
		List<Recipe> recipes = this.itemToCraft.getRecipes();

		int recipeIndex = (recipe + recipes.size()) % recipes.size();
		Recipe selectedRecipe = recipes.get(recipeIndex);

		List<Ingredient> ingredients = selectedRecipe.getIngredients();
		int recipeQuantityToCraft = selectedRecipe.getQuantityToCraft();

		if (selectedRecipe.needsCraftingTable()) {
			Item craftingTable = selectedRecipe.getCraftingTable();

			if (this.inventory.getItemQuantity(craftingTable) < 1) {
				String errorMessage = String.format(
						"Inventory does not have the necessary ingredients to craft %d of \"%s\" items with any recipe.",
						this.quantityToCraft, this.itemToCraft.getName());

				throw new NonCraftableItemException(errorMessage);
			}
		}

		for (Ingredient ingredient : ingredients) {
			Item ingItem = ingredient.getItem();
			int ingQuantity = ingredient.getQuantity();
			int requiredQuantity = (int) Math.ceil(this.quantityToCraft / (double) recipeQuantityToCraft) * ingQuantity;

			if (this.inventory.getItemQuantity(ingItem) < requiredQuantity) {
				String errorMessage = String.format(
						"Inventory does not have the necessary ingredients to craft %d of \"%s\" items with any recipe.",
						this.quantityToCraft, this.itemToCraft.getName());

				throw new NonCraftableItemException(errorMessage);
			}
		}

		for (Ingredient ingredient : ingredients) {
			Item item = ingredient.getItem();
			int quantity = (int) Math.ceil(this.quantityToCraft / (double) selectedRecipe.getQuantityToCraft())
					* ingredient.getQuantity();

			try {
				this.inventory.removeItem(item, quantity);
			} catch (ItemNotFoundException | OutOfRangeException e) {
				// With an required ingredient quantity below the available one, and knowing
				// that it exists inside the inventory, it's never throw an
				// ItemNotFoundException or OutOfRangeException.
			}
		}

		int itemsCrafted = selectedRecipe.getQuantityToCraft()
				* (int) Math.ceil(this.quantityToCraft / (double) selectedRecipe.getQuantityToCraft());

		try {
			this.inventory.addItem(this.itemToCraft, itemsCrafted);
			this.history.addItem(this.itemToCraft, selectedRecipe, itemsCrafted);
		} catch (OutOfRangeException e) {
			// With a crafted item quantity greater than 1, it's never throw an
			// OutOfRangeException.
		}

		CraftedItem craftedItem = new CraftedItem(this.itemToCraft, selectedRecipe, itemsCrafted);

		return craftedItem;
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
			int itemIngredientQuantity = (int) Math.ceil(quantityCrafted / (double) usedRecipe.getQuantityToCraft())
					* ingredient.getQuantity();

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
