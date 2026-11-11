package NTUBurger.Roles;

import NTUBurger.Restaurant.MenuItem;

// Customer has its behaviours and actions

public abstract class Customer {

    private final int id;
    //private final int arrivalTurn;  // Determine Oldest Customer
    private int servedTurn; // Determine when to pay
    private int satisfaction=100; // Starting Satisfaction
    private CustomerStatus status = CustomerStatus.WAITING;
    private MenuItem chosenFood; // Customer choose their own food
    private int finishEatingTurn; // Determine oldest first for payment

    public Customer(int id){
        this.id = id;
    }
    
    // Getters
    public int getId() { return id; }
    public int getServedTurn(){ return servedTurn; }
    public int getSatisfaction() { return satisfaction; }
    public CustomerStatus getStatus() { return status; }
    public MenuItem getChosenFood() { return chosenFood; }
    public int finishEatingTurn(){ return finishEatingTurn; }
    // Human-readable type, e.g. CustomerRegular -> Regular
    public String getTypeLabel(){ return getClass().getSimpleName().replace("Customer", ""); }

    // Customer Actions
    public void seat() { status = CustomerStatus.SEATED; }
    public void chooseFood(MenuItem food){ chosenFood = food; }
    public void eat(int servedTurn) { 
        status = CustomerStatus.SERVED; 
        this.servedTurn = servedTurn;
    }
    public void doneEating(int finishEatingTurn) { 
        status = CustomerStatus.READY_TO_PAY; 
        this.finishEatingTurn = finishEatingTurn;
    }
    public void pay() { status = CustomerStatus.PAID; }
    public void abandon() { status = CustomerStatus.ABANDONED; }

    // True if Customer Angry
    public boolean isSatisfactionZero(){
        return satisfaction==0;
    }

    // Deduct Satisfaction score per FoodNotServed Turn
    public void loseSatisfaction(){
        satisfaction = Math.max(0, satisfaction - satisfactionLossPerTurn());
    }

    // Abstract methods for different customer
    public abstract int satisfactionLossPerTurn();
    // Takes the list price in cents and returns the final price in cents.
    // Done in integer cents on purpose: a percentage stored as an int (10) times a price would overshoot.
    public abstract int applyDiscount(int priceCents);
}
