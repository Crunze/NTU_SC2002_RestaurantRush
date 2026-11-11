package NTUBurger.Restaurant;

//A combo is a MenuItem made of 2 other items: summed prep units, 90% of the summed price
//Price stays in integer cents: (900+1800)*90/100 = 2430

public class ComboMeal extends MenuItem{
    public ComboMeal (MenuItem first, MenuItem second){
        super(first.getName() + "+" + second.getName(),
        (first.getPrice() + second.getPrice()) * 90/100,
        first.getPrepUnits() + second.getPrepUnits());
    }
}
