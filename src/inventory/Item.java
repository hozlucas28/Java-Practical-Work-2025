package inventory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import recipe.Recipe;

public class Item {
	private final String name;
	private final List<Recipe> recipes;

	public Item(String name) {
		this.name = name;
		this.recipes = null;
	}

	public Item(String name, List<Recipe> recipes) {
		this.name = name;
		this.recipes = recipes;
	}

	public String getName() {
		return this.name;
	}

	public List<Recipe> getRecipes() {
		return Collections.unmodifiableList(this.recipes);
	}

	public boolean isBase() {
		return this.recipes == null;
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

		if (!(obj instanceof Item)) {
			return false;
		}

		Item other = (Item) obj;

		return this.name.equalsIgnoreCase(other.name);
	}
}
