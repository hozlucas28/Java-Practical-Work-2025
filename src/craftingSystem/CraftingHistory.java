package craftingSystem;

import java.util.List;

import exceptions.ThereAreNoCraftedItemsException;
import inventory.Item;
import recipe.Recipe;

public class CraftingHistory {
	private List<CraftedItem> items;

	public CraftingHistory() {
	};

	public List<CraftedItem> getItems() {
		return this.items;
	};

	public void addItem(Item item, Recipe usedRecipe) {
		CraftedItem craftedItem = new CraftedItem(item.getName(), item.getRecipes(), usedRecipe);
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
