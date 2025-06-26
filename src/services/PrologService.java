package services;

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

class PrologService {
	private final String baseItemFactName;
	private final String ingredientFactName;
	private final String itemInInventoryFactName;

	private final ItemsRepository itemsRepository;
	private final Inventory inventory;

	public PrologService(PrologServiceBuilder builder) {
		this.baseItemFactName = builder.baseItemFactName;
		this.ingredientFactName = builder.ingredientFactName;
		this.itemInInventoryFactName = builder.itemInInventoryFactName;
		this.itemsRepository = builder.itemsRepository;
		this.inventory = builder.inventory;
	}

	public HashMap<Item, Integer> craftableItems() throws IOException {
		HashMap<Item, Integer> craftableItems = new HashMap<Item, Integer>();

		File tempFile = File.createTempFile("prologService__craftableItems", ".tmp.pl");
		String tempFilePath = tempFile.getPath().replace("\\", "/");
		FileWriter tempFileWriter = new FileWriter(tempFile);

		this.toFile(tempFileWriter);
		tempFileWriter.close();

		String consultQuery = String.format("consult(\"%s\")", tempFilePath);
		Query consult = new Query(consultQuery);

		if (!consult.hasSolution()) {
			return craftableItems;
		}

		Variable itemVar = new Variable("Item");
		Variable craftableUnitsVar = new Variable("Craftable_Units");
		Compound goal = new Compound("craftable_items", new Term[] { itemVar, craftableUnitsVar });

		Query query = new Query(goal);

		while (query.hasMoreSolutions()) {
			Map<String, Term> solution = query.nextSolution();
			String item = solution.get("Item").name();
			int craftableUnits = solution.get("Craftable_Units").intValue();

			craftableItems.put(this.itemsRepository.getItem(item), craftableUnits);
		}

		tempFile.delete();

		return craftableItems;
	}

	private void toFile(FileWriter fWriter) throws IOException {
		fWriter.append("\n");

		fWriter.append("% Ingredients\n");
		fWriter.append(this.toProlog(this.itemsRepository));

		fWriter.append("\n\n");

		fWriter.append("% Inventory\n");
		fWriter.append(this.toProlog(this.inventory));

		fWriter.append("\n\n");

		fWriter.append("% Utility rules\n\n");
		fWriter.append(this.utilityRules());
	}

	private String toProlog(ItemsRepository itemsRepository) {
		String prologLine;
		String prologBaseItems = null;
		String prologIngredients = null;

		HashMap<String, Item> repositoryItems = itemsRepository.getItems();

		// Migrate base items and ingredients to prolog
		for (Map.Entry<String, Item> entry : repositoryItems.entrySet()) {
			String itemName = entry.getKey();
			Item item = entry.getValue();

			// Base item to prolog
			if (item.isBase()) {
				prologLine = String.format("%s(\"%s\").", this.baseItemFactName, itemName);
				prologBaseItems = prologBaseItems == null ? prologLine
						: String.format("%s\n%s", prologBaseItems, prologLine);

				continue;
			}

			List<Recipe> itemRecipes = item.getRecipes();

			for (Recipe recipe : itemRecipes) {
				int quantityToCraft = recipe.getQuantityToCraft();
				List<Ingredient> ingredients = recipe.getIngredients();

				// Crafting table to prolog if it's exists
				if (recipe.needsCraftingTable()) {
					Item craftingTable = recipe.getCraftingTable();
					String craftingTableName = craftingTable.getName();
					int craftingTableQuantity = 1;

					prologLine = String.format("%s(\"%s\", %d, \"%s\", %d).", this.ingredientFactName, itemName,
							quantityToCraft, craftingTableName, craftingTableQuantity);
					prologIngredients = prologIngredients == null ? prologLine
							: String.format("%s\n%s", prologIngredients, prologLine);
				}

				// Ingredient to prolog
				for (Ingredient ingredient : ingredients) {
					Item ingredientItem = ingredient.getItem();
					String ingredientName = ingredientItem.getName();
					int ingredientQuantity = ingredient.getQuantity();

					prologLine = String.format("%s(\"%s\", %d, \"%s\", %d).", this.ingredientFactName, itemName,
							quantityToCraft, ingredientName, ingredientQuantity);
					prologIngredients = prologIngredients == null ? prologLine
							: String.format("%s\n%s", prologIngredients, prologLine);
				}
			}
		}

		String prologLines = "";

		if (prologBaseItems != null) {
			prologLines = prologBaseItems;
		}

		if (prologIngredients != null) {
			prologLines = String.format("%s\n\n%s", prologLines, prologIngredients);
		}

		return prologLines;
	}

	private String toProlog(Inventory inventory) {
		String prologLine;
		String prologLines = null;

		HashMap<Item, Integer> inventoryItems = inventory.getItems();

		for (Map.Entry<Item, Integer> entry : inventoryItems.entrySet()) {
			Item item = entry.getKey();
			String itemName = item.getName();
			Integer itemQuantity = entry.getValue();

			prologLine = String.format("%s(\"%s\", %d).", this.itemInInventoryFactName, itemName, itemQuantity);
			prologLines = prologLines == null ? prologLine : String.format("%s\n%s", prologLines, prologLine);
		}

		return prologLines == null ? "" : prologLines;
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
