package NTUBurger.Restaurant;

// Two dishes sold together: costs 90% of the two prices (rounded half up) and takes the sum of their prep units
// A ComboMeal IS a MenuItem, so orders, the Chef and the menu list need no changes to handle it

public class ComboMeal extends MenuItem {

    public static final int COMBO_PERCENT = 90;

    public ComboMeal(String name, MenuItem first, MenuItem second) {
        super(name,
              ((first.getPrice() + second.getPrice()) * COMBO_PERCENT + 50) / 100,
              first.getPrepUnits() + second.getPrepUnits());
    }
}
