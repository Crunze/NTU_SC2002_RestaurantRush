package NTUBurger.Restaurant;

import java.util.Arrays;
import java.util.List;

// Tracking of table 
// I dont know where to put the counting of empty and occupied tables

public interface TableView {
    Table[] getTables();

    // Default allows adding of full code implementation and can be reuse again without adding it to anywhere
    default List<Table> getOccupiedTables() {
        return Arrays.stream(getTables()).filter(Table::isOccupied).toList();
    }

    default int countOccupiedTables() {
        return getOccupiedTables().size();
    }

    // Lowest-numbered free table, or null if every table is taken
    default Table firstFreeTable() {
        return Arrays.stream(getTables()).filter(table -> !table.isOccupied()).findFirst().orElse(null);
    }

    // Table the customer is sitting at, or null
    default Table findTableOf(int customerId) {
        return Arrays.stream(getTables()).filter(table -> table.getCustomerId() == customerId)
                .findFirst().orElse(null);
    }
}
