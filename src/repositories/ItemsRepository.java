package repositories;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import exceptions.ItemNotFoundException;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;
import utilities.StringTransformers;

/**
 * Repository class for managing a collection of {@link Item} objects. Provides
 * methods to retrieve, filter, and load items from a JSON file, as well as to
 * format the repository contents as a string.
 */
public class ItemsRepository {
	private final HashMap<String, Item> items;

	public ItemsRepository(HashMap<String, Item> items) {
		this.items = items;
	}

	/**
	 * @return a {@link HashMap} of item names to {@link Item} objects
	 */
	public HashMap<String, Item> getItems() {
		return this.items;
	}

	/**
	 * @return a {@link HashMap} of craftable item names (non-base items) to
	 *         {@link Item} objects
	 */
	public HashMap<String, Item> getCraftableItems() {
		HashMap<String, Item> craftableItems = new HashMap<String, Item>();

		for (Map.Entry<String, Item> itemEntry : this.items.entrySet()) {
			String itemName = itemEntry.getKey();
			Item item = itemEntry.getValue();

			if (!item.isBase()) {
				craftableItems.put(itemName, item);
			}
		}

		return craftableItems;
	}

	/**
	 * Retrieves an item by its name.
	 *
	 * @param name item name to retrieve
	 * @return the {@link Item} with the specified name, or {@code null} if not
	 *         found
	 */
	public Item getItem(String name) {
		Item item = this.items.get(name);
		return item;
	}

	/**
	 * Loads an {@code ItemsRepository} from a JSON file at the specified path. The
	 * JSON must define items and their recipes, which are parsed and linked.
	 *
	 * @param path file path to the JSON file
	 * @return a new {@code ItemsRepository} instance loaded from the JSON file
	 * @throws FileNotFoundException if the file does not exist
	 * @throws JsonIOException       if there is an I/O error during JSON parsing
	 * @throws JsonSyntaxException   if the JSON is malformed
	 * @throws ItemNotFoundException if a referenced item is not found in the items
	 *                               map
	 * @throws IOException           if an I/O error occurs
	 */
	public static ItemsRepository loadFromJSON(String path)
			throws FileNotFoundException, JsonIOException, JsonSyntaxException, ItemNotFoundException, IOException {
		FileInputStream fileStream = new FileInputStream(path);
		InputStreamReader streamReader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);

		JsonArray json = JsonParser.parseReader(streamReader).getAsJsonArray();

		HashMap<String, Item> items = new HashMap<String, Item>();

		// Parse items
		for (JsonElement jsonItemElement : json) {
			JsonObject jsonItemElementAsObj = jsonItemElement.getAsJsonObject();

			// Get name
			String itemName = jsonItemElementAsObj.get("name").getAsString().trim();

			List<Recipe> itemRecipes = null;

			if (jsonItemElementAsObj.has("recipes")) {
				JsonArray jsonItemElementRecipes = jsonItemElementAsObj.getAsJsonArray("recipes");

				itemRecipes = new ArrayList<Recipe>();

				// Get recipes
				for (JsonElement jsonItemElementRecipe : jsonItemElementRecipes) {
					JsonObject jsonItemElementRecipeAsObj = jsonItemElementRecipe.getAsJsonObject();
					JsonObject jsonItemElementRecipeIngredients = jsonItemElementRecipeAsObj
							.getAsJsonObject("ingredients");

					// Get ingredients
					List<Ingredient> ingredients = new ArrayList<Ingredient>();

					for (String ingredientName : jsonItemElementRecipeIngredients.keySet()) {
						int ingredientQuantity = jsonItemElementRecipeIngredients.get(ingredientName).getAsInt();
						Ingredient ingredient = new Ingredient(new Item(ingredientName), ingredientQuantity);
						ingredients.add(ingredient);
					}

					// Get time to craft in milliseconds
					int timeToCraftInMilliseconds = jsonItemElementRecipeAsObj.get("time_to_craft").getAsInt();

					// Get quantity to craft
					int quantityToCraft = jsonItemElementRecipeAsObj.get("quantity_to_craft").getAsInt();

					// Get crafting table if it's exists
					Item craftingTable = null;

					if (jsonItemElementRecipeAsObj.has("crafting_table")) {
						String craftingTableName = jsonItemElementRecipeAsObj.get("crafting_table").getAsString()
								.trim();
						craftingTable = new Item(craftingTableName);
					}

					// Append recipe to list
					Recipe recipe = craftingTable == null
							? new Recipe(ingredients, timeToCraftInMilliseconds, quantityToCraft)
							: new Recipe(craftingTable, ingredients, timeToCraftInMilliseconds, quantityToCraft);

					itemRecipes.add(recipe);
				}
			}

			// Put item in map
			Item item = itemRecipes == null ? new Item(itemName) : new Item(itemName, itemRecipes);
			items.put(itemName, item);
		}

