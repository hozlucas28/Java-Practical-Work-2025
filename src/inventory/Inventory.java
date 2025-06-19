package inventory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import exceptions.ItemNotFoundException;
import exceptions.OutOfRangeException;
import repositories.ItemsRepository;

public class Inventory {
	private HashMap<Item, Integer> items;

	public Inventory(HashMap<Item, Integer> items) {
		this.items = items;
	}

	public HashMap<Item, Integer> getItems() {
		return this.items;
	}

	public int getItemQuantity(Item item) {
		return this.items.getOrDefault(item, 0);
	}

	public void addItem(Item item, int quantity) throws OutOfRangeException {
		if (quantity < 1) {
			String errorMessage = String.format("Received %d as quantity, but expect it greater than or equal to %d",
					quantity, 1);
			throw new OutOfRangeException(errorMessage);
		}

		if (this.items.containsKey(item)) {
			this.items.compute(item, (key, value) -> value + quantity);
		} else {
			this.items.put(item, quantity);
		}
	}

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

	public void storeOnJSON(String path) throws IOException {
		JsonObject json = new JsonObject();

		for (Map.Entry<Item, Integer> entry : this.items.entrySet()) {
			Item item = entry.getKey();
			Integer quantity = entry.getValue();

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

	public String toProlog(String eventName) {
		String prologLines = null;

		for (Map.Entry<Item, Integer> entry : this.items.entrySet()) {
			Item item = entry.getKey();
			Integer itemQuantity = entry.getValue();

			String prologLine = String.format("%s(\"%s\", %d).", eventName, item.getName(), itemQuantity);
			prologLines = prologLines == null ? prologLine : String.format("%s\n%s", prologLines, prologLine);
		}

		return prologLines;
	}
}
