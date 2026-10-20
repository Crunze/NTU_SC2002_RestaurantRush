package NTUBurger.Manager;

// Holds choices chosen by Manager

// Record: All components are private final (constants). This is a final class (cannot extend classes or be extended)
// Meant to be a data carrier
public record ManagerChoice(Task waiterTask, int waiterCustomerId){

}
