# NTUBurger (Restaurant Rush)

A turn-based restaurant management game for the command line, written in Java for SC2002. You are the manager: each
turn you give one task to the Waiter and one to the Chef, and the Cashier works on its own. Serve enough happy
customers within 18 turns to win.

## Stages

The game is built in three stages, each its own commit and tag so that every stage can be built, run and tested alone.

| Stage | Adds | Tag |
|-------|------|-----|
| 1 | Base game: Regular customers, Waiter, Chef, Cashier | `base-stage-1` |
| 2 | VIP and Critic customers | `base-stage-2` |
| 3 | Host, Combo Meals and Happy Hour | `base-stage-3` |

This README describes **Stage 2**.

## Requirements

- JDK 17 or newer. No other dependencies.

## Build, run, test (Windows)

| Task | Command |
|------|---------|
| Build | `build.bat` |
| Play | `run.bat` |
| Run all tests | `test.bat` |

## The base setting (`BaseSetting.java`)

- 18 turns, 2 tables, one Waiter, one Chef, one Cashier.
- Customers arrive on turns 1, 4, 7, 10, 13 and 16 with satisfaction 100. Customer 2 (turn 4) is a VIP and customer 3
  (turn 7) is a Critic; the rest are Regular. While waiting or seated without being served, a Regular loses 8
  satisfaction per turn, a VIP 5 and a Critic 12.
- Menu: Salad $9.00 (1 unit), Burger $15.00 (1 unit), Pasta $18.00 (2 units). Regular and VIP customers pick a dish at
  random from a fixed seed (18), so a run is reproducible. A Critic always orders the dish with the most preparation
  units (Pasta).
- A VIP pays 90% of the price, rounded to the nearest cent. Regular and Critic customers pay full price.
- **Win** after turn 18: at least 4 customers paid, revenue of at least $45.00, and an average satisfaction of paid
  customers of at least 60. The game always plays all 18 turns.

## Turn order (`GameController`)

1. Arrivals join the waiting queue; the board is shown.
2. You choose the Waiter's task (seat / take order / serve / wait), then the Chef's order to cook (or wait).
   An invalid choice prints why and asks again; it never uses up the turn.
3. The Waiter acts first, then the Chef. The Chef can cook an order taken this turn, but the Waiter can never serve
   food that the Chef finishes in the same turn.
4. The Cashier collects from the customer who has been ready to pay the longest (only customers who became ready on an
   earlier turn). Then customers served on an earlier turn finish eating.
5. Customers still waiting or seated lose satisfaction. At 0 they leave and their order is cancelled.
6. The end-of-turn summary is shown. After turn 18 the result is shown.

If the input ends (closed stdin), every remaining prompt counts as "wait" and the game still finishes.

## Project layout

```
NTUBurger/
  GameApp.java            entry point and wiring
  GameController.java     the six-step turn loop (no rules, only sequencing)
  BaseSetting.java        default configuration
  DisplayController.java  all console output
  Result.java             revenue, average satisfaction, victory
  Manager/                the manager's choices: menu, validator, available tasks
  Restaurant/             Restaurant (processes, tables), StateManager (status lists), Order, Table, views
  Roles/                  Customer types, Waiter, Chef, Cashier
test/NTUBurger/           dependency-free tests (TestRunner)
```

## Testing

`test.bat` runs rule-level tests plus whole-game tests: scripted input, input that ends early, a strategy bot that
wins the base game, and 300 random games in which the status lists, tables and satisfaction are audited at every prompt.

## Stage 1 → Stage 2 changes

**Added**

| Class | Responsibility |
|-------|----------------|
| `CustomerVIP` | Loses 5 satisfaction per turn and pays 90% of the price (`applyDiscount`, rounded to the nearest cent). |
| `CustomerCritic` | Loses 12 satisfaction per turn, pays full price and always orders the dish with the most preparation units (overrides `pickFood`). |

**Modified**

| Class | Change | Reason |
|-------|--------|--------|
| `Customer` | `chooseFood(MenuItem)` became `chooseFood(menu, random)`, which calls a new overridable `pickFood` (default: random dish). | Customers choose their own food, so each type can have its own rule. The Critic overrides `pickFood` instead of the controller checking the type. `chosenFood` stays private: subclasses only return a choice. |
| `Restaurant` | `processNewOrder` asks the customer to choose (`customer.chooseFood(menu, random)`) instead of picking the dish itself. | Follows from the change above. |
| `CustomerFactory` | Creates a VIP or Critic when the customer id is listed in `BaseSetting`, otherwise a Regular. | The one place that decides customer types, so the rest of the game never checks them. |
| `BaseSetting` | Adds `VIP_CUSTOMER_IDS = [2]` and `CRITIC_CUSTOMER_IDS = [3]`. | The base run must include at least one VIP and one Critic. They arrive early (turns 4 and 7) so they can be served and pay. |

**Critic behaviour.** A critic wants to judge the kitchen's most demanding dish, so it orders the item with the most
preparation units. Combined with the fastest satisfaction loss, this makes the Critic the hardest customer to serve and
forces the manager to choose between prioritising the Critic and serving others. The rule uses prep units rather than a
named dish, so it still works if the menu changes.

**What still works.** All 345 Stage 1 tests pass unchanged (`test.bat`), including 300 random games with the status
lists, tables and satisfaction audited at every prompt.

---

# Game Design (Implemented)

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