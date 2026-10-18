package NTUBurger.Restaurant;

// Tracking of table 
// I dont know where to put the counting of empty and occupied tables

public interface TableView {
    Table[] getTables();

    // Default allows adding of full code implementation and can be reuse again without adding it to anywhere
    default int countOccupiedTables(){
        int occupied = 0;
        for (Table table : getTables()){
            if (table.isOccupied()){
                occupied++;
            }
        }
        return occupied;
    }
}
