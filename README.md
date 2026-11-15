# NTUBurger (Restaurant Rush)

A turn-based restaurant management game for the command line, written in Java for SC2002. You are the manager: each
turn you give one task to the Waiter and one to the Chef, and the Cashier works on its own. Serve enough happy
customers within 18 turns to win.

## Stages

The game is built in three stages, each its own commit and tag so that every stage can be built, run and tested alone.

| Stage | Adds | Tag |
|-------|------|-----|
| 1 | Base game: Regular customers, Waiter, Chef, Cashier | [stage-1](https://github.com/Crunze/NTU_SC2002_RestaurantRush/releases/tag/stage-1) |
| 2 | VIP and Critic customers | [stage-2](https://github.com/Crunze/NTU_SC2002_RestaurantRush/releases/tag/stage-2) |
| 3 | Host, Combo Meals and Happy Hour | [stage-3](https://github.com/Crunze/NTU_SC2002_RestaurantRush/releases/tag/stage-3) |

This README describes the game up to **Stage 3**: the complete game with VIP and Critic customers, a Host, Combo Meals
and Happy Hour.

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
- Customers arrive on turns 1, 4, 7, 10, 13 and 16 with satisfaction 100. Customer 2 (turn 4) is a VIP and customer 3
  (turn 7) is a Critic; the rest are Regular. While waiting or seated without being served, a Regular loses 8
  satisfaction per turn, a VIP 5 and a Critic 12.
- Menu: Salad $9.00 (1 unit), Burger $15.00 (1 unit), Pasta $18.00 (2 units).
- Combo Meals (two dishes, 90% of their summed price, summed preparation units): Salad + Burger $21.60 (2 units),
  Salad + Pasta $24.30 (3 units), Pasta + Burger $29.70 (3 units).
- Customers with an even id (2, 4, 6) choose from the Combo Meals; the others choose from the single dishes. Regular and
  VIP customers pick at random from a fixed seed (18), so a run is reproducible. A Critic always orders the item with
  the most preparation units from the list it is offered.
- A VIP pays 90% of the price, rounded to the nearest cent. Regular and Critic customers pay full price.
- Happy Hour can be switched on once per game in the beginning of a turn, and lasts that turn and the next turn. Orders taken
  while it is active get 20% off after the customer's own discount, rounded to the nearest cent (a VIP Burger is
  $15 × 0.9 × 0.8 = $10.80). The price is locked when the order is taken.
- **Win** after turn 18: at least 4 customers paid, revenue of at least $45.00, and an average satisfaction of paid
  customers of at least 60. The game always plays all 18 turns.

## Turn order (`GameController`)

1. Arrivals join the waiting queue; the board is shown. While Happy Hour is unused, you are asked whether to switch it
   on. While it is active, customers still waiting for a table or food recover 15 
2. You choose the Waiter's task (seat / take order / serve / wait), then a waiting customer for the Host to seat (or
   wait), then the Chef's order to cook (or wait). An invalid choice prints why and asks again; it never uses up the
   turn.
3. The Waiter acts first, then the Host, then the Chef. The Chef can cook an order taken this turn, but the Waiter can never serve
   food that the Chef finishes in the same turn.
4. The Cashier collects from the customer who has been ready to pay the longest (only customers who became ready on an
   earlier turn). Then customers served on an earlier turn finish eating.
5. Customers still waiting or seated lose satisfaction. At 0 they leave and their order is cancelled.
6. The end-of-turn summary is shown and Happy Hour counts down. After turn 18 the result is shown.

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
  Restaurant/             Restaurant (processes, tables), StateManager (status lists), Order, Table, ComboMeal,
                        HappyHour, views
  Roles/                  Customer types, Waiter, Host, Chef, Cashier
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

## Stage 2 → Stage 3 changes

### Host

**Added**

| Class | Responsibility |
|-------|----------------|
| `Host` | A second member of staff who can seat one waiting customer per turn (`seatCustomer`). |

**Modified**

| Class | Change | Reason |
|-------|--------|--------|
| `Restaurant` | `seatProcess` is overloaded for the Waiter and the Host; both call one private `seat` method. | One copy of the seating rule, so the status change stays inside `updateCustomerState` for both. |
| `ChoiceValidator` | Adds `validateHost`: the customer must be waiting, must not be the one the Waiter is seating, and a table must still be free after the Waiter. | Invalid Host choices are explained and asked again, like the other staff. |
| `ManagerMenu`, `StaffRole` | Adds `promptHost()` and `StaffRole.HOST`. | Every manager decision is asked here. |
| `GameController`, `GameApp` | Creates the Host, asks for its task after the Waiter and acts on it after the Waiter and before the Chef. | Sequencing only. |

### Combo Meals

**Added**

| Class | Responsibility |
|-------|----------------|
| `ComboMeal` | A `MenuItem` made of two dishes: summed preparation units and 90% of the summed price. |

**Modified**

| Class | Change | Reason |
|-------|--------|--------|
| `BaseSetting` | Adds the three Combo Meals (`COMBOS`). | Part of the base configuration, like the single dishes. |
| `Restaurant.processNewOrder` | Takes the menu and the combos; customers with an even id choose from the combos. | The documented combo choice rule. Because a `ComboMeal` is a `MenuItem`, ordering, cooking and pricing need no other change. |

### Happy Hour

**Added**

| Class | Responsibility |
|-------|----------------|
| `HappyHour` | The Happy Hour policy: can be activated once per game, stays active for that turn and the next (`turnsLeft` counts 2 → 0), and `applyTo` takes 20% off a price, rounded to the nearest cent. All its numbers (2 turns, 20%, +15 satisfaction) live in this class. |

**Modified**

| Class | Change | Reason |
|-------|--------|--------|
| `Restaurant` | Owns the `HappyHour` and exposes methods to activate it, check it and count it down. New `processHappyHourRecovery` gives WAITING and SEATED customers +15 satisfaction while it is active. | `Restaurant` already owns the processes (ordering, satisfaction), so other classes ask it instead of changing `HappyHour` directly. Customers who are served or have left are not affected. |
| `Restaurant.processNewOrder` | Sets the order's price when it is taken: `happyHour.applyTo(customer.applyDiscount(price))`. | The brief locks the price when the order is taken, with the VIP discount first and then Happy Hour. Orders already placed keep their price. |
| `Cashier` | `processPayment` no longer applies the discount; it only marks the order paid. | The price is already final in the order. Applying the discount again would discount a VIP twice. |
| `Customer` | Adds `recoverSatisfaction(amount)`, capped at 100. | `satisfaction` is private, so only `Customer` can change it. Mirrors `loseSatisfaction`. |
| `ManagerMenu` | Adds `askHappyHour()` (1 = yes, 0 = no). | Every manager decision is asked here. |
| `DisplayController` | Adds `showHappyHour`, which shows the turns left while it is active. | The brief requires the remaining duration to be shown. |
| `GameController` | `step1` asks about Happy Hour after the board (only while it is unused) and runs the recovery; `step6` counts it down. | Sequencing only: the rules stay in `HappyHour` and `Restaurant`. |

**Design note.** Moving pricing from payment to ordering is the main Stage 3 change to earlier code. In Stage 2 the
result was the same either way, because a customer's own discount never changes. Happy Hour is the first rule that
depends on *when* the order was taken, so the price now has to be fixed at that moment.

### What still works

All earlier tests still pass. The scripted tests and the test bot now answer the Happy Hour and Host questions: the
greedy bot always declines Happy Hour and never uses the Host, so its winning strategy is unchanged, while the random
bots switch Happy Hour on and use the Host at random turns with every invariant still audited. 18 new checks cover the
Happy Hour rules, including the brief's VIP Burger example ($10.80) and that orders placed before Happy Hour keep their
price: 363 tests pass in total.

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
