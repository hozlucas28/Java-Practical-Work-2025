package craftingSystem;

import java.util.ArrayList;
import java.util.List;

import exceptions.ThereAreNoCraftedItemsException;
import inventory.Item;
import recipe.Recipe;

public class CraftingHistory {
	private List<CraftedItem> items;

	public CraftingHistory() {
		this.items = new ArrayList<CraftedItem>();
	};

	public List<CraftedItem> getItems() {
		return this.items;
	};

	public CraftedItem getLastItem() throws ThereAreNoCraftedItemsException {
		final int lastIndex = this.items.size() - 1;

		if (lastIndex < 0) {
			throw new ThereAreNoCraftedItemsException();
		}

		return this.items.get(lastIndex);
	};

	public void addItem(Item item, Recipe usedRecipe) {
		CraftedItem craftedItem = new CraftedItem(item.getId(), item.getName(), item.getRecipes(), usedRecipe);
		this.items.add(craftedItem);
	}

	public CraftedItem removeLastItem() throws ThereAreNoCraftedItemsException {
		final int lastIndex = this.items.size() - 1;

		if (lastIndex < 0) {
			throw new ThereAreNoCraftedItemsException();
		}

		CraftedItem lastCraftedItem = this.items.remove(lastIndex);

		return lastCraftedItem;
	}
}
