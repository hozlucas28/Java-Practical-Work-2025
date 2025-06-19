package services;

import java.util.HashMap;

import inventory.Inventory;
import inventory.Item;
import repositories.ItemsRepository;

public class PrologServiceBuilder {
	protected String baseItemFactName = "base_item";
	protected String ingredientFactName = "ingredient";
	protected String itemInInventoryFactName = "have";

	protected ItemsRepository itemsRepository = new ItemsRepository(new HashMap<String, Item>());
	protected Inventory inventory = new Inventory(new HashMap<Item, Integer>());

	public PrologServiceBuilder() {
	}

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
		return new PrologService(this);
	}
}
