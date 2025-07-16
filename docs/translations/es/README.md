<h1 align="center">
    Trabajo Práctico de Java [2025]
</h1>

<p align="center">
    <strong>Repositorio para el trabajo práctico del curso de Paradigmas de Programación</strong>
    <br>
    <strong>- <a href="https://www.unlam.edu.ar/">UNLaM</a> (Universidad Nacional de La Matanza) -</strong>
</p>

<p align="center">
    <a href="#summary">Resumen</a> •
    <a href="#features">Características</a> •
    <a href="#installation">Instalación</a> •
    <a href="#installation">Diagramas</a> •
    <a href="#team-workflow">Flujo de trabajo en equipo</a> •
    <a href="#development-team">Equipo de desarrollo</a>
    <br>
    <a href="#additional-material">Material adicional</a> •
    <a href="#license">Licencia</a> •
    <a href="#acknowledgments">Agradecimientos</a>
</p>

<p align="center">
    <a href="../../../README.md">[ Versión en inglés ]</a>
</p>

<p align="center">
    <a href="https://youtu.be/-exAnyC0znc">
        <img src="../../assets/preview.png" width="800" alt="Vista previa">
    </a>
</p>

<p align="center">
    <a href="https://youtu.be/-exAnyC0znc" target="_blank">(video de demostración)</a>
</p>

## Resumen

