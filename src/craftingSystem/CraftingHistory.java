package craftingSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exceptions.ItemNotFoundException;
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

	public CraftedItem getLastItem() throws ItemNotFoundException {
		final int lastIndex = this.items.size() - 1;

		if (lastIndex < 0) {
			throw new ItemNotFoundException("There are no crafted items within the `CraftingHistory` instance");
		}

		return this.items.get(lastIndex);
	}

	public void addItem(Item item, Recipe usedRecipe) {
		CraftedItem craftedItem = new CraftedItem(item, usedRecipe);
		this.items.add(craftedItem);
	}

	public void addItem(Item item, Recipe usedRecipe, int quantityCrafted) {
		CraftedItem craftedItem = new CraftedItem(item, usedRecipe, quantityCrafted);
		this.items.add(craftedItem);
	}

	public CraftedItem removeLastItem() throws ItemNotFoundException {
		final int lastIndex = this.items.size() - 1;

		if (lastIndex < 0) {
			throw new ItemNotFoundException("There are no crafted items within the `CraftingHistory` instance");
		}

		CraftedItem lastCraftedItem = this.items.remove(lastIndex);

		return lastCraftedItem;
	}
}
