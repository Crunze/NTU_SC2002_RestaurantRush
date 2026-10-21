package NTUBurger.Roles;

// Extend Customer.java

public class CustomerRegular extends Customer{

    public CustomerRegular(int id){ // , int arrivalTurn
        super(id); //, arrivalTurn
    }

    // Function Overriding
    @Override
    public int satisfactionLossPerTurn(){ return 8; }

    // To be written as Full amt - 0.0 x Full amt = AmountToPay
    @Override 
    public int discount(){ return 0; }
}
