package NTUBurger;

import static NTUBurger.TestRunner.check;
import static NTUBurger.TestRunner.checkEquals;

import java.util.List;
import java.util.Random;

import NTUBurger.Restaurant.Order;
import NTUBurger.Restaurant.OrderStatus;
import NTUBurger.Restaurant.Restaurant;
import NTUBurger.Restaurant.StateManager;
import NTUBurger.Restaurant.Table;
import NTUBurger.Roles.Cashier;
import NTUBurger.Roles.Chef;
import NTUBurger.Roles.Customer;
import NTUBurger.Roles.CustomerRegular;
import NTUBurger.Roles.CustomerStatus;
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

        r.seatProcess(w, c);
        checkEquals(List.of(), sm.getCustomersByStatus(CustomerStatus.WAITING), "seated customer leaves WAITING");
        checkEquals(List.of(c), sm.getCustomersByStatus(CustomerStatus.SEATED), "seated customer is SEATED");
        check(sm.isConsistent(), "consistent after seating");

        r.processNewOrder(w, c);
        Order order = sm.findOrderOf(c.getId());
        checkEquals(900, order.getPrice(), "order price is the menu price");
        r.preparingFood(chef, order);
        checkEquals(OrderStatus.READY, order.getStatus(), "salad is ready after one cook");
        r.processServeOrder(w, c, order, 2);
        checkEquals(CustomerStatus.SERVED, c.getStatus(), "served customer is SERVED");
        checkEquals(List.of(c), sm.getCustomersByStatus(CustomerStatus.SERVED), "and filed under SERVED");
        int before = c.getSatisfaction();
        r.processSatisfaction();
        checkEquals(before, c.getSatisfaction(), "a customer who has been served loses no satisfaction");

        r.processFinishEating(3);
        checkEquals(CustomerStatus.READY_TO_PAY, c.getStatus(), "customer is ready to pay");
        r.processReadyToPay(cashier);
        checkEquals(CustomerStatus.PAID, c.getStatus(), "payment moves the customer itself to PAID");
        checkEquals(OrderStatus.PAID, order.getStatus(), "and the order to PAID");
        check(sm.isConsistent(), "consistent after payment");
        checkEquals(0, r.countOccupiedTables(), "payment frees the table");
    }
}
