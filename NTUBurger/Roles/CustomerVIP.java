package NTUBurger.Roles;

// VIP: patient (loses 5 satisfaction per turn) and pays 90% of the price
public class CustomerVIP extends Customer {
    public CustomerVIP(int id){
        super(id);
    }    

    @Override
    public int satisfactionLossPerTurn(){ return 5; }

    @Override 
    public int applyDiscount(int priceCents){
        // Integer cents: price * 90 / 100, rounded half up
        return (priceCents * 90 + 50) / 100;
    }
}
