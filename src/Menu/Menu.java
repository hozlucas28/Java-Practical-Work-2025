package Menu;

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

/**
 * {@code Menu} class provides a command-line interface for interacting with a
 * crafting system. It allows users to select items to craft, manage inventory,
 * view and craft recipes, and interact with a Prolog service for advanced
 * crafting queries.
 *
 * <p>
 * This class handles user input, displays available operations, and delegates
 * crafting logic to the underlying {@link CraftingSystem}, {@link Inventory},
 * {@link ItemsRepository}, and {@link PrologService}.
 * </p>
 */
public class Menu {
	private final Scanner scanner;

	private final Inventory inventory;
	private final ItemsRepository itemsRepository;

	private Item itemToCraft;
	private int quantityToCraft;
	private final CraftingSystem craftingSystem;

	private final PrologService prologService;

	/**
	 * Constructs a new {@code Menu} with the specified dependencies and initial
	 * crafting selection.
	 *
	 * @param scanner         scanner for user input
	 * @param inventory       user's inventory
	 * @param itemsRepository repository of items and recipes
	 * @param itemToCraft     initial item to craft (can be {@code null})
	 * @param quantityToCraft initial quantity to craft
	 * @param craftingSystem  crafting system
	 * @param prologService   Prolog service
	 */
	public Menu(Scanner scanner, Inventory inventory, ItemsRepository itemsRepository, Item itemToCraft,
			int quantityToCraft, CraftingSystem craftingSystem, PrologService prologService) {
		this.scanner = scanner;

		this.inventory = inventory;
		this.itemsRepository = itemsRepository;

		this.itemToCraft = itemToCraft;
		this.quantityToCraft = quantityToCraft;
		this.craftingSystem = craftingSystem;

		this.prologService = prologService;
	}

	private void setItemToCraft() {
		String itemName = null;
		boolean isCraftable = true;

		do {
			do {
				System.out.print("> Which item do you want to craft? ");
				itemName = this.scanner.nextLine().toLowerCase().trim();
				this.itemToCraft = this.itemsRepository.getItem(itemName);

				if (this.itemToCraft == null) {
					System.out.printf("> %s item was not found within items repository. Try again...\n",
							StringTransformers.toCapitalize(itemName));
				}
			} while (itemToCraft == null);

			do {
				System.out.printf("> How many %ss do you want to craft as minimum? ", itemName);

				try {
					this.quantityToCraft = this.scanner.nextInt();

					if (this.quantityToCraft < 1) {
						System.out.println("> Error! Quantity to craft must be greater or equal to 1. Try again...");
					}
				} catch (NoSuchElementException e) {
					this.quantityToCraft = 0;
					System.out
							.println("> Error! Quantity to craft must be a number greater or equal to 1. Try again...");
				} finally {
					this.scanner.nextLine();
				}
			} while (this.quantityToCraft < 1);

			try {
				this.craftingSystem.setItemToCraft(this.itemToCraft, this.quantityToCraft);
				isCraftable = true;
			} catch (InvalidItemException e) {
				isCraftable = false;
				System.out.printf("\n> %s is a base item! So, it can not be set to be a craftable one.\n\n", itemName);
			}
		} while (!isCraftable);
	}

	/**
	 * Starts the menu loop, displaying available operations and handling user
	 * selections.
	 */
	public void init() {
		if (this.itemToCraft == null) {
			this.setItemToCraft();
		}

		int operation = 0;
		String itemToCraftName = this.itemToCraft.getName();

		int branch;

		do {
			System.out.println();
			operation = requestOperation(itemToCraftName, this.quantityToCraft);
			System.out.println();

			switch (operation) {
			case 1:
				try {
					int recipeToCraft = this.requestRecipeToCraft();
					CraftedItem craftedItem = this.craftingSystem.craftItem(recipeToCraft);

					int itemsCrafted = craftedItem.getQuantityCrafted();
					int craftingTimeInMilliseconds = craftedItem.getCraftingTimeInMilliseconds();

					System.out.printf("> %d %ss crafted in %d milliseconds.\n", itemsCrafted, itemToCraftName,
							craftingTimeInMilliseconds);
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
				String inventorySavePath = this.requestInventorySavePath();

				try {
					this.inventory.storeOnJSON(inventorySavePath);
					System.out.println("> Inventory saved.");
				} catch (IOException e) {
					System.out.printf("> An error occurred on try to save the inventory within \"%s\" file.\n",
							inventorySavePath);
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
				System.out.println("> Items repository:\n");
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

			case 15:
				String prologPath = this.requestPrologPath();

				try {
					this.prologService.toFile(prologPath);
					System.out.println("> Prolog file created.");
				} catch (IOException e) {
					System.out.printf("> An error occurred on try to write prolog content within \"%s\" file.\n",
							prologPath);
				}
				break;
			}
		} while (operation != 0);
	}

	private int requestOperation(String item, int quantity) {
		int operation = -1;

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
			System.out.println("  12 - Show items repository");
			System.out.println("  13 - Show repository craftable items");
			System.out.println("  14 - Show craftable items with inventory items (Prolog service)");
			System.out.println("  15 - Create a Prolog file with items repository, inventory, and all the necessary facts and rules");
			System.out.println("  0  - Exit\n");
			// @formatter:on

			System.out.printf("> Select an operation: ");

			try {
				operation = this.scanner.nextInt();
			} catch (NoSuchElementException e) {
				// Ignore the exception occurrence, just continue requesting the operation.
			} finally {
				this.scanner.nextLine();
			}

			if (operation < 0 || operation > 15) {
				System.out.printf("> %d is an invalid operation! Try again...\n\n", operation);
			}
		} while (operation < 0 || operation > 15);

		return operation;
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

	private int requestRecipeToCraft() {
		int recipe = 0;
		int maxRecipe = this.itemToCraft.getRecipes().size();

		if (maxRecipe < 2) {
			return 0;
		}

		do {
			System.out.printf("> Enter the recipe number to craft: ");

			try {
				recipe = this.scanner.nextInt();

				if (recipe < 0 || recipe > maxRecipe) {
					System.out.printf("> Invalid recipe, it should be between 0 and %d (included). Try again...\n\n",
							maxRecipe);
				}
			} catch (NoSuchElementException e) {
				recipe = 0;
				System.out.printf(
						"> Invalid recipe, it should be a number between 0 and %d (included). Try again...\n\n",
						maxRecipe);
			} finally {
				this.scanner.nextLine();
			}
		} while (recipe < 0 || recipe > maxRecipe);

		return recipe - 1;
	}

	private String requestPrologPath() {
		String savePath = "";

		do {
			System.out.printf("> Enter the file path where do you want to create the Prolog file: ");
			savePath = this.scanner.nextLine().trim();

			if (!savePath.endsWith(".pl")) {
				System.out.printf("> \"%s\" is an invalid Prolog path. Try again (for example \"prolog_file.pl\")...\n",
						savePath);
			}
		} while (!savePath.endsWith(".pl"));

		return savePath;
	}

	private String requestInventorySavePath() {
		String savePath = "";

		do {
			System.out.printf("> Enter the file path where do you want to store the inventory: ");
			savePath = this.scanner.nextLine().trim();

			if (!savePath.endsWith(".json")) {
				System.out.printf(
						"> \"%s\" is an invalid JSON path. Try again (for example \"final_inventory.json\")...\n",
						savePath);
			}
		} while (!savePath.endsWith(".json"));

		return savePath;
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
}
