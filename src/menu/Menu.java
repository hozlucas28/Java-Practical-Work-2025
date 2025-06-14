package menu;

import java.util.Scanner;

public class Menu {

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("===== CRAFTING SYSTEM =====");
            System.out.println("1. Show ingredients to craft an item (first level)");
            System.out.println("2. Show base ingredients to craft an item from scratch");
            System.out.println("3. Show missing ingredients to craft an item (first level)");
            System.out.println("4. Show missing base ingredients to craft an item from scratch");
            System.out.println("5. Show how many items can be crafted with current inventory");
            System.out.println("6. Perform crafting");
            System.out.println("7. Show crafting history");
            System.out.println("8. Show current inventory");
            System.out.println("9. Show crafting time for an item (full chain)");
            System.out.println("10. Load new recipe from external file");
            System.out.println("11. Exit");
            System.out.print("Enter an option: ");
            option = scanner.nextInt();

            switch (option) {
            case 1:
                System.out.println("[TODO] Showing ingredients required to craft an item (first level only)...");
                break;
            case 2:
                System.out.println("[TODO] Showing base ingredients required to craft an item from scratch...");
                break;
            case 3:
                System.out.println("[TODO] Checking which ingredients are missing to craft an item (first level only)...");
                break;
            case 4:
                System.out.println("[TODO] Checking which base ingredients are missing to craft an item from scratch...");
                break;
            case 5:
                System.out.println("[TODO] Calculating how many units of an item can be crafted with the current inventory...");
                break;
            case 6:
                System.out.println("[TODO] Performing the crafting and updating the inventory...");
                break;
            case 7:
                System.out.println("[TODO] Displaying the history of crafted items...");
                break;
            case 8:
                System.out.println("[TODO] Showing the current state of the inventory...");
                break;
            case 9:
                System.out.println("[TODO] Calculating total crafting time for the selected item (full chain)...");
                break;
            case 10:
                System.out.println("[TODO] Loading a new recipe from an external file...");
                break;
            case 11:
                System.out.println("Exiting system. Goodbye!");
                break;
            default:
                System.out.println("Invalid option. Try again.");
        }

        


        } while (option != 12);

        scanner.close();
    }
}
