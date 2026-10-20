package NTUBurger.Manager;

// Valid action task for Manager to choose

public enum Task { 
    WAITING("Wait"),
    SEATING("Seat a queued customer"), 
    ORDERING("Take a seated customer's order"), 
    COOKING("Cook an order"),
    SERVING("Serve a READY order");

    private final String label;

    Task(String label){
        this.label = label;
    }

    public String getLabel(){ return label; }
}
