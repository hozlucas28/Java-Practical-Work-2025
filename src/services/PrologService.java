package services;

import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jpl7.Compound;
import org.jpl7.Query;
import org.jpl7.Term;
import org.jpl7.Variable;

import inventory.Inventory;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;
import repositories.ItemsRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * PrologService class provides functionality to generate Prolog facts and rules
 * from Java objects representing items, recipes, and inventory, and to query
 * which items are craftable given the current inventory using Prolog logic.
 *
 * <p>
 * This service is responsible for:
 *
 * <ul>
 * <li>Serializing items and inventory into Prolog facts.</li>
 * <li>Writing utility rules for crafting logic in Prolog.</li>
 * <li>Interfacing with a Prolog engine to determine craftable items and their
 * quantities.</li>
 * </ul>
 */
public class PrologService {
	private final String baseItemFactName;
	private final String ingredientFactName;
	private final String itemInInventoryFactName;

	private final ItemsRepository itemsRepository;
	private final Inventory inventory;

	/**
	 * Constructs a new {@link PrologService} with the specified fact names,
	 * repository, and inventory.
	 *
	 * @param baseItemFactName        Prolog fact name for base items
	 * @param ingredientFactName      Prolog fact name for ingredients
	 * @param itemInInventoryFactName Prolog fact name for inventory items
	 * @param itemsRepository         items repository
	 * @param inventory               inventory
	 */
	public PrologService(String baseItemFactName, String ingredientFactName, String itemInInventoryFactName,
			ItemsRepository itemsRepository, Inventory inventory) {
		this.baseItemFactName = baseItemFactName;
		this.ingredientFactName = ingredientFactName;
		this.itemInInventoryFactName = itemInInventoryFactName;

		this.itemsRepository = itemsRepository;
		this.inventory = inventory;
	}

	/**
	 * Determines which items are craftable and in what quantities, based on the
	 * inventory. This method generates a temporary Prolog file, consults it, and
	 * queries for craftable items.
	 *
	 * @return craftable items with the maximum number of units that can be crafted
	 * @throws IOException if an I/O error occurs during file operations
	 */
	public HashMap<Item, Integer> craftableItems() throws IOException {
		HashMap<Item, Integer> craftableItems = new HashMap<Item, Integer>();

		// Create temporary Prolog file
		File tempFile = File.createTempFile("prologService__craftableItems", ".tmp.pl");
		String tempFilePath = tempFile.getPath().replace("\\", "/");
		FileWriter tempFileWriter = new FileWriter(tempFile, StandardCharsets.UTF_8);

		this.toFile(tempFileWriter);
		tempFileWriter.close();

		// Clear previous Prolog definitions to avoid redefinition warnings
		new Query(String.format("abolish(%s/1)", this.baseItemFactName)).hasSolution();
		new Query(String.format("abolish(%s/4)", this.ingredientFactName)).hasSolution();
		new Query(String.format("abolish(%s/2)", this.itemInInventoryFactName)).hasSolution();
		new Query("abolish(craftable_items/2)").hasSolution();
		new Query("abolish(maximum_craftable_items/2)").hasSolution();
		new Query("abolish(maximum_craftable_items/3)").hasSolution();

		// Build query
		String consultQuery = String.format("consult(\"%s\")", tempFilePath);
		Query consult = new Query(consultQuery);

		if (!consult.hasSolution()) {
			return craftableItems;
		}

		Variable itemVar = new Variable("Item");
		Variable craftableUnitsVar = new Variable("Craftable_Units");
		Compound goal = new Compound("craftable_items", new Term[] { itemVar, craftableUnitsVar });

		Query query = new Query(goal);

		// Run query
		while (query.hasMoreSolutions()) {
			Map<String, Term> solution = query.nextSolution();
			String item = solution.get("Item").name();
			int craftableUnits = solution.get("Craftable_Units").intValue();

			// Save result
			craftableItems.put(this.itemsRepository.getItem(item), craftableUnits);
		}

		// Delete temporary Prolog file
		tempFile.delete();

		return craftableItems;
	}

	/**
	 * Writes the Prolog representation of the items, inventory, and related rules
	 * into a file at the specified path.
	 *
	 * @param file path
	 * @throws IOException if an I/O error occurs during writing
	 */
	public void toFile(String path) throws IOException {
		FileWriter writer = new FileWriter(path, StandardCharsets.UTF_8);
		this.toFile(writer);
		writer.close();
	}

	private void toFile(FileWriter fWriter) throws IOException {
		fWriter.append("\n");

		fWriter.append("% Ingredients\n\n");
		fWriter.append(this.toProlog(this.itemsRepository));

		fWriter.append("\n");

		fWriter.append("% Inventory\n\n");
		fWriter.append(this.toProlog(this.inventory));

		fWriter.append("\n");

		fWriter.append("% Utility rules\n\n");
		fWriter.append(this.utilityRules());
	}

