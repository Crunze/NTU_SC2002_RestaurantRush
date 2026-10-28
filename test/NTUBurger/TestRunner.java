package NTUBurger;

import java.util.Objects;

// Tiny dependency-free test harness (JDK only). Exits non-zero if any check fails

public final class TestRunner {
    private static int passed;
    private static int failed;

    private TestRunner() { }

    public static void check(boolean condition, String name) {
        if (condition) {
            passed++;
        } else {
            failed++;
            System.out.println("FAIL: " + name);
        }
    }

    public static void checkEquals(Object expected, Object actual, String name) {
        check(Objects.equals(expected, actual), name + " expected=" + expected + " actual=" + actual);
    }

    public static void main(String[] args) {
        UnitTests.run();
        GameTests.run();
        System.out.printf("%d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }
}
