
import inventory.Inventory;
import repositories.ItemsRepository;

class Menu {
	private final Inventory playerInventory;
	private final ItemsRepository itemsRepository;

	public Menu(Inventory playerInventory, ItemsRepository itemsRepository) {
		this.playerInventory = playerInventory;
		this.itemsRepository = itemsRepository;
	}

	public void init() {
		// TODO: request items to craft (name and quantity)
		// TODO: call `selectCraftingSystemOption()` method
		// TODO: based on selected option call the appropriate method
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
