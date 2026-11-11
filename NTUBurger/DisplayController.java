package NTUBurger;

import java.util.List;
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
// No calculation needed here (numbers come from Result)

public class DisplayController {

    private static final String RULE = "-".repeat(62);
    private static final String DOUBLE_RULE = "=".repeat(62);

    private DisplayController() {}

    public static String toDollars(int cents){
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }

    // Average shown to one decimal place, or N/A when nobody has paid yet
    public static String formatAverage(Result result) {
        return result.paidCount() == 0 ? "N/A" : String.format("%.1f", result.avgSatisfaction());
    }

    // e.g. "C3 (Regular)"
    public static String label(Customer customer) {
        return "C" + customer.getId() + " (" + customer.getTypeLabel() + ")";
    }

    public static void showArrival(Customer customer) {
        System.out.println(" >> " + label(customer) + " arrives and joins the queue.");
    }

    public static void showStartOfTurn(ManagerView view, StateView stateView, TableView tableView, Result result) {
        System.out.println();
        System.out.println(RULE);
        System.out.println(" TURN " + result.getTurnNo() + " / " + BaseSetting.MAX_TURNS
                + "     Revenue " + toDollars(result.revenue())
                + "     Avg satisfaction (paid) " + formatAverage(result));
        System.out.println(RULE);

        System.out.println(" Waiting Queue: " + idList(view.getTasks(Task.SEATING)));

        System.out.println(" Tables:");
        for (Table table : tableView.getTables()) {
            if (table.isOccupied()) {
                System.out.println("   Table " + table.getNumber() + ": customer " + table.getCustomerId());
            } else {
                System.out.println("   Table " + table.getNumber() + ": -- free --");
            }
        }

        // Satisfaction + Order Status for each customers
        System.out.println(" Customers:");
        for (Customer customer : stateView.getActiveCustomers()) {
            Order order = stateView.findOrderOf(customer.getId());
            System.out.printf("   #%-3d %-8s %-13s satisfaction %-4s order %s%n",
                    customer.getId(),
                    customer.getTypeLabel(),
                    customer.getStatus(),
                    customer.getSatisfaction(),
                    order == null ? "-" : order.getStatus());
        }

        // Other Order Status PAID / CANCELLED
        for (OrderStatus status : List.of(OrderStatus.PAID, OrderStatus.CANCELLED)) {
            String customerId = stateView.getOrdersByStatus(status).stream()
                    .map(order -> "#" + order.getCustomerId())
                    .collect(Collectors.joining(", "));
            System.out.printf("   %-10s %s%n", status + ":", customerId.isEmpty() ? "-" : customerId);
        }

        // Tasks required for each customers
        System.out.println(" Available tasks:");
        for (Task task : Task.values()) {
            if (task == Task.WAITING) continue;
            System.out.printf("   %-30s %s%n", task.getLabel() + ":", idList(view.getTasks(task)));
        }
        System.out.println(RULE);
    }

    // What happened during the turn, then the running totals
    public static void showEndOfTurn(Result result, List<String> events) {
        System.out.println();
        System.out.println(" END OF TURN " + result.getTurnNo());
        for (String event : events) {
            System.out.println("   " + event);
        }
        System.out.println(" Served " + result.servedCount()
                + " | Unhappy departures " + result.unhappyDeparture()
                + " | Revenue " + toDollars(result.revenue())
                + " | Avg sat (paid) " + formatAverage(result)
                + " | Turns used " + result.getTurnNo() + "/" + BaseSetting.MAX_TURNS);
    }

    public static void endGameResult(Result result, StateView stateView){
        System.out.println();
        System.out.println(DOUBLE_RULE);
        System.out.println(" FINAL RESULT");
        System.out.println(targetRow("Paid customers", String.valueOf(result.paidCount()),
                String.valueOf(BaseSetting.MIN_PAID_CUSTOMERS), result.paidCount() >= BaseSetting.MIN_PAID_CUSTOMERS));
        System.out.println(targetRow("Revenue", toDollars(result.revenue()),
                toDollars(BaseSetting.MIN_REVENUE_CENTS), result.revenue() >= BaseSetting.MIN_REVENUE_CENTS));
        System.out.println(targetRow("Avg satisfaction", formatAverage(result),
                String.valueOf(BaseSetting.MIN_AVG_SATISFACTION),
                result.paidCount() > 0 && result.avgSatisfaction() >= BaseSetting.MIN_AVG_SATISFACTION));
        System.out.println("   Unhappy departures: " + result.unhappyDeparture());

        // Customers who never paid, so the player can see what went wrong
        List<Customer> unpaid = stateView.getActiveCustomers();
        if (!unpaid.isEmpty()) {
            System.out.println("   Not paid by the end:");
            for (Customer customer : unpaid) {
                System.out.println("     #" + customer.getId() + "  " + customer.getStatus()
                        + "  satisfaction " + customer.getSatisfaction());
            }
        }
        System.out.println(" *** " + (result.isVictory() ? "VICTORY!" : "DEFEAT") + " ***");
        System.out.println(DOUBLE_RULE);
    }

    private static String targetRow(String label, String value, String need, boolean met) {
        return String.format("   %-18s %-9s %-18s %s", label, value, "(need >= " + need + ")", met ? "PASS" : "FAIL");
    }

    private static String idList(List<Integer> ids) {
        return ids.isEmpty() ? "-" : ids.stream().map(id -> "#" + id).collect(Collectors.joining(", "));
    }
}
