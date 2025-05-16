# Practical Assignment

This practical assignment aims to apply the concepts, principles, and characteristics of the object-oriented programming paradigm (Classes, objects, methods, interface, inheritance, polymorphism, collections, etc.).

## Delivery Schedule

| Delivery Type       | Date         | Material to Submit                                                                |
| :------------------ | :----------- | :-------------------------------------------------------------------------------- |
| Intermediate Report | May 27, 2025 | Tentative class diagram                                                           |
| Final Report        | July 1, 2025 | Complete practical work (tentative class diagram, report, and project repository) |

## Objective

Implement the concepts addressed from the Object-Oriented Programming paradigm as a solution to a real-world problem, starting from problem analysis, designing the solution (Class Diagram), and subsequently building the final product (Software).

### Specific Objectives

-   Apply the fundamental concepts of the object-oriented paradigm in a playful and concrete context.
-   Analyze a real-world problem and model its solution through classes, relationships, and responsibilities.
-   Design an extensible system, with low coupling and high cohesion.
-   Work with collections, dynamic data structures, and external files.
-   Perform automated tests that verify the correct behavior of the system in various scenarios.

## Task

### Theme

Model a crafting system inspired by video games, applying object-oriented programming principles. The goal is to represent crafting recipes, basic ingredients, intermediate objects, and a player's inventory, without using graph structures. Additionally, the solution should be easily adaptable to the incorporation of new objects, ingredients, or rules.

> [!NOTE]
> Crafting is the process of combining different elements or materials to create new objects. It is a common mechanic in video games, where players must gather basic resources and use them according to recipes to make tools, weapons, structures, or other useful items.

In this system, some objects can be crafted by combining other objects, while others are **basic elements** that cannot be crafted and must be gathered. Each recipe specifies the quantity of each ingredient required and the quantity of items it produces. For example: You may need 2 wood and 1 coal to produce 3 torches in 6 minutes.

The player has an **inventory** with quantities of different objects. Based on this, the system must respond to queries related to the crafting system.

### Glossary

-   **Crafting**: combining elements to create new objects.
-   **Recipe**: a set of ingredients and required quantities to create something.
-   **Basic ingredient**: a collectible resource that cannot be crafted.
-   **Intermediate object**: an object that is crafted and then can be used as part of another recipe.
-   **Inventory**: a list of objects and quantities owned by the player.

## General Description

The system is expected to support the following functionalities:

<details>
<summary>What do I need to craft an object?</summary>

Given a craftable object, show the list of ingredients and required quantities (only the first level of the recipe, without breaking down the ingredients).

</details>

<details>
<summary>What do I need to craft an object from scratch?</summary>

Given a craftable object, show all the necessary basic elements, with their total quantities, considering the complete breakdown of its ingredients into basic elements.

</details>

<details>
<summary>What am I missing to craft an object?</summary>

Given an object and an inventory, indicate which ingredients and in what quantity are missing to be able to craft it (first level only).

</details>

<details>
<summary>What am I missing to craft an object from scratch?</summary>

Same as point 3, but considering the basic elements needed for the entire crafting chain.

</details>

<details>
<summary>How many can I craft?</summary>

Given an object and an inventory, indicate how many units of the object can be crafted using the inventory's elements (and crafting intermediate ingredients if necessary).

</details>

<details>
<summary>Perform the indicated crafting</summary>

The inventory initially contains certain elements and objects, and it should be possible to perform a crafting operation. This is an action that modifies the inventory contents, which must be updated accordingly.

</details>

<details>
<summary>In all cases, indicate the required times</summary>

Crafting, by nature, takes time. It is not instantaneous, and these times must be considered and reported in each of the previous questions. When performing chained crafting, the times must be added and multiplied appropriately based on the number of units involved.

</details>

<details>
<summary>Crafting history</summary>

Record each crafted object, with its ingredients used and the date or turn of creation.

</details>

## Technical Requirements

-   Do not use graphs.
-   Use classes, objects, and relationships between them.
-   Apply principles of encapsulation, composition, and single responsibility.
-   Use automated tests and simple verification methods to test the above scenarios.
-   It must be possible to add new recipes and objects without modifying existing code (open/closed principle).

