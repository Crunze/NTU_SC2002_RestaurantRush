package NTUBurger.Restaurant;

import java.util.List;

import NTUBurger.Roles.Customer;
import NTUBurger.Roles.CustomerStatus;

// Ensure only lists are exposed

public interface StateView {
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Customer> getCustomersByStatus(CustomerStatus status);
    Order findOrderOf(Customer customer);
    Customer findCustomerById(int CustomerId);
}
