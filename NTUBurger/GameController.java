package NTUBurger;

import NTUBurger.Manager.ManagerMenu;
import NTUBurger.Manager.ManagerView;
import NTUBurger.Manager.StaffAction;
import NTUBurger.Manager.StaffRole;
import NTUBurger.Manager.ChoiceValidator;
import NTUBurger.Restaurant.Restaurant;
import NTUBurger.Restaurant.StateManager;

// Controls the gameflow via 6 steps in a turn and ensure 18 turns max

public class GameController {
    private final Restaurant restaurant;
    private final StateManager stateManager;
    private final ManagerView managerView;
    private final ManagerMenu managerMenu;
    private final ChoiceValidator validator;

    public GameController(Restaurant restaurant, StateManager stateManager,
        ManagerView managerView, ManagerMenu managerMenu, ChoiceValidator validator){
        this.restaurant = restaurant;
        this.stateManager = stateManager;
        this.managerView = managerView;
        this.managerMenu = managerMenu;
        this.validator = validator;
        
    }

    private void step1(){
        // INSERT here for instantiating customer objects

        // Retrieve the most updated list
        managerView.refreshList();

        // INSERT here for display
    }

    private void step2(){
        // Reset ManagerChoice
        managerMenu.reset();
        ChoiceValidator.Result result;

        do {
            managerMenu.tellWaiter();
            result = validator.validateWaiter(managerMenu.getChoice(StaffRole.WAITER));
            if (!result.valid()) { System.out.println("Invalid: " + result.reason() + " Choose again."); }
        } while (!result.valid());

        do {
            managerMenu.promptChef();
            result = validator.validateChef(managerMenu.getChoice(StaffRole.CHEF), managerMenu.getChoice(StaffRole.WAITER));
            if (!result.valid()) { System.out.println("Invalid: " + result.reason() + " Choose again."); }
        } while (!result.valid());
    }

    private void step3() {
        StaffAction waiter = managerMenu.getChoice(StaffRole.WAITER);
        StaffAction chef = managerMenu.getChoice(StaffRole.CHEF);
        // Waiter first: turn waiter.customerId() into a Customer with findCustomerById, then switch on waiter.task()
        // Chef second: find the order AFTER the Waiter has acted, so an order taken this turn exists by then
    }


    private void step4(){

    }

    private void step5(){

    }

    private void step6(){

    }

    private void runTurn(){
        step1();
        step2();
        step3();
        step4();
        step5();
        step6();
    }

    public void runGame(){
        for (int turn = 1; turn <= BaseSetting.MAX_TURNS; turn++){
            runTurn();
        }
    }


}
