package NTUBurger.Roles;

import NTUBurger.Restaurant.Table;

public class Host extends Staff{
    public Host(int id){
        super(id);
    }

    // Host assigns table to customer
    public void seatCustomer(int customerId, Table table){
        table.seat(customerId);
    }
}
