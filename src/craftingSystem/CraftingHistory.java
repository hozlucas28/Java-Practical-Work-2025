package craftingSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exceptions.EmptyHistoryException;
import inventory.Item;
import recipe.Recipe;

class CraftingHistory {
	private List<CraftedItem> items;

	public CraftingHistory() {
		this.items = new ArrayList<CraftedItem>();
	}

	public List<CraftedItem> getItems() {
		return Collections.unmodifiableList(this.items);
	}

	public CraftedItem getLastItem() throws EmptyHistoryException {
		if (this.items.isEmpty()) {
			throw new EmptyHistoryException("There are no crafted items within the `CraftingHistory` instance");
		}

		return this.items.get(0);
	}

	public void addItem(Item item, Recipe usedRecipe, int quantityCrafted) {
		CraftedItem craftedItem = new CraftedItem(item, usedRecipe, quantityCrafted);
		this.items.add(0, craftedItem);
	}

	public CraftedItem removeLastItem() throws EmptyHistoryException {
		if (this.items.isEmpty()) {
			throw new EmptyHistoryException("There are no crafted items within the `CraftingHistory` instance");
		}

		CraftedItem lastCraftedItem = this.items.remove(0);

		return lastCraftedItem;
	}
}
