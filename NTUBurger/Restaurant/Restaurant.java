package NTUBurger.Restaurant;

import NTUBurger.BaseSetting;
import NTUBurger.Roles.Cashier;
import NTUBurger.Roles.Chef;
import NTUBurger.Roles.Customer;
import NTUBurger.Roles.CustomerStatus;
import NTUBurger.Roles.Waiter;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Optional;
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
    public void seatProcess(Waiter waiter, Customer customer){
        stateManager.updateCustomerState(customer, () -> {
            Table table = firstFreeTable();
            waiter.seatCustomer(customer.getId(), table);
            customer.seat();
        });
    }

    // Order Status: PLACED -> COOKING || READY \ COOKING -> READY
    public void preparingFood(Chef chef, Order order){
        stateManager.updateOrderState(order, () -> chef.cooking(order));
    }

    // Order Status: PLACED
    public void processNewOrder(Waiter waiter, Customer customer){
        List<MenuItem> menu = BaseSetting.MENU;
        customer.chooseFood(menu.get(random.nextInt(menu.size())));
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
    public List<Customer> processFinishEating(int turn){
        List<Customer> fullCustomers = new ArrayList<>();
        for (Customer customer : stateManager.getCustomersByStatus(CustomerStatus.SERVED)){
            if (customer.getServedTurn() < turn){
                stateManager.updateCustomerState(customer, () -> customer.doneEating(turn));
                fullCustomers.add(customer);
            }
        }
        return fullCustomers;
    }

    // Customer Status: READY TO PAY -> PAID
    // Order Status: SERVED -> PAID
    public Optional<Receipt> processReadyToPay(Cashier cashier){
        Optional<Customer> next = stateManager.getCustomersByStatus(CustomerStatus.READY_TO_PAY).stream().findFirst();
        // No one ready to pay
        if (next.isEmpty()) {return Optional.empty();}

        Customer customer = next.get();
        Order order = stateManager.findOrderOf(customer.getId());
        Table table = findTableOf(customer.getId());
        int tableNo = table.getNumber();

        stateManager.updateCustomerState(customer, () -> {
            stateManager.updateOrderState(order, () -> cashier.processPayment(order, customer));
            customer.pay();
        });
        table.free();

        return Optional.of(new Receipt(customer.getId(), order.getPrice(), tableNo));
    }

    // WAITING and SEATED but UNSERVED Customers lose satisfaction and left at 0
    public List<CustomerSatisfaction> processSatisfaction(){
        List<CustomerSatisfaction> customerSatisfaction = new ArrayList<>();
        for (CustomerStatus customerStatus : List.of( CustomerStatus.WAITING, CustomerStatus.SEATED)){
            for (Customer customer : stateManager.getCustomersByStatus(customerStatus)) {
                int initSat = customer.getSatisfaction();
                customer.loseSatisfaction();
                if (customer.isSatisfactionZero()) {
                    processAbandoned(customer);
                }
                customerSatisfaction.add(new CustomerSatisfaction(customer.getId(), initSat, 
                customer.getSatisfaction()));
            }
        }
        return customerSatisfaction;
    }

    // :: same as lambda but for no argument
    public void processAbandoned(Customer customer){
        CustomerStatus oldStatus = customer.getStatus();

        // Stop further processing if customer has already left
        if (oldStatus == CustomerStatus.PAID || oldStatus == CustomerStatus.ABANDONED){ return; }

        // Customer left
        stateManager.updateCustomerState(customer, customer::abandon);
        
        // Cancel Order if customer ordered
        Order order = stateManager.findOrderOf(customer.getId());
        if (order != null){
            stateManager.updateOrderState(order, order::cancelOrder);
        }

        // free their table if they had one
        for (Table t : tables) {           
            if (t.getCustomerId() == customer.getId()) { t.free(); }
        }
    }
}