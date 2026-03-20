package catan;

public class SpendValue implements Value {
    private static final int HAND_THRESHOLD = 5;

    @Override
    public double evaluate(BuildRoadAction action, Player player) {
        return player.handSize() - 2 < HAND_THRESHOLD ? 0.5 : 0;
    }

    @Override
    public double evaluate(BuildSettlementAction action, Player player) {
        return player.handSize() - 4 < HAND_THRESHOLD ? 0.5 : 0;
    }

    @Override
    public double evaluate(BuyCardAction action, Player player) {
        return player.handSize() - 3 < HAND_THRESHOLD ? 0.5 : 0;
    }

    @Override
    public double evaluate(PassAction action, Player player) {
        return 0;
    }

    @Override
    public double evaluate(UpgradeToCityAction action, Player player) {
        return player.handSize() - 5 < HAND_THRESHOLD ? 0.5 : 0;
    }
}
