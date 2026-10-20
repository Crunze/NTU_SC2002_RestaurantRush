package NTUBurger.Roles;

import NTUBurger.Restaurant.MenuItem;
import NTUBurger.Restaurant.Order;
import NTUBurger.Restaurant.Table;

// Instantiate Order since created by Waiter
// No wait() since it does nothing and more of Manager Choice

public class Waiter extends Staff {

    public Waiter(int id){
        super(id);
    }

    // Waiter assigns table to customer
    public void seatCustomer(Customer customer, Table table){
        table.seat(customer);
    }

    // Waiter take down customer order
    public Order takeOrder(int customerId, MenuItem food){
        Order newOrder = new Order(customerId, food);
        return newOrder;
    }

    // Waiter serves food
    public void serveOrder(Order order){
        order.served();
    }
}
