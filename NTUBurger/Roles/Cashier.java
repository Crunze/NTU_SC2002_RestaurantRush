package NTUBurger.Roles;

import NTUBurger.Restaurant.Order;

// Cashier: Handles payment

public class Cashier extends Staff {
    
        public Cashier(int id){
        super(id);
    }

    public void processPayment(Order order, Customer customer){
        order.setPrice(customer.applyDiscount(order.getPrice()));
        order.paid();
        customer.pay();
    }

}
