package inventory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import exceptions.ItemNotFoundException;
import exceptions.OutOfRangeException;
import repositories.ItemsRepository;
import utilities.StringTransformers;

/**
 * Represents a repository that manages items and their quantities. Providing
 * methods to add, remove, retrieve, and persist items within it.
 */
public class Inventory {
	private HashMap<Item, Integer> items;

	/**
	 * Constructs an Inventory with the specified items.
	 *
	 * @param items {@link HashMap} of items and their quantities
	 */
	public Inventory(HashMap<Item, Integer> items) {
		this.items = items;
	}

	/**
	 * @return a {@link HashMap} of items and their quantities
	 */
	public HashMap<Item, Integer> getItems() {
		return this.items;
	}

	/**
	 * Returns the quantity of the specified {@link Item} in the inventory.
	 *
	 * @param item
	 * @return the quantity of the item, or 0 if not present
	 */
	public int getItemQuantity(Item item) {
		return this.items.getOrDefault(item, 0);
	}

	/**
	 * Adds the specified quantity of an {@link Item} to the inventory.
	 *
	 * @param item     item to add
	 * @param quantity quantity to add (must be >= 1)
	 * @throws OutOfRangeException if the quantity is less than 1
	 */
	public void addItem(Item item, int quantity) throws OutOfRangeException {
		if (quantity < 1) {
			String errorMessage = String.format("Received %d as quantity, but expect it greater than or equal to %d",
					quantity, 1);
			throw new OutOfRangeException(errorMessage);
		}

		this.items.put(item, this.items.getOrDefault(item, 0) + quantity);
	}

	/**
	 * Removes the specified quantity of an {@link Item} from the inventory.
	 *
	 * @param item     item to remove
	 * @param quantity quantity to remove (must be >= 1 and <= current quantity)
	 * @throws ItemNotFoundException if the item is not found in the inventory
	 * @throws OutOfRangeException   if the quantity is out of valid range
	 */
	public void removeItem(Item item, int quantity) throws ItemNotFoundException, OutOfRangeException {
		String errorMessage;

		if (!this.items.containsKey(item)) {
			errorMessage = String.format("Item with \"%s\" name was not found inside the inventory", item.getName());
			throw new ItemNotFoundException(errorMessage);
		}

		int currentQuantity = this.items.get(item);

		if (quantity < 1 || quantity > currentQuantity) {
			errorMessage = String.format("Received %d as quantity, but expect it between %d and %d (inclusive)",
					quantity, 1, currentQuantity);
			throw new OutOfRangeException(errorMessage);
		}

		int newQuantity = currentQuantity - quantity;

		if (newQuantity == 0) {
			this.items.remove(item);
		} else {
			this.items.replace(item, newQuantity);
		}
	}

	/**
	 * Stores the inventory data as a JSON file.
	 *
	 * @param path JSON file path
	 * @throws IOException if an I/O error occurs during writing
	 */
	public void storeOnJSON(String path) throws IOException {
		JsonObject json = new JsonObject();

		for (Map.Entry<Item, Integer> itemEntry : this.items.entrySet()) {
			Item item = itemEntry.getKey();
			Integer quantity = itemEntry.getValue();

			json.addProperty(item.getName(), quantity);
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		try {
			FileWriter writer = new FileWriter(path, StandardCharsets.UTF_8);
			gson.toJson(json, writer);
			writer.close();
		} catch (IOException e) {
			String errorMessage = String.format("Failed to write inventory to \"%s\" file", path);
			throw new IOException(errorMessage);
		}
	}

	/**
	 * Loads inventory from a JSON file content.
	 *
	 * @param path            JSON file path
	 * @param itemsRepository items repository to resolve item names to {@link Item}
	 *                        objects
	 * @return an Inventory object loaded with the JSON file content
	 * @throws FileNotFoundException if the file does not exist
	 * @throws JsonIOException       if there is a problem reading the JSON
	 * @throws JsonSyntaxException   if the JSON is malformed
	 * @throws IOException           if an I/O error occurs during reading
	 */
	public static Inventory loadFromJSON(String path, ItemsRepository itemsRepository)
			throws FileNotFoundException, JsonIOException, JsonSyntaxException, IOException {
		FileInputStream fileStream = new FileInputStream(path);
		InputStreamReader streamReader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);

		JsonObject json = JsonParser.parseReader(streamReader).getAsJsonObject();

		HashMap<Item, Integer> items = new HashMap<Item, Integer>();

		for (String name : json.keySet()) {
			Item item = itemsRepository.getItem(name);
			int quantity = json.get(name).getAsInt();

			items.put(item, quantity);
		}

		streamReader.close();
		fileStream.close();

		Inventory inventory = new Inventory(items);

		return inventory;
	}

	/**
	 * Returns a string representation of the inventory with custom formatting.
	 *
	 * @param itemMarker string marker to prefix each item
	 * @param lPadding   left padding for each line
	 * @return a formatted string representation of the inventory
	 */
	public String toString(String itemMarker, int lPadding) {
		StringBuilder builder = new StringBuilder();
		Formatter formatter = new Formatter(builder);

		for (Map.Entry<Item, Integer> itemEntry : this.items.entrySet()) {
			String itemName = itemEntry.getKey().getName();
			Integer itemQuantity = itemEntry.getValue();

			formatter.format("%" + lPadding + "s%s ", " ", itemMarker);
			formatter.format("%s (x%d)\n", StringTransformers.toTitle(itemName), itemQuantity);
		}

		formatter.close();

		return builder.toString().stripTrailing();
	}

	@Override
	public int hashCode() {
		return Objects.hash(items);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		Inventory other = (Inventory) obj;

		return Objects.equals(this.items, other.items);
	}
}
