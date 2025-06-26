package craftingSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import exceptions.ItemNotFoundException;
import exceptions.NonCraftableItemException;
import exceptions.OutOfRangeException;
import inventory.Inventory;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;

public class CraftingSystem {
	private final Inventory inventory;
	private HashMap<Item, Integer> itemsToCraft;
	private final CraftingHistory history;

	public CraftingSystem(Inventory inventory) {
		this.inventory = inventory;
		this.history = new CraftingHistory();
	}

	public List<CraftedItem> getCraftedItems() {
		return this.history.getItems();
	}

	public HashMap<Item, Integer> getCraftableUnits() {
		HashMap<Item, Integer> craftableUnits = new HashMap<Item, Integer>();
		HashMap<Item, HashMap<Recipe, List<Ingredient>>> requiredIngredientsPerItem = this.getRequiredIngredients();

		for (Map.Entry<Item, Integer> itemEntry : this.itemsToCraft.entrySet()) {
			Item itemToCraft = itemEntry.getKey();

			int itemCraftableUnits = 0;
			HashMap<Recipe, List<Ingredient>> itemRecipes = requiredIngredientsPerItem.get(itemToCraft);

			for (Map.Entry<Recipe, List<Ingredient>> recipeEntry : itemRecipes.entrySet()) {
				Recipe recipe = recipeEntry.getKey();
				List<Ingredient> ingredients = recipeEntry.getValue();

				Item recipeCraftingTable = recipe.getCraftingTable().orElseGet(() -> null);
				int recipeQuantityToCraft = recipe.getQuantityToCraft();

				Integer possibleCraftableUnits = null;

				for (Ingredient ingredient : ingredients) {
					Item item = ingredient.getItem();
					int quantity = ingredient.getQuantity();
					int quantityInInventory = this.inventory.getItemQuantity(item);

					int ingredientUnits = quantityInInventory / quantity;

					if (item == recipeCraftingTable) {
						if (quantityInInventory < 1) {
							possibleCraftableUnits = 0;
							break;
						}
					} else {
						possibleCraftableUnits = possibleCraftableUnits == null ? ingredientUnits
								: Math.min(possibleCraftableUnits, ingredientUnits);
					}
				}

				int totalCraftable = possibleCraftableUnits == null ? 0
						: possibleCraftableUnits * recipeQuantityToCraft;

				itemCraftableUnits = Math.max(itemCraftableUnits, totalCraftable);
			}

			craftableUnits.put(itemToCraft, itemCraftableUnits);
		}

		return craftableUnits;
	}

	public HashMap<Item, HashMap<Recipe, List<Ingredient>>> getMissingIngredients() {
		return this.getMissingIngredients(this.getRequiredIngredients());
	}

	public HashMap<Item, HashMap<Recipe, List<Ingredient>>> getMissingBaseIngredients() {
		// TODO: add parameter to select which path of recipes the algorithm should follow
		return this.getMissingIngredients(this.getRequiredBaseIngredients());
	}

	private HashMap<Item, HashMap<Recipe, List<Ingredient>>> getMissingIngredients(
			HashMap<Item, HashMap<Recipe, List<Ingredient>>> ingredients) {
		HashMap<Item, HashMap<Recipe, List<Ingredient>>> missingIngredients = new HashMap<Item, HashMap<Recipe, List<Ingredient>>>();

		for (Map.Entry<Item, HashMap<Recipe, List<Ingredient>>> entry01 : ingredients.entrySet()) {
			Item item = entry01.getKey();
			HashMap<Recipe, List<Ingredient>> itemRecipes = entry01.getValue();

			HashMap<Recipe, List<Ingredient>> missingIngredientsPerRecipe = new HashMap<Recipe, List<Ingredient>>();

			for (Map.Entry<Recipe, List<Ingredient>> entry02 : itemRecipes.entrySet()) {
				Recipe recipe = entry02.getKey();
				List<Ingredient> recipeIngredients = entry02.getValue();

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

				Optional<Item> craftingTable = recipe.getCraftingTable();

				if (craftingTable.isPresent() && this.inventory.getItemQuantity(craftingTable.get()) < 1) {
					Ingredient table = new Ingredient(craftingTable.get(), 1);
					missingRecipeIngredients.add(table);
				}

				missingIngredientsPerRecipe.put(recipe, missingRecipeIngredients);
			}

			missingIngredients.put(item, missingIngredientsPerRecipe);
		}

		return missingIngredients;
	}

	public HashMap<Item, HashMap<Recipe, List<Ingredient>>> getRequiredIngredients() {
		HashMap<Item, HashMap<Recipe, List<Ingredient>>> requiredIngredients = new HashMap<Item, HashMap<Recipe, List<Ingredient>>>();

		for (Map.Entry<Item, Integer> entry : this.itemsToCraft.entrySet()) {
			Item item = entry.getKey();
			int quantityToCraft = entry.getValue();

			List<Recipe> recipes = item.getRecipes();
			HashMap<Recipe, List<Ingredient>> ingredientsPerRecipe = new HashMap<Recipe, List<Ingredient>>();

			if (quantityToCraft > 0) {
				for (Recipe recipe : recipes) {
					List<Ingredient> recipeIngredients = recipe.getIngredients();
					int recipeQuantityToCraft = recipe.getQuantityToCraft();

					List<Ingredient> realRecipeIngredients = new ArrayList<Ingredient>();

					for (Ingredient ingredient : recipeIngredients) {
						int realQuantity = (int) Math.ceil(quantityToCraft / (double) recipeQuantityToCraft)
								* ingredient.getQuantity();
						Ingredient realIngredient = new Ingredient(ingredient.getItem(), realQuantity);

						realRecipeIngredients.add(realIngredient);
					}

					Optional<Item> craftingTable = recipe.getCraftingTable();

					if (craftingTable.isPresent()) {
						Ingredient table = new Ingredient(craftingTable.get(), 1);
						realRecipeIngredients.add(table);
					}

					ingredientsPerRecipe.put(recipe, realRecipeIngredients);
				}
			}

			requiredIngredients.put(item, ingredientsPerRecipe);
		}

		return requiredIngredients;
	}

