package NTUBurger.Restaurant;

import NTUBurger.BaseSetting;

// A once-per-game promotion. When started at the beginning of a turn it lasts BaseSetting.HAPPY_HOUR_TURNS turns:
// orders taken while it is active are discounted, and waiting customers regain satisfaction at the start of each turn

public class HappyHour {

    private boolean used;
    private int remainingTurns;

    // True until Happy Hour has been started
    public boolean isAvailable(){ return !used; }

    public boolean isActive(){ return remainingTurns > 0; }

    public int getRemainingTurns(){ return remainingTurns; }

    // Returns false if it was already used
    public boolean activate(){
        if (used){ return false; }
        used = true;
        remainingTurns = BaseSetting.HAPPY_HOUR_TURNS;
        return true;
    }

    // Called once at the end of every turn
    public void endTurn(){
        if (remainingTurns > 0){ remainingTurns--; }
    }

    // The price after the discount, in integer cents rounded half up. Unchanged when not active
    public int apply(int priceCents){
        if (!isActive()){ return priceCents; }
        return (priceCents * (100 - BaseSetting.HAPPY_HOUR_DISCOUNT_PERCENT) + 50) / 100;
    }
}
