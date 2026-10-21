package NTUBurger.Restaurant;

// Each time a meal is ordered thru Waiter, an order will be created

public class Order {

    private final int customerId;
    private OrderStatus status = OrderStatus.PLACED;
    private int prepUnits;
    private int priceCents;

    public Order(int customerId, MenuItem item){
        this.customerId = customerId;
        prepUnits = item.getPrepUnits();
        priceCents = item.getPrice();
    }

    // Display Interface <-- Might need ToStrings mtd
    public OrderStatus getStatus(){ return status; }
    // Order hold price
    public int getPrice(){ return priceCents; }
    public int getCustomerId(){ return customerId; }

    // For cashier to set after calculating the price after discount
    public void setPrice(int priceCents){ this.priceCents = priceCents;}

    // Chef determines the cooking progress via cookUnits
    public void preparing(int cookUnits){
        // Only if OrderStatus is Cooking or Placed, then proceed
        if (status != OrderStatus.PLACED && status != OrderStatus.COOKING){ return; }

        status = OrderStatus.COOKING;
        prepUnits -= cookUnits;

        // Set status to ready if cooked finish
        if(prepUnits <= 0){ status = OrderStatus.READY; }
    }

    // if Served
    public void served(){ status = OrderStatus.SERVED; }

    // if Paid
    public void paid(){ status = OrderStatus.PAID; }

    // If Customer left, cancel order
    public void cancelOrder(){ status = OrderStatus.CANCELLED; }

    // True if cooked finish
    public boolean isOrderReady(){ return status == OrderStatus.READY; }

    // True if food is served
    public boolean isServed(){ return status == OrderStatus.SERVED; }

    // True if food is paid
    public boolean isPaid(){ return status == OrderStatus.PAID; }

    // True if food is Cancelled
    public boolean isCancelled(){ return status == OrderStatus.CANCELLED; }

}
