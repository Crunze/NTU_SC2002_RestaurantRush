package NTUBurger.Restaurant;

import NTUBurger.BaseSetting;
import NTUBurger.Roles.Cashier;
import NTUBurger.Roles.Chef;
import NTUBurger.Roles.Customer;
import NTUBurger.Roles.CustomerStatus;
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
    public void seatProcess(Waiter waiter, Customer customer, Table table){
        stateManager.updateCustomerState(customer, () -> {
            waiter.seatCustomer(customer.getId(), table);
            customer.seat();
        });
    }

    // Lowest-numbered free table, or null if every table is taken
    public Table firstFreeTable(){
        for (Table table : tables){
            if (!table.isOccupied()){ return table; }
        }
        return null;
    }

    // Table the customer is sitting at, or null
    public Table findTableOf(Customer customer){
        for (Table table : tables){
            if (table.getCustomerId() == customer.getId()){ return table; }
        }
        return null;
    }

    // Queue Operation - process of seating customer, requires edit
    public void processQueue(Waiter waiter){
        List<Customer> queueList = stateManager.getCustomersByStatus(CustomerStatus.WAITING);
        if (queueList.isEmpty()){ return; } // No one in queue! Need test Exception Handling
        
        for (Table table : tables){
            if (!table.isOccupied()){
                seatProcess(waiter, queueList.get(0), table); // if we want Customer 2 to seat before Customer 1, need edit
                break;
            }
        }
    }

    // Order Status: PLACED -> COOKING || READY \ COOKING -> READY
    public void preparingFood(Chef chef, Order order){
        stateManager.updateOrderState(order, () -> chef.cooking(order));
    }

    // Order Status: PLACED
    public void processNewOrder(Waiter waiter, Customer customer, List<MenuItem> menu){
        customer.chooseFood(menu.get(random.nextInt(menu.size()))); // Hardcoded Food, Need to set to Random
        MenuItem food = customer.getChosenFood();
        stateManager.addOrder(waiter.takeOrder(customer.getId(), food));
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
}