package NTUBurger.Restaurant;

import NTUBurger.Roles.Customer;

// Each time a meal is ordered thru Waiter, an order will be created

public class Order {

    // Status is a nested class and any enum declared = static by default
    // enum -> Strictly enforces the states here, meaning any typos of state = compile error
    // AKA Represent a group of Constants
    public enum Status{ PLACED, COOKING, READY, SERVED, PAID, CANCELLED }

    private Customer customer;
    private Status status = Status.PLACED;
    private int prepUnits;
    private int priceCents;

    public Order(Customer customer, MenuItem item){
        this.customer = customer;
        prepUnits = item.getPrepUnits();
        priceCents = item.getPrice();
    }

    // Display Interface <-- Might need ToStrings mtd
    public Status getStatus(){ return status; }
    // Order hold price
    public int getPrice(){ return priceCents; }
    public Customer getCustomer(){ return customer; }

    // For cashier to set after calculating the price after discount
    public void setPrice(int priceCents){ this.priceCents = priceCents;}

    // Chef determines the cooking progress via cookUnits
    public void preparing(int cookUnits){
        if(prepUnits>0){
            status = Status.COOKING;
            prepUnits -= cookUnits;
        }
        // Set status to ready if cooked finish
        if(prepUnits <= 0){ status = Status.READY; }
    }

    // if Served
    public void served(){ status = Status.SERVED; }

    // if Paid
    public void paid(){ status = Status.PAID; }

    // If Customer left, cancel order
    public void cancelOrder(){ status = Status.CANCELLED; }

    // True if cooked finish
    public boolean isOrderReady(){ return status == Status.READY; }

    // True if food is served
    public boolean isServed(){ return status == Status.SERVED; }

    // True if food is paid
    public boolean isPaid(){ return status == Status.PAID; }

    // True if food is Cancelled
    public boolean isCancelled(){ return status == Status.CANCELLED; }

}
