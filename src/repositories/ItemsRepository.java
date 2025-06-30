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

public class ItemsRepository {
	private final HashMap<String, Item> items;

	public ItemsRepository(HashMap<String, Item> items) {
		this.items = items;
	}

	public HashMap<String, Item> getItems() {
		return this.items;
	}

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

	public Item getItem(String name) {
		Item item = this.items.get(name);
		return item;
	}

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
			if (item.isBase()) {
				continue;
			}

			List<Recipe> itemRecipes = item.getRecipes();
			for (Recipe recipe : itemRecipes) {
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

				recipe.setIngredients(realIngredients);
			}
		}

		// Create repository
		ItemsRepository itemsRepository = new ItemsRepository(items);

		return itemsRepository;
	}

	public String toString(String itemMarkers[], int lPadding) {
		StringBuilder builder = new StringBuilder();
		Formatter formatter = new Formatter(builder);

		HashMap<String, Item> basicItems = new HashMap<String, Item>();
		HashMap<String, Item> complexItems = new HashMap<String, Item>();

		for (Map.Entry<String, Item> itemEntry : this.items.entrySet()) {
			String itemName = itemEntry.getKey();
			Item item = itemEntry.getValue();

			if (item.isBase()) {
				basicItems.put(itemName, item);
			} else {
				complexItems.put(itemName, item);
			}
		}

		for (String itemName : basicItems.keySet()) {
			formatter.format("%" + lPadding + "s%s ", " ", itemMarkers[0]);
			formatter.format("%s\n", StringTransformers.toTitle(itemName));
		}

		for (Map.Entry<String, Item> itemEntry : complexItems.entrySet()) {
			String itemName = itemEntry.getKey();
			Item item = itemEntry.getValue();

			formatter.format("%" + lPadding + "s%s ", " ", itemMarkers[0]);
			formatter.format("%s\n", StringTransformers.toTitle(itemName));

			List<Recipe> itemRecipes = item.getRecipes();
			int itemRecipesLength = itemRecipes.size();

			for (int i = 0; i < itemRecipesLength; i++) {
				Recipe recipe = itemRecipes.get(i);

				List<Ingredient> ingredients = recipe.getIngredients();
				int quantityToCraft = recipe.getQuantityToCraft();
				int timeToCraftInMilliseconds = recipe.getTimeToCraftInMilliseconds();

				formatter.format("%" + lPadding * 2 + "s%s ", " ", itemMarkers[1]);

				formatter.format("Recipe #%d (x%d ~ %d milliseconds)\n", i + 1, quantityToCraft,
						timeToCraftInMilliseconds);

				if (recipe.needsCraftingTable()) {
					String craftingTable = recipe.getCraftingTable().getName();

					formatter.format("%" + lPadding * 3 + "s%s ", " ", itemMarkers[2]);
					formatter.format("%s (x1)\n", StringTransformers.toTitle(craftingTable));
				}

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
