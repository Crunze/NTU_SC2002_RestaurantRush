package NTUBurger.Restaurant;

import NTUBurger.Roles.Customer;

// Table Class for Table State

public class Table {

    private final int number;
    private Customer customer = null;

    public Table(int number){
        this.number = number;
    }

    public int getNumber(){ return number; }
    public Customer getCustomer(){ return customer; }

    // Check if table is empty
    public boolean isOccupied(){ return customer != null; }

    // Seat Customer
    public void seat(Customer c){
        if(!isOccupied()){
            customer = c;
        } else{
            throw new IllegalStateException("Table " + number + " is already occupied!");
        }
    }

    // Customer Left
    public void free(){
        customer = null;
    }
}