	public HashMap<Item, HashMap<Recipe, List<Ingredient>>> getRequiredBaseIngredients() {
		HashMap<Item, HashMap<Recipe, List<Ingredient>>> requiredBaseIngredients = new HashMap<Item, HashMap<Recipe, List<Ingredient>>>();

		for (Map.Entry<Item, Integer> entry : this.itemsToCraft.entrySet()) {
			Item item = entry.getKey();
			int quantityToCraft = entry.getValue();

			List<Recipe> recipes = item.getRecipes();
			HashMap<Recipe, List<Ingredient>> ingredientsPerRecipe = new HashMap<>();

			if (quantityToCraft > 0) {
				for (Recipe recipe : recipes) {
					List<Ingredient> baseIngredients = getBaseIngredientsRecursive(recipe, quantityToCraft);
					ingredientsPerRecipe.put(recipe, baseIngredients);
				}
			}

			requiredBaseIngredients.put(item, ingredientsPerRecipe);
		}

		return requiredBaseIngredients;
	}

	private List<Ingredient> getBaseIngredientsRecursive(Recipe recipe, int totalToCraft) {
		List<Ingredient> ingredients = recipe.getIngredients();

		Map<Item, Integer> baseCount = new HashMap<Item, Integer>();
		List<Ingredient> baseIngredients = new ArrayList<Ingredient>();

		int craftsNeeded = (int) Math.ceil(totalToCraft / (double) recipe.getQuantityToCraft());

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

					baseCount.put(baseItem, baseCount.getOrDefault(baseItem, 0) + baseItemQuantity);
				}
			}
		}

		Optional<Item> craftingTable = recipe.getCraftingTable();

		if (craftingTable.isPresent()) {
			Item table = craftingTable.get();
			baseCount.put(table, 1);
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
		HashMap<Item, HashMap<Recipe, List<Ingredient>>> missingIngredientsPerItem = this.getMissingIngredients();

		for (HashMap<Recipe, List<Ingredient>> missingIngredientsPerRecipe : missingIngredientsPerItem.values()) {
			for (List<Ingredient> missingIngredients : missingIngredientsPerRecipe.values()) {
				if (!missingIngredients.isEmpty()) {
					return false;
				}
			}
		}

		return true;
	};

	public CraftingSystem setItemsToCraft(HashMap<Item, Integer> items) {
		this.itemsToCraft = items;
		return this;
	}

	public void craftItems() throws NonCraftableItemException {
		HashMap<Item, HashMap<Recipe, List<Ingredient>>> missingIngredientsPerItem = this.getMissingIngredients();

		HashMap<Item, Recipe> recipesToCraftPerItem = new HashMap<Item, Recipe>();

		for (Map.Entry<Item, HashMap<Recipe, List<Ingredient>>> itemEntry : missingIngredientsPerItem.entrySet()) {
			Item itemToCraft = itemEntry.getKey();
			HashMap<Recipe, List<Ingredient>> recipes = itemEntry.getValue();

			Recipe firstEmptyRecipe = null;

			for (Map.Entry<Recipe, List<Ingredient>> recipeEntry : recipes.entrySet()) {
				Recipe recipe = recipeEntry.getKey();
				List<Ingredient> missingIngredients = recipeEntry.getValue();

				if (missingIngredients.isEmpty()) {
					firstEmptyRecipe = recipe;
					break;
				}
			}

			if (this.itemsToCraft.get(itemToCraft) > 0 && firstEmptyRecipe == null) {
				String errorMessage = String.format(
						"Inventory does not have the necessary ingredients to craft %d of \"%s\" items with any recipe.",
						this.itemsToCraft.get(itemToCraft), itemToCraft.getName());

				throw new NonCraftableItemException(errorMessage);
			}

			if (this.itemsToCraft.get(itemToCraft) > 0) {
				recipesToCraftPerItem.put(itemToCraft, firstEmptyRecipe);
			}
		}

		for (Map.Entry<Item, Recipe> entry : recipesToCraftPerItem.entrySet()) {
			Item itemToCraft = entry.getKey();
			Recipe recipeToCraft = entry.getValue();

			List<Ingredient> ingredientsToRemove = recipeToCraft.getBaseIngredients();

			for (Ingredient ingredient : ingredientsToRemove) {
				Item itemToRemove = ingredient.getItem();
				int quantityToRemove = ingredient.getQuantity();

				try {
					this.inventory.removeItem(itemToRemove, quantityToRemove);
				} catch (ItemNotFoundException | OutOfRangeException e) {
					// TODO
				}
			}

			int quantityToCraft = recipeToCraft.getQuantityToCraft()
					* (int) Math.ceil(this.itemsToCraft.get(itemToCraft) / (double) recipeToCraft.getQuantityToCraft());

			try {
				this.inventory.addItem(itemToCraft, quantityToCraft);
				this.history.addItem(itemToCraft, recipeToCraft, quantityToCraft);
			} catch (OutOfRangeException e) {
				// TODO
			}
		}
	}

	public void undoLastCraft() throws ItemNotFoundException {
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
	}
}
