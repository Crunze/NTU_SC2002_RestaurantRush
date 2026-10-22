package NTUBurger;

import NTUBurger.Restaurant.Order;
import NTUBurger.Restaurant.OrderStatus;
import NTUBurger.Restaurant.StateView;
import NTUBurger.Roles.Customer;
import NTUBurger.Roles.CustomerStatus;

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
    public int revenue(){
        return stateView.getOrdersByStatus(OrderStatus.PAID).stream()
                .mapToInt(Order::getPrice).sum();
    }
    // Average the total
    // If no one, throws an exception, so need to catch it during display
    public double avgSatisfaction(){
        return stateView.getCustomersByStatus(CustomerStatus.PAID).stream()
                .mapToInt(Customer::getSatisfaction).average()
                .orElse(0.0);
    }

    // Served + readytopay + paid
    public int servedCount(){
        return stateView.getCustomersByStatus(CustomerStatus.SERVED).size()
        + stateView.getCustomersByStatus(CustomerStatus.READY_TO_PAY).size() +
        stateView.getCustomersByStatus(CustomerStatus.PAID).size();
    }

    public int unhappyDeparture(){
        return stateView.getCustomersByStatus(CustomerStatus.ABANDONED).size();
    }

    // Final Outcome
    public boolean isVictory() {
        int paid = stateView.getCustomersByStatus(CustomerStatus.PAID).size();
        Double avg = avgSatisfaction();

        return paid >= BaseSetting.MIN_PAID_CUSTOMERS
                && revenue() >= BaseSetting.MIN_REVENUE_CENTS
                && avg >= BaseSetting.MIN_AVG_SATISFACTION;
    }
}
