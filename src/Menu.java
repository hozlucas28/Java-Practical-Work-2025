
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;

import craftingSystem.CraftedItem;
import craftingSystem.CraftingSystem;
import exceptions.EmptyHistoryException;
import exceptions.InvalidItemException;
import exceptions.ItemNotFoundException;
import exceptions.NonCraftableItemException;
import inventory.Inventory;
import inventory.Item;
import recipe.Ingredient;
import recipe.Recipe;
import repositories.ItemsRepository;
import services.PrologService;
import utilities.StringTransformers;

class Menu {
	private final Scanner scanner;

	private final Inventory inventory;
	private final ItemsRepository itemsRepository;

	private Item itemToCraft;
	private int quantityToCraft;
	private final CraftingSystem craftingSystem;

	private final PrologService prologService;

	public Menu(Scanner scanner, Inventory inventory, ItemsRepository itemsRepository, PrologService prologService) {
		this.scanner = scanner;

		this.inventory = inventory;
		this.itemsRepository = itemsRepository;

		this.itemToCraft = null;
		this.quantityToCraft = 0;
		this.craftingSystem = new CraftingSystem(this.inventory);

		this.prologService = prologService;
	}

	public void init() {
		this.setItemToCraft();

		int option = 0;
		String itemToCraftName = this.itemToCraft.getName();

		int branch;

		do {
			System.out.println();
			option = requestOperation(itemToCraftName, this.quantityToCraft);
			System.out.println();

			switch (option) {
			case 1:
				try {
					int itemsCrafted = this.craftingSystem.craftItem();

					System.out.printf("> %d %ss crafted.\n", itemsCrafted, itemToCraftName);
				} catch (NonCraftableItemException e) {
					System.out.printf("> You don't have the required ingredients to craft %d %ss.\n",
							this.quantityToCraft, itemToCraftName);
				}
				break;

			case 2:
				int craftableUnits = this.craftingSystem.getCraftableUnits();

				System.out.printf("> With your actual inventory, you can craft %d %ss.\n", craftableUnits,
						itemToCraftName);
				break;

			case 3:
				this.setItemToCraft();
				itemToCraftName = this.itemToCraft.getName();
				break;

			case 4:
				HashMap<Recipe, List<Ingredient>> requiredIngredients = this.craftingSystem.getRequiredIngredients();

				System.out.printf("> Required ingredients to craft %d %ss:\n\n", this.quantityToCraft, itemToCraftName);
				this.showIngredientsCollection(requiredIngredients.values(), (index) -> {
					return String.format("  • Recipe #%d: not requires ingredients.", index + 1);
				});
				break;

			case 5:
				HashMap<Recipe, List<Ingredient>> missingIngredients = this.craftingSystem.getMissingIngredients();

				System.out.printf("> Missing ingredients to craft %d %ss:\n\n", this.quantityToCraft, itemToCraftName);
				this.showIngredientsCollection(missingIngredients.values(), (index) -> {
					return String.format("  • Recipe #%d: no missing ingredients within inventory.", index + 1);
				});
				break;

			case 6:
				branch = this.requestBranch();
				HashMap<Recipe, List<Ingredient>> requiredBaseIngredients = this.craftingSystem
						.getRequiredBaseIngredients(branch);

				System.out.printf("> Required base ingredients to craft %d %ss:\n\n", this.quantityToCraft,
						itemToCraftName);
				this.showIngredientsCollection(requiredBaseIngredients.values(), (index) -> {
					return String.format("  • Recipe #%d: not requires base ingredients.", index + 1);
				});
				break;

			case 7:
				branch = this.requestBranch();
				HashMap<Recipe, List<Ingredient>> missingBaseIngredients = this.craftingSystem
						.getMissingBaseIngredients(branch);

				System.out.printf("\n> Missing base ingredients to craft %d %ss (based on recipe path #%d):\n\n",
						this.quantityToCraft, itemToCraftName, branch + 1);
				this.showIngredientsCollection(missingBaseIngredients.values(), (index) -> {
					return String.format("  • Recipe #%d: no missing base ingredients within inventory.", index + 1);
				});
				break;

			case 8:
				System.out.println("> Current inventory:\n");
				System.out.printf("%s\n", this.inventory.toString("•", 2));
				break;

			case 9:
				String savePath = this.requestInventorySavePath();

				try {
					this.inventory.storeOnJSON(savePath);
					System.out.println("> Inventory saved.");
				} catch (IOException e) {
					System.out.printf("> An error occurred on try to save the inventory within \"%s\" file.\n",
							savePath);
				}
				break;

			case 10:
				List<CraftedItem> craftedItems = this.craftingSystem.getCraftedItems();

				if (craftedItems.size() > 0) {
					System.out.println("> Crafted items:\n");

					for (CraftedItem craftedItem : craftedItems) {
						System.out.printf("• %s\n", craftedItem);
					}
				} else {
					System.out.println("> There are no crafted items within the crafting history.");
				}
				break;

			case 11:
				try {
					CraftedItem lastCraftedItem = this.craftingSystem.undoLastCraft();
					String lastCraftedItemName = lastCraftedItem.getName();

					System.out.printf("> %d %ss were decrafted.\n", lastCraftedItem.getQuantityCrafted(),
							lastCraftedItemName);
				} catch (EmptyHistoryException e) {
					System.out.println("> The crafting history is empty.");
				} catch (ItemNotFoundException e) {
					System.out.println("> The last crafted item was not found in the inventory.");
				}
				break;

			case 12:
				System.out.println("> Repository items:\n");
				System.out.println(itemsRepository.toString(new String[] { "•", "•", "◦" }, 2));
				break;

			case 13:
				HashMap<String, Item> craftableItems = this.itemsRepository.getCraftableItems();

				System.out.println("> Repository craftable items:\n");
				for (String itemName : craftableItems.keySet()) {
					System.out.printf("  • %ss.\n", StringTransformers.toTitle(itemName));
				}
				break;

			case 14:
				try {
					System.out.println("> Craftable items:\n");
					this.showCraftableItemsByProlog();
				} catch (IOException e) {
					System.out.println("> An error occurred on try to communicate with the Prolog service.");
				}
				break;
			}

		} while (option != 0);
	}

