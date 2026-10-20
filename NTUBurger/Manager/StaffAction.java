package NTUBurger.Manager;

// Create a Record (Data Structure) to hold [CHEF/WAITER Action, Target Customer]

// Record: All components are private final (constants). This is a final class (cannot extend classes or be extended)
// Meant to be a data carrier, can hold mixed types of data unlike list, arrays
public record StaffAction(Task task, int customerId){
    
    // Hold invalid ID of -1 to indicate no customer is chosen
    public static final int NONE = -1;

    // Default Record [WAIT, NONE]
    public static final StaffAction WAIT = new StaffAction(Task.WAITING, NONE);

    public boolean isWait(){ return task == Task.WAITING; }
}
