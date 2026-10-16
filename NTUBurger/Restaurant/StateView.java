package NTUBurger.Restaurant;

import java.util.List;

import NTUBurger.Roles.Customer;

// Ensure only lists are exposed

public interface StateView {
    List<Order> getOrdersByStatus(Order.Status status);
    List<Customer> getCustomersByStatus(Customer.Status status);
}
