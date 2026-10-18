package NTUBurger.Roles;

import NTUBurger.Restaurant.Order;

// Chef
// No wait() since it does nothing and more of Manager Choice

public class Chef extends Staff {

    public Chef(int id){
        super(id);
    }

    public void cooking(Order order){
        order.preparing();
    }
    
}
