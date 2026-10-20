package NTUBurger.Manager;

import java.util.List;
import java.util.Scanner;

// Requires fallback just incase the action is not valid

// Wait() will be added in here
// An invalid choice must explain why it failed and let the manager choose again, without using up
// the staff task <-- Made use of ManagerView
// BUT if Waiter task take customer order and ManagerView might not reflect this for Chef to proceed
// Check if Waiter take customer order this turn then allow chef to proceed based on that

// Menu just display and store user choice

// userChoice InputValidator and check against TaskAvailable

public class ManagerMenu {
    private static final List<Task> WAITER_TASKS = List.of(Task.WAITING, Task.SEATING, Task.ORDERING, Task.SERVING);
    private static final List<Task> CHEF_TASKS = List.of(Task.WAITING, Task.COOKING);
    private final Scanner scanner;

    public ManagerMenu(Scanner scanner){
        this.scanner = scanner;
    }

    
}
