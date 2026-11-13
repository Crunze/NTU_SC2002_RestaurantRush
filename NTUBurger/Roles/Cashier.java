package NTUBurger.Roles;

import NTUBurger.Restaurant.Order;

// Cashier: Handles payment

public class Cashier extends Staff {
    
        public Cashier(int id){
        super(id);
    }

    // Stage 3 change: The price was already locked when the order was taken
    // The customer's own status change (PAID) is done by Restaurant.processReadyToPay, inside the StateManager action
    public void processPayment(Order order){
        order.paid();
    }

}
