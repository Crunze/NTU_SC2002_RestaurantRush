package NTUBurger.Roles;

import java.util.List;
import java.util.Random;
import NTUBurger.Restaurant.MenuItem;

// Critic: loses satisfaction fastest, pays full price,
// and always orders the dish with the most preparation units (to judge the kitchen's hardest dish)
public class CustomerCritic extends Customer {

    public CustomerCritic(int id){
        super(id);
    }
    @Override
    public int satisfactionLossPerTurn(){ return 12; }

    @Override 
    public int applyDiscount(int priceCents){ return priceCents; }

    @Override
    protected MenuItem pickFood(List<MenuItem> menu, Random random){
        MenuItem best =menu.get(0);
        for (MenuItem item : menu){
            if (item.getPrepUnits() > best.getPrepUnits()){
                best = item;
            }
        }
        return best;
    }
}
