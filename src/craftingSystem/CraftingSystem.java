package craftingSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exceptions.EmptyHistoryException;
import exceptions.InvalidItemException;
import exceptions.ItemNotFoundException;
import exceptions.NonCraftableItemException;
import exceptions.OutOfRangeException;
import inventory.Inventory;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;

/**
 * {@code CraftingSystem} class provides functionality for crafting items using
 * recipes, managing inventory, and tracking crafting history. It supports
 * calculating craftable units, determining missing and required ingredients,
 * and undoing crafting actions.
 *
 * <p>
 * This class interacts with {@link Inventory}, {@link Item}, {@link Recipe},
 * {@link Ingredient}, and {@link CraftingHistory} to facilitate a flexible
 * crafting system.
 * </p>
 *
 * <p>
 * Usage includes setting the item and quantity to craft, checking if crafting
 * is possible, performing the crafting operation, and undoing the last craft.
 * </p>
 */
public class CraftingSystem {
	private Item itemToCraft;
	private int quantityToCraft;
	private final Inventory inventory;
	private final CraftingHistory history;

	/**
	 * Constructs a new {@code CraftingSystem} with the specified inventory.
	 *
	 * @param inventory
	 */
	public CraftingSystem(Inventory inventory) {
		this.inventory = inventory;
		this.history = new CraftingHistory();
	}

	/**
	 * Returns the {@link List} of crafted items from the crafting history.
	 *
	 * @return a {@link List} of {@link CraftedItem} objects
	 */
	public List<CraftedItem> getCraftedItems() {
		return this.history.getItems();
	}

	/**
	 * Calculates the maximum number of units that can be crafted for the current
	 * item and quantity. Taking into account if it can craft the missing
	 * ingredients.
	 *
	 * @return the maximum craftable units
	 */
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

		// Take a snapshot of current inventory
		HashMap<Item, Integer> inventorySnapshot = new HashMap<Item, Integer>();

		for (Ingredient ingredient : ingredients) {
			Item item = ingredient.getItem();
			inventorySnapshot.put(item, this.inventory.getItemQuantity(item));
		}

		// Track crafting table if needed
		Item craftingTable = null;
		List<Ingredient> craftingTableIngredients = null;

		if (recipe.needsCraftingTable()) {
			craftingTable = recipe.getCraftingTable();
			int quantityInInventory = this.inventory.getItemQuantity(craftingTable);

			if (quantityInInventory < 1) {
				// If crafting table is not in inventory, check if it is craftable
				if (!craftingTable.isBase()) {
					int maxCraftable = 0;
					Recipe bestRecipe = null;

					for (Recipe tableRecipe : craftingTable.getRecipes()) {
						int craftableUnits = getCraftableUnits(tableRecipe);

						if (craftableUnits > maxCraftable) {
							maxCraftable = craftableUnits;
							bestRecipe = tableRecipe;
						}
					}

					if (maxCraftable < 1) {
						return 0;
					}

					// Subtract ingredients needed for crafting table from inventory snapshot
					craftingTableIngredients = bestRecipe.getIngredients();

					for (Ingredient ingredient : craftingTableIngredients) {
						Item item = ingredient.getItem();
						int quantity = ingredient.getQuantity();

						inventorySnapshot.put(item,
								inventorySnapshot.getOrDefault(item, this.inventory.getItemQuantity(item)) - quantity);
					}
				} else {
					return 0;
				}
			}
		}

		// Check the existence of each required ingredient to craft the recipe
		for (Ingredient ingredient : ingredients) {
			Item item = ingredient.getItem();
			int requiredQuantity = ingredient.getQuantity();
			int quantityInInventory = inventorySnapshot.getOrDefault(item, this.inventory.getItemQuantity(item));

			int totalAvailable = quantityInInventory;

			// If the ingredient quantity isn't enough, get the number of craftable ones
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

			// If the ingredient quantity isn't enough and the number of craftable ones is 0
			// return
			if (minCraftableUnits < 1) {
				return 0;
			}
		}

		int craftableUnits = minCraftableUnits == null ? 0 : minCraftableUnits * recipe.getQuantityToCraft();

