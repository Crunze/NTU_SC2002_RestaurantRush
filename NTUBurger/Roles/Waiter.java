package NTUBurger.Roles;

import java.util.List;

import NTUBurger.Restaurant.MenuItem;
import NTUBurger.Restaurant.Order;
import NTUBurger.Restaurant.Table;

// Instantiate Order since created by Waiter

public class Waiter extends Staff {

    public Waiter(int id){
        super(id, Role.WAITER);
    }

    public void seatCustomer(Customer customer, Table table){
        table.seat(customer);
        customer.seat();
    }

    // Waiter talks with Customer and return Customer Order
    public Order takeOrder(Customer customer, List<MenuItem> menu){
        customer.chooseFood(menu.get(0)); // Hardcoded Food, Need to set to Random
        MenuItem food = customer.getChosenItem();
        Order newOrder = new Order(customer, food);
        return newOrder;
    }

    public void serveOrder(Customer customer, Order order, int turnNo){
        order.served();
        customer.eat(turnNo);
    }

    @Override
    public String describeToDos(){
        return "";
    }
    
}
