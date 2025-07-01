import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Scanner;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import Menu.Menu;
import Menu.MenuBuilder;
import craftingSystem.CraftingSystem;
import exceptions.ItemNotFoundException;
import inventory.Inventory;
import repositories.ItemsRepository;
import services.PrologService;
import services.PrologServiceBuilder;

public class Main {
	public static void main(String[] args) {
		// Load items repository
		String recipesPath = Paths.get("src", "assets", "recipes.json").toString();
		ItemsRepository itemsRepository = null;

		try {
			itemsRepository = ItemsRepository.loadFromJSON(recipesPath);
		} catch (FileNotFoundException e) {
			System.out.print("> Fatal error! Items repository path was not found.");
			System.exit(101);
		} catch (JsonIOException | JsonSyntaxException e) {
			System.out.print("> Fatal error! Items repository file has an invalid JSON syntax.");
			System.exit(102);
		} catch (ItemNotFoundException e) {
			System.out.print("> Fatal error! Items repository file has an invalid recipes structure.");
			System.exit(103);
		} catch (Exception e) {
			System.out.printf("> Fatal error! An error occurred on try to load items repository from \"%s\" json file.",
					recipesPath);
			System.exit(104);
		}

		// Load inventory
		String inventoryPath = Paths.get("src", "assets", "inventory.json").toString();
		Inventory inventory = null;

		try {
			inventory = Inventory.loadFromJSON(inventoryPath, itemsRepository);
		} catch (FileNotFoundException e) {
			System.out.print("> Fatal error! Inventory path was not found.");
			System.exit(201);
		} catch (JsonIOException | JsonSyntaxException e) {
			System.out.print("> Fatal error! Inventory file has an invalid JSON syntax.");
			System.exit(202);
		} catch (Exception e) {
			System.out.printf("> Fatal error! An error occurred on try to load the inventory from \"%s\" json file.",
					inventoryPath);
			System.exit(203);
		}

		// Build prolog service
		// @formatter:off
		PrologService prologService = new PrologServiceBuilder()
			.setBaseItemFactName("base_item")
			.setIngredientFactName("ingredient")
			.setItemInInventoryFactName("have")
			.setItemsRepository(itemsRepository)
			.setInventory(inventory)
			.build();
		// @formatter:on

		// Build menu
		Scanner stdin = new Scanner(System.in);

		// @formatter:off
		Menu menu = new MenuBuilder()
			.setScanner(stdin)
			.setInventory(inventory)
			.setItemsRepository(itemsRepository)
			.setCraftingSystem(new CraftingSystem(inventory))
			.setPrologService(prologService)
			.build();
		// @formatter:on

		// Print items within items repository
		System.out.println("> Repository items:\n");
		System.out.println(itemsRepository.toString(new String[] { "•", "•", "◦" }, 2));

		// Print items within inventory
		System.out.println("\n> Inventory:\n");
		System.out.printf("%s\n\n", inventory.toString("•", 2));

		// Initialize menu
		menu.init();
		stdin.close();

		System.out.println("> Program finished.");
	}
}
