package catan;

public final class SimulationConfig {
    private final int rounds;
    private final long seed;

    public SimulationConfig(int rounds, long seed) {
        this.rounds = Math.min(rounds, 8192);
        this.seed = seed;
    }

    public int getRounds() {
        return rounds;
    }

    public long getSeed() {
        return seed;
    }
}