		streamReader.close();
		fileStream.close();

		// Link recipes with items
		for (Item item : items.values()) {
			// Don't link it if it is base item
			if (item.isBase()) {
				continue;
			}

			List<Recipe> itemRecipes = item.getRecipes();
			for (Recipe recipe : itemRecipes) {
				// Link crafting table if it's exists
				if (recipe.needsCraftingTable()) {
					Item craftingTable = recipe.getCraftingTable();
					Item craftingTableRef = items.get(craftingTable.getName());

					if (craftingTableRef == null) {
						String errorMessage = String.format("Item with \"%s\" name was not found within items hash map",
								craftingTable.getName());

						throw new ItemNotFoundException(errorMessage);
					}

					recipe.setCraftingTable(craftingTableRef);
				}

				// Link ingredients with items
				List<Ingredient> ingredients = recipe.getIngredients();
				List<Ingredient> realIngredients = new ArrayList<Ingredient>();

				for (Ingredient ingredient : ingredients) {
					Item ingItem = ingredient.getItem();
					int ingQuantity = ingredient.getQuantity();

					Item ingItemRef = items.get(ingItem.getName());

					if (ingItemRef == null) {
						String errorMessage = String.format("Item with \"%s\" name was not found within items hash map",
								ingItem.getName());

						throw new ItemNotFoundException(errorMessage);

					}

					Ingredient realIngredient = new Ingredient(ingItemRef, ingQuantity);
					realIngredients.add(realIngredient);
				}

				// Set linked ingredients
				recipe.setIngredients(realIngredients);
			}
		}

		// Create repository
		ItemsRepository itemsRepository = new ItemsRepository(items);

		return itemsRepository;
	}

	/**
	 * Returns a formatted string representation of the repository's items and their
	 * recipes.
	 *
	 * @param itemMarkers array with three string markers used for formatting items
	 *                    and recipes.
	 * @param lPadding    left padding to apply for formatting
	 * @return a formatted string representing the items and their recipes
	 */
	public String toString(String itemMarkers[], int lPadding) {
		StringBuilder builder = new StringBuilder();
		Formatter formatter = new Formatter(builder);

		HashMap<String, Item> baseItems = new HashMap<String, Item>();
		HashMap<String, Item> craftableItems = new HashMap<String, Item>();

		// Separate base items from craftable ones
		for (Map.Entry<String, Item> itemEntry : this.items.entrySet()) {
			String itemName = itemEntry.getKey();
			Item item = itemEntry.getValue();

			if (item.isBase()) {
				baseItems.put(itemName, item);
			} else {
				craftableItems.put(itemName, item);
			}
		}

		// Append base items to formatter
		for (String itemName : baseItems.keySet()) {
			formatter.format("%" + lPadding + "s%s ", " ", itemMarkers[0]);
			formatter.format("%s\n", StringTransformers.toTitle(itemName));
		}

		// Append craftable items to formatter
		for (Map.Entry<String, Item> itemEntry : craftableItems.entrySet()) {
			String itemName = itemEntry.getKey();
			Item item = itemEntry.getValue();

			formatter.format("%" + lPadding + "s%s ", " ", itemMarkers[0]);
			formatter.format("%s\n", StringTransformers.toTitle(itemName));

			List<Recipe> itemRecipes = item.getRecipes();
			int itemRecipesLength = itemRecipes.size();

			// Append recipes to formatter
			for (int i = 0; i < itemRecipesLength; i++) {
				Recipe recipe = itemRecipes.get(i);

				List<Ingredient> ingredients = recipe.getIngredients();
				int quantityToCraft = recipe.getQuantityToCraft();
				int timeToCraftInMilliseconds = recipe.getTimeToCraftInMilliseconds();

				formatter.format("%" + lPadding * 2 + "s%s ", " ", itemMarkers[1]);

				formatter.format("Recipe #%d (x%d ~ %d milliseconds)\n", i + 1, quantityToCraft,
						timeToCraftInMilliseconds);

				// Append crafting table to formatter if it's exists
				if (recipe.needsCraftingTable()) {
					String craftingTable = recipe.getCraftingTable().getName();

					formatter.format("%" + lPadding * 3 + "s%s ", " ", itemMarkers[2]);
					formatter.format("%s (x1)\n", StringTransformers.toTitle(craftingTable));
				}

				// Append recipe ingredients to formatter
				for (Ingredient ingredient : ingredients) {
					formatter.format("%s\n", ingredient.toString(itemMarkers[2], lPadding * 3));
				}
			}
		}

		formatter.close();

		return builder.toString().stripTrailing();
	}

	@Override
	public int hashCode() {
		return Objects.hash(items);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		ItemsRepository other = (ItemsRepository) obj;

		return Objects.equals(this.items, other.items);
	}
}
