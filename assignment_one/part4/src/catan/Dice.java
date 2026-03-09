package catan;

import java.util.Random;

public final class Dice {
    private final Random rng = new Random();

    public int roll() {
        return (rng.nextInt(6) + 1) + (rng.nextInt(6) + 1);
    }
}