	private String toProlog(ItemsRepository itemsRepository) {
		HashMap<String, Item> baseItems = new HashMap<String, Item>();
		HashMap<String, Item> craftableItems = new HashMap<String, Item>();

		HashMap<String, Item> repositoryItems = itemsRepository.getItems();

		// Separate base items from craftable ones
		for (Map.Entry<String, Item> itemEntry : repositoryItems.entrySet()) {
			String itemName = itemEntry.getKey();
			Item item = itemEntry.getValue();

			if (item.isBase()) {
				baseItems.put(itemName, item);
			} else {
				craftableItems.put(itemName, item);
			}
		}

		StringBuilder prologLinesBuilder = new StringBuilder();
		Formatter prologLinesFormatter = new Formatter(prologLinesBuilder);

		StringBuilder ingredientsBuilder = new StringBuilder();
		Formatter ingredientsFormatter = new Formatter(ingredientsBuilder);

		// Migrate base items to prolog
		for (String itemName : baseItems.keySet()) {
			prologLinesFormatter.format("%s(\"%s\").\n", this.baseItemFactName, itemName);
		}

		prologLinesFormatter.format("\n");

		// Migrate craftable items to prolog
		for (Map.Entry<String, Item> itemEntry : craftableItems.entrySet()) {
			String itemName = itemEntry.getKey();
			Item item = itemEntry.getValue();

			List<Recipe> itemRecipes = item.getRecipes();

			for (Recipe recipe : itemRecipes) {
				int quantityToCraft = recipe.getQuantityToCraft();
				List<Ingredient> ingredients = recipe.getIngredients();

				// Crafting table to prolog if it's exists
				if (recipe.needsCraftingTable()) {
					Item craftingTable = recipe.getCraftingTable();
					String craftingTableName = craftingTable.getName();
					int craftingTableQuantity = 1;

					ingredientsFormatter.format("%s(\"%s\", %d, \"%s\", %d).\n", this.ingredientFactName, itemName,
							quantityToCraft, craftingTableName, craftingTableQuantity);
				}

				// Ingredient to prolog
				for (Ingredient ingredient : ingredients) {
					Item ingredientItem = ingredient.getItem();
					String ingredientName = ingredientItem.getName();
					int ingredientQuantity = ingredient.getQuantity();

					ingredientsFormatter.format("%s(\"%s\", %d, \"%s\", %d).\n", this.ingredientFactName, itemName,
							quantityToCraft, ingredientName, ingredientQuantity);
				}
			}
		}

		prologLinesFormatter.close();
		ingredientsFormatter.close();

		if (prologLinesBuilder.isEmpty()) {
			prologLinesBuilder.append("\n");
		}

		prologLinesBuilder.append(ingredientsBuilder);

		return prologLinesBuilder.toString();
	}

	private String toProlog(Inventory inventory) {
		StringBuilder prologLinesBuilder = new StringBuilder();
		Formatter prologLinesFormatter = new Formatter(prologLinesBuilder);

		// Migrate inventory items to prolog
		HashMap<Item, Integer> inventoryItems = inventory.getItems();

		for (Map.Entry<Item, Integer> itemEntry : inventoryItems.entrySet()) {
			Item item = itemEntry.getKey();
			String itemName = item.getName();
			Integer itemQuantity = itemEntry.getValue();

			// Item to prolog
			prologLinesFormatter.format("%s(\"%s\", %d).\n", this.itemInInventoryFactName, itemName, itemQuantity);
		}

		prologLinesFormatter.close();

		return prologLinesBuilder.toString();
	}

	private String utilityRules() {
		// @formatter:off
		 String utilityRules = "% Calculate the maximum quantity of craftable items\n"
		 		+ "craftable_items(Item, Maximum_Quantity) :-\n"
				+ String.format("    setof(Ite, Ing^IngQuantity^OutputIte^ingredient(Ite, OutputIte, Ing, IngQuantity), Items),\n", this.ingredientFactName)
				+ "    member(Item, Items),\n"
				+ "    maximum_craftable_items(Item, Maximum_Quantity),\n"
				+ "    Maximum_Quantity > 0.\n"
		 		+ "\n"
				+ "% Helper for calculate the maximum quantity of craftable items of the item\n"
		 		+ "maximum_craftable_items(Item, Maximum_Quantity) :-\n"
		 		+ "    maximum_craftable_items(Item, [], Maximum_Quantity).\n"
		 		+ "\n"
		 		+ "maximum_craftable_items(Item, Items_Visited, Maximum_Quantity) :-\n"
		 		+ "    \\+ member(Item, Items_Visited),\n"
		 		+ "    findall(\n"
		 		+ "        (Output_Quantity, Ingredient, Ingredient_Quantity),\n"
		 		+ String.format("        %s(Item, Output_Quantity, Ingredient, Ingredient_Quantity),\n", this.ingredientFactName)
		 		+ "        Ingredients\n"
		 		+ "    ),\n"
		 		+ "    \n"
		 		+ String.format("    %% For each ingredient, calculate how many times the recipe (%s fact) can be repeated\n", this.ingredientFactName)
		 		+ "    findall(\n"
		 		+ "        Limit,\n"
		 		+ "        (\n"
		 		+ "            member((Output_Quantity, Ingredient, Ingredient_Quantity), Ingredients),\n"
		 		+ "            (\n"
		 		+ String.format("                %s(\n", this.itemInInventoryFactName)
		 		+ "                    Ingredient, Quantity_In_Inventory) -> Maximum_Repetitions is floor(Quantity_In_Inventory / Ingredient_Quantity);\n"
		 		+ "                    maximum_craftable_items(Ingredient, [Item | Items_Visited], Sub_Quantity),\n"
		 		+ "                    Maximum_Repetitions is floor(Sub_Quantity / Ingredient_Quantity\n"
		 		+ "                    )\n"
		 		+ "            ),\n"
		 		+ "            Limit is Maximum_Repetitions * Output_Quantity\n"
		 		+ "        ),\n"
		 		+ "        Limits\n"
		 		+ "    ),\n"
		 		+ "    (\n"
		 		+ "        Limits == [] -> Maximum_Quantity = 0;\n"
		 		+ "        min_list(Limits, Maximum_Quantity)\n"
		 		+ "    ).";
		// @formatter:on

		return utilityRules;
	}
}
