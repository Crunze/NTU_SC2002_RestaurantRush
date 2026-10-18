package NTUBurger.Roles;

import NTUBurger.Restaurant.Order;

// Cashier to collect payment

public class Cashier extends Staff {
    
        public Cashier(int id){
        super(id);
    }

    public void processPayment(Order order){
        order.paid();
    }

    // calculate payment

}
