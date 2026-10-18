package NTUBurger.Restaurant;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import NTUBurger.Roles.Customer;

// Handles State change for Customer and Order

public class StateManager implements StateView {
    // Map< KEY : VALUE >, KEY Type: Enum Constant, VALUE Type: List
    // EnumMap strictly create available slots based on status defined in Customer and Order
    // EnumMap is upcasted here to Map (Interface), if in the future, can just edit the right side to change EnumMap to something else
    private final Map<Customer.Status, List<Customer>> customerState = new EnumMap<>(Customer.Status.class);
    private final Map<Order.Status, List<Order>> orderState = new EnumMap<>(Order.Status.class);

    public StateManager(){
        // For each KEY (status), create a VALUE (list)
        // Updating all Six Enummap slots to KEY : VALUE
        for (Customer.Status cs : Customer.Status.values()){
            customerState.put(cs, new ArrayList<>());
        }
        for (Order.Status os : Order.Status.values()){
            orderState.put(os, new ArrayList<>());
        }
    }

    // List.copyOf prevents the original list from being modified. Same as Collections.unmodifiable
    // Return list of orders based on their status
    @Override 
    public List<Order> getOrdersByStatus(Order.Status status){
        return List.copyOf(orderState.get(status));
    }
    // Return list of customers based on their status
    @Override 
    public List<Customer> getCustomersByStatus(Customer.Status status){
        return List.copyOf(customerState.get(status));
    }

    // find customer's order
    @Override 
    public Order findOrderOf(Customer customer) {
        Order.Status[] unserved = { Order.Status.PLACED, Order.Status.COOKING, Order.Status.READY };
        for (Order.Status orderStatus : unserved) {
            for (Order order : orderState.get(orderStatus)) {
                if (order.getCustomer() == customer) { return order; }
            }
        }
        return null;
    }

    // Add Customer to their status
    public void addCustomerToQueue(Customer customer){
        customerState.get(customer.getStatus()).add(customer);
    }
    // Add order to their status
    public void addOrder(Order order){
        orderState.get(order.getStatus()).add(order);
    }

    // Using Runnable action, action.run() as a placeholder
    // Update Order state Boilerplate
    public void updateOrderState(Order order, Runnable action){
        Order.Status oldStatus = order.getStatus();
        action.run(); // Placeholder action
        Order.Status newStatus = order.getStatus();

        // if (oldStatus == newStatus) return; <-- Exception might be needed

        orderState.get(oldStatus).remove(order);
        orderState.get(newStatus).add(order);
    }

    // Update Customer state Boilerplate
    public void updateCustomerState(Customer customer, Runnable action){
        Customer.Status oldStatus = customer.getStatus();
        action.run(); // Placeholder action
        Customer.Status newStatus = customer.getStatus();

        // if (oldStatus == newStatus) return; <-- Exception might be needed

        customerState.get(oldStatus).remove(customer);
        customerState.get(newStatus).add(customer);
    }
}
