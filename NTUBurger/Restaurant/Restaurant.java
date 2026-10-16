package NTUBurger.Restaurant;

import NTUBurger.BaseSetting;
import NTUBurger.Roles.Cashier;
import NTUBurger.Roles.Chef;
import NTUBurger.Roles.Customer;
import NTUBurger.Roles.Waiter;

import java.util.List;
import java.util.Arrays;

// Restaurant - Handles restuarant operations n processes
// Owns Table so initialise table here

// final ensures that the customerState/orderState can't be reassigned later but their contents can be changed
public class Restaurant implements TableView{
    private final StateManager stateManager;
    private final Table[] tables;

    public Restaurant(StateManager stateManager){
        
        // Manage CustomerState and OrderState
        this.stateManager = stateManager;

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
    public void seatProcess(Waiter waiter, Customer customer, Table table){
        stateManager.updateCustomerState(customer, () -> waiter.seatCustomer(customer, table));
    }

    // Queue Operation - process of seating customer, requires edit
    public void processQueue(Waiter waiter){
        List<Customer> queueList = stateManager.getCustomersByStatus(Customer.Status.WAITING);
        if (queueList.isEmpty()){ return; } // No one in queue! Need test Exception Handling
        
        for (Table table : tables){
            if (!table.isOccupied()){
                seatProcess(waiter, queueList.get(0), table);
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
        stateManager.addOrder(waiter.takeOrder(customer, menu));
    }

    // Nested wrapper from both boilerplate to sync both customer n order state
    // no arguments needed for lambda since action.run() takes no params
    // Customer Status: SEATED -> SERVED
    // Order Status: READY -> SERVED
    public void processServeOrder(Waiter waiter, Customer customer, Order order, int turnNo){
        stateManager.updateCustomerState(customer, () ->
            stateManager.updateOrderState(order, () -> waiter.serveOrder(customer, order, turnNo)
            ));
    }

    // Customer Status: SERVED -> READY TO PAY
    // finishEatingTurn might not be needed since they are all tracked using a list, First to enter list = oldest
    public void processFinishEating(Customer customer, int finishEatingTurn){
        stateManager.updateCustomerState(customer, () -> customer.doneEating(finishEatingTurn));
    }

    // Customer Status: READY TO PAY -> PAID
    // Order Status: SERVED -> PAID
    public void processReadyToPay(Cashier cashier, Customer customer, Order order, Table table){
        stateManager.updateCustomerState(customer, () ->
            stateManager.updateOrderState(order, () -> cashier.processPayment(order)
            ));
        table.free();
    }

    public void processSatisfaction(){
        for (Customer.Status s : new Customer.Status[]{ Customer.Status.WAITING, Customer.Status.SEATED}){
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
        Customer.Status oldStatus = customer.getStatus();

        // Stop further processing if customer has already left
        if (oldStatus == Customer.Status.PAID || oldStatus == Customer.Status.ABANDONED){ return; }

        // Customer left
        stateManager.updateCustomerState(customer, customer::abandon);
        
        // Cancel Order if customer ordered
        Order order = stateManager.findOrderOf(customer);
        if (order != null){
            stateManager.updateOrderState(order, order::cancelOrder);
        }

        // free their table if they had one
        for (Table t : tables) {           
            if (t.getCustomer() == customer) { t.free(); }
        }
    }
}