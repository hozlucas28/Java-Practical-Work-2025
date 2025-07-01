package Menu;

import java.util.Scanner;

import craftingSystem.CraftingSystem;
import inventory.Inventory;
import inventory.Item;
import repositories.ItemsRepository;
import services.PrologService;

// @formatter:off
/**
 * Builder class for constructing instances of {@link Menu}.
 */
// @formatter:on
public class MenuBuilder {
	private Scanner scanner;

	private Inventory inventory;
	private ItemsRepository itemsRepository;

	private Item itemToCraft;
	private int quantityToCraft;
	private CraftingSystem craftingSystem;

	private PrologService prologService;

	public MenuBuilder setScanner(Scanner scanner) {
		this.scanner = scanner;
		return this;
	}

	public MenuBuilder setInventory(Inventory inventory) {
		this.inventory = inventory;
		return this;
	}

	public MenuBuilder setItemsRepository(ItemsRepository itemsRepository) {
		this.itemsRepository = itemsRepository;
		return this;
	}

	public MenuBuilder setItemToCraft(Item itemToCraft) {
		this.itemToCraft = itemToCraft;
		return this;
	}

	public MenuBuilder setQuantityToCraft(int quantityToCraft) {
		this.quantityToCraft = quantityToCraft;
		return this;
	}

	public MenuBuilder setCraftingSystem(CraftingSystem craftingSystem) {
		this.craftingSystem = craftingSystem;
		return this;
	}

	public MenuBuilder setPrologService(PrologService prologService) {
		this.prologService = prologService;
		return this;
	}

	public Menu build() {
		// @formatter:off
		Menu menu = new Menu(
			this.scanner,
			this.inventory,
			this.itemsRepository,
			this.itemToCraft,
			this.quantityToCraft,
			this.craftingSystem,
			this.prologService
		);
		// @formatter:on

		return menu;
	}
}
