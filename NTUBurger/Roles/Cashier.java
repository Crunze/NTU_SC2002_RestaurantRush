package NTUBurger.Roles;

import NTUBurger.Restaurant.Order;

// Cashier: Handles payment

public class Cashier extends Staff {
    
        public Cashier(int id){
        super(id);
    }

    // Prices the order for this customer and marks it paid
    // The customer's own status change (PAID) is done by Restaurant.processReadyToPay, inside the StateManager action
    public void processPayment(Order order, Customer customer){
        order.setPrice(customer.applyDiscount(order.getPrice()));
        order.paid();
    }

}
