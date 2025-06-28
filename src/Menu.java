
import java.util.Scanner;

import craftingSystem.CraftingSystem;
import exceptions.InvalidItemException;
import inventory.Inventory;
import inventory.Item;
import repositories.ItemsRepository;
import utilities.StringTransformers;

class Menu {
	private final Scanner inputScanner;

	private final Inventory playerInventory;
	private final ItemsRepository itemsRepository;
	private final CraftingSystem craftingSystem;

	public Menu(Inventory playerInventory, ItemsRepository itemsRepository) {
		this.inputScanner = new Scanner(System.in);

		this.playerInventory = playerInventory;
		this.itemsRepository = itemsRepository;
		this.craftingSystem = new CraftingSystem(this.playerInventory);
	}

	public void init() {
		Item itemToCraft;
		int quantityToCraft;

		boolean isCraftableItem = true;

		do {
			itemToCraft = this.requestItemToCraft();
			quantityToCraft = this.requestQuantityToCraft(itemToCraft.getName());

			try {
				this.craftingSystem.setItemToCraft(itemToCraft, quantityToCraft);
			} catch (InvalidItemException e) {
				isCraftableItem = false;
				System.out.printf("\n> %s is a base item! So, it can not be set to be a craftable one.\n\n",
						StringTransformers.toCapitalize(itemToCraft.getName()));
			}
		} while (!isCraftableItem);

		// TODO: call `selectCraftingSystemOption()` method
		// TODO: based on selected option call the appropriate method
		
		this.inputScanner.close();
	}

	private Item requestItemToCraft() {
		String itemName = "";
		Item itemToCraft = null;

		do {
			System.out.print("> Which item do you want to craft? ");
			itemName = this.inputScanner.nextLine().toLowerCase();
			itemToCraft = this.itemsRepository.getItem(itemName);

			if (itemToCraft == null) {
				System.out.printf("> %s item was not found within items repository. Try again...\n", itemName);
			}
		} while (itemToCraft == null);

		return itemToCraft;
	}

	private int requestQuantityToCraft(String itemName) {
		int quantityToCraft = 0;

		do {
			System.out.printf("> How many %ss do you want to craft? ", itemName);
			quantityToCraft = this.inputScanner.nextInt();
			this.inputScanner.nextLine();

			if (quantityToCraft < 1) {
				System.out.println("> Error! Quantity to craft must be greater or equal to 1. Try again...");
			}
		} while (quantityToCraft < 1);

		return quantityToCraft;
	}

	private int selectCraftingSystemOption() {
		int selectedOption = 0;

		// @formatter:off
		// TODO
		System.out.println("XXX");
		// @formatter:on

		return selectedOption;
	}
}
