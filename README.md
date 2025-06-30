<h1 align="center">
    Java Practical Work [2025] [WIP] <!-- TODO -->
</h1>

<p align="center">
    <strong>Repository for the practical work of the Programming Paradigms course</strong>
    <br>
    <strong>- <a href="https://www.unlam.edu.ar/">UNLaM</a> (National University of La Matanza) -</strong>
</p>

<p align="center">
    <a href="#summary">Summary</a> •
    <a href="#features">Features</a> •
    <a href="#installation">Installation</a> •
    <a href="#installation">Diagrams</a> •
    <a href="#team-workflow">Team workflow</a> •
    <a href="#development-team">Development team</a>
    <br>
    <a href="#additional-material">Additional material</a> •
    <a href="#license">License</a> •
    <a href="#acknowledgments">Acknowledgments</a>
</p>

<p align="center">
    <a href="./docs/translations/es/README.md">[ Spanish version ]</a>
</p>

<p align="center">
    <a href="#"> <!-- TODO -->
        <img src="./docs/assets/preview.png" width="800" alt="Preview">
    </a>
</p>

<p align="center">
    <a href="#" target="_blank">(demonstration video)</a> <!-- TODO -->
</p>

## Summary

This repository contains the practical work for the Programming Paradigms course at the [National University of La Matanza (UNLaM)](https://www.unlam.edu.ar/). The practical work consists of doing a crafting system in Java and testing it with [JUnit 5](https://junit.org/junit5/).

## Features

-   Architecture planning
-   Code conventions and standards
-   Code documentation
-   Collections
-   Commits following the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
-   Deployment of releases
-   E2E testing with [JUnit 5](https://junit.org/junit5/)
-   File reading and interpretation
-   Input control using validations
-   Local storage of records
-   Team Workflow planning (branches, tags, and releases)
-   Unit testing with [JUnit 5](https://junit.org/junit5/)

## Installation

1. Clone the repository to your device.
2. Install [Java](https://www.java.com/en/download/) and [Prolog](https://www.swi-prolog.org/download/stable) (check `Add swipl to the system PATH` option during the installation).
3. Install [Eclipse IDE for Java developers](https://www.eclipse.org/downloads/packages/).
4. Open the cloned repository with Eclipse IDE.
5. Then, press right click on [Main.java](./src/Main.java) file and select `Run As` -> `Java Application`.
6. That's all, enjoy the crafting system through the interaction with the console.

<details>
<summary>How can I run all JUnit tests?</summary>

If you want to run all JUnit tests, you have to press `Right click` on the project within `Package explorer` of Eclipse IDE, and select `Run as` -> `JUnit Test`. That's all, Eclipse IDE will start running all the tests.

</details>

<details>
<summary>How can I change the inventory?</summary>

To change the items in the inventory, you have to update the [inventory.json](./src/assets/inventory.json) file with the desired ones.

> It's important to follow the same structure as the original ones, and these must be defined inside the [recipes.json](./src/assets/recipes.json) file.

</details>

<details>
<summary>How can I change the list of available items to craft?</summary>

To change the list of available items to craft, you must update the [recipes.json](./src/assets/recipes.json) file with the new craftable items.

> It's important to follow the same structure as the original ones.

</details>

### Known issues

| Issue                                  | Solution                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| :------------------------------------- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Prolog could not find system resources | _In Eclipse IDE, go to `File` tab and select `Import` option. Then, you have to search for `Launch configurations`, select it and press `Next`. After that, browse the project directory, select `Java-Practical-Work-2025`, `Main`, and `PrologServiceTests` files and check `Overwrite existing launch configurations without warning.` option. Finally, press `Finish` to load your local Prolog instance. If this didn't work, check out the value of each environment variable inside `Environment` tab in each run configurations of `Java-Practical-Work-2025`, `Main`, and `PrologServiceTests`, as they must aim to your local Prolog directory._ |

## Diagrams

<details>
<summary>Class diagram</summary>

```mermaid
---
config:
  class:
    hideEmptyMembersBox: true

  theme: redux
  look: neo
  layout: elk
---
classDiagram
direction TB

    class ItemsRepository {
	    -HashMap~String, Item~ items

	    +HashMap~String, Item~ getItems()
	    +HashMap~String, Item~ getCraftableItems()
	    +Item getItem(String name)
        +static ItemsRepository loadFromJSON(String path)
        +String toString(String itemMarkers[], int lPadding)
    }

    class PrologService {
	    -String baseItemFactName
	    -String ingredientFactName
	    -String itemInInventoryFactName
	    -ItemsRepository itemsRepository
	    -Inventory inventory

        +HashMap~Item, Integer~ craftableItems()
        -void toFile(FileWriter fWriter)
        -String toProlog(ItemsRepository itemsRepository)
        -String toProlog(Inventory inventory)
        -String utilityRules()
    }

    class PrologServiceBuilder {
	    -String baseItemFactName
	    -String ingredientFactName
	    -String itemInInventoryFactName
	    -ItemsRepository itemsRepository
	    -Inventory inventory

        +PrologServiceBuilder setBaseItemFactName(String baseItemFactName)
        +PrologServiceBuilder setIngredientFactName(String ingredientFactName)
        +PrologServiceBuilder setItemInInventoryFactName(String itemInInventoryFactName)
        +PrologServiceBuilder setItemsRepository(ItemsRepository itemsRepository)
        +PrologServiceBuilder setInventory(Inventory inventory)
        +PrologService build()
    }

    class Item {
	    -String name
	    -List~Recipe~ recipes

	    +String getName()
	    +List~Recipe~ getRecipes()
	    +boolean isBase()
    }

    class Recipe {
	    -Item craftingTable
	    -List~Ingredient~ ingredients
	    -int timeToCraftInMilliseconds
	    -int quantityToCraft

	    +Item getCraftingTable()
	    +List~Item~ getIngredients()
	    +int getTimeToCraftInMilliseconds()
	    +int getQuantityToCraft()
	    +List~Item~ getBaseIngredients()
	    +boolean needsCraftingTable()
        +void setCraftingTable(Item craftingTable)
        +void setIngredients(List~Ingredient~ ingredients)
    }

    class CraftedItem {
	    -ZonedDateTime date
	    -Recipe usedRecipe
	    -int quantityCrafted
        -int craftingTimeInMilliseconds

	    +ZonedDateTime getDate()
	    +Recipe getUsedRecipe()
	    +int getQuantityCrafted()
        +int getCraftingTimeInMilliseconds()
    }

    class Inventory {
        -HashMap~Item, Integer~ items

	    +HashMap~Item, Integer~ getItems()
	    +Item getItemQuantity(Item item)
	    +void addItem(Item item, int quantity)
	    +void removeItem(Item item, int quantity)
        +void storeOnJSON(String path)
        +static Inventory loadFromJSON(String path)
        +String toString(String itemMarker, int lPadding)
    }

    class Ingredient {
	    -Item item
	    -int quantity

	    +Item getItem()
	    +int getQuantity()
        +String toString(String itemMarker, int lPadding)
    }

    class CraftingSystem {
	    -Item itemToCraft
        -int quantityToCraft
	    -Inventory inventory
	    -CraftingHistory history

        +List~CraftedItem~ getCraftedItems()
	    +int getCraftableUnits()
        +HashMap~Recipe, List~Ingredient~~ getMissingIngredients()
        +HashMap~Recipe, List~Ingredient~~ getMissingBaseIngredients(int branch)
        -HashMap~Recipe, List~Ingredient~~ getMissingIngredients(HashMap~Recipe, List~Ingredient~~ recipes)
	    +HashMap~Recipe, List~Ingredient~~ getRequiredIngredients()
        +HashMap~Recipe, List~Ingredient~~ getRequiredBaseIngredients(int branch)
        -List~Ingredient~ getBaseIngredientsRecursive(Recipe recipe, int totalToCraft, int branch)
	    +boolean canCraft()
	    +void setItemToCraft(Item item, int quantity)
	    +int craftItem(int recipe)
	    +CraftedItem undoLastCraft()
    }

    class CraftingHistory {
	    -List~CraftedItem~ items

        +List~CraftedItem~ getItems()
        +CraftedItem getLastItem()
	    +void addItem(Item item, Recipe usedRecipe)
	    +CraftedItem removeLastItem()
    }

    ItemsRepository "1" --o "0...*" Item : Has

    PrologService "1" --o "1" Inventory : Has a reference to
    PrologService "1" --o "1" ItemsRepository : Has a reference to
    PrologService "1" --* "1" PrologServiceBuilder : Build by

    Item "1" --o "0...*" Recipe : Has

    Recipe "1" --o "1...*" Ingredient : Has
    Recipe "1" --o "0...1" Item : Has a reference to

    CraftedItem --|>  Item : Inherits from

    Inventory "1" --o "0...*" Item : Has references to

    CraftingSystem "1*" --o "0..." Item : Has references to
    CraftingSystem "1" --o "1" Inventory : Has a reference to
    CraftingSystem "1" --* "1" CraftingHistory : Has

    CraftingHistory "1" --o "0...*" CraftedItem : Has
```

</details>

> [!NOTE]
> The diagrams were developed from scratch as part of the preliminary project reports.

## Team workflow

```mermaid
---
config:
  logLevel: debug
  theme: base
  gitGraph:
    showBranches: true
    showCommitLabel: true
    mainBranchName: Master
    parallelCommits: true
---
gitGraph:
        commit
        commit tag: "v0.0.1"
        branch "Aguilera Emanuel"
        commit
        commit
        checkout Master
        branch "De Marco Juan"
        commit
        commit
        checkout Master
        branch "Hoz Lucas"
        commit
        commit
        checkout Master
        branch "Rueda Olarte Joel"
        commit
        commit
        checkout Master
        branch "Maudet Alejandro"
        commit
        commit
        checkout Master
        branch "Monges Omar"
        commit
        commit
        checkout Master
        merge "De Marco Juan"
        merge "Aguilera Emanuel"
        merge "Rueda Olarte Joel"
        merge "Maudet Alejandro"
        merge "Hoz Lucas"
        merge "Monges Omar" tag: "v1.0.0"
```

### Tags

-   `vMAJOR.MINOR.PATCH`: This tag indicates a [release](https://github.com/hozlucas28/Java-Practical-Work-2025/releases) of the practical work following [Semantic Versioning](https://semver.org/), and will only be present in the `Master` branch commits.

### Branches

-   `Master`: Branch containing the development versions of the practical work, where team members will introduce new changes (commits).

> [!IMPORTANT]
> Stable versions are only available as [releases](https://github.com/hozlucas28/Java-Practical-Work-2025/releases).

> [!NOTE]
> The other branches are fictional and represent individual contributions from each member to the `Master` branch.

## Development team

-   [Aguilera Emanuel](https://github.com/EmaaAg)
-   [Hoz Lucas](https://github.com/hozlucas28)
-   [De Marco Juan](https://github.com/juDemarco)
-   [Maudet Alejandro](https://github.com/Mabbdet)
-   [Monges Omar](https://github.com/Omar-Monges)
-   [Rueda Olarte Joel](https://github.com/joelalexisrueda)

## Additional material

-   [Practical work report](#) <!-- TODO -->
-   [Practical work requirements](./docs/translations/en/requirements.md)

## License

This repository is under the [MIT License](./LICENSE). For more information about what is permitted with the contents of this repository, visit [choosealicense.com](https://choosealicense.com/licenses/).

## Acknowledgments

We would like to thank the teachers from the [UNLaM](https://www.unlam.edu.ar/) Programming Paradigms course for their support and guidance.
