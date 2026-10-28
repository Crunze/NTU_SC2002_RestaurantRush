package NTUBurger.Roles;

import NTUBurger.Restaurant.Table;

// Anyone who can seat a customer: the Waiter and (Stage 3) the Host
// Restaurant.seatProcess takes this interface, so the seating rules are written once

public interface Seater {
    void seatCustomer(int customerId, Table table);
}
