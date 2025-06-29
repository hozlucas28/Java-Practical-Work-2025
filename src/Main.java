import java.io.FileNotFoundException;
import java.nio.file.Paths;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import exceptions.ItemNotFoundException;
import exceptions.OutOfRangeException;
import inventory.Inventory;
import repositories.ItemsRepository;

public class Main {
	public static void main(String[] args) {
		// Load items repository
		String recipesPath = Paths.get("src", "assets", "recipes.json").toString();
		ItemsRepository itemsRepository = null;

		try {
			itemsRepository = ItemsRepository.loadFromJSON(recipesPath);
		} catch (FileNotFoundException e) {
			System.out.print("Fatal error! Items repository path was not found.");
			System.exit(101);
		} catch (JsonIOException | JsonSyntaxException e) {
			System.out.print("Fatal error! Items repository file has an invalid JSON syntax.");
			System.exit(102);
		} catch (ItemNotFoundException | OutOfRangeException e) {
			System.out.print("Fatal error! Items repository file has an invalid recipes structure.");
			System.exit(103);
		} catch (Exception e) {
			System.out.printf("Fatal error! An error occured on try to load items repository from \"%s\" json file.",
					recipesPath);
			System.exit(104);
		}

		// Load inventory
		String inventoryPath = Paths.get("src", "assets", "inventory.json").toString();
		Inventory inventory = null;

		try {
			inventory = Inventory.loadFromJSON(inventoryPath, itemsRepository);
		} catch (FileNotFoundException e) {
			System.out.print("Fatal error! Inventory path was not found.");
			System.exit(201);
		} catch (JsonIOException | JsonSyntaxException e) {
			System.out.print("Fatal error! Inventory file has an invalid JSON syntax.");
			System.exit(202);
		} catch (Exception e) {
			System.out.printf("Fatal error! An error occured on try to load the inventory from \"%s\" json file.",
					inventoryPath);
			System.exit(203);
		}

		// Print items within items repository
		System.out.println("> Repository items:\n");
		System.out.println(itemsRepository.toString(new String[] { "•", "•", "◦" }, 2));

		// Print items within inventory
		System.out.println("\n> Preloaded inventory:\n");
		System.out.printf("%s\n\n", inventory.toString("•", 2));

		// Create and initialize menu
		Menu menu = new Menu(inventory, itemsRepository);
		menu.init();

		System.out.println("> Program finished.");
	}
}
