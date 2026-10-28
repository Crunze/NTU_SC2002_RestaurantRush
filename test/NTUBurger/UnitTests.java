package NTUBurger;

import static NTUBurger.TestRunner.check;
import static NTUBurger.TestRunner.checkEquals;

import java.util.List;
import java.util.Random;

import NTUBurger.Manager.ChoiceValidator;
import NTUBurger.Manager.ManagerView;
import NTUBurger.Manager.StaffAction;
import NTUBurger.Manager.Task;
import NTUBurger.Restaurant.HappyHour;
import NTUBurger.Restaurant.Order;
import NTUBurger.Restaurant.OrderStatus;
import NTUBurger.Restaurant.Restaurant;
import NTUBurger.Restaurant.StateManager;
import NTUBurger.Restaurant.Table;
import NTUBurger.Roles.Cashier;
import NTUBurger.Roles.Chef;
import NTUBurger.Roles.Customer;
import NTUBurger.Roles.CustomerCritic;
import NTUBurger.Roles.CustomerFactory;
import NTUBurger.Roles.CustomerRegular;
import NTUBurger.Roles.CustomerStatus;
import NTUBurger.Roles.CustomerVIP;
import NTUBurger.Roles.Host;
import NTUBurger.Roles.Waiter;

// Rule-level tests that need no console

public final class UnitTests {
    private UnitTests() { }

    public static void run() {
        customerBasics();
        orderLifecycle();
        tables();
        stateManagerGuards();
        fullLifecycleKeepsStatusFilingInSync();
        customerTypes();
        combos();
        happyHour();
        hostAndSeating();
        hostValidation();
    }

    private static void customerBasics() {
        Customer c = new CustomerRegular(1);
        checkEquals(100, c.getSatisfaction(), "starts at 100");
        c.loseSatisfaction();
        checkEquals(92, c.getSatisfaction(), "Regular loses 8");
        for (int i = 0; i < 20; i++) {
            c.loseSatisfaction();
        }
        checkEquals(0, c.getSatisfaction(), "satisfaction never goes below 0");
        check(c.isSatisfactionZero(), "zero satisfaction detected");
        checkEquals(1500, c.applyDiscount(1500), "Regular pays full price");
        checkEquals("Regular", c.getTypeLabel(), "type label");
    }

    private static void orderLifecycle() {
        Order pasta = new Order(1, BaseSetting.PASTA);
        checkEquals(OrderStatus.PLACED, pasta.getStatus(), "new order is PLACED");
        pasta.preparing(1);
        checkEquals(OrderStatus.COOKING, pasta.getStatus(), "first pasta unit: COOKING");
        pasta.preparing(1);
        checkEquals(OrderStatus.READY, pasta.getStatus(), "second pasta unit: READY");
        pasta.preparing(1);
        checkEquals(OrderStatus.READY, pasta.getStatus(), "a READY order cannot be cooked again");
        Order salad = new Order(2, BaseSetting.SALAD);
        salad.preparing(1);
        checkEquals(OrderStatus.READY, salad.getStatus(), "salad needs one unit");
    }

    private static void tables() {
        Table t = new Table(1);
        check(!t.isOccupied(), "new table is free");
        t.seat(5);
        check(t.isOccupied(), "seated table is occupied");
        checkEquals(5, t.getCustomerId(), "table remembers the customer id");
        boolean threw = false;
        try {
            t.seat(6);
        } catch (IllegalStateException e) {
            threw = true;
        }
        check(threw, "seating at an occupied table is rejected");
        t.free();
        check(!t.isOccupied(), "free() empties the table");
    }

    // The two mistakes that desynchronise a status filing are detected instead of silently accepted
    private static void stateManagerGuards() {
        StateManager sm = new StateManager();
        Customer c = new CustomerRegular(1);
        sm.addCustomerToQueue(c);
        check(sm.isConsistent(), "new customer is filed under WAITING");
        boolean threw = false;
        try {
            sm.updateCustomerState(c, () -> { });
        } catch (IllegalStateException e) {
            threw = true;
        }
        check(threw, "an action that changes no status is rejected");
        c.seat();
        check(!sm.isConsistent(), "changing a customer behind the manager's back is detected");
    }

