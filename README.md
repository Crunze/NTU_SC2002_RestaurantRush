# NTUBurger

A Java restaurant game for SC2002 (Object-Oriented Design & Programming). Players manage customers, waiters, chefs and a cashier across three stages.

> **Status:** Stage 1 work in progress

## Features

- Menu and order management
- Roles: customer, waiter, chef, cashier
- Three game stages

## Requirements *(to be confirmed)*

- Java JDK 17 or later (adjust to your version)

## How to Run *(to be confirmed)*

1. Clone the repo
2. Open the folder in VS Code
3. Run `GameApp.java`

## Project Structure

- `Restaurant/`: menu and restaurant classes
- `Roles/`: role classes
- `GameApp.java`: entry point

## Team

- Your Name
- Teammate names

---

# Development Notes

## To Be Implemented (High Priority)

1. **DisplayController**: controls which display to use (Start Display, End Display, etc.)
2. **ManagerMenu**: opens a numbered menu for the user to choose an action *(easier to implement)*
3. **GameController**: the core that runs all classes and objects
4. **GameApp**: entry point of the program and initialisation of all core classes
5. **Cashier**: payment calculation method
6. **Exception handling** for all classes
7. **Access modifiers**: adjust (public/protected/private) according to security needs for all classes

## Missing Project Requirements (High Priority)

1. **Invalid Manager choices**
   - Example: asking the Waiter to seat a Customer when there is no one in the queue
   - Should not consume an action point
   - Tell the Manager to choose again and explain why the previous option was invalid
2. **Staff tasks available at start-of-turn display (Ongoing)** 
   - Will be done via ManagerView class
3. **GameController turn order**
   - Waiter must run first, then Chef
   - This lets the Chef cook the current turn's order taken by the Waiter
   - But the Waiter must not serve READY food made by the Chef in the same turn

## Future Design Considerations

1. **Order class as a receipt** *(ongoing)*
   - Order also functions as a receipt, so the Cashier can update the price set in Order and charge accordingly
2. **Configurable staff and actions** *(low priority)*
   - `BaseSetting` allows the TA to edit the number of Cashiers/Chefs/Waiters and number of actions
   - The game must be ready to adjust accordingly

---

# Game Design (Implemented)

## View Restrictions

- `StateView` only allows retrieval of Customer and Order lists sorted by status from the `StateManager` class
- `TableView` only allows retrieval of the table array from the `Restaurant` class

## Restaurant Class Split into StateManager and Restaurant

- `Restaurant` now **only** handles restaurant operations (processes only)
- `Restaurant` owns the tables, so it includes an array to store them
- `StateManager` stores both Customer and Order states and performs operations relating to status
- `StateManager` uses a `Map<Status, List<...>>`: each status is a key, and its value is the list of Customers/Orders

## Others

- `BaseSetting` stores default configurations, subject to TA changes
- No `Menu` class: a list of 3 `MenuItem` objects is used instead, since they are considered base configuration
- `TableView` counts no. of occupied tables
- `ManagerView` tracks tasks available
- Planning to put wait() as manager option rather than Chef/Waiter since they are more of Manager choice and not role specific actions.

---

# Reflection

- Underestimated the workload. Should have done the UML diagram first to track the work required.
- Java has quite a number of convenient methods that bypassed the need of foreach