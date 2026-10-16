package NTUBurger;

import java.util.Scanner;

// Requires fallback just incase the action is not valid

public class ManagerMenu {

    public enum ManagerAction { SEAT_CUSTOMER, TAKE_ORDER, COOK, SERVE, PROCESS_PAYMENT, WAIT }

    private final Scanner scanner;

    public ManagerMenu(Scanner scanner){
        this.scanner = scanner;
    }

    public ManagerAction promptMenu(){

        // Retrieve all enum Constants and placed them into array called actions
        ManagerAction[] actions = ManagerAction.values();

        while(true){
            for(int i=0; i<actions.length; i++){
                // 1. SEAT_CUSTOMER
                System.out.println((i+1) + ". " + actions[i]);
            }
        }

    }
}
