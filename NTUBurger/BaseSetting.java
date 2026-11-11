package NTUBurger;

import java.util.List;

import NTUBurger.Restaurant.MenuItem;

// Base Config, final prevents this class from having subclasses, NOT A Superclass
// Specific number of Staff action, E.g., MAX_WAITER_TASK_PER_TURN should be in their own Role Class

public final class BaseSetting {

    private BaseSetting() { }   //Prevent creating object from this class

    public static final int RANDOMSEED = 18;
    public static final int MAX_TURNS = 18;
    public static final int NUM_TABLES = 2;

    // Staff
    public static final int NUM_WAITERS = 1;
    public static final int NUM_CHEFS = 1;
    public static final int NUM_CASHIERS = 1;

    // List.of creates an unmodifiable list
    public static final List<Integer> CUSTOMER_ARRIVAL_TURN = List.of(1, 4, 7, 10, 13, 16);

    // Objectives
    public static final int MIN_PAID_CUSTOMERS = 4;
    public static final int MIN_REVENUE_CENTS = 4500;
    public static final int MIN_AVG_SATISFACTION = 60;

    // Menu
    public static final MenuItem SALAD  = new MenuItem("Salad", 900, 1);
    public static final MenuItem BURGER = new MenuItem("Burger", 1500, 1);
    public static final MenuItem PASTA  = new MenuItem("Pasta", 1800, 2);

    // .of() : bundles individual item into an unmodifiable list
    public static final List<MenuItem> MENU = List.of(SALAD, BURGER, PASTA);
}
