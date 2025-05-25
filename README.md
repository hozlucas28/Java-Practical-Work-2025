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
    <a href="#team-workflow">Team workflow</a> •
    <a href="#development-team">Development team</a> •
    <a href="#additional-material">Additional material</a>
    <br>
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

This repository contains the practical work for the Programming Paradigms course at the [National University of La Matanza (UNLaM)](https://www.unlam.edu.ar/). The practical work consists of [TODO]. <!-- TODO -->

## Features

-   [TODO]. <!-- TODO -->

## Installation

1. [TODO]. <!-- TODO -->

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

    class RecipesRepository {
	    +HashMap~String, Item~ items

	    Item getItem(String key)
	    List~Item~ getItems()
    }

    class PreInit {
	    +static Inventory loadInventoryJSON(String path)
	    +static RecipesRepository loadRecipesJSON(String path)
    }

    class Item {
	    -String name
	    -int quantity
	    -List~Recipe~ recipes

	    +String getName()
	    +int getQuantity()
	    +List~Recipe~ getCraftableRecipes(Inventory inventory)
	    +bool isBase()
    }

    class Recipe {
	    -Item craftingTable
	    -List~Ingredient~ ingredients
	    -int time
	    -int itemsToCraft

	    +List~Item~ getIngredients()
	    +List~Item~ getIngredientsToBase()
    }

    class Inventory {
	    -List~Item~ items

	    +List~Item~ getItems()
	    +String toJSON()
	    +void toJSON(String path)
    }

    class CraftedItem {
	    -Date fecha
	    -Recipe usedRecipe

	    +List~Item~ undo()
    }

    class Ingredient {
	    -Item item
	    -int quantity

	    +Item getItem()
	    +int getQuantity()
    }

    class CraftingSystem {
	    -List~Item~ itemsToCraft
	    -Inventory inventory
	    -CraftingHistory history

	    +int getCraftableUnits()
	    +List~Item~ getRequiredIngredients()
	    +List~Item~ getRequiredIngredientsToBase()
	    +List~Item~ setItemsToCraft(List~Item~ itemsToCraft)
	    +bool canCraft()
	    +craftItems()
	    +bool redoCraft()
	    +bool undoLastCraft()
    }

    class CraftingHistory {
	    -List~CraftedItem~ items
	    -List~CraftedItem~ undoItems

	    +bool redoCraft(Inventory inventory)
	    +bool undoLastCraft(Inventory inventory)
    }

    Recipe "0...*" o-- "1" Item : Has

    Item "0...*" o-- "1" Inventory : Has

    Item <|-- CraftedItem : Inherits from

    Ingredient "1...*" o-- "1" Recipe : Has

    Item "0...*" o-- "1" CraftingSystem : Has
    Inventory "1" o-- "1" CraftingSystem : Has
    CraftingHistory "1" *-- "1" CraftingSystem : Instance

    CraftedItem "0...*" o-- "1" CraftingHistory : Has
```

</details>

> [!NOTE]
> The diagrams were developed from scratch as part of the preliminary project reports.

## Team workflow

<div media="(prefers-color-scheme: dark)">

```mermaid
---
config:
  logLevel: debug
  theme: dark
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

</div>

<div media="(prefers-color-scheme: light), (prefers-color-scheme: no-preference)">

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

</div>

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
