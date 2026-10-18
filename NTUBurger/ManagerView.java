package NTUBurger;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import NTUBurger.Restaurant.Order;
import NTUBurger.Restaurant.StateView;
import NTUBurger.Roles.Customer;

// For tracking of tasks Available. 
// No interface is needed to protect this class. Nth much to protect here
// Java has so many convenient methods

public class ManagerView {
   
    public enum Task { SEATING, ORDERING, SERVING, COOKING }

    private final StateView stateView;
    private final Map<Task, List<Customer>> taskAvailable = new EnumMap<>(Task.class);

    public ManagerView(StateView stateView){
        this.stateView = stateView;

        for (Task taskStatus : Task.values()){
            taskAvailable.put(taskStatus, new ArrayList<>());
        }
    }

    // Update taskAvailable
    public void refreshList(){
        // Clear all list in Map after every turn. Outputs: []
        for (List<Customer> list : taskAvailable.values()){
            list.clear();
        }

        // Add all queueing customers that requires a table
        // SEATING depends on CUSTOMER is WAITING
        taskAvailable.get(Task.SEATING).addAll(stateView.getCustomersByStatus(Customer.Status.WAITING));
        
        // ORDERING depends on CUSTOMER is SEATED AND NO PLACED ORDER
        // Add all customers that are seated.
        taskAvailable.get(Task.ORDERING).addAll(stateView.getCustomersByStatus(Customer.Status.SEATED));
        // Remove customer from list if they ordered already. No need to use ForEach
        taskAvailable.get(Task.ORDERING).removeIf(customer -> stateView.findOrderOf(customer) != null);

        // COOKING depends on ORDER is PLACED OR COOKING
        for (Order order : stateView.getOrdersByStatus(Order.Status.PLACED)){
            taskAvailable.get(Task.COOKING).add(order.getCustomer());
        }
        for (Order order : stateView.getOrdersByStatus(Order.Status.COOKING)){
            taskAvailable.get(Task.COOKING).add(order.getCustomer());
        }

        // SERVING depends on ORDER is READY
        for (Order order : stateView.getOrdersByStatus(Order.Status.READY)){
            taskAvailable.get(Task.SERVING).add(order.getCustomer());
        }
    }

    public List<Customer> getTasks(Task task){
        return List.copyOf(taskAvailable.get(task));
    }
}




