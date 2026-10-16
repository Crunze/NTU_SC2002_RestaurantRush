package NTUBurger;

import java.util.ArrayList;
import java.util.List;

import NTUBurger.Restaurant.Restaurant;
import NTUBurger.Restaurant.StateManager;
import NTUBurger.Roles.Cashier;
import NTUBurger.Roles.Chef;
import NTUBurger.Roles.Waiter;

// Instantiate the environment: Restaurant, the people (Staff, Customer)
// Instantiate the game logic: gameController

public class GameApp {
    public static void main(String[] args) {
        
        


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
        Restaurant restaurant = new Restaurant(stateManager);

        GameController gameController = new GameController(restaurant, stateManager);
        gameController.runGame();

    }
}
