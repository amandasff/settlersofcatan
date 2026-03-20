package catan;

import java.util.Random;

public final class ValueStrategy implements AgentStrategy {    
    
    @Override
    public Action select(List<Action> options) {
        List<Value> valueRules = (new VPValue(), new BuildValue(), new SpendValue());
        Action bestAction;
        double maxValue = 0;

        for (Action action : options) {
            for (Value vr : valueRules) {
                double value = rule.evaluate()
                if (value>maxValue) {
                    bestAction = action;
                }
            }
        }

        if (value == 0) {
            Random random = new Random();
            return options.get(random.nextInt(options.size()));

        }

        return bestAction;
    }
}