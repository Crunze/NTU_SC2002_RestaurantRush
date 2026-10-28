package NTUBurger.Roles;

import NTUBurger.Restaurant.Table;

// Host: seats queued customers, which frees the Waiter for orders and serving
// No wait() since it does nothing and more of Manager Choice

public class Host extends Staff implements Seater {

    public Host(int id){
        super(id);
    }

    @Override
    public void seatCustomer(int customerId, Table table){
        table.seat(customerId);
    }
}
