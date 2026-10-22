package NTUBurger;


import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import NTUBurger.Manager.ManagerView;
import NTUBurger.Restaurant.Order;
import NTUBurger.Restaurant.OrderStatus;
import NTUBurger.Restaurant.StateView;
import NTUBurger.Restaurant.Table;
import NTUBurger.Restaurant.TableView;
import NTUBurger.Roles.Customer;
import NTUBurger.Manager.Task;

// Just display
// No calculation needed here

public class DisplayController {

    public DisplayController() {}

    public static String toDollars(int cents){
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }
    public static String formatAverage(OptionalDouble avg) {
        return avg.isPresent() ? String.format("%.2f", avg.getAsDouble()) : "N/A";
    }

    public static void showStartOfTurn(ManagerView view, StateView stateView, TableView tableView, Result result) {
        System.out.println("Turn: " + result.getTurnNo());
        System.out.println("Revenue: " + toDollars(result.revenue()));
        double avg = result.avgSatisfaction();
        System.out.println("Average Satisfaction of Paid Customers: " + (avg == 0.0 ? "N/A" : String.format("%.2f", avg)));
        System.out.println("Waiting Queue: " + view.getTasks(Task.SEATING));
        System.out.println("Occupied tables:");
        for (Table table : tableView.getOccupiedTables())
            System.out.println("  Table " + table.getNumber() + ": customer " + table.getCustomerId());

        // Satisfaction + Order Status for each customers
        System.out.println("Customers:");
        for (Customer customer : stateView.getActiveCustomers()) {
            Order order = stateView.findOrderOf(customer);
            System.out.printf("  #%-3d %-13s satisfaction %-4s order %s%n",
                    customer.getId(),
                    customer.getStatus(),
                    customer.getSatisfaction(),
                    order == null ? "-" : order.getStatus());
        }

        // Other Order Status PAID / CANCELLED
        for (OrderStatus status : List.of(OrderStatus.PAID, OrderStatus.CANCELLED)) {
            String customerId = stateView.getOrdersByStatus(status).stream()
                    .map(order -> "#" + order.getCustomerId())
                    .collect(Collectors.joining(", "));
            System.out.printf("  %-10s %s%n", status + ":", customerId.isEmpty() ? "-" : customerId);
        }

        // Tasks required for each customers
        System.out.println("Available tasks:");
        for (Task task : Task.values()) {
            if (task == Task.WAITING) continue;
            List<Integer> ids = view.getTasks(task);
            String customerId = ids.isEmpty() ? "-" : ids.stream()
                    .map(id -> "#" + id)
                    .collect(Collectors.joining(", "));
            System.out.printf("  %-9s service required: %s%n", task.getLabel(), customerId);
        }
    }

    public void showEndOfTurn(Result result) {
        System.out.println("Turn: " + result.getTurnNo());
        System.out.println("Number of Customers Served: " + result.servedCount());
        System.out.println("Unhappy Departures Count: " + result.unhappyDeparture());
        System.out.println("Revenue: " + toDollars(result.revenue()));
        double avg = result.avgSatisfaction();
        System.out.println("Average Satisfaction of Paid Customers: " + (avg == 0.0 ? "N/A" : String.format("%.2f", avg)));
        
    }

    public static void endGameResult(Result result){
        System.out.println(result.isVictory() ? "VICTORY!" : "DEFEAT");
    }
    
}