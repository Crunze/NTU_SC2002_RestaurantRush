package NTUBurger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import NTUBurger.Manager.ChoiceValidator;
import NTUBurger.Manager.ManagerMenu;
import NTUBurger.Manager.ManagerView;
import NTUBurger.Manager.StaffAction;
import NTUBurger.Manager.StaffRole;
import NTUBurger.Restaurant.Order;
import NTUBurger.Restaurant.Restaurant;
import NTUBurger.Restaurant.StateManager;
import NTUBurger.Restaurant.Table;
import NTUBurger.Roles.Cashier;
import NTUBurger.Roles.Chef;
import NTUBurger.Roles.Customer;
import NTUBurger.Roles.CustomerFactory;
import NTUBurger.Roles.CustomerStatus;
import NTUBurger.Roles.Waiter;

// Controls the gameflow via 6 steps in a turn and ensure 18 turns max
// Contains no rules: it only sequences calls to Restaurant, which owns the processes

public class GameController {

    private final Restaurant restaurant;
    private final StateManager stateManager;
    private final ManagerView managerView;
    private final ManagerMenu managerMenu;
    private final ChoiceValidator validator;
    private final List<Waiter> waiters;
    private final List<Chef> chefs;
    private final List<Cashier> cashiers;
    private final Result result;

    private int turn;
    private int nextCustomerId = 1;
    // What happened this turn, printed at the end of the turn
    private final List<String> events = new ArrayList<>();

    public GameController(Restaurant restaurant, StateManager stateManager,
        ManagerView managerView, ManagerMenu managerMenu, ChoiceValidator validator,
        List<Waiter> waiters, List<Chef> chefs, List<Cashier> cashiers){
        this.restaurant = restaurant;
        this.stateManager = stateManager;
        this.managerView = managerView;
        this.managerMenu = managerMenu;
        this.validator = validator;
        this.waiters = waiters;
        this.chefs = chefs;
        this.cashiers = cashiers;
        this.result = new Result(stateManager, 0);
    }

    public Result getResult(){ return result; }
    public Restaurant getRestaurant(){ return restaurant; }
    public StateManager getStateManager(){ return stateManager; }
    public ManagerView getManagerView(){ return managerView; }

    // Step 1: arrivals join the queue, then the start-of-turn board is shown
    private void step1(){
        events.clear();
        result.setTurnNo(turn);
        if (BaseSetting.CUSTOMER_ARRIVAL_TURN.contains(turn)){
            Customer customer = CustomerFactory.create(nextCustomerId++);
            restaurant.updateArrivalQueue(customer);
            DisplayController.showArrival(customer);
        }
        // Retrieve the most updated list
        managerView.refreshList();
        DisplayController.showStartOfTurn(managerView, stateManager, restaurant, result);
    }

    // Step 2: the manager picks one task for the Waiter, then one for the Chef
    private void step2(){
        // Reset ManagerChoice
        managerMenu.reset();
        ChoiceValidator.Result check;
        do {
            managerMenu.tellWaiter();
            check = validator.validateWaiter(managerMenu.getChoice(StaffRole.WAITER));
            if (!check.valid()) { System.out.println("Invalid: " + check.reason() + " Choose again."); }
        } while (!check.valid());

        do {
            managerMenu.promptChef();
            check = validator.validateChef(managerMenu.getChoice(StaffRole.CHEF), managerMenu.getChoice(StaffRole.WAITER));
            if (!check.valid()) { System.out.println("Invalid: " + check.reason() + " Choose again."); }
        } while (!check.valid());
    }

