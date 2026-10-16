package NTUBurger;

import NTUBurger.Restaurant.StateView;
import NTUBurger.Restaurant.Order;
import NTUBurger.Roles.Customer;

// Calculate Result for end of each turn

public class Result {
    private final StateView stateView; // Upcast StateManager to StateView
    private int turnNo;

    public Result(StateView stateview, int turnNo){
        this.stateView = stateview;
        this.turnNo = turnNo;
    }

    public int getTurnNo(){ return turnNo; }
    public void setTurnNo(int turnNo){ this.turnNo = turnNo; }

    // Convert List to Stream then normal stream into specialised stream for Double
    // Sum everything to give total revenue. Returns 0.0 if stream is nth
    public double revenue(){
        return stateView.getOrdersByStatus(Order.Status.PAID).stream()
                .mapToDouble(Order::getPrice).sum();
    }
    // Average the total
    // If no one, throws an exception, so need to catch it during display
    public double avgSatisfaction(){
        return stateView.getCustomersByStatus(Customer.Status.PAID).stream()
                .mapToInt(Customer::getSatisfaction).average()
                .orElseThrow(() -> new IllegalStateException("N/A"));
    }

    // Served + readytopay + paid
    public int servedCount(){
        return stateView.getCustomersByStatus(Customer.Status.SERVED).size()
        + stateView.getCustomersByStatus(Customer.Status.READY_TO_PAY).size() +
        stateView.getCustomersByStatus(Customer.Status.PAID).size();
    }

    public int unhappyDeparture(){
        return stateView.getCustomersByStatus(Customer.Status.ABANDONED).size();
    }
}
