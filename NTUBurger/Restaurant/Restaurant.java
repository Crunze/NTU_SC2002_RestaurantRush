package NTUBurger.Restaurant;

import NTUBurger.BaseSetting;
import NTUBurger.Roles.Cashier;
import NTUBurger.Roles.Chef;
import NTUBurger.Roles.Customer;
import NTUBurger.Roles.CustomerStatus;
import NTUBurger.Roles.Seater;
import NTUBurger.Roles.Waiter;

import java.util.List;
import java.util.Arrays;
import java.util.Random;
// Restaurant - Handles restuarant operations n processes
// Owns Table so initialise table here

// final ensures that the customerState/orderState can't be reassigned later but their contents can be changed
public class Restaurant implements TableView{
    private final StateManager stateManager;
    private final Table[] tables;
    private final Random random;
    private final HappyHour happyHour = new HappyHour();

    public Restaurant(StateManager stateManager, Random random){
        
        // Manage CustomerState and OrderState
        this.stateManager = stateManager;
        this.random = random;

        // Initialise tables from Table.java
        tables = new Table[BaseSetting.NUM_TABLES];
        for(int i=0; i < tables.length; i++){
            tables[i] = new Table(i+1);
        }
    }

    public HappyHour getHappyHour(){ return happyHour; }

    // Return Table
    @Override 
    public Table[] getTables(){
        return Arrays.copyOf(tables, tables.length);
    }

    // Restaurant Operations Methods
    // Customer Initial Status: WAITING
    public void updateArrivalQueue(Customer customer){
        stateManager.addCustomerToQueue(customer);
    }

    // Customer status: WAITING -> SEATED
    // The status change is INSIDE the action: updateCustomerState reads the status after the action runs,
    // so a change made outside it would leave the customer filed under the old status.
    public void seatProcess(Seater seater, Customer customer, Table table){
        stateManager.updateCustomerState(customer, () -> {
            seater.seatCustomer(customer.getId(), table);
            customer.seat();
        });
    }

    // Order Status: PLACED -> COOKING || READY \ COOKING -> READY
    public void preparingFood(Chef chef, Order order){
        stateManager.updateOrderState(order, () -> chef.cooking(order));
    }

    // Order Status: PLACED
    public void processNewOrder(Waiter waiter, Customer customer, List<MenuItem> menu){
        customer.chooseFood(menu, random);
        MenuItem food = customer.getChosenFood();
        Order order = waiter.takeOrder(customer.getId(), food);
        // The Happy Hour discount is locked into the order when it is taken; orders taken earlier keep their old price
        order.setPrice(happyHour.apply(order.getPrice()));
        stateManager.addOrder(order);
    }

    // Nested wrapper from both boilerplate to sync both customer n order state
    // no arguments needed for lambda since action.run() takes no params
    // Customer Status: SEATED -> SERVED
    // Order Status: READY -> SERVED
    public void processServeOrder(Waiter waiter, Customer customer, Order order, int turnNo){
        stateManager.updateCustomerState(customer, () -> {
            stateManager.updateOrderState(order, () -> waiter.serveOrder(order));
            customer.eat(turnNo);
        });
    }

    // Customer Status: SERVED -> READY TO PAY
    // finishEatingTurn might not be needed since they are all tracked using a list, First to enter list = oldest
    public void processFinishEating(Customer customer, int finishEatingTurn){
        stateManager.updateCustomerState(customer, () -> customer.doneEating(finishEatingTurn));
    }

    // Customer Status: READY TO PAY -> PAID
    // Order Status: SERVED -> PAID
    public void processReadyToPay(Cashier cashier, Customer customer, Order order, Table table){
        stateManager.updateCustomerState(customer, () -> {
            stateManager.updateOrderState(order, () -> cashier.processPayment(order, customer));
            customer.pay();
        });
        table.free();
    }

    public void processSatisfaction(){
        for (CustomerStatus s : new CustomerStatus[]{ CustomerStatus.WAITING, CustomerStatus.SEATED}){
            for (Customer c : stateManager.getCustomersByStatus(s)) {
                c.loseSatisfaction();
                if (c.isSatisfactionZero()) {
                    processAbandoned(c);
                }
            }
        }
    }

    // :: same as lambda but for no argument
    public void processAbandoned(Customer customer){
        CustomerStatus oldStatus = customer.getStatus();

        // Stop further processing if customer has already left
        if (oldStatus == CustomerStatus.PAID || oldStatus == CustomerStatus.ABANDONED){ return; }

        // Customer left
        stateManager.updateCustomerState(customer, customer::abandon);
        
        // Cancel Order if customer ordered
        Order order = stateManager.findOrderOf(customer);
        if (order != null){
            stateManager.updateOrderState(order, order::cancelOrder);
        }

        // free their table if they had one
        for (Table t : tables) {           
            if (t.getCustomerId() == customer.getId()) { t.free(); }
        }
    }

    // Start of turn: while Happy Hour is active, every customer still waiting for service regains satisfaction
    // Returns a line per customer for the display
    public List<String> processHappyHourRecovery(){
        List<String> changes = new java.util.ArrayList<>();
        if (!happyHour.isActive()){ return changes; }
        for (CustomerStatus s : new CustomerStatus[]{ CustomerStatus.WAITING, CustomerStatus.SEATED}){
            for (Customer c : stateManager.getCustomersByStatus(s)) {
                int before = c.getSatisfaction();
                c.gainSatisfaction(BaseSetting.HAPPY_HOUR_RECOVERY);
                changes.add("#" + c.getId() + " " + before + " -> " + c.getSatisfaction());
            }
        }
        return changes;
    }
}
