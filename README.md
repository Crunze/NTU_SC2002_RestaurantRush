# NTUBurger (Restaurant Rush)

A turn-based restaurant management game for the command line, written in Java for SC2002. You are the manager: each
turn you give one task to the Waiter, the Host and the Chef, and the Cashier works on its own. Serve enough happy
customers within 18 turns to win.

## Stages

The game is built in three stages, each its own commit and tag so that every stage can be built, run and tested alone.

| Stage | Adds | Tag |
|-------|------|-----|
| 1 | Base game: Regular customers, Waiter, Chef, Cashier | `base-stage-1` |
| 2 | VIP and Critic customers | `base-stage-2` |
| 3 | Host, Combo Meals and Happy Hour | `base-stage-3` |

This README describes the current (**Stage 3**) game. Check out a tag to see an earlier stage.

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