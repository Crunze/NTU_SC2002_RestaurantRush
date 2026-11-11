package NTUBurger.Restaurant;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import NTUBurger.Roles.Customer;
import NTUBurger.Roles.CustomerStatus;

// Handles State change for Customer and Order

public class StateManager implements StateView {
    // Map< KEY : VALUE >, KEY Type: Enum Constant, VALUE Type: List
    // EnumMap strictly create available slots based on status defined in Customer and Order
    // EnumMap is upcasted here to Map (Interface), if in the future, can just edit the right side to change EnumMap to something else
    private final Map<CustomerStatus, List<Customer>> customerState = new EnumMap<>(CustomerStatus.class);
    private final Map<OrderStatus, List<Order>> orderState = new EnumMap<>(OrderStatus.class);

    public StateManager(){
        // For each KEY (status), create a VALUE (list)
        // Updating all Six Enummap slots to KEY : VALUE
        for (CustomerStatus cs : CustomerStatus.values()){
            customerState.put(cs, new ArrayList<>());
        }
        for (OrderStatus os : OrderStatus.values()){
            orderState.put(os, new ArrayList<>());
        }
    }

    // List.copyOf prevents the original list from being modified. Same as Collections.unmodifiable
    // Return list of orders based on their status
    @Override 
    public List<Order> getOrdersByStatus(OrderStatus status){
        return List.copyOf(orderState.get(status));
    }
    // Return list of customers based on their status
    @Override 
    public List<Customer> getCustomersByStatus(CustomerStatus status){
        return List.copyOf(customerState.get(status));
    }

    // find customer's order
    // orderStatus created from the List.of(OrderStatus)
    @Override 
    public Order findOrderOf(int customerId) {
        return orderState.values().stream().flatMap(List::stream)
                    .filter(order -> order.getCustomerId() == customerId).findFirst()
                    .orElse(null);
    }
    
    // find customer by customerId
    // flatMap(List::stream) : Merge all lists from customerState.values() into one stream of customers
    // filter keeps the found customer that has matching ID
    @Override 
    public Customer findCustomerById(int customerId){
        return customerState.values().stream().flatMap(List::stream).filter(customer -> customer.getId() == customerId)
                            .findFirst().orElse(null);
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
        OrderStatus oldStatus = order.getStatus();
        action.run(); // Placeholder action
        OrderStatus newStatus = order.getStatus();

        // COOKING -> COOKING is normal for a multi-unit dish; any other unchanged status means the action did nothing
        if (oldStatus == newStatus) {
            if (oldStatus == OrderStatus.COOKING) { return; }
            throw new IllegalStateException("Order of customer " + order.getCustomerId() + " stayed " + oldStatus);
        }

        orderState.get(oldStatus).remove(order);
        orderState.get(newStatus).add(order);
    }

    // Update Customer state Boilerplate
    public void updateCustomerState(Customer customer, Runnable action){
        CustomerStatus oldStatus = customer.getStatus();
        action.run(); // Placeholder action
        CustomerStatus newStatus = customer.getStatus();

        // An unchanged status means the status change happened outside the action (or not at all)
        if (oldStatus == newStatus) {
            throw new IllegalStateException("Customer " + customer.getId() + " stayed " + oldStatus);
        }

        customerState.get(oldStatus).remove(customer);
        customerState.get(newStatus).add(customer);
    }

    // True if every customer/order sits in exactly one list, the one matching its own status
    public boolean isConsistent(){
        int customers = 0;
        for (Map.Entry<CustomerStatus, List<Customer>> entry : customerState.entrySet()){
            for (Customer customer : entry.getValue()){
                if (customer.getStatus() != entry.getKey()){ return false; }
                customers++;
            }
        }
        int orders = 0;
        for (Map.Entry<OrderStatus, List<Order>> entry : orderState.entrySet()){
            for (Order order : entry.getValue()){
                if (order.getStatus() != entry.getKey()){ return false; }
                orders++;
            }
        }
        long distinctCustomers = customerState.values().stream().flatMap(List::stream).distinct().count();
        long distinctOrders = orderState.values().stream().flatMap(List::stream).distinct().count();
        return customers == distinctCustomers && orders == distinctOrders;
    }
}