	private void setItemToCraft() {
		String itemName = "";
		Item itemToCraft = null;
		int quantityToCraft = 0;
		boolean isCraftable = true;

		do {
			do {
				System.out.print("> Which item do you want to craft? ");
				itemName = this.scanner.nextLine().toLowerCase();
				itemToCraft = this.itemsRepository.getItem(itemName);

				if (itemToCraft == null) {
					System.out.printf("> %s item was not found within items repository. Try again...\n",
							StringTransformers.toCapitalize(itemName));
				}
			} while (itemToCraft == null);

			do {
				System.out.printf("> How many %ss do you want to craft as minimum? ", itemName);

				try {
					quantityToCraft = this.scanner.nextInt();

					if (quantityToCraft < 1) {
						System.out.println("> Error! Quantity to craft must be greater or equal to 1. Try again...");
					}
				} catch (NoSuchElementException e) {
					quantityToCraft = 0;
					System.out
							.println("> Error! Quantity to craft must be a number greater or equal to 1. Try again...");
				} finally {
					this.scanner.nextLine();
				}
			} while (quantityToCraft < 1);

			try {
				this.craftingSystem.setItemToCraft(itemToCraft, quantityToCraft);
				isCraftable = true;
			} catch (InvalidItemException e) {
				isCraftable = false;
				System.out.printf("\n> %s is a base item! So, it can not be set to be a craftable one.\n\n", itemName);
			}
		} while (!isCraftable);

		this.itemToCraft = itemToCraft;
		this.quantityToCraft = quantityToCraft;
	}

