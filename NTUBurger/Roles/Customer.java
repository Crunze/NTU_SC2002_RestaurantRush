package NTUBurger.Roles;

import NTUBurger.Restaurant.MenuItem;

// Customer has its behaviours and actions

public abstract class Customer {

    // Status is a nested class and any enum declared = static by default
    // enum -> Strictly enforces the states here, meaning any typos of state = compile error
    public enum Status { WAITING, SEATED, SERVED, READY_TO_PAY, PAID, ABANDONED }

    private final int id;
    //private final int arrivalTurn;  // Determine Oldest Customer
    private int servedTurn; // Determine when to pay
    private int satisfaction=100; // Starting Satisfaction
    private Status status = Status.WAITING;
    private MenuItem chosenItem; // Customer choose their own food
    private int finishEatingTurn; // Determine oldest first for payment

    public Customer(int id){ //int arrivalTurn
        this.id = id;
        //this.arrivalTurn = arrivalTurn;
    }
    
    // Getters
    public int getId() { return id; }
    //public int getArrivalTurn() { return arrivalTurn; }
    public int getServedTurn(){ return servedTurn; }
    public int getSatisfaction() { return satisfaction; }
    public Status getStatus() { return status; }
    public MenuItem getChosenItem() { return chosenItem; }
    public int finishEatingTurn(){ return finishEatingTurn; }

    // Customer Actions
    public void seat() { status = Status.SEATED; }
    public void chooseFood(MenuItem food){ chosenItem = food; }
    public void eat(int servedTurn) { 
        status = Status.SERVED; 
        this.servedTurn = servedTurn;
    }
    public void doneEating(int finishEatingTurn) { 
        status = Status.READY_TO_PAY; 
        this.finishEatingTurn = finishEatingTurn;
    }
    public void pay() { status = Status.PAID; }
    public void abandon() { status = Status.ABANDONED; }

    // True if food received by Customer
    public boolean hasReceivedFood(){
        return status == Status.SERVED || status == Status.READY_TO_PAY || status == Status.PAID;
    }

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
    public abstract int discount();
}
