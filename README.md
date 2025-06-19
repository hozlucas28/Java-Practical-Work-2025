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

1. Clone the repository to your device and install [Eclipse IDE for Java developers](https://www.eclipse.org/downloads/packages/).
2. Open the cloned repository with Eclipse IDE and the [Index.java](./src/Main.java) file.
3. Then, press the green button (`Run index`) at the top of the navbar.
4. That's all, enjoy the crafting system through the interaction with the console.

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
	    +Item getItem(String name)
        +static ItemsRepository loadFromJSON(String path)
        +String toProlog()
    }

    class Item {
	    -String name
	    -List~Recipe~ recipes

	    +String getName()
	    +List~Recipe~ getRecipes()
	    +List~Recipe~ getCraftableRecipes(Inventory inventory)
	    +bool isBase()
        +void addRecipe(Recipe recipe)
        +Item copy()
    }

    class Recipe {
	    -List~Ingredient~ ingredients
	    -int timeToCraftInMilliseconds
	    -int itemsToCraft
	    -Optional~Item~ craftingTable

	    +List~Item~ getIngredients()
	    +int getTimeToCraftInMilliseconds()
	    +int getItemsToCraft()
	    +List~Item~ getIngredientsToBase()
	    +Optional~Item~ getCraftingTable()
    }

    class JSONRecipe {
        <<adapter>>

	    -HashMap~String, Integer~ ingredients
	    -int timeToCraftInMilliseconds
	    -int itemsToCraft
	    -Optional~Item~ craftingTable

	    +static void linkItemsAndRecipes(HashMap~String, Item~ items, HashMap~String, List~JSONRecipe~~ recipesPerItem)
    }

    class Inventory {
        -HashMap~Item, Integer~ items

	    +HashMap~Item, Integer~ getItems()
	    +Item getItemQuantity(Item item)
	    +void addItem(Item item, int quantity)
	    +void removeItem(Item item, int quantity)
        +void storeOnJSON(String path)
        +static Inventory loadFromJSON(String path)
        +String toProlog(String eventName)
    }

    class CraftedItem {
	    -ZonedDateTime date
	    -Recipe usedRecipe
	    -int craftedItems

	    +ZonedDateTime getDate()
	    +Recipe getUsedRecipe()
	    +int getCraftedItems()
    }

    class Ingredient {
	    -Item item
	    -int quantity

	    +Item getItem()
	    +int getQuantity()
        +void incrementQuantity(int quantity)
    }

    class CraftingSystem {
	    -Inventory inventory
	    -List~Item~ itemsToCraft
	    -CraftingHistory history

	    +int getCraftableUnits()
        +HashMap<Item, List<List<Ingredient>>> getMissingIngredients()
        +HashMap<Item, List<List<Ingredient>>> getMissingIngredientsToBase()
	    +HashMap<Item, List<List<Ingredient>>> getRequiredIngredients()
	    +HashMap<Item, List<List<Ingredient>>> getRequiredIngredientsToBase()
	    +bool canCraft()
	    +this setItemsToCraft(List~Item~ itemsToCraft)
	    +craftItems()
	    +bool undoLastCraft()
    }

    class CraftingHistory {
	    -List~CraftedItem~ items

        +List~CraftedItem~ getItems()
        +CraftedItem getLastItem()
	    +void addItem(Item item, Recipe usedRecipe)
	    +CraftedItem removeLastItem()
    }

    Item "0...*" o-- "1" ItemsRepository : Has
    JSONRecipe "1...*" *-- "1" ItemsRepository : Instance and uses the static method

    Recipe "0...*" o-- "1" Item : Has

    Item "0...*" o-- "1" Inventory : Has

    Item <|-- CraftedItem : Inherits from

    Ingredient "1...*" o-- "1" Recipe : Has

    Item "0...1" o-- "1" JSONRecipe : Has

    Item "0...*" o-- "1" CraftingSystem : Has
    Inventory "1" o-- "1" CraftingSystem : Has
    CraftingHistory "1" *-- "1" CraftingSystem : Instance

    CraftedItem "0...*" o-- "1" CraftingHistory : Has
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
