package repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class ItemsRepository {
	private final HashMap<String, Item> items;

	public ItemsRepository(HashMap<String, Item> items) {
		this.items = items;
	};

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
					int timeToCraftInMilliseconds = jsonItemElementRecipeAsObj.get("quantity_to_craft").getAsInt();

					// Get items to craft
					int quantityToCraft = jsonItemElementRecipeAsObj.get("items_to_craft").getAsInt();

					// Get crafting table if it's exists
					Item craftingTable = null;

					if (jsonItemElementRecipeAsObj.has("crafting_table")) {
						String craftingTableName = jsonItemElementRecipeAsObj.get("crafting_table").getAsString()
								.trim();
						craftingTable = new Item(craftingTableName);
					}

					// Append recipe to list
					JSONRecipe jsonRecipe = craftingTable == null
							? new JSONRecipe(ingredients, timeToCraftInMilliseconds, quantityToCraft)
							: new JSONRecipe(ingredients, timeToCraftInMilliseconds, quantityToCraft, craftingTable);

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

		if (this.items.size() != other.items.size()) {
			return false;
		}

		for (String itemName : this.items.keySet()) {
			if (!other.items.containsKey(itemName)
					|| !Objects.equals(this.items.get(itemName), other.items.get(itemName))) {
				return false;
			}
		}

		return true;
	}
}
