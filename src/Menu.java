
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

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
import utilities.StringTransformers;

class Menu {
	private final Scanner inputScanner;

	private final Inventory playerInventory;
	private final ItemsRepository itemsRepository;

	private Item itemToCraft = null;
	private int quantityToCraft = 0;
	private final CraftingSystem craftingSystem;

	public Menu(Inventory playerInventory, ItemsRepository itemsRepository) {
		this.inputScanner = new Scanner(System.in);

		this.playerInventory = playerInventory;
		this.itemsRepository = itemsRepository;

		this.itemToCraft = null;
		this.quantityToCraft = 0;
		this.craftingSystem = new CraftingSystem(this.playerInventory);
	}

	public void init() {
		this.setItemToCraft();

		int option = 0;
		String itemToCraftName = this.itemToCraft.getName();

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
				break;

			case 4:
				System.out.printf("> Required ingredients to craft %d %ss:\n\n", this.quantityToCraft, itemToCraftName);
				this.showRequiredIngredients();
				break;

			case 5:
				// TODO: Show required base ingredients to craft
				break;

			case 6:
				// TODO: Show missing ingredients to craft
				break;

			case 7:
				// TODO: Show missing base ingredients to craft
				break;

			case 8:
				System.out.println("> Current inventory:\n");
				System.out.printf("%s\n", this.playerInventory.toString("•", 2));
				break;

			case 9:
				String savePath = this.requestInventorySavePath();

				try {
					this.playerInventory.storeOnJSON(savePath);
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
				// TODO: Show crafteable items
				break;

			case 13:
				System.out.println("> Repository items:\n");
				System.out.println(itemsRepository.toString(new String[] { "•", "•", "◦" }, 2));
				break;
			}
		} while (option != 0);

		this.inputScanner.close();
	}

	private void setItemToCraft() {
		String itemName = "";
		Item itemToCraft = null;
		int quantityToCraft = 0;
		boolean isCraftable = true;

		do {
			do {
				System.out.print("> Which item do you want to craft? ");
				itemName = this.inputScanner.nextLine().toLowerCase();
				itemToCraft = this.itemsRepository.getItem(itemName);

				if (itemToCraft == null) {
					System.out.printf("> %s item was not found within items repository. Try again...\n",
							StringTransformers.toCapitalize(itemName));
				}
			} while (itemToCraft == null);

			do {
				System.out.printf("> How many %ss do you want to craft as minimum? ", itemName);
				quantityToCraft = this.inputScanner.nextInt();
				this.inputScanner.nextLine();

				if (quantityToCraft < 1) {
					System.out.println("> Error! Quantity to craft must be greater or equal to 1. Try again...");
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
		int option = 0;

		do {
			// @formatter:off
			System.out.println("> Available operations:\n");
			System.out.printf("  1  - Craft %d %ss\n", quantity, item);
			System.out.printf("  2  - How many %ss can I craft?\n", item);
			System.out.println("  3  - Change item to craft");
			System.out.printf("  4  - Show required ingredients to craft %d %ss\n", quantity, item);
			System.out.printf("  5  - Show required base ingredients to craft %d %ss\n", quantity, item);
			System.out.printf("  6  - Show missing ingredients to craft %d %ss\n", quantity, item);
			System.out.printf("  7  - Show missing base ingredients to craft %d %ss\n", quantity, item);
			System.out.println("  8  - Show inventory");
			System.out.println("  9  - Save inventory");
			System.out.println("  10 - Show crafting history");
			System.out.println("  11 - Undo last craft");
			System.out.println("  12 - Show crafteable items");
			System.out.println("  13 - Show repository items");
			System.out.println("  0  - Exit\n");
			// @formatter:on

			System.out.printf("> Select an operation: ");
			option = this.inputScanner.nextInt();
			this.inputScanner.nextLine();

			if (option < 0 || option > 13) {
				System.out.printf("> %d is an invalid operation! Try again...\n\n", option);
			}
		} while (option < 0 || option > 13);

		return option;
	}

	private String requestInventorySavePath() {
		String savePath = "";

		do {
			System.out.printf("> Enter the file path where do you want to store the inventory: ");
			savePath = this.inputScanner.nextLine();

			if (!savePath.endsWith(".json")) {
				System.out.printf(
						"> \"%s\" is an invalid JSON path. Try again (for example \"my_saved_inventory.json\")...\n",
						savePath);
			}
		} while (!savePath.endsWith(".json"));

		return savePath;
	}

	private void showRequiredIngredients() {
		HashMap<Recipe, List<Ingredient>> requiredIngredients = this.craftingSystem.getRequiredIngredients();

		int i = 1;

		for (List<Ingredient> ingredients : requiredIngredients.values()) {
			System.out.printf("> Recipe #%d: ", i);

			for (int j = 0; j < ingredients.size(); j++) {
				Ingredient ingredient = ingredients.get(j);
				String ingredientName = ingredient.getItem().getName();
				int ingredientQuantity = ingredient.getQuantity();

				if (j == ingredients.size() - 1) {
					System.out.printf("%s%ss (x%d).\n", j == 0 ? "" : "and ", ingredientName, ingredientQuantity);
				} else {
					System.out.printf("%ss (x%d), ", ingredientName, ingredientQuantity);
				}
			}

			i++;
		}
	}
}
