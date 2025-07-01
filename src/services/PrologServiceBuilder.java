package services;

import inventory.Inventory;
import repositories.ItemsRepository;

// @formatter:off
/**
 * Builder class for constructing instances of {@link PrologService}.
 * 
 * <p>Usage example:</p>
 * 
 * <pre>
 * PrologService service = new PrologServiceBuilder()
 *     .setBaseItemFactName("baseItem")
 *     .setIngredientFactName("ingredient")
 *     .setItemInInventoryFactName("itemInInventory")
 *     .setItemsRepository(itemsRepository)
 *     .setInventory(inventory)
 *     .build();
 * </pre>
 */
// @formatter:on
public class PrologServiceBuilder {
	private String baseItemFactName;
	private String ingredientFactName;
	private String itemInInventoryFactName;

	private ItemsRepository itemsRepository;
	private Inventory inventory;

	public PrologServiceBuilder setBaseItemFactName(String baseItemFactName) {
		this.baseItemFactName = baseItemFactName;
		return this;
	}

	public PrologServiceBuilder setIngredientFactName(String ingredientFactName) {
		this.ingredientFactName = ingredientFactName;
		return this;
	}

	public PrologServiceBuilder setItemInInventoryFactName(String itemInInventoryFactName) {
		this.itemInInventoryFactName = itemInInventoryFactName;
		return this;
	}

	public PrologServiceBuilder setItemsRepository(ItemsRepository itemsRepository) {
		this.itemsRepository = itemsRepository;
		return this;
	}

	public PrologServiceBuilder setInventory(Inventory inventory) {
		this.inventory = inventory;
		return this;
	}

	public PrologService build() {
		// @formatter:off
		return new PrologService(
			this.baseItemFactName,
			this.ingredientFactName,
			this.itemInInventoryFactName,
			this.itemsRepository,
			this.inventory
		);
		// @formatter:on
	}
}
