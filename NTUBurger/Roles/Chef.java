package NTUBurger.Roles;

import NTUBurger.Restaurant.Order;

// Chef, added prep unit since chef has the ability to advance it
// No wait() since it does nothing and more of Manager Choice

public class Chef extends Staff {
    private int cookUnits = 1;

    public Chef(int id){
        super(id);
    }

    public void cooking(Order order){
        order.preparing(cookUnits);
    }
    
}
