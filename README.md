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

This README describes the current (**Stage 3**) game. Check out a tag to see an earlier stage, e.g. `git checkout stage-1`,
then `build.bat`, `run.bat` and `test.bat` work the same way at every tag.

## Stage changes

### Stage 1 &rarr; Stage 2: VIP and Critic customers

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

**Effect on the design.** Satisfaction loss (`satisfactionLossPerTurn`), billing (`applyDiscount`) and food choice
(`pickFood`) were already, or became, methods on the abstract `Customer`. Adding two customer types therefore meant
two new subclasses plus one line each in `CustomerFactory`; `Waiter`, `Chef`, `Cashier`, `StateManager` and the
turn loop did not change. This is the open/closed principle and polymorphism at work.

**What still works.** Every Stage 1 test is unchanged and still passes (345 tests at `stage-1`, 383 at `stage-2`).
The new tests cover VIP and Critic loss rates, the VIP bill, the Critic's dish and a VIP payment keeping the status
lists consistent.

### Stage 2 &rarr; Stage 3: Host, Combo Meals and Happy Hour

Compare: [`stage-2...stage-3`](https://github.com/Crunze/NTU_SC2002_RestaurantRush/compare/stage-2...stage-3)

**Added**

| Class | Responsibility |
|-------|----------------|
| `Host` | A staff member who seats at most one queued customer per turn, in addition to the Waiter. |
| `Seater` | Interface for anyone who can seat a customer; implemented by `Waiter` and `Host`. |
| `ComboMeal` | A `MenuItem` made of two dishes: 90% of their combined price (rounded half up) and the sum of their preparation units. |
| `HappyHour` | The once-per-game promotion: activation, remaining turns, 20% off orders taken while active. |

**Modified**

| Class | Change | Reason |
|-------|--------|--------|
| `Waiter` | Implements `Seater`. | So the Waiter and the Host share one seating process. |
| `Restaurant` | `seatProcess` takes a `Seater` instead of a `Waiter`; owns the `HappyHour`, applies its discount when an order is taken, and adds `processHappyHourRecovery`. | Seating rules stay written once for both seaters. The price is locked when the order is taken, as the brief requires. |
| `Customer` | Adds `gainSatisfaction(amount)`, capped at 100. | Happy Hour recovery. |
| `BaseSetting` | Adds `NUM_HOSTS`, `COMBO_A`, `COMBO_B` (added to `MENU`) and the Happy Hour constants. | New configuration lives with the rest of the base setting. |
| `StaffRole`, `ManagerMenu`, `ChoiceValidator` | New `HOST` role, the Host and Happy Hour prompts, and `validateHost`. | The Host may not seat the customer the Waiter is seating, or use the table the Waiter is taking. |
| `GameController` | A start-of-turn Happy Hour phase, the Host's turn between the Waiter and the Chef, and Happy Hour counting down at the end of the turn. | Sequences the new steps; the rules themselves stay in `Restaurant`, `HappyHour` and the validator. |
| `DisplayController` | Shows whether Happy Hour is active (with turns left) or still available. | The brief asks to display its remaining duration. |
| `GameApp`, `TestGame` | Create the Host. | Wiring. |

**Effect on the design.** `ComboMeal` *is a* `MenuItem`, so `Order`, `Chef`, the menu list and the Critic's
"most preparation units" rule handle combos with no changes. The Host reuses `Restaurant.seatProcess` through the
`Seater` interface instead of copying the seating logic.

**What still works.** No earlier test was removed (383 at `stage-2`, 421 at `stage-3`). Three whole-game tests and the
strategy bot now answer the new Happy Hour and Host prompts, and one assertion changed on purpose: the Critic
now orders Combo B (3 units) instead of Pasta, because Combo B is the dish with the most preparation units on the
Stage 3 menu. The 300 random games still find no inconsistent status lists, tables or satisfaction values.

## Requirements

- JDK 17 or newer. No other dependencies.

## Build, run, test (Windows)

| Task | Command |
|------|---------|
| Build | `build.bat` |
| Play | `run.bat` |
| Run all tests | `test.bat` |

## The base setting (`BaseSetting.java`)

- 18 turns, 2 tables, one Waiter, one Host, one Chef, one Cashier.
- Customers arrive on turns 1, 4, 7, 10, 13 and 16 with satisfaction 100. C2 is a **VIP** and C4 is a **Critic**
  (set in `BaseSetting`); the rest are Regular.

  | Type | Satisfaction lost per turn while waiting or seated | Bill | Dish |
  |------|------------------------------------------------------|------|------|
  | Regular | 8 | full price | random |
  | VIP | 5 | 90% of the price, rounded half up | random |
  | Critic | 12 | full price | always the dish with the most preparation units |

  The discount is `Customer.applyDiscount(priceCents)`, which takes the list price and returns the final price in cents.
- Menu (each customer picks at random from a fixed seed, 18, so a run is reproducible; a Critic ignores the random choice):

  | Item | Price | Chef units |
  |------|-------|-----------|
  | Salad | $9.00 | 1 |
  | Burger | $15.00 | 1 |
  | Pasta | $18.00 | 2 |
  | Combo A (Salad + Burger) | $21.60 | 2 |
  | Combo B (Salad + Pasta) | $24.30 | 3 |

  A combo costs 90% of its two dishes together (rounded half up) and takes the sum of their units.
- **Happy Hour**: once per game, lasts 2 turns. Orders taken during it get 20% off, and customers waiting for service
  regain 15 satisfaction (up to 100) at the start of each of those turns. Orders taken before it keep their old price.
  The VIP discount is applied on top, when the customer pays.
- **Win** after turn 18: at least 4 customers paid, revenue of at least $45.00, and an average satisfaction of paid
  customers of at least 60. The game always plays all 18 turns.

## Turn order (`GameController`)

1. Arrivals join the waiting queue. While Happy Hour is unused you are asked whether to start it now; if it is active,
   waiting customers recover. The board is shown.
2. You choose the Waiter's task (seat / take order / serve / wait), then the Host's customer to seat (or wait), then the
   Chef's order to cook (or wait). An invalid choice prints why and asks again; it never uses up the turn.
   The Host cannot seat the customer the Waiter is seating, nor use a table the Waiter is taking.
3. The Waiter acts first, then the Host, then the Chef. The Chef can cook an order taken this turn, but the Waiter can
   never serve food that the Chef finishes in the same turn.
4. The Cashier collects from the customer who has been ready to pay the longest (only customers who became ready on an
   earlier turn). Then customers served on an earlier turn finish eating.
5. Customers still waiting or seated lose satisfaction. At 0 they leave and their order is cancelled.
6. An active Happy Hour loses one turn, and the end-of-turn summary is shown. After turn 18 the result is shown.

If the input ends (closed stdin), every remaining prompt counts as "wait" (and Happy Hour as "no") and the game still
finishes.

## Project layout

```
NTUBurger/
  GameApp.java            entry point and wiring
  GameController.java     the turn loop (no rules, only sequencing)
  BaseSetting.java        default configuration: turns, tables, staff, arrivals, menu, Happy Hour
  DisplayController.java  all console output
  Result.java             revenue, average satisfaction, victory
  Manager/                the manager's choices: menu, validator, available tasks
  Restaurant/             Restaurant (processes, tables), StateManager (status lists), Order, Table, MenuItem,
                          ComboMeal, HappyHour, read-only views
  Roles/                  Customer types, CustomerFactory, Waiter, Host, Chef, Cashier
test/NTUBurger/           dependency-free tests (TestRunner)
```

## Testing

`test.bat` runs rule-level tests plus whole-game tests: scripted input, input that ends early, a strategy bot that
wins the base game, and 300 random games in which the status lists, tables and satisfaction are audited at every prompt.

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