Este repositorio contiene el trabajo práctico para el curso de Paradigmas de Programación en la [Universidad Nacional de La Matanza (UNLaM)](https://www.unlam.edu.ar/). El trabajo práctico consiste en realizar un sistema de crafteo en Java y probarlo con [JUnit 5](https://junit.org/junit5/).

> [!TIP]
> Si desea leer una documentación extensa de cada proceso involucrado dentro del sistema, consulte la [página con la documentación dedicada](https://deepwiki.com/hozlucas28/Java-Practical-Work-2025).

## Características

-   Almacenamiento local de registros
-   Colecciones
-   Commits siguiendo el estándar [Conventional Commits](https://www.conventionalcommits.org/es/v1.0.0/)
-   Control de entradas mediante validaciones
-   Convenciones y estándares de código
-   Despliegue de versiones
-   Documentación del código usando la sintaxis de [JavaDoc](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html)
-   Integración con [Prolog](https://www.swi-prolog.org/)
-   Lectura e interpretación de archivos
-   Página de [documentación dedicada](https://deepwiki.com/hozlucas28/Java-Practical-Work-2025)
-   Planificación de arquitectura
-   Planificación del flujo de trabajo en equipo (ramas, etiquetas y versiones)
-   Pruebas E2E con [JUnit 5](https://junit.org/junit5/)
-   Pruebas unitarias con [JUnit 5](https://junit.org/junit5/)

## Instalación

1. Descarga el [código fuente del último release](https://github.com/hozlucas28/Java-Practical-Work-2025/releases) (`zip version`) en tu dispositivo.
2. Instala [Java](https://www.java.com/en/download/) y [Prolog](https://www.swi-prolog.org/download/stable) (marca la opción `Add swipl to the system PATH` durante la instalación).
3. Instala [Eclipse IDE for Java developers](https://www.eclipse.org/downloads/packages/).
4. Abre el código fuente descargado con Eclipse IDE.
5. Luego, haz clic derecho sobre el archivo [Main.java](../../../src/Main.java) y selecciona `Run As` -> `Java Application`.
6. Eso es todo, disfruta del sistema de crafteo a través de la interacción con la consola.

<details>
<summary>¿Cómo puedo ejecutar todas las pruebas JUnit?</summary>

Si deseas ejecutar todas las pruebas JUnit, debes hacer clic derecho sobre el proyecto dentro del `Package explorer` de Eclipse IDE, y seleccionar `Run as` -> `JUnit Test`. Eso es todo, Eclipse IDE comenzará a ejecutar todas las pruebas.

</details>

<details>
<summary>¿Cómo puedo cambiar el inventario?</summary>

Para cambiar los ítems del inventario, debes actualizar el archivo [inventory.json](../../../src/assets/inventory.json) con los ítems deseados.

> Es importante seguir la misma estructura que los originales, y estos deben estar definidos dentro del archivo [recipes.json](../../../src/assets/recipes.json).

</details>

<details>
<summary>¿Cómo puedo cambiar la lista de ítems disponibles para craftear?</summary>

Para cambiar la lista de ítems disponibles para craftear, debes actualizar el archivo [recipes.json](../../../src/assets/recipes.json) con los nuevos ítems crafteables.

> Es importante seguir la misma estructura que los originales.

</details>

### Problemas conocidos

| Problema                               | Solución                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| :------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Prolog no puede encontrar recursos del sistema | _En Eclipse IDE, ve a la pestaña `File` y selecciona la opción `Import`. Luego, debes buscar `Launch configurations`, seleccionarla y presionar `Next`. Después, busca el directorio del proyecto, selecciona los archivos `Java-Practical-Work-2025`, `Main`, y `PrologServiceTests` y marca la opción `Overwrite existing launch configurations without warning.`. Finalmente, presiona `Finish` para cargar tu instancia local de Prolog. Si esto no funcionó, revisa el valor de cada variable de entorno dentro de la pestaña `Environment` en cada configuración de ejecución de `Java-Practical-Work-2025`, `Main`, y `PrologServiceTests`, ya que deben apuntar a tu directorio local de Prolog._ |

## Diagramas

<details>
<summary>Diagrama de clases</summary>

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

    class Menu {
        -Scanner scanner
        -Inventory inventory
        -ItemsRepository itemsRepository
        -Item itemToCraft
        -int quantityToCraft
        -CraftingSystem craftingSystem
        -PrologService prologService

        -void setItemToCraft()
        +void init()
        -int requestOperation(String item, int quantity)
        -int requestBranch()
        -int requestRecipeToCraft()
        -String requestPrologPath()
        -String requestInventorySavePath()
        -void showCraftableItemsByProlog()
        -void showIngredientsCollection(Collection~List~Ingredient~~ collection, Function~Integer, String~ onEmptyList)
    }

    Menu "1" --o "1" Inventory : Has a reference to
    Menu "1" --o "1" ItemsRepository : Has a reference to
    Menu "1" --o "1" Item : Has
    Menu "1" --o "1" CraftingSystem : Has
    Menu "1" --o "1" PrologService : Has a reference to

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
        +void toFile(String path)
        -void toFile(FileWriter fWriter)
        -String toProlog(ItemsRepository itemsRepository)
        -String toProlog(Inventory inventory)
        -String utilityRules()
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
        -int getCraftableUnits(Recipe recipe)
        +HashMap~Recipe, List~Ingredient~~ getMissingIngredients()
        +HashMap~Recipe, List~Ingredient~~ getMissingBaseIngredients(int branch)
        -HashMap~Recipe, List~Ingredient~~ getMissingIngredients(HashMap~Recipe, List~Ingredient~~ recipes)
	    +HashMap~Recipe, List~Ingredient~~ getRequiredIngredients()
        +HashMap~Recipe, List~Ingredient~~ getRequiredBaseIngredients(int branch)
        -List~Ingredient~ getBaseIngredientsRecursive(Recipe recipe, int totalToCraft, int branch, Set~Item~ processedCraftingTables)
	    +boolean canCraft()
	    +void setItemToCraft(Item item, int quantity)
	    +CraftedItem craftItem(int recipe)
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
> Los diagramas fueron desarrollados desde cero como parte de los informes preliminares del proyecto.

## Flujo de trabajo en equipo

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

### Etiquetas

-   `vMAJOR.MINOR.PATCH`: Esta etiqueta indica una [versión](https://github.com/hozlucas28/Java-Practical-Work-2025/releases) del trabajo práctico siguiendo [Semantic Versioning](https://semver.org/), y solo estará presente en los commits de la rama `Master`.

### Ramas

-   `Master`: Rama que contiene las versiones de desarrollo del trabajo práctico, donde los miembros del equipo introducirán nuevos cambios (commits).

> [!IMPORTANT]
> Las versiones estables solo están disponibles como [releases](https://github.com/hozlucas28/Java-Practical-Work-2025/releases).

> [!NOTE]
> Las demás ramas son ficticias y representan las contribuciones individuales de cada miembro a la rama `Master`.

## Equipo de desarrollo

-   [Aguilera Emanuel](https://github.com/EmaaAg)
-   [Hoz Lucas](https://github.com/hozlucas28)
-   [De Marco Juan](https://github.com/juDemarco)
-   [Maudet Alejandro](https://github.com/Mabbdet)
-   [Monges Omar](https://github.com/Omar-Monges)
-   [Rueda Olarte Joel](https://github.com/joelalexisrueda)

## Material adicional

-   [Informe del trabajo práctico](../../assets/report.pdf)
-   [Requerimientos del trabajo práctico](./requirements.md)

## Licencia

Este repositorio está bajo la [Licencia MIT](./LICENSE). Para más información sobre lo que está permitido con el contenido de este repositorio, visita [choosealicense.com](https://choosealicense.com/licenses/).

## Agradecimientos

Queremos agradecer a los docentes del curso de Paradigmas de Programación de la [UNLaM](https://www.unlam.edu.ar/) por su apoyo y guía.
