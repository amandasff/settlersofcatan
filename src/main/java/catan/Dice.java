package catan;

import java.util.Random;

public class Dice {
    private final Random rng;

    public Dice() {
        this.rng = new Random();
    }

    public int roll() {
        return (rng.nextInt(6) + 1) + (rng.nextInt(6) + 1);
    }
}
