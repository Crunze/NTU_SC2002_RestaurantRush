package NTUBurger;

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

// Builds the same game as GameApp.main, but with the input and random seed supplied by the test.
// Lives in the test folder so the game itself needs no extra entry point. Keep it in step with GameApp.main.

final class TestGame {

    // The seed GameApp.main uses
    static final long SEED = 18;

    private TestGame() { }

    static GameController create(Scanner scanner, Random random) {
        StateManager stateManager = new StateManager();
        Restaurant restaurant = new Restaurant(stateManager, random);
        ManagerView managerView = new ManagerView(stateManager);
        ChoiceValidator validator = new ChoiceValidator(managerView, restaurant);
        ManagerMenu menu = new ManagerMenu(scanner, validator);
        return new GameController(restaurant, stateManager, managerView, menu, validator,
                List.of(new Waiter(1)), List.of(new Chef(1)), List.of(new Cashier(1)));
    }

    static Result play(Scanner scanner, Random random) {
        GameController game = create(scanner, random);
        game.runGame();
        return game.getResult();
    }
}
