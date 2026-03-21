package catan;

import java.util.List;
import java.util.Random;

public final class ValueStrategy implements AgentStrategy {
    private final List<Value> valueRules = List.of(new VPValue(), new BuildValue(), new SpendValue());
    private final Random rng = new Random();

    @Override
    public Action select(List<Action> options, Player player, GameState state) {
        if (options == null || options.isEmpty()) {
            return new PassAction();
        }
        return pickBest(options, player);
    }

    private Action pickBest(List<Action> options, Player player) {
        double maxScore = -1;
        Action best = null;

        for (Action action : options) {
            double score = 0;
            for (Value rule : valueRules) {
                score += dispatch(rule, action, player);
            }
            if (score > maxScore) {
                maxScore = score;
                best = action;
            }
        }

        if (maxScore == 0) {
            return options.get(rng.nextInt(options.size()));
        }

        return best;
    }

    private double dispatch(Value rule, Action action, Player player) {
        if (action instanceof BuildRoadAction a)       return rule.evaluate(a, player);
        if (action instanceof BuildSettlementAction a) return rule.evaluate(a, player);
        if (action instanceof BuyCardAction a)         return rule.evaluate(a, player);
        if (action instanceof UpgradeToCityAction a)   return rule.evaluate(a, player);
        if (action instanceof PassAction a)            return rule.evaluate(a, player);
        return 0;
    }
}
