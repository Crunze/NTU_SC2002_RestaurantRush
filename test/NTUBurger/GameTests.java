package NTUBurger;

import static NTUBurger.TestRunner.check;
import static NTUBurger.TestRunner.checkEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import NTUBurger.Manager.ManagerView;
import NTUBurger.Manager.Task;
import NTUBurger.Restaurant.Table;
import NTUBurger.Roles.Customer;
import NTUBurger.Roles.CustomerStatus;

// Whole-game tests: scripted input, input that ends early, and a bot that plays many random games
// while the status filing and table bookkeeping are audited at every prompt

public final class GameTests {
    private GameTests() { }

    public static void run() {
        idleGameIsDefeat();
        inputEndingEarlyStillFinishes();
        invalidInputDoesNotUseUpTheTurn();
        byteOrderMarkIsIgnored();
        greedyBotWins();
        randomGames(300);
    }

    // Runs a full game on scripted text; returns the captured console output and the result
    private static Object[] play(String input) {
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
        Result result;
        try {
            result = TestGame.play(new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))),
                    new Random(TestGame.SEED));
        } finally {
            System.setOut(original);
        }
        return new Object[] { captured.toString(StandardCharsets.UTF_8), result };
    }

    private static void idleGameIsDefeat() {
        // Each turn: Happy Hour? no, Waiter waits, Host waits, Chef waits
        Object[] out = play("0\n0\n0\n0\n".repeat(BaseSetting.MAX_TURNS));
        Result r = (Result) out[1];
        check(!r.isVictory(), "waiting every turn is DEFEAT");
        checkEquals(0, r.paidCount(), "idle game pays nobody");
        check(((String) out[0]).contains("DEFEAT"), "output announces DEFEAT");
        check(((String) out[0]).contains("TURN 18 / 18"), "all 18 turns are played");
    }

    private static void inputEndingEarlyStillFinishes() {
        Object[] out = play("");
        check(((String) out[0]).contains("TURN 18 / 18"), "empty input still plays all turns");
        check(((String) out[0]).contains("input closed"), "closed input is reported");
    }

    private static void invalidInputDoesNotUseUpTheTurn() {
        // turn 1: no Happy Hour, then junk, an unknown customer, then a valid seat; host and chef wait
        Object[] out = play("0\nx\n9\n1\n7\n1\n1\n0\n0\n");
        String text = (String) out[0];
        check(text.contains("Please enter a valid number from 0 to 3"), "non-number is explained");
        check(text.contains("Customer 7 is not in the waiting queue"), "unknown customer is explained");
        check(text.contains("Waiter seats C1 (Regular) at table 1"), "valid command still runs in the same turn");
    }

    private static void byteOrderMarkIsIgnored() {
        // The BOM sits before the first answer (Happy Hour? no)
        Object[] out = play("﻿0\n1\n1\n0\n0\n");
        check(((String) out[0]).contains("Waiter seats C1 (Regular) at table 1"), "BOM before the first number is ignored");
    }

    // ---- bot ----

    // Feeds the game one line at a time, choosing each move from the live game state
    private static final class Bot extends InputStream {
        private final Random random;      // null = greedy
        private GameController game;
        private byte[] pending = new byte[0];
        private int pos;
        private int stage;                // 0 waiter choice, 1 customer id, 3 host id, 2 chef id
        private int happyHourAskedTurn;   // Stage 3: the turn whose Happy Hour question was already answered
        private Task waiterTask = Task.WAITING;
        private int waiterTarget;
        final List<String> problems = new ArrayList<>();

        Bot(Random random) { this.random = random; }

        void attach(GameController game) { this.game = game; }

        private String nextLine() {
            audit();
            ManagerView view = game.getManagerView();
            String line;
            // Stage 3: while Happy Hour is unused, the first question of each turn is whether to activate it
            int turn = game.getResult().getTurnNo();
            if (stage == 0 && game.getRestaurant().canActivateHappyHour() && happyHourAskedTurn != turn) {
                happyHourAskedTurn = turn;
                boolean activate = random != null && random.nextInt(4) == 0;
                return (activate ? "1" : "0") + "\n";
            }
            switch (stage) {
                case 0 -> {
                    waiterTask = chooseWaiterTask(view);
                    waiterTarget = waiterTask == Task.WAITING ? 0 : pick(view.getTasks(waiterTask));
                    line = String.valueOf(waiterTask == Task.WAITING ? 0
                            : waiterTask == Task.SEATING ? 1 : waiterTask == Task.ORDERING ? 2 : 3);
                    stage = waiterTask == Task.WAITING ? 3 : 1;
                }
                case 1 -> {
                    line = String.valueOf(waiterTarget);
                    stage = 3;
                }
                case 3 -> {
                    // Stage 3: the Host may seat another waiting customer if a table is still free after the Waiter
                    line = String.valueOf(chooseHostTarget(view));
                    stage = 2;
                }
                default -> {
                    List<Integer> cookable = new ArrayList<>(view.getTasks(Task.COOKING));
                    if (waiterTask == Task.ORDERING) {
                        cookable.add(waiterTarget);
                    }
                    line = String.valueOf(cookable.isEmpty() ? 0 : pick(cookable));
                    stage = 0;
                }
            }
            return line + "\n";
        }

        private Task chooseWaiterTask(ManagerView view) {
            boolean freeTable = game.getRestaurant().countOccupiedTables() < BaseSetting.NUM_TABLES;
            List<Task> options = new ArrayList<>();
            if (!view.getTasks(Task.SERVING).isEmpty()) options.add(Task.SERVING);
            if (!view.getTasks(Task.ORDERING).isEmpty()) options.add(Task.ORDERING);
            if (freeTable && !view.getTasks(Task.SEATING).isEmpty()) options.add(Task.SEATING);
            if (options.isEmpty()) return Task.WAITING;
            if (random == null) return options.get(0);
            options.add(Task.WAITING);
            return options.get(random.nextInt(options.size()));
        }

        private int chooseHostTarget(ManagerView view) {
            if (random == null) return 0;
            boolean waiterSeats = waiterTask == Task.SEATING;
            int free = BaseSetting.NUM_TABLES - game.getRestaurant().countOccupiedTables() - (waiterSeats ? 1 : 0);
            List<Integer> waiting = new ArrayList<>(view.getTasks(Task.SEATING));
            if (waiterSeats) waiting.remove(Integer.valueOf(waiterTarget));
            if (free <= 0 || waiting.isEmpty() || random.nextBoolean()) return 0;
            return pick(waiting);
        }

        private int pick(List<Integer> ids) {
            return random == null ? ids.get(0) : ids.get(random.nextInt(ids.size()));
        }

        // Structural invariants that must hold whenever the manager is being prompted
        private void audit() {
            if (!game.getStateManager().isConsistent()) {
                problems.add("status filing disagrees with the customers and orders");
            }
            int occupied = 0;
            for (Table t : game.getRestaurant().getTables()) {
                if (t.isOccupied()) {
                    occupied++;
                    Customer c = game.getStateManager().findCustomerById(t.getCustomerId());
                    CustomerStatus s = c == null ? null : c.getStatus();
                    if (s != CustomerStatus.SEATED && s != CustomerStatus.SERVED && s != CustomerStatus.READY_TO_PAY) {
                        problems.add("table " + t.getNumber() + " holds a customer who is " + s);
                    }
                }
            }
            int seatedOrLater = 0;
            for (CustomerStatus s : List.of(CustomerStatus.SEATED, CustomerStatus.SERVED, CustomerStatus.READY_TO_PAY)) {
                seatedOrLater += game.getStateManager().getCustomersByStatus(s).size();
            }
            if (occupied != seatedOrLater) {
                problems.add("occupied tables " + occupied + " != seated customers " + seatedOrLater);
            }
            for (CustomerStatus s : CustomerStatus.values()) {
                for (Customer c : game.getStateManager().getCustomersByStatus(s)) {
                    if (c.getSatisfaction() < 0 || c.getSatisfaction() > 100) {
                        problems.add("satisfaction out of range for #" + c.getId());
                    }
                }
            }
        }

        @Override
        public int read() {
            if (pos >= pending.length) {
                pending = nextLine().getBytes(StandardCharsets.UTF_8);
                pos = 0;
            }
            return pending[pos++];
        }

        @Override
        public int read(byte[] b, int off, int len) {
            if (len == 0) return 0;
            if (pos >= pending.length) {
                pending = nextLine().getBytes(StandardCharsets.UTF_8);
                pos = 0;
            }
            int n = Math.min(len, pending.length - pos);
            System.arraycopy(pending, pos, b, off, n);
            pos += n;
            return n;
        }
    }

    private static Result playBot(Bot bot, long seed) {
        PrintStream original = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        try {
            GameController game = TestGame.create(new Scanner(bot), new Random(seed));
            bot.attach(game);
            game.runGame();
            return game.getResult();
        } finally {
            System.setOut(original);
        }
    }

    private static void greedyBotWins() {
        Bot bot = new Bot(null);
        Result r = playBot(bot, TestGame.SEED);
        checkEquals(List.of(), bot.problems, "greedy bot: no invariant violations");
        check(r.paidCount() >= 1, "greedy bot gets customers paid");
        check(r.isVictory(), "a sensible strategy wins the base game (paid " + r.paidCount()
                + ", revenue " + r.revenue() + ", avg " + r.avgSatisfaction() + ")");
    }

    private static void randomGames(int games) {
        int wins = 0;
        int violations = 0;
        for (int seed = 1; seed <= games; seed++) {
            Bot bot = new Bot(new Random(seed));
            Result r = playBot(bot, seed);
            if (r.isVictory()) wins++;
            if (!bot.problems.isEmpty()) {
                violations++;
                check(false, "seed " + seed + ": " + bot.problems.get(0));
            }
            check(r.getTurnNo() == BaseSetting.MAX_TURNS, "seed " + seed + " plays every turn");
        }
        System.out.println("random games: " + games + " played, " + wins + " victories, " + violations + " with violations");
    }
}
