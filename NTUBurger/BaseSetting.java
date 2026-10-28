package NTUBurger;

import java.util.List;

import NTUBurger.Restaurant.ComboMeal;
import NTUBurger.Restaurant.MenuItem;

// Base Config, final prevents this class from having subclasses, NOT A Superclass
// Specific number of Staff action, E.g., MAX_WAITER_TASK_PER_TURN should be in their own Role Class

public final class BaseSetting {

    private BaseSetting() { }   //Prevent creating object from this class

    public static final int MAX_TURNS = 18;
    public static final int NUM_TABLES = 2;

    // Staff
    public static final int NUM_WAITERS = 1;
    public static final int NUM_HOSTS = 1;
    public static final int NUM_CHEFS = 1;
    public static final int NUM_CASHIERS = 1;

    // List.of creates an unmodifiable list
    public static final List<Integer> CUSTOMER_ARRIVAL_TURN = List.of(1, 4, 7, 10, 13, 16);

    // Special customers, by arrival number (every other customer is a Regular)
    public static final List<Integer> VIP_CUSTOMER_IDS = List.of(2);
    public static final List<Integer> CRITIC_CUSTOMER_IDS = List.of(4);

    // Objectives
    public static final int MIN_PAID_CUSTOMERS = 4;
    public static final int MIN_REVENUE_CENTS = 4500;
    public static final int MIN_AVG_SATISFACTION = 60;

    // Menu
    public static final MenuItem SALAD  = new MenuItem("Salad", 900, 1);
    public static final MenuItem BURGER = new MenuItem("Burger", 1500, 1);
    public static final MenuItem PASTA  = new MenuItem("Pasta", 1800, 2);

    // .of() : bundles individual item into an unmodifiable list
    // Combo meals: two dishes sold together at 90% of their prices, taking the sum of their prep units
    public static final MenuItem COMBO_A = new ComboMeal("Combo A (Salad + Burger)", SALAD, BURGER);
    public static final MenuItem COMBO_B = new ComboMeal("Combo B (Salad + Pasta)", SALAD, PASTA);

    // Single dishes first, then the combos
    public static final List<MenuItem> MENU = List.of(SALAD, BURGER, PASTA, COMBO_A, COMBO_B);

    // Happy Hour: once per game
    public static final int HAPPY_HOUR_TURNS = 2;
    public static final int HAPPY_HOUR_DISCOUNT_PERCENT = 20;
    public static final int HAPPY_HOUR_RECOVERY = 15;
}
