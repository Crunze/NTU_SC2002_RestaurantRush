package NTUBurger.Roles;

// VIP: patient (loses 5 per turn) and pays 90% of the price, rounded to the nearest cent

public class CustomerVIP extends Customer {
    public CustomerVIP(int id){
        super(id);
    }    

    @Override
    public int satisfactionLossPerTurn(){ return 5; }

    @Override 
    public int applyDiscount(int priceCents){ return (int) Math.round(priceCents * 90 / 100.0); }
}
