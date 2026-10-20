package NTUBurger.Manager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import NTUBurger.Restaurant.Order;
import NTUBurger.Restaurant.StateView;
import NTUBurger.Roles.Customer;

// For tracking of tasks Available (Customers lacking which service) - Store Customer ID
// To identify special customers, might need to find a way to identify them...
// No interface is needed to protect this class. Nth much to protect here

public class ManagerView {

    private final StateView stateView;
    private final Map<Task, List<Integer>> taskAvailable = new EnumMap<>(Task.class);

    public ManagerView(StateView stateView){
        this.stateView = stateView;

        for (Task taskStatus : Task.values()){
            taskAvailable.put(taskStatus, new ArrayList<>());
        }
    }

    // Update taskAvailable
    public void refreshList(){
        // Clear all list in Map after every turn. Outputs: []
        for (List<Integer> list : taskAvailable.values()){
            list.clear();
        }
        
        // SEATING depends on CUSTOMER is WAITING
        // Add all queueing customers that requires a table
        taskAvailable.get(Task.SEATING).addAll(stateView.getCustomersByStatus(Customer.Status.WAITING).stream().map(Customer::getId).toList());
        
        // ORDERING depends on CUSTOMER is SEATED AND NO PLACED ORDER
        // Add all customers that are seated, then remove those that placed order already
        taskAvailable.get(Task.ORDERING).addAll(stateView.getCustomersByStatus(Customer.Status.SEATED).stream()
                    .filter(customer -> stateView.findOrderOf(customer) == null).map(Customer::getId).toList());

        // COOKING depends on ORDER is PLACED OR COOKING
        taskAvailable.get(Task.COOKING).addAll(stateView.getOrdersByStatus(Order.Status.PLACED)
                    .stream().map(Order::getCustomerId).toList());
        taskAvailable.get(Task.COOKING).addAll(stateView.getOrdersByStatus(Order.Status.COOKING)
                    .stream().map(Order::getCustomerId).toList());

        // SERVING depends on ORDER is READY
        taskAvailable.get(Task.SERVING).addAll(stateView.getOrdersByStatus(Order.Status.READY)
                    .stream().map(Order::getCustomerId).toList());
    }

    public List<Integer> getTasks(Task task){
        return List.copyOf(taskAvailable.get(task));
    }
}




