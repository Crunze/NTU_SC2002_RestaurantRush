package NTUBurger.Manager;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
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
    // Empty if the input has ended (closed stdin / end of a script), so the game can finish instead of crashing
    // Handles running from file to support game test
    private OptionalInt readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);

            // if stdin is closed or a piped file runs out of lines
            if (!scanner.hasNextLine()) {
                System.out.println("(input closed - waiting)");
                return OptionalInt.empty();
            }

            // A piped file can start with a byte-order mark, which would make the first number unreadable
            // \uFeFF is byte-order mark (BOM), sits at first line of file. Remove BOM 
            String line = scanner.nextLine().replace("\uFEFF", "");

            ChoiceValidator.Result result = validator.validateNumber(line, min, max);
            
            // .valid() and .reason() are accessor from record Result in ChoiceValidator
            if (result.valid()) { return OptionalInt.of(Integer.parseInt(line.trim())); }
            // .reason() retrieve the failed message
            System.out.println(result.reason());
        }
    }

    public void tellWaiter(){
        System.out.println("What should waiter do?");
        for (int i=0; i<WAITER_TASKS.size(); i++){
            System.out.println(i + ". " + WAITER_TASKS.get(i).getLabel());
        }
        OptionalInt choice = readInt("Choose: ", 0, WAITER_TASKS.size() - 1);
        if (choice.isEmpty() || WAITER_TASKS.get(choice.getAsInt()) == Task.WAITING) {
            managerChoice.put(StaffRole.WAITER, StaffAction.WAIT);
            return;
        }
        Task task = WAITER_TASKS.get(choice.getAsInt());
        OptionalInt customerId = readInt("Customer id: ", 1, 999999999);
        managerChoice.put(StaffRole.WAITER,
                customerId.isEmpty() ? StaffAction.WAIT : new StaffAction(task, customerId.getAsInt()));
    }

    public void promptChef() {
        System.out.println("What order should chef prepare?");
        System.out.println("Enter the customer id whose order to cook, or 0 to Wait.");
        OptionalInt customerId = readInt("Customer id: ", 0, 999999999);
        managerChoice.put(StaffRole.CHEF,
                customerId.isEmpty() || customerId.getAsInt() == 0
                        ? StaffAction.WAIT : new StaffAction(Task.COOKING, customerId.getAsInt()));
    }
}
