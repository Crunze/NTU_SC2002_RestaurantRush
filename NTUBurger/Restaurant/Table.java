package NTUBurger.Restaurant;

// Table Class for Table State

public class Table {

    private final int number;
    private int customerId = -1;

    public Table(int number){
        this.number = number;
    }

    public int getNumber(){ return number; }
    public int getCustomerId(){ return customerId; }

    // Check if table is empty
    public boolean isOccupied(){ return customerId != -1; }

    // Seat Customer
    public void seat(int customerId){
        if(!isOccupied()){
            this.customerId = customerId;
        } else{
            throw new IllegalStateException("Table " + number + " is already occupied!");
        }
    }

    // Customer Left
    public void free(){
        customerId = -1;
    }
}
