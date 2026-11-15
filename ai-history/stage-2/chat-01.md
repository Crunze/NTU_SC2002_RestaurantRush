# AI history: Stage 2, chat 01

- **Member:** Cheng Xin
- **Tool:** Claude
- **Topic:** Planning Stages 2 and 3, VIP and Critic customers, CustomerFactory, git help
- **Note:** Part 1 of one continuous chat. It continues in [../stage-3/chat-01.md](../stage-3/chat-01.md).

---

**Me:**

SC2002 Restaurant Rush group project, Java. Groupmate finished Stage 1, I need to do Stage 2 (VIP and Critic customers) and Stage 3 (Host, ComboMeal, Happy Hour). I’m a beginner and want to learn, not just copy.

*(attached the assignment brief)*

**Claude:**

I'll start by reading the assignment brief so I can see exactly what Stages 2 and 3 ask for.

I've read the brief. Since you want to learn, here's how I'd think about Stages 2 and 3, with the "why" behind each choice. I can't see your groupmate's Stage 1 code yet, so some of this depends on how they built it. I'll flag where.

## The big idea in both stages

Most of the marks for Stages 2 and 3 are about adding new behaviour without breaking or rewriting Stage 1. Each new feature should mostly be a new class plugged into the existing design. The brief says this directly for Stage 2: different customer behaviour must be "dispatched through the customer objects, not through a chain of type checks." That means using polymorphism.

Here's the difference, using a generic example rather than your actual code:

```java
// ❌ What they DON'T want: the controller knows about every type
if (c instanceof VIPCustomer) c.satisfaction -= 5;
else if (c instanceof CriticCustomer) c.satisfaction -= 12;
else c.satisfaction -= 8;

// ✅ What they DO want: each customer knows its own rule
c.loseSatisfactionForWaiting();   // the controller doesn't care which type it is
```

With the second version, adding a fourth customer type later needs no change to the controller. That is the Open/Closed Principle (OCP), and you can name it in your design discussion.

## Stage 2: VIP and Critic

**1. Check how Stage 1's `Customer` is written.** This is the most important step. If it hardcodes `8` somewhere, or the turn controller does the satisfaction maths itself, you'll need to make a small change. Move that rule into a method that subclasses can override. This kind of change is fine. The brief expects "small registration or setup changes," as long as you explain them in the README.

**2. Decide what varies between customer types.** From the table in the brief, three things vary:
- satisfaction lost per waiting turn (8 / 5 / 12)
- the bill (full price / 10% off)
- the Critic's special behaviour, which you design yourself

Each of these becomes a method on `Customer` that the subclasses override. A good question to ask yourself is whether `Customer` should be abstract with `RegularCustomer` as a subclass, or whether Regular should just be the default `Customer`. Both work. Pick one and be ready to explain why in the video.

**3. Design the Critic's behaviour.** Keep it simple and easy to show in the video. Two possible directions:
- *Order preference:* the Critic always orders the dish with the most prep units, which makes them harder to serve quickly.
- *Reaction to service:* if the Critic is served within N turns of ordering, they gain some satisfaction back. If not, they lose a bonus amount.

Either one should be a method the Critic overrides, such as `chooseDish(...)` or `onServed(turn)`, and not an `if` statement in the controller.

**4. Update the arrival schedule.** Swap two of the six arrivals for a VIP and a Critic. This is a setup change, not a rules change.

**5. Watch the money.** Store prices in integer cents. 10% off $15.00 is `1500 * 0.9 = 1350`. Decide where rounding happens using `Math.round`, because it matters in Stage 3.

## Stage 3: Host, ComboMeal, Happy Hour

**Host.** This is a new staff role. If Stage 1 has a staff base class, `Host` extends it. The key design point is that both Host and Waiter can seat customers. If the seating logic currently lives inside `Waiter`, move it somewhere both can call, such as a `Restaurant.seat(customer, table)` method, so you don't copy-paste it. You also need to decide two things: whether the Host acts before the Waiter, and whether the manager picks who the Host seats or it happens automatically, oldest first, like the Cashier. Document both decisions.

**ComboMeal.** This is the clearest example of composition in the whole project: a combo *has* two menu items. How you build it depends on Stage 1:
- If menu items are an **enum** (`SALAD, BURGER, PASTA`), you'll have a problem, because a `ComboMeal` can't be an enum value. The clean fix is a small interface, for example `Dish` with `getPriceCents()` and `getPrepUnits()`, that both the enum and `ComboMeal` implement. This is a real reason to add an interface, which is what the brief means by not creating interfaces "just to satisfy a checklist." It makes a good discussion point.
- If menu items are already a class, `ComboMeal` can work alongside it in a similar way.

Check your maths against the example in the brief: Salad + Pasta = 2700 cents × 0.9 = 2430 cents, with 1 + 2 = 3 prep units.

**Happy Hour.** Treat it as a pricing policy object that tracks whether it has been used, and how many turns are left (2, then 1, then 0). There are three places it connects to the game:
1. **Start of turn:** offer to activate it if it hasn't been used yet, show the turns remaining, and apply +15 satisfaction (capped at 100) to waiting customers.
2. **Taking an order:** this is where the price gets *locked*. Your `Order` should store its final price in cents when it is created. The calculation is base price, then the customer's discount, then Happy Hour. For example, a VIP ordering a Burger: 1500 × 0.9 × 0.8 = 1080.
3. **Everywhere else:** nothing changes. Orders already placed keep their locked price.

## Suggested order of work

1. Read through Stage 1 fully and sketch its classes on paper. You'll need to explain the design in the video anyway.
2. Make sure `stage-1` is tagged, then start Stage 2 from it.
3. Stage 2: first refactor the satisfaction and bill rules into `Customer`. Check that Stage 1 still behaves the same. Then add VIP, then Critic, then update the arrivals. Tag `stage-2`.
4. Stage 3: do Host, then ComboMeal, then Happy Hour. Happy Hour is last because it touches pricing, which ComboMeal also changes. Tag `stage-3`.
5. Keep notes as you go on which files changed and why. The README needs this for each stage transition.

One reminder: each of you has to submit a GAI declaration, so keep a rough note of how you used AI help. Working this way, where you write the code and get explanations and reviews, is much easier to declare honestly than pasted code.

**Next step:** paste your groupmate's `Customer` class, the menu item class or enum, `Order`, and the part of the turn controller that handles satisfaction loss and payment. With those I can tell you exactly what needs to change for Stage 2. Then you write the VIP class and I'll review it.

---