    // Regression for the three easy mistakes: seat must leave the queue, a served customer must stop
    // losing satisfaction, and paying must move the customer itself to PAID
    private static void fullLifecycleKeepsStatusFilingInSync() {
        StateManager sm = new StateManager();
        Restaurant r = new Restaurant(sm, new Random(1));
        Waiter w = new Waiter(1);
        Chef chef = new Chef(1);
        Cashier cashier = new Cashier(1);
        Customer c = new CustomerRegular(1);
        r.updateArrivalQueue(c);

        r.seatProcess(w, c, r.firstFreeTable());
        checkEquals(List.of(), sm.getCustomersByStatus(CustomerStatus.WAITING), "seated customer leaves WAITING");
        checkEquals(List.of(c), sm.getCustomersByStatus(CustomerStatus.SEATED), "seated customer is SEATED");
        check(sm.isConsistent(), "consistent after seating");

        r.processNewOrder(w, c, List.of(BaseSetting.SALAD));
        Order order = sm.findOrderOf(c);
        checkEquals(900, order.getPrice(), "order price is the menu price");
        r.preparingFood(chef, order);
        checkEquals(OrderStatus.READY, order.getStatus(), "salad is ready after one cook");
        r.processServeOrder(w, c, order, 2);
        checkEquals(CustomerStatus.SERVED, c.getStatus(), "served customer is SERVED");
        checkEquals(List.of(c), sm.getCustomersByStatus(CustomerStatus.SERVED), "and filed under SERVED");
        int before = c.getSatisfaction();
        r.processSatisfaction();
        checkEquals(before, c.getSatisfaction(), "a customer who has been served loses no satisfaction");

        r.processFinishEating(c, 3);
        checkEquals(CustomerStatus.READY_TO_PAY, c.getStatus(), "customer is ready to pay");
        r.processReadyToPay(cashier, c, order, r.findTableOf(c));
        checkEquals(CustomerStatus.PAID, c.getStatus(), "payment moves the customer itself to PAID");
        checkEquals(OrderStatus.PAID, order.getStatus(), "and the order to PAID");
        check(sm.isConsistent(), "consistent after payment");
        checkEquals(0, r.countOccupiedTables(), "payment frees the table");
    }

    // Stage 2: VIP and Critic differ from Regular only through the overridden methods
    private static void customerTypes() {
        Customer vip = new CustomerVIP(2);
        Customer critic = new CustomerCritic(4);
        vip.loseSatisfaction();
        critic.loseSatisfaction();
        checkEquals(95, vip.getSatisfaction(), "VIP loses 5 per turn");
        checkEquals(88, critic.getSatisfaction(), "Critic loses 12 per turn");
        checkEquals(1350, vip.applyDiscount(1500), "VIP pays 90% of a burger");
        checkEquals(810, vip.applyDiscount(900), "VIP pays 90% of a salad");
        checkEquals(1620, vip.applyDiscount(1800), "VIP pays 90% of a pasta");
        checkEquals(815, vip.applyDiscount(905), "814.5 rounds half up to 815");
        checkEquals(814, vip.applyDiscount(904), "813.6 rounds to 814");
        checkEquals(1500, critic.applyDiscount(1500), "Critic pays full price");
        checkEquals("VIP", vip.getTypeLabel(), "VIP label");
        checkEquals("Critic", critic.getTypeLabel(), "Critic label");

        for (int seed = 0; seed < 20; seed++) {
            critic.chooseFood(BaseSetting.MENU, new Random(seed));
            checkEquals("Combo B (Salad + Pasta)", critic.getChosenFood().getName(),
                    "Critic always orders the dish with most prep units");
        }

        checkEquals("Regular", CustomerFactory.create(1).getTypeLabel(), "C1 is Regular");
        checkEquals("VIP", CustomerFactory.create(2).getTypeLabel(), "C2 is VIP");
        checkEquals("Regular", CustomerFactory.create(3).getTypeLabel(), "C3 is Regular");
        checkEquals("Critic", CustomerFactory.create(4).getTypeLabel(), "C4 is Critic");
        checkEquals("Regular", CustomerFactory.create(5).getTypeLabel(), "C5 is Regular");

        // A VIP's burger is billed at the discounted price, and that is what counts as revenue
        StateManager sm = new StateManager();
        Restaurant r = new Restaurant(sm, new Random(1));
        Waiter w = new Waiter(1);
        Chef chef = new Chef(1);
        Cashier cashier = new Cashier(1);
        r.updateArrivalQueue(vip);
        r.seatProcess(w, vip, r.firstFreeTable());
        r.processNewOrder(w, vip, List.of(BaseSetting.BURGER));
        Order order = sm.findOrderOf(vip);
        r.preparingFood(chef, order);
        r.processServeOrder(w, vip, order, 2);
        r.processFinishEating(vip, 3);
        r.processReadyToPay(cashier, vip, order, r.findTableOf(vip));
        checkEquals(1350, order.getPrice(), "VIP burger is billed at 13.50");
        checkEquals(1350, new Result(sm, 4).revenue(), "revenue counts the discounted bill");
        check(sm.isConsistent(), "consistent after a VIP payment");
    }

