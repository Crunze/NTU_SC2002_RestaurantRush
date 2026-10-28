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

// Instantiate the environment: Restaurant, the people (Staff, Customer)
// Instantiate the game logic: gameController

public class GameApp {
    public static void main(String[] args) {
        // Chosen seed value to be 18.
        Random random = new Random(18);

        //Creating Waiter/Chef/Cashier
        List<Waiter> waiters = new ArrayList<>();
        for (int i=0; i<BaseSetting.NUM_WAITERS; i++){
            waiters.add(new Waiter(i+1));
        }
        List<Chef> chefs = new ArrayList<>();
        for (int i=0; i<BaseSetting.NUM_CHEFS; i++){
            chefs.add(new Chef(i+1));
        }
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
                waiters, chefs, cashiers);
        gameController.runGame();
    }
}
