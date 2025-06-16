package inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import recipe.Recipe;

public class Item {
		private final String name;
	private final List<Recipe> recipes;

	public Item(String name) {
				this.name = name;
		this.recipes = new ArrayList<Recipe>();
	}

	public Item(String name, List<Recipe> recipes) {
				this.name = name;
		this.recipes = recipes;
	}

	public String getName() {
		return this.name;
	}

	public List<Recipe> getRecipes() {
		return this.recipes;
	}

	public List<Recipe> getCraftableRecipes(Inventory inventory) {
		// TODO

		return this.recipes;
	}

	public boolean isBase() {
		return this.recipes.size() == 0;
	}

	public Item copy() {
		return new Item(this.id, this.name, this.recipes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		Item other = (Item) obj;

		return this.name == other.name;
	}
}
