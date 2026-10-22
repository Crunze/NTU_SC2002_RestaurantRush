package NTUBurger.Restaurant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import NTUBurger.Roles.Customer;
import NTUBurger.Roles.CustomerStatus;

// Ensure only lists are exposed

public interface StateView {
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Customer> getCustomersByStatus(CustomerStatus status);
    Order findOrderOf(Customer customer);
    Customer findCustomerById(int CustomerId);

    default List<Customer> getActiveCustomers() {
        List<Customer> all = new ArrayList<>();
        for (CustomerStatus customerStatus : List.of(CustomerStatus.WAITING, CustomerStatus.SEATED,
                                        CustomerStatus.SERVED, CustomerStatus.READY_TO_PAY)) {
            all.addAll(getCustomersByStatus(customerStatus));
        }
        all.sort(Comparator.comparingInt(Customer::getId));
        return all;
    }
}
