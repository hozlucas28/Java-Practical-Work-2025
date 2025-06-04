package craftingSystem;

import java.util.ArrayList;
import java.util.List;

import exceptions.ThereAreNoCraftedItemsException;
import inventory.Inventory;
import inventory.Item;
import recipe.Ingredient;

public class CraftingSystem {
	private final Inventory inventory;
	private List<Item> itemsToCraft;
	private final CraftingHistory history;

	public CraftingSystem(Inventory inventory) {
		this.inventory = inventory;
		this.history = new CraftingHistory();
	}

	public int getCraftableUnits() {
		// TODO

		return 0;
	}

	public List<Item> getRequiredIngredients() {
		// TODO
		List<Item> requiredIngredients = new ArrayList<Item>();

		return requiredIngredients;
	}

	public List<Item> getRequiredIngredientsToBase() {
		// TODO
		List<Item> requiredIngredients = new ArrayList<Item>();

		return requiredIngredients;
	}

	public List<CraftedItem> getCraftedItems() {
		return this.history.getItems();
	}

	public boolean canCraft() {
		// TODO

		return false;
	};

	public CraftingSystem setItemsToCraft(List<Item> items) {
		this.itemsToCraft = items;
		return this;
	}

	public void craftItems() {
		// TODO
	}

	public void undoLastCraft() throws ThereAreNoCraftedItemsException {
		// TODO
		CraftedItem lastCraftedItem = this.history.removeLastItem();
		List<Ingredient> usedIngredients = lastCraftedItem.getUsedRecipe().getIngredients();
		
		// TODO: remover item crafteado (si y solo si existe)
		// TODO: agregar ingredientes utilizados al inventario
	}
}
