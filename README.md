# NTUBurger (Restaurant Rush)

A turn-based restaurant management game for the command line, written in Java for SC2002. You are the manager: each
turn you give one task to the Waiter, the Host and the Chef, and the Cashier works on its own. Serve enough happy
customers within 18 turns to win.

## Stages

The game is built in three stages, each its own commit and tag so that every stage can be built, run and tested alone.

| Stage | Adds | Tag |
|-------|------|-----|
| 1 | Base game: Regular customers, Waiter, Chef, Cashier | [`stage-1`](https://github.com/Crunze/NTU_SC2002_RestaurantRush/tree/stage-1) |
| 2 | VIP and Critic customers | [`stage-2`](https://github.com/Crunze/NTU_SC2002_RestaurantRush/tree/stage-2) |
| 3 | Host, Combo Meals and Happy Hour | [`stage-3`](https://github.com/Crunze/NTU_SC2002_RestaurantRush/tree/stage-3) |

## Requirements 

- Java JDK 17 or later 

## How to Run 

Compare: [`stage-1...stage-2`](https://github.com/Crunze/NTU_SC2002_RestaurantRush/compare/stage-1...stage-2)

**Added**

| Class | Responsibility |
|-------|----------------|
| `CustomerVIP` | A customer who loses 5 satisfaction per turn and pays 90% of the price (`applyDiscount`, rounded half up). |
| `CustomerCritic` | A customer who loses 12 satisfaction per turn, pays full price and always orders the dish with the most preparation units (overrides `pickFood`). |

**Modified**

| Class | Change | Reason |
|-------|--------|--------|
| `Customer` | `chooseFood(MenuItem)` became `chooseFood(menu, random)`, which calls a new overridable `pickFood` (default: random dish). | The brief says customers choose their own food, and the Critic needs a different choice. Putting the choice in the customer lets each type decide through overriding instead of a type check in the controller. |
| `Restaurant` | `processNewOrder` asks the customer to choose (`customer.chooseFood(menu, random)`) instead of picking the dish itself. | Follows from the change above. |
| `CustomerFactory` | Creates a VIP or Critic when the arrival number is listed in `BaseSetting`, otherwise a Regular. | The one place that decides customer types, so the rest of the game never checks them. |
| `BaseSetting` | Adds `VIP_CUSTOMER_IDS = [2]` and `CRITIC_CUSTOMER_IDS = [4]`. | The base run must include at least one VIP and one Critic; there are still six arrivals. |
| `GameController` | The order message also shows what the customer will pay when it differs from the list price. | Makes the VIP discount visible to the player. Uses `customer.applyDiscount`, so no type check. |

---

# Development Notes

## To Be Implemented 

1. **GameController**: the core that runs all classes and objects
2. **GameApp**: entry point of the program and initialisation of all core classes
3. **Exception handling** for all classes
4. **Access modifiers**: adjust (public/protected/private) according to security needs for all classes

## Missing Project Requirements 

1. **GameController turn order**
   - Waiter must run first, then Chef
   - This lets the Chef cook the current turn's order taken by the Waiter
   - But the Waiter must not serve READY food made by the Chef in the same turn

**What still works.** Every Stage 1 test is unchanged and still passes (345 tests at `stage-1`, 383 at `stage-2`).
The new tests cover VIP and Critic loss rates, the VIP bill, the Critic's dish and a VIP payment keeping the status
lists consistent.

1. **Configurable staff and actions** 
   - `BaseSetting` allows the TA to edit the number of Cashiers/Chefs/Waiters
   - The game must be ready to adjust accordingly

---

# Game Design 

## View Restrictions

- `StateView` only allows retrieval of Customer and Order lists sorted by status from the `StateManager` class
- `TableView` only allows retrieval of the table array and related table view operations from the `Restaurant` class

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
- `Chef` has cookUnits attribute to determine no. of prepUnits to be deducted in `Order` class
- Planning to put wait() as manager option rather than Chef/Waiter since they are more of Manager choice and not role specific actions.

---

# Reflection

- Underestimated the workload. Should have done the UML diagram first to track the work required.
- Java has quite a number of convenient methods that bypassed the need of foreach