package NTUBurger;

import NTUBurger.Restaurant.Restaurant;
import NTUBurger.Restaurant.StateManager;

// Controls the gameflow via 6 steps in a turn and ensure 18 turns max

public class GameController {
    private final Restaurant restaurant;
    private final StateManager stateManager;

    public GameController(Restaurant restaurant, StateManager stateManager){
        this.restaurant = restaurant;
        this.stateManager = stateManager;
    }

    private void step1(){

    }

    private void step2(){

    }

    private void step3(){

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
