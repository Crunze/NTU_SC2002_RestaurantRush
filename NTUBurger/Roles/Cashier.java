package NTUBurger.Roles;

import NTUBurger.Restaurant.Order;

// Cashier: Handles payment

public class Cashier extends Staff {
    
        public Cashier(int id){
        super(id);
    }

    public void processPayment(Order order, Customer customer){
        int originalPrice = order.getPrice();
        int totalPrice = originalPrice - (customer.discount() * originalPrice);
        order.setPrice(totalPrice);
        order.paid();
    }

}
