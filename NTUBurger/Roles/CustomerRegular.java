package NTUBurger.Roles;

// Extend Customer.java

public class CustomerRegular extends Customer{

    public CustomerRegular(int id){ // , int arrivalTurn
        super(id); //, arrivalTurn
    }

    // Function Overriding
    @Override
    public int satisfactionLossPerTurn(){ return 8; }

    // Regular customers pay the full price
    @Override 
    public int applyDiscount(int priceCents){ return priceCents; }
}
