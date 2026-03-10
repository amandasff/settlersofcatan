package catan;

import java.util.Random;

public final class Dice {
    private final Random rng;

    public Dice() {
        this.rng = new Random();
    }

    public Dice(long seed) {
        this.rng = new Random(seed);
    }

    public int roll() {
        return (rng.nextInt(6) + 1) + (rng.nextInt(6) + 1);
    }
}