    // Step 3: Waiter acts first, then the Chef
    // The Chef can therefore cook an order the Waiter took this turn,
    // but the Waiter can never serve food the Chef finishes this turn
    private void step3() {
        StaffAction waiterAction = managerMenu.getChoice(StaffRole.WAITER);
        StaffAction chefAction = managerMenu.getChoice(StaffRole.CHEF);
        Waiter waiter = waiters.get(0);
        Chef chef = chefs.get(0);

        if (!waiterAction.isWait()) {
            Customer customer = stateManager.findCustomerById(waiterAction.customerId());
            switch (waiterAction.task()) {
                case SEATING -> {
                    Table table = restaurant.firstFreeTable();
                    restaurant.seatProcess(waiter, customer, table);
                    events.add("Waiter seats " + DisplayController.label(customer) + " at table " + table.getNumber() + ".");
                }
                case ORDERING -> {
                    restaurant.processNewOrder(waiter, customer, BaseSetting.MENU);
                    Order order = stateManager.findOrderOf(customer);
                    int bill = customer.applyDiscount(order.getPrice());
                    events.add("Waiter takes " + DisplayController.label(customer) + "'s order: "
                            + customer.getChosenFood().getName() + " " + DisplayController.toDollars(order.getPrice())
                            + (bill == order.getPrice() ? "" : " (customer pays " + DisplayController.toDollars(bill) + ")")
                            + " (" + customer.getChosenFood().getPrepUnits() + " unit(s)).");
                }
                case SERVING -> {
                    Order order = stateManager.findOrderOf(customer);
                    restaurant.processServeOrder(waiter, customer, order, turn);
                    events.add("Waiter serves " + customer.getChosenFood().getName() + " to "
                            + DisplayController.label(customer) + ".");
                }
                default -> throw new IllegalStateException("Not a Waiter task: " + waiterAction.task());
            }
        }
        if (!chefAction.isWait()) {
            Customer customer = stateManager.findCustomerById(chefAction.customerId());
            // Find the order AFTER the Waiter has acted, so an order taken this turn exists by now
            Order order = stateManager.findOrderOf(customer);
            restaurant.preparingFood(chef, order);
            events.add("Chef cooks " + customer.getChosenFood().getName() + " for " + DisplayController.label(customer)
                    + " (" + order.getStatus() + ").");
        }
    }

    // Step 4: each Cashier collects from the customer who has been ready to pay the longest,
    // then customers served on an earlier turn finish eating
    private void step4(){
        for (Cashier cashier : cashiers) {
            List<Customer> eligible = new ArrayList<>(stateManager.getCustomersByStatus(CustomerStatus.READY_TO_PAY));
            eligible.removeIf(c -> c.finishEatingTurn() >= turn);
            // Oldest first; ties broken by lower id
            eligible.sort(Comparator.comparingInt(Customer::finishEatingTurn).thenComparingInt(Customer::getId));
            if (eligible.isEmpty()) { continue; }
            Customer customer = eligible.get(0);
            Order order = stateManager.findOrderOf(customer);
            Table table = restaurant.findTableOf(customer);
            restaurant.processReadyToPay(cashier, customer, order, table);
            events.add("Cashier collects " + DisplayController.toDollars(order.getPrice()) + " from "
                    + DisplayController.label(customer) + " (satisfaction " + customer.getSatisfaction()
                    + "), table " + table.getNumber() + " is free.");
        }
        for (Customer customer : stateManager.getCustomersByStatus(CustomerStatus.SERVED)) {
            if (customer.getServedTurn() < turn) {
                restaurant.processFinishEating(customer, turn);
                events.add(DisplayController.label(customer) + " finishes eating and is ready to pay.");
            }
        }
    }

    // Step 5: customers still waiting or seated lose satisfaction; at 0 they leave
    private void step5(){
        Map<Customer, Integer> before = new LinkedHashMap<>();
        for (CustomerStatus status : List.of(CustomerStatus.WAITING, CustomerStatus.SEATED)) {
            for (Customer customer : stateManager.getCustomersByStatus(status)) {
                before.put(customer, customer.getSatisfaction());
            }
        }
        restaurant.processSatisfaction();
        for (Map.Entry<Customer, Integer> entry : before.entrySet()) {
            Customer customer = entry.getKey();
            events.add(DisplayController.label(customer) + " loses " + (entry.getValue() - customer.getSatisfaction())
                    + " satisfaction (" + entry.getValue() + " -> " + customer.getSatisfaction() + ").");
            if (customer.getStatus() == CustomerStatus.ABANDONED) {
                events.add("!! " + DisplayController.label(customer) + " runs out of patience and leaves unhappy.");
            }
        }
    }

    // Step 6: end-of-turn summary
    private void step6(){
        DisplayController.showEndOfTurn(result, events);
    }

    private void runTurn(){
        step1();
        step2();
        step3();
        step4();
        step5();
        step6();
    }

    // Plays every turn (never ends early), then shows the final outcome
    public void runGame(){
        for (turn = 1; turn <= BaseSetting.MAX_TURNS; turn++){
            runTurn();
        }
        DisplayController.endGameResult(result, stateManager);
    }
}