		return craftableUnits;
	}

	/**
	 * @return a {@code HashMap} of recipes and each value is a {@link List} with
	 *         the missing {@link Ingredient}s within inventory.
	 */
	public HashMap<Recipe, List<Ingredient>> getMissingIngredients() {
		return this.getMissingIngredients(this.getRequiredIngredients());
	}

	/**
	 * Returns a {@code HashMap} of recipes and each value is a {@link List} with
	 * the missing base {@link Ingredient}s within inventory for the specified
	 * recipe branch.
	 *
	 * @param branch recipe branch
	 * @return a {@code HashMap} of recipes and each value is a {@link List} with
	 *         the missing base {@link Ingredient}s
	 */
	public HashMap<Recipe, List<Ingredient>> getMissingBaseIngredients(int branch) {
		return this.getMissingIngredients(this.getRequiredBaseIngredients(branch));
	}

	/**
	 * Returns a {@code HashMap} of recipes and each value is a {@link List} with
	 * the missing {@link Ingredient}s within inventory for the specified recipe
	 * branch.
	 *
	 * @param branch recipe branch
	 * @return a {@code HashMap} of recipes and each value is a {@link List} with
	 *         the missing {@link Ingredient}s
	 */
	private HashMap<Recipe, List<Ingredient>> getMissingIngredients(HashMap<Recipe, List<Ingredient>> recipes) {
		HashMap<Recipe, List<Ingredient>> missingIngredientsPerRecipe = new HashMap<Recipe, List<Ingredient>>();

		for (Map.Entry<Recipe, List<Ingredient>> recipeEntry : recipes.entrySet()) {
			Recipe recipe = recipeEntry.getKey();
			List<Ingredient> recipeIngredients = recipeEntry.getValue();

			List<Ingredient> missingRecipeIngredients = new ArrayList<Ingredient>();

			// Calculate the missing quantity of ingredients to craft the item
			for (Ingredient ingredient : recipeIngredients) {
				Item ingredientItem = ingredient.getItem();
				int ingredientQuantity = ingredient.getQuantity();
				int missingQuantity = ingredientQuantity - this.inventory.getItemQuantity(ingredientItem);

				if (missingQuantity > 0) {
					Ingredient missingIngredient = new Ingredient(ingredientItem, missingQuantity);
					missingRecipeIngredients.add(missingIngredient);
				}
			}

			// Link recipe with the calculated missing ingredients
			missingIngredientsPerRecipe.put(recipe, missingRecipeIngredients);
		}

		return missingIngredientsPerRecipe;
	}

	/**
	 * Returns a {@code HashMap} of recipes and each value is a {@link List} with
	 * the required {@link Ingredient}s to craft the item.
	 *
	 * @return a {@code HashMap} of recipes and each value is a {@link List} with
	 *         the required {@link Ingredient}s
	 */
	public HashMap<Recipe, List<Ingredient>> getRequiredIngredients() {
		HashMap<Recipe, List<Ingredient>> ingredientsPerRecipe = new HashMap<Recipe, List<Ingredient>>();

		if (this.quantityToCraft > 0) {
			List<Recipe> recipes = this.itemToCraft.getRecipes();

			for (Recipe recipe : recipes) {
				List<Ingredient> recipeIngredients = recipe.getIngredients();
				int recipeQuantityToCraft = recipe.getQuantityToCraft();

				List<Ingredient> realRecipeIngredients = new ArrayList<Ingredient>();

				// Append crafting table if the recipe required it
				if (recipe.needsCraftingTable()) {
					Ingredient table = new Ingredient(recipe.getCraftingTable(), 1);
					realRecipeIngredients.add(table);
				}

				// Calculate the required quantity of ingredients to craft the item
				for (Ingredient ingredient : recipeIngredients) {
					int realQuantity = (int) Math.ceil(this.quantityToCraft / (double) recipeQuantityToCraft)
							* ingredient.getQuantity();

					Ingredient realIngredient = new Ingredient(ingredient.getItem(), realQuantity);

					realRecipeIngredients.add(realIngredient);
				}

				// Link recipe with the calculated required ingredients
				ingredientsPerRecipe.put(recipe, realRecipeIngredients);
			}
		}

		return ingredientsPerRecipe;
	}

	/**
	 * Returns a {@code HashMap} of recipes and each value is a {@link List} with
	 * the required base {@link Ingredient}s to craft the item.
	 *
	 * @return a {@code HashMap} of recipes and each value is a {@link List} with
	 *         the required base {@link Ingredient}s
	 */
	public HashMap<Recipe, List<Ingredient>> getRequiredBaseIngredients(int branch) {
		HashMap<Recipe, List<Ingredient>> ingredientsPerRecipe = new HashMap<Recipe, List<Ingredient>>();

		if (this.quantityToCraft > 0) {
			List<Recipe> recipes = this.itemToCraft.getRecipes();

			for (Recipe recipe : recipes) {
				// Calculate required base ingredients to craft the item
				List<Ingredient> baseIngredients = getBaseIngredientsRecursive(recipe, this.quantityToCraft, branch,
						new HashSet<Item>());

				// Link recipe with the calculated required base ingredients
				ingredientsPerRecipe.put(recipe, baseIngredients);
			}
		}

		return ingredientsPerRecipe;
	}

	private List<Ingredient> getBaseIngredientsRecursive(Recipe recipe, int totalToCraft, int branch,
			Set<Item> processedCraftingTables) {

		HashMap<Item, Integer> quantitiesPerItem = new HashMap<Item, Integer>();
		List<Ingredient> baseIngredients = new ArrayList<Ingredient>();

		int craftsNeeded = (int) Math.ceil(totalToCraft / (double) recipe.getQuantityToCraft());

		Item craftingTable = recipe.getCraftingTable();

		// Handle crafting table
		if (recipe.needsCraftingTable()) {
			if (!processedCraftingTables.contains(craftingTable)) {
				processedCraftingTables.add(craftingTable);

				// Check if the crafting table is a base one
				if (craftingTable.isBase()) {
					// Set quantity if it is a base one
					quantitiesPerItem.put(craftingTable, 1);
				} else {
					// Follow the recipe path if it is a complex one
					List<Recipe> recipes = craftingTable.getRecipes();

					int recipeIndex = (branch + recipes.size()) % recipes.size();
					Recipe desiredRecipe = recipes.get(recipeIndex);

					List<Ingredient> tableBaseIngredients = getBaseIngredientsRecursive(desiredRecipe, 1, branch,
							processedCraftingTables);

					// Set each base ingredient quantity of the complex one
					for (Ingredient baseIngredient : tableBaseIngredients) {
						Item baseItem = baseIngredient.getItem();
						int baseItemQuantity = baseIngredient.getQuantity();

						// Check if base item is the crafting table
						if (baseItem.equals(craftingTable)) {
							quantitiesPerItem.put(craftingTable, 1);
						} else {
							quantitiesPerItem.put(baseItem,
									quantitiesPerItem.getOrDefault(baseItem, 0) + baseItemQuantity);
						}
					}
				}
			}
		}

		List<Ingredient> ingredients = recipe.getIngredients();

		// Set each ingredient quantity
		for (Ingredient ingredient : ingredients) {
			Item item = ingredient.getItem();
			int itemQuantityNeeded = ingredient.getQuantity() * craftsNeeded;

			if (item.isBase()) {
				// Set quantity if it is a base one
				quantitiesPerItem.put(item, quantitiesPerItem.getOrDefault(item, 0) + itemQuantityNeeded);
			} else if (!item.equals(craftingTable)) {
				// Follow the recipe path if it is a complex one
				List<Recipe> itemRecipes = item.getRecipes();

				int recipeIndex = (branch + itemRecipes.size()) % itemRecipes.size();
				Recipe desiredRecipe = itemRecipes.get(recipeIndex);

				List<Ingredient> desiredRecipeBaseIngredients = getBaseIngredientsRecursive(desiredRecipe,
						itemQuantityNeeded, branch, processedCraftingTables);

				// Set each base ingredient quantity of the complex one
				for (Ingredient baseIngredient : desiredRecipeBaseIngredients) {
					Item baseItem = baseIngredient.getItem();
					int baseItemQuantity = baseIngredient.getQuantity();

					// Check if base item is the crafting table
					if (baseItem.equals(craftingTable)) {
						quantitiesPerItem.put(craftingTable, 1);
					} else {
						quantitiesPerItem.put(baseItem, quantitiesPerItem.getOrDefault(baseItem, 0) + baseItemQuantity);
					}
				}
			}
		}

		// Calculate real base ingredient quantities
		for (Map.Entry<Item, Integer> itemEntry : quantitiesPerItem.entrySet()) {
			Item item = itemEntry.getKey();
			Integer itemQuantity = itemEntry.getValue();

			Ingredient baseIngredient = new Ingredient(item, itemQuantity);

			baseIngredients.add(baseIngredient);
		}

		return baseIngredients;
	}

	/**
	 * Determines if the current item and quantity can be crafted with the available
	 * inventory.
	 *
	 * @return {@code true} if crafting is possible, {@code false} otherwise
	 */
	public boolean canCraft() {
		HashMap<Recipe, List<Ingredient>> missingIngredientsPerRecipe = this.getMissingIngredients();

		for (List<Ingredient> missingIngredients : missingIngredientsPerRecipe.values()) {

			// Return true when it finds a craftable recipe
			if (missingIngredients.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Sets the item and quantity to craft.
	 *
	 * @param item
	 * @param quantity
	 * @throws InvalidItemException if the item is a base one
	 */
	public void setItemToCraft(Item item, int quantity) throws InvalidItemException {
		if (item.isBase()) {
			throw new InvalidItemException("Item to craft should not be a base one");
		}

		this.itemToCraft = item;
		this.quantityToCraft = quantity;
	}

	/**
	 * Crafts the specified quantity of the item using the specified recipe.
	 *
	 * @param recipe recipe branch index to use for crafting
	 * @return the crafted item as a {@link CraftedItem}
	 * @throws NonCraftableItemException if the item cannot be crafted due to
	 *                                   missing ingredients
	 */
	public CraftedItem craftItem(int recipe) throws NonCraftableItemException {
		List<Recipe> recipes = this.itemToCraft.getRecipes();

		int recipeIndex = (recipe + recipes.size()) % recipes.size();
		Recipe selectedRecipe = recipes.get(recipeIndex);

		List<Ingredient> ingredients = selectedRecipe.getIngredients();
		int recipeQuantityToCraft = selectedRecipe.getQuantityToCraft();

		// Check if the required crafting table is within inventory
		if (selectedRecipe.needsCraftingTable()) {
			Item craftingTable = selectedRecipe.getCraftingTable();

			if (this.inventory.getItemQuantity(craftingTable) < 1) {
				String errorMessage = String.format(
						"Inventory does not have the necessary ingredients to craft %d of \"%s\" items with any recipe.",
						this.quantityToCraft, this.itemToCraft.getName());

				throw new NonCraftableItemException(errorMessage);
			}
		}

		// Check if each ingredient quantity is within inventory
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

		// Remove ingredients from the inventory
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

		// Add crafted item to the inventory and append it to the crafting history
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

	/**
	 * Undoes the last crafted item, removing it from inventory and restoring used
	 * ingredients.
	 *
	 * @return the last crafted item as a {@link CraftedItem}
	 * @throws EmptyHistoryException if the crafting history is empty
	 * @throws ItemNotFoundException if the crafted item is not found in the
	 *                               inventory
	 */
	public CraftedItem undoLastCraft() throws EmptyHistoryException, ItemNotFoundException {
		CraftedItem lastCraftedItem = this.history.getLastItem();
		int quantityCrafted = lastCraftedItem.getQuantityCrafted();

		// Remove crafted item from the inventory
		try {
			this.inventory.removeItem(lastCraftedItem, quantityCrafted);
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException("The last crafted item was not found in the inventory");
		} catch (OutOfRangeException e) {
			// With a crafted items quantity greater than 1, it's never throw an
			// OutOfRangeException.
		}

		// Remove crafted item from the crafting history
		lastCraftedItem = this.history.removeLastItem();
		Recipe usedRecipe = lastCraftedItem.getUsedRecipe();
		List<Ingredient> usedIngredients = usedRecipe.getIngredients();

		// Add used ingredients
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
