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
		StringBuilder prologLinesBuilder = new StringBuilder();
		Formatter prologLinesFormatter = new Formatter(prologLinesBuilder);

		HashMap<String, Item> repositoryItems = itemsRepository.getItems();

		StringBuilder ingredientsBuilder = new StringBuilder();
		Formatter ingredientsFormatter = new Formatter(ingredientsBuilder);

		// Migrate base items and ingredients to prolog
		for (Map.Entry<String, Item> entry : repositoryItems.entrySet()) {
			String itemName = entry.getKey();
			Item item = entry.getValue();

			// Base item to prolog
			if (item.isBase()) {
				prologLinesFormatter.format("%s(\"%s\").\n", this.baseItemFactName, itemName);
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

		HashMap<Item, Integer> inventoryItems = inventory.getItems();

		for (Map.Entry<Item, Integer> entry : inventoryItems.entrySet()) {
			Item item = entry.getKey();
			String itemName = item.getName();
			Integer itemQuantity = entry.getValue();

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
