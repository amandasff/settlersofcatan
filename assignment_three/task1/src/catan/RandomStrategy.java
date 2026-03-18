package catan;

import java.util.List;
import java.util.Random;

public final class RandomStrategy implements AgentStrategy {
    private final Random rng;

    public RandomStrategy() {
        this.rng = new Random();
    }

    public RandomStrategy(long seed) {
        this.rng = new Random(seed);
    }

    @Override
    public Action select(List<Action> options) {
        if (options == null || options.isEmpty()) {
            return new PassAction();
        }
        return options.get(rng.nextInt(options.size()));
    }
}