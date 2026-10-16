package NTUBurger.Restaurant;

// MenuItem class : Blueprint for creating menu item. For BaseSetting.java Menu

public class MenuItem {
    private final String name;
    private final int priceCents;
    private final int prepUnits;

    public MenuItem(String name, int priceCents, int prepUnits) {
        this.name = name;
        this.priceCents = priceCents;
        this.prepUnits = prepUnits;
    }

    public String getName(){ return name; }
    public int getPrice(){ return priceCents; }
    public int getPrepUnits(){ return prepUnits; }
}
