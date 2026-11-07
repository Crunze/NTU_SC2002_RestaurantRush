package NTUBurger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import NTUBurger.Manager.ChoiceValidator;
import NTUBurger.Manager.ManagerMenu;
import NTUBurger.Manager.ManagerView;
import NTUBurger.Restaurant.Restaurant;
import NTUBurger.Restaurant.StateManager;
import NTUBurger.Roles.Cashier;
import NTUBurger.Roles.Chef;
import NTUBurger.Roles.Waiter;
import NTUBurger.Roles.Host; 

// Instantiate the environment: Restaurant, the people (Staff, Customer)
// Instantiate the game logic: gameController

public class GameApp {
    public static void main(String[] args) {
        // Chosen seed value to be 18.
        Random random = new Random(BaseSetting.RANDOMSEED);

        //Creating Waiter/Chef/Cashier
        // Only ONE waiter and ONE chef is supported
        Waiter waiter = new Waiter(BaseSetting.NUM_WAITERS);
        Chef chef = new Chef(BaseSetting.NUM_CHEFS);
        Host host = new Host(1);

        // Multiple Cashier is supported
        List<Cashier> cashiers = new ArrayList<>();
        for (int i=0; i<BaseSetting.NUM_CASHIERS; i++){
            cashiers.add(new Cashier(i+1));
        }

        //Creating Main Game
        Scanner scanner = new Scanner(System.in);
        StateManager stateManager = new StateManager();
        Restaurant restaurant = new Restaurant(stateManager, random);
        ManagerView managerView = new ManagerView(stateManager);
        ChoiceValidator validator = new ChoiceValidator(managerView, restaurant);
        ManagerMenu menu = new ManagerMenu(scanner, validator);
        GameController gameController = new GameController(restaurant, stateManager, managerView, menu, validator,
                waiter, chef, host, cashiers);
        gameController.runGame();
    }
}
