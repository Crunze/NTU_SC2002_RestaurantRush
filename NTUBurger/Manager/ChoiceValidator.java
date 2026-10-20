package NTUBurger.Manager;

import NTUBurger.BaseSetting;
import NTUBurger.Restaurant.TableView;

// Validates Manager Choice based on taskAvailable in managerView and tableAvailable in tableView

public class ChoiceValidator {
    
    private final ManagerView managerView;
    private final TableView tableView;

    public ChoiceValidator(ManagerView managerView, TableView tableView){
        this.managerView = managerView;
        this.tableView = tableView;
    }

    // Outcome of ManagerChoice: Valid or Invalid with Reason
    // PASSED: valid is set as true
    // fail: valid is set as false and reason
    public record Result(boolean valid, String reason){
        public static final Result PASSED = new Result(true, "");
        public static Result fail(String reason){ return new Result(false, reason);}
    }
    
    public Result validateWaiter(StaffAction action){
        
        // If wait is selected, skip all checks
        if (action.isWait()){ return Result.PASSED; }

        //Record data attributes getter is done by compiler so can access directly
        Task task = action.task(); 
        int customerId = action.customerId();

        if (!managerView.isAvailable(task, customerId)) {
            return Result.fail(switch (task) {
                case SEATING -> "Customer " + customerId + " is not in the waiting queue.";
                case ORDERING -> "Customer " + customerId + " is not seated, or has already ordered.";
                case SERVING -> "Customer " + customerId + " has no order that was READY at the start of this turn ";
                // Switch expression must cover all enum values so need to include WAITING & COOKING
                case WAITING, COOKING -> "Not a Waiter task.";
            });
        }

        // Seat Customer failed due to all table occupied
        if (task == Task.SEATING && tableView.countOccupiedTables() >= BaseSetting.NUM_TABLES) {
            return Result.fail("There is no free table.");
        }
        return Result.PASSED;
    }

    public Result validateChef(StaffAction chef, StaffAction waiter) {
        if (chef.isWait()) { return Result.PASSED; }

        int id = chef.customerId();
        boolean isOrderTaken = managerView.isAvailable(Task.COOKING, id);
        boolean isOrderTakenThisTurn = waiter.task() == Task.ORDERING && waiter.customerId() == id;

        if (isOrderTaken || isOrderTakenThisTurn) { return Result.PASSED; }
        return Result.fail("Customer " + id + " has no order the Chef can cook. ");
    }

    // Checks that the number is valid
    public Result validateNumber(String input, int min, int max) {

        // ignored tells the Reader that { } meant to be empty
        // parseInt: Convert text into whole Number and trim() remove whitespace
        try {
            int n = Integer.parseInt(input.trim());
            if (n >= min && n <= max) { return Result.PASSED; }
        } catch (NumberFormatException ignored) { }

        return Result.fail("Please enter a valid number from " + min + " to " + max + ".");
    }
}
