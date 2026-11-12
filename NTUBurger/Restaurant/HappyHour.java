package NTUBurger.Restaurant;

// Lasts for the turn it is activated and the next turn

public class HappyHour {

    private static final int DURATION_TURNS = 2;
    private static final int DISCOUNT_PERCENT = 20;

    private boolean used = false;   // Can only be activated once per game
    private int turnsLeft = 0;      // Active while above 0

    // True only if Happy Hour has never been used this game
    public boolean canActivate(){ return !used; }

    // Switch on at the start of a turn: active this turn and the next
    public void activate(){
        used = true;
        turnsLeft = DURATION_TURNS;
    }

    public boolean isActive(){ return turnsLeft > 0; }

    public int getTurnsLeft(){ return turnsLeft; }

    // Called at the end of every turn: counts down while active
    public void endTurn(){
        if (isActive()) { turnsLeft--; }
    }

    // rounded to the nearest cent
    public int applyTo(int priceCents){
        if (!isActive()) { return priceCents; }
        return (int) Math.round(priceCents * (100 - DISCOUNT_PERCENT) / 100.0);
    }
}
