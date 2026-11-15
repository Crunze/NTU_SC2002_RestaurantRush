# AI history: Stage 3, chat 01

- **Member:** Cheng Xin
- **Tool:** Claude
- **Topic:** Happy Hour, tests, README, sequence diagram, report, stage tags, AI history
- **Note:** Part 2 of one continuous chat, continued from [../stage-2/chat-01.md](../stage-2/chat-01.md).

---

**Me:**

ive understood the logic to stage 2, can u now give me a big picture of stage 3 and guide me how to start coding?

**Claude:**

## Stage 3: the big picture

Stage 3 adds three features. Each one tests a different OOP idea, and the goal is the same as in Stage 2: **add new classes and change as little existing code as possible.**

| Feature | What it is | Main OOP idea |
|---|---|---|
| **Host** | A second staff member who can seat one waiting customer per turn | Inheritance (`Host extends Staff`), reusing the seating logic |
| **ComboMeal** | Two dishes sold together at 90% of their combined price, with prep units added | Composition (a combo *has* two `MenuItem`s) and polymorphism (it *is* a `MenuItem`) |
| **Happy Hour** | Used once per game, lasts 2 turns, 20% off orders taken during it, +15 satisfaction to waiting customers | Single Responsibility: one class owns all its rules |

**The key design question is where the price gets decided.** In Stage 2 the discount happened at payment. In Stage 3 an order taken during Happy Hour must keep its discount even after Happy Hour ends, so the price has to be **locked when the order is taken**:

```
base price → customer discount (VIP 10%) → Happy Hour (20%) → order.setPrice(...)
```

After that, the Cashier just marks the order paid.

## How to start coding

Do them in this order. Each step builds on the one before.

1. **Host first, since it's the simplest.**
   - Make `Host extends Staff`.
   - Move the seating steps into one place in `Restaurant` that both the Waiter and the Host can call, so you don't copy-paste them.
   - Add `HOST` to `StaffRole`, a prompt in `ManagerMenu`, and a check in `ChoiceValidator`.

2. **ComboMeal next.**
   - Make `ComboMeal extends MenuItem`, holding two items.
   - Its price is `(a + b) * 90 / 100`, rounded in cents, and its prep units are `a + b`.
   - Because it *is* a `MenuItem`, `Order`, `Chef` and `Cashier` need no changes. Test this by checking Salad + Pasta = 2430 cents with 3 units.

3. **Happy Hour last, because it touches pricing.**
   - Write a `HappyHour` class first, on its own. Give it `used` and `turnsLeft` fields, and the methods `canActivate()`, `activate()`, `isActive()`, `endTurn()` and `applyTo(priceCents)`.
   - Test it alone before connecting it to anything.
   - Then connect it in three places:
     - **Start of turn:** ask the manager whether to activate it, and give waiting customers +15 (capped at 100).
     - **Taking an order:** lock the price.
     - **End of turn:** `endTurn()`.

4. **After each feature, run the tests and commit.** One small commit per step makes the Stage 2 → Stage 3 comparison easy to explain.

Start by writing `HappyHour.java` with just the fields and `canActivate()` / `activate()`, then paste it here and I'll review it.

---