    // Stage 3: a combo is a MenuItem costing 90% of its two dishes and taking the sum of their units
    private static void combos() {
        checkEquals(2160, BaseSetting.COMBO_A.getPrice(), "Combo A is 90% of 2400");
        checkEquals(2430, BaseSetting.COMBO_B.getPrice(), "Combo B is 90% of 2700");
        checkEquals(2, BaseSetting.COMBO_A.getPrepUnits(), "Combo A takes 1 + 1 units");
        checkEquals(3, BaseSetting.COMBO_B.getPrepUnits(), "Combo B takes 1 + 2 units");
        checkEquals(5, BaseSetting.MENU.size(), "three dishes plus two combos");
        checkEquals("Salad", BaseSetting.MENU.get(0).getName(), "single dishes come first");
        Order order = new Order(1, BaseSetting.COMBO_B);
        Chef chef = new Chef(1);
        for (int i = 0; i < 2; i++) {
            order.preparing(1);
        }
        checkEquals(OrderStatus.COOKING, order.getStatus(), "Combo B is still cooking after 2 units");
        order.preparing(1);
        checkEquals(OrderStatus.READY, order.getStatus(), "Combo B is ready after 3 units");
    }

    private static void happyHour() {
        HappyHour hh = new HappyHour();
        check(hh.isAvailable() && !hh.isActive(), "available and inactive at the start");
        checkEquals(1500, hh.apply(1500), "no discount while inactive");
        check(hh.activate(), "first activation works");
        check(!hh.isAvailable(), "no longer available once started");
        checkEquals(2, hh.getRemainingTurns(), "lasts two turns");
        checkEquals(1200, hh.apply(1500), "20% off a burger");
        checkEquals(1555, hh.apply(1944), "1555.2 rounds to 1555");
        hh.endTurn();
        check(hh.isActive(), "still active after one turn");
        hh.endTurn();
        check(!hh.isActive(), "over after two turns");
        hh.endTurn();
        checkEquals(0, hh.getRemainingTurns(), "remaining turns never go negative");
        check(!hh.activate(), "cannot be used a second time");

        // Orders keep the price they were taken at; recovery is capped at 100
        StateManager sm = new StateManager();
        Restaurant r = new Restaurant(sm, new Random(1));
        Waiter w = new Waiter(1);
        Customer early = new CustomerRegular(1);
        Customer late = new CustomerRegular(2);
        r.updateArrivalQueue(early);
        r.updateArrivalQueue(late);
        r.seatProcess(w, early, r.firstFreeTable());
        r.seatProcess(w, late, r.firstFreeTable());
        r.processNewOrder(w, early, List.of(BaseSetting.BURGER));
        r.getHappyHour().activate();
        r.processNewOrder(w, late, List.of(BaseSetting.BURGER));
        checkEquals(1500, sm.findOrderOf(early).getPrice(), "order taken before Happy Hour keeps its price");
        checkEquals(1200, sm.findOrderOf(late).getPrice(), "order taken during Happy Hour is 20% off");
        early.loseSatisfaction();
        r.processHappyHourRecovery();
        checkEquals(100, early.getSatisfaction(), "92 + 15 is capped at 100");
        checkEquals(100, late.getSatisfaction(), "full satisfaction stays at 100");
    }

    private static void hostAndSeating() {
        StateManager sm = new StateManager();
        Restaurant r = new Restaurant(sm, new Random(1));
        Host host = new Host(1);
        Customer c = new CustomerRegular(1);
        r.updateArrivalQueue(c);
        r.seatProcess(host, c, r.firstFreeTable());
        checkEquals(CustomerStatus.SEATED, c.getStatus(), "the Host seats a customer like the Waiter");
        checkEquals(1, r.findTableOf(c).getNumber(), "at the lowest free table");
        check(sm.isConsistent(), "consistent after the Host seats");
    }

    // The Host acts after the Waiter, so the Waiter's choice this turn limits what the Host may do
    private static void hostValidation() {
        StateManager sm = new StateManager();
        Restaurant r = new Restaurant(sm, new Random(1));
        ManagerView mv = new ManagerView(sm);
        ChoiceValidator v = new ChoiceValidator(mv, r);
        Waiter w = new Waiter(1);
        for (int id = 1; id <= 3; id++) {
            r.updateArrivalQueue(new CustomerRegular(id));
        }
        mv.refreshList();
        StaffAction seat1 = new StaffAction(Task.SEATING, 1);
        StaffAction seat2 = new StaffAction(Task.SEATING, 2);
        check(v.validateHost(StaffAction.WAIT, StaffAction.WAIT).valid(), "Host may wait");
        check(v.validateHost(seat1, StaffAction.WAIT).valid(), "Host may seat a queued customer");
        check(!v.validateHost(new StaffAction(Task.SEATING, 9), StaffAction.WAIT).valid(), "unknown customer rejected");
        check(!v.validateHost(seat1, seat1).valid(), "Host cannot seat who the Waiter is seating");
        check(v.validateHost(seat2, seat1).valid(), "Host can seat a different customer in the second table");

        r.seatProcess(w, sm.findCustomerById(1), r.firstFreeTable());
        mv.refreshList();
        check(v.validateHost(seat2, StaffAction.WAIT).valid(), "one table left for the Host");
        check(!v.validateHost(new StaffAction(Task.SEATING, 3), new StaffAction(Task.SEATING, 2)).valid(),
                "no table left once the Waiter takes the last one");
        check(!v.validateHost(seat1, StaffAction.WAIT).valid(), "a seated customer is not in the queue");
    }
}
