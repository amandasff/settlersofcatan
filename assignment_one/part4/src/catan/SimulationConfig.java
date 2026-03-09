package catan;

public final class SimulationConfig {
    private final int turns;
    private final long seed;

    public SimulationConfig(int turns, long seed) {
        if (turns < 1 || turns > 8192) {
            throw new IllegalArgumentException(
                    "turns must be between 1 and 8192 inclusive, but was " + turns
            );
        }

        this.turns = turns;
        this.seed = seed;
    }

    public int getTurns() {
        return turns;
    }

    public int getRounds() {
        return getTurns();
    }

    public long getSeed() {
        return seed;
    }
}