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
import exceptions.OutOfRangeException;
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

	public Item getItem(String name) {
		Item item = this.items.get(name);
		return item;
	}

	public static ItemsRepository loadFromJSON(String path) throws FileNotFoundException, JsonIOException,
			JsonSyntaxException, ItemNotFoundException, OutOfRangeException, IOException {
		FileInputStream fileStream = new FileInputStream(path);
		InputStreamReader streamReader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);

		JsonArray json = JsonParser.parseReader(streamReader).getAsJsonArray();

		HashMap<String, Item> items = new HashMap<String, Item>();
		HashMap<String, List<JSONRecipe>> recipesPerItem = new HashMap<String, List<JSONRecipe>>();

		for (JsonElement jsonItemElement : json) {
			JsonObject jsonItemElementAsObj = jsonItemElement.getAsJsonObject();

			// Get item
			String itemName = jsonItemElementAsObj.get("name").getAsString().trim();
			Item item = new Item(itemName);

			// Put item in map
			items.put(itemName, item);

			if (jsonItemElementAsObj.has("recipes")) {
				JsonArray jsonItemElementRecipes = jsonItemElementAsObj.getAsJsonArray("recipes");

				List<JSONRecipe> jsonItemRecipes = new ArrayList<JSONRecipe>();

				// Get item recipes
				for (JsonElement jsonItemElementRecipe : jsonItemElementRecipes) {
					JsonObject jsonItemElementRecipeAsObj = jsonItemElementRecipe.getAsJsonObject();
					JsonObject jsonItemElementRecipeIngredients = jsonItemElementRecipeAsObj
							.getAsJsonObject("ingredients");

					// Get ingredients
					HashMap<String, Integer> ingredients = new HashMap<String, Integer>();

					for (String ingredientName : jsonItemElementRecipeIngredients.keySet()) {
						int ingredientQuantity = jsonItemElementRecipeIngredients.get(ingredientName).getAsInt();
						ingredients.put(ingredientName, ingredientQuantity);
					}

					// Get time to craft in milliseconds
					int timeToCraftInMilliseconds = jsonItemElementRecipeAsObj.get("time_to_craft").getAsInt();

					// Get items to craft
					int quantityToCraft = jsonItemElementRecipeAsObj.get("quantity_to_craft").getAsInt();

					// Get crafting table if it's exists
					Item craftingTable = null;

					if (jsonItemElementRecipeAsObj.has("crafting_table")) {
						String craftingTableName = jsonItemElementRecipeAsObj.get("crafting_table").getAsString()
								.trim();
						craftingTable = new Item(craftingTableName);
					}

					// Append recipe to list
					JSONRecipe jsonRecipe = new JSONRecipe(craftingTable, ingredients, timeToCraftInMilliseconds,
							quantityToCraft);

					jsonItemRecipes.add(jsonRecipe);
				}

				// Put recipes in map
				recipesPerItem.put(itemName, jsonItemRecipes);
			}
		}

		streamReader.close();
		fileStream.close();

		// Assign and link items with recipes
		JSONRecipe.linkItemsWithRecipes(items, recipesPerItem);

		// Create repository
		ItemsRepository itemsRepository = new ItemsRepository(items);

		return itemsRepository;
	}

	public String toString(String itemMarkers[], int lPadding) {
		StringBuilder builder = new StringBuilder();
		Formatter formatter = new Formatter(builder);

		for (Map.Entry<String, Item> itemEntry : this.items.entrySet()) {
			String itemName = itemEntry.getKey();
			Item item = itemEntry.getValue();

			formatter.format("%" + lPadding + "s%s ", " ", itemMarkers[0]);
			formatter.format(StringTransformers.toTitle(itemName));

			if (item.isBase()) {
				formatter.format("\n");
			} else {
				formatter.format(":\n");
				List<Recipe> itemRecipes = item.getRecipes();

				for (int i = 0; i < itemRecipes.size(); i++) {
					Recipe recipe = itemRecipes.get(i);

					int quantityToCraft = recipe.getQuantityToCraft();
					List<Ingredient> ingredients = recipe.getIngredients();

					formatter.format("%" + lPadding * 2 + "s%s ", " ", itemMarkers[1]);

					if (quantityToCraft == 1) {
						formatter.format("Recipe #%d:\n", i + 1);
					} else {
						formatter.format("Recipe #%d (x%d):\n", i + 1, quantityToCraft);
					}

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
