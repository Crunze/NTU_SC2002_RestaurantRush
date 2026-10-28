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

    // Chosen seed value to be 18.
    public static final long SEED = 18;

    public static void main(String[] args) {
        play(new Scanner(System.in), new Random(SEED));
    }

    // Wires the game together and plays all turns. Scanner and Random are passed in so tests can script a game
    // Returns the result so a caller can check VICTORY / DEFEAT
    public static Result play(Scanner scanner, Random random) {
        GameController gameController = create(scanner, random);
        gameController.runGame();
        return gameController.getResult();
    }

    // Builds the whole game without running it
    public static GameController create(Scanner scanner, Random random) {
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
        StateManager stateManager = new StateManager();
        Restaurant restaurant = new Restaurant(stateManager, random);
        ManagerView managerView = new ManagerView(stateManager);
        ChoiceValidator validator = new ChoiceValidator(managerView, restaurant);
        ManagerMenu menu = new ManagerMenu(scanner, validator);
        GameController gameController = new GameController(restaurant, stateManager, managerView, menu, validator,
                waiters, chefs, cashiers);
        return gameController;
    }
}