## Integration with Prolog

Integrate with Prolog to answer the following question:

What are all the products that could be generated with the current inventory?

The system must translate the current inventory into Prolog facts (`have/2`) and define rules and recipes (`ingredient/3`). Prolog will respond with the list of possible objects, automatically deducing them based on the available recipes and quantities.

Example (does not solve the proposed problem):

```pl
% Facts
ingredient(stick, wood, 2).
ingredient(sword, iron, 3).
ingredient(sword, stick, 1).
basic_element(wood).
basic_element(iron).

% Inventory
have(wood, 4).
have(iron, 6).

% Rules
can_craft(Object) :-
    ingredient(Object, Ing, Qty),
    have(Ing, AvailableQty),
    AvailableQty >= Qty.
```

## Input Data

At least two files must be used to input the necessary information into the system. This information **must not be hardcoded but used based on the provided file**.

1. A `recipes.json` (or XML) file, describing the elements and items, also indicating the ingredients they are made of (if they are not basic elements). That is, everything needed to operate.
2. A `inventory.json` (or XML) file, specifying the elements present in the player’s initial inventory.
3. As a bonus **(+1 point)** when closing the program, create a `inventory-out.json` (or XML) file specifying the elements present in the player’s final inventory.

## Testing

Create a test suite for each functional question, as well as for inventory changes as crafting is performed.

Use JUnit for this and validate that crafting operations are executed correctly. Proper test coverage is expected for all described functionalities, including both successful cases and scenarios with errors or restrictions.

## Bonus

Bonuses are optional goals that will increase the group's grade. The grade of the practical work with all the above requirements fulfilled is 8 (if everything is perfect). One or more of these bonuses can be added to increase the grade (up to a maximum of 6 points). They can be used to compensate for any unfinished functionality or to raise the grade to a 10.

<details>
<summary>Show the crafting tree <strong>(+1 point)</strong></summary>

Through a text interface, display how a particular crafting operation would be performed, properly informing each required step.

</details>

<details>
<summary>Alternative recipes or variants <strong>(+1 point)</strong></summary>

Allow the same object to be crafted with different combinations of ingredients (e.g., a torch can be made with charcoal or wood coal).

</details>

Final inventory (previously mentioned) **(+1 point)**

<details>
<summary>Catalysts <strong>(+3 points)</strong></summary>

Catalysts are additional elements that optimize the crafting process, possibly reducing the number of base materials needed or increasing the amount of resulting materials. Each catalyst is linked to a specific type of recipe (e.g., fire catalysts can only be used with fire-type recipes). There is one type of catalyst per recipe type, and the inventory may contain none or multiple units of each type.

> Catalysts cannot be used as ingredients in recipes.

</details>

<details>
<summary>Workbenches <strong>(+3 points)</strong></summary>

Workbenches are optional tools that expand the set of available crafting recipes. Each bench unlocks between `1` and `N` additional recipes. Multiple types of benches must exist, and their presence or absence in the inventory (`0` or `1` per type) directly affects which recipes are available, thus modifying the system's overall behavior. This item can be combined with bonus point 2, but it is not compatible with the catalyst mechanic (bonus point 4).

> If I had the ingredients, I could craft workbenches **(+1 point)**.

</details>

## Interface

The proper functioning of the system will be evaluated using two simultaneous approaches:

1. Each developed functionality must be tested using JUnit.
2. A main method must be executable, in which the elements are created, and crafting operations are performed automatically.

> [!TIP]
> We suggest preparing several scenarios to demonstrate all developed features.

## Submission

The assignment will be done in groups of 4 to 6 people.

A report is expected to include at least:

-   Cover page
-   Table of contents
-   Introduction
-   Development
-   Conclusions
-   References (APA)

There will be an oral presentation and defense on the designated date, and a series of rubrics will be applied accordingly.

## More Information

Grow, A. et al. 2017. Crafting in Games\
[https://www.digitalhumanities.org/dhq/vol/11/4/000339/000339.html](https://www.digitalhumanities.org/dhq/vol/11/4/000339/000339.html)