	private int requestOperation(String item, int quantity) {
		int option = -1;

		do {
			// @formatter:off
			System.out.println("> Available operations:\n");
			System.out.printf("  1  - Craft %d %ss\n", quantity, item);
			System.out.printf("  2  - How many %ss can I craft?\n", item);
			System.out.println("  3  - Change item to craft");
			System.out.printf("  4  - Show required ingredients to craft %d %ss\n", quantity, item);
			System.out.printf("  5  - Show missing ingredients to craft %d %ss\n", quantity, item);
			System.out.printf("  6  - Show required base ingredients to craft %d %ss\n", quantity, item);
			System.out.printf("  7  - Show missing base ingredients to craft %d %ss\n", quantity, item);
			System.out.println("  8  - Show inventory");
			System.out.println("  9  - Save inventory");
			System.out.println("  10 - Show crafting history");
			System.out.println("  11 - Undo last craft");
			System.out.println("  12 - Show repository items");
			System.out.println("  13 - Show repository craftable items");
			System.out.println("  14 - Show craftable items with inventory items (Prolog service)");
			System.out.println("  0  - Exit\n");
			// @formatter:on

			System.out.printf("> Select an operation: ");

			try {
				option = this.scanner.nextInt();
			} catch (NoSuchElementException e) {
				// Ignore the exception occurrence, just continue requesting the option.
			} finally {
				this.scanner.nextLine();
			}

			if (option < 0 || option > 14) {
				System.out.printf("> %d is an invalid operation! Try again...\n\n", option);
			}
		} while (option < 0 || option > 14);

		return option;
	}

	private String requestInventorySavePath() {
		String savePath = "";

		do {
			System.out.printf("> Enter the file path where do you want to store the inventory: ");
			savePath = this.scanner.nextLine();

			if (!savePath.endsWith(".json")) {
				System.out.printf(
						"> \"%s\" is an invalid JSON path. Try again (for example \"my_saved_inventory.json\")...\n",
						savePath);
			}
		} while (!savePath.endsWith(".json"));

		return savePath;
	}

	private void showIngredientsCollection(Collection<List<Ingredient>> collection,
			Function<Integer, String> onEmptyList) {
		int i = 0;

		for (List<Ingredient> ingredients : collection) {
			int ingredientsLength = ingredients.size();

			if (ingredientsLength > 0) {
				System.out.printf("  • Recipe #%d: ", i + 1);
			} else {
				System.out.println(onEmptyList.apply(i));
			}

			for (int j = 0; j < ingredientsLength; j++) {
				Ingredient ingredient = ingredients.get(j);
				String ingredientName = ingredient.getItem().getName();
				int ingredientQuantity = ingredient.getQuantity();

				if (j == ingredientsLength - 1) {
					System.out.printf("%s%ss (x%d).\n", j == 0 ? "" : "and ", ingredientName, ingredientQuantity);
				} else {
					System.out.printf("%ss (x%d), ", ingredientName, ingredientQuantity);
				}
			}

			i++;
		}
	}

	private int requestBranch() {
		int branch = 0;

		do {
			System.out.printf("> Enter the branch to follow: ");

			try {
				branch = this.scanner.nextInt();

				if (branch < 1) {
					System.out.println("> Invalid branch, it should be greater or equal to 1. Try again...");
				}
			} catch (NoSuchElementException e) {
				branch = 0;
				System.out.println("> Invalid branch, it should be a number greater or equal to 1. Try again...");
			} finally {
				this.scanner.nextLine();
			}
		} while (branch < 1);

		return branch - 1;
	}

	private void showCraftableItemsByProlog() throws IOException {
		HashMap<Item, Integer> craftableItems = this.prologService.craftableItems();

		for (Map.Entry<Item, Integer> craftableItemEntry : craftableItems.entrySet()) {
			Item item = craftableItemEntry.getKey();
			Integer quantity = craftableItemEntry.getValue();

			String itemName = StringTransformers.toTitle(item.getName());

			System.out.printf("  • %ss (x%d).\n", itemName, quantity);
		}
	}
}
