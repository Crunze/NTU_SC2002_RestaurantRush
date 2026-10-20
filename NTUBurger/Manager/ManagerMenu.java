package NTUBurger.Manager;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// Menu: display and store user choice for Waiter and Chef
// Manager Choice: Map {WAITER: [ACTION, CUSTOMER], CHEF: [ACTION, CUSTOMER]}

public class ManagerMenu {

    private static final List<Task> WAITER_TASKS = List.of(Task.WAITING, Task.SEATING, Task.ORDERING, Task.SERVING);
    private final Map<StaffRole, StaffAction> managerChoice = new EnumMap<>(StaffRole.class);
    private final ChoiceValidator validator;
    private final Scanner scanner;


    public ManagerMenu(Scanner scanner, ChoiceValidator validator){
        this.scanner = scanner;
        this.validator = validator;
        reset();
    }


    // Default value: Map {WAITER: [WAIT, NONE], CHEF: [WAIT, NONE]}
    public void reset(){
        for (StaffRole role : StaffRole.values()){
            managerChoice.put(role, StaffAction.WAIT);
        }
    }


    // Getter to retrieve [ACTION, CUSTOMER] from Map
    public StaffAction getChoice(StaffRole role){
        return managerChoice.get(role);
    }


    // Re-prompts until a valid number is entered
    private int readInt(String prompt, int min, int max) {
        while (true) {
        System.out.print(prompt);
        String line = scanner.nextLine();

        ChoiceValidator.Result result = validator.validateNumber(line, min, max);
        // .valid() and .reason() are accessor from record Result in ChoiceValidator
        if (result.valid()) { return Integer.parseInt(line.trim()); }
        
        // .reason() retrieve the failed message
        System.out.println(result.reason());
        }
    }


    public void tellWaiter(){
        System.out.println("What should waiter do?");
        for (int i=0; i<WAITER_TASKS.size(); i++){
            System.out.println(i + ". " + WAITER_TASKS.get(i).getLabel());
        }

        Task task = WAITER_TASKS.get(readInt("Choose: ", 0, WAITER_TASKS.size() - 1));
        if (task == Task.WAITING) {
            managerChoice.put(StaffRole.WAITER, StaffAction.WAIT);
            return;
        }
        int customerId = readInt("Customer id: ", 1, 999999999);
        managerChoice.put(StaffRole.WAITER, new StaffAction(task, customerId));
    }


    public void promptChef() {
        System.out.println("What order should chef prepare?");
        System.out.println("Enter the customer id whose order to cook, or 0 to Wait.");

        int customerId = readInt("Customer id: ", 0, 999999999);
        managerChoice.put(StaffRole.CHEF,
                customerId == 0 ? StaffAction.WAIT : new StaffAction(Task.COOKING, customerId));
    }
}
