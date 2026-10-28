package NTUBurger.Roles;

import NTUBurger.Restaurant.MenuItem;
import NTUBurger.Restaurant.Order;
import NTUBurger.Restaurant.Table;

// Instantiate Order since created by Waiter
// No wait() since it does nothing and more of Manager Choice

public class Waiter extends Staff implements Seater {

    public Waiter(int id){
        super(id);
    }

    // Waiter assigns table to customer
    @Override
    public void seatCustomer(int customerId, Table table){
        table.seat(customerId);
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
