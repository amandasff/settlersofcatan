package catan;

import java.util.List;
import java.util.Random;

public class RandomStrategy implements AgentStrategy {
    private final Random rng;

    public RandomStrategy() {
        this.rng = new Random();
    }

    public RandomStrategy(Random rng) {
        this.rng = rng;
    }

    @Override
    public Action select(List<Action> options) {
        return options.get(rng.nextInt(options.size()));
    }
}
