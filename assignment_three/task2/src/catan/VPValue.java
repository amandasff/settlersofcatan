package catan;

public class VPValue implements Value {
    @Override
    public double evaluate(BuildRoadAction action, Player player) {
        return 0;
    }

    @Override
    public double evaluate(BuildSettlementAction action, Player player) {
        return 1;
    }

    @Override
    public double evaluate(BuyCardAction action, Player player) {
        return 0;
    }

    @Override
    public double evaluate(PassAction action, Player player) {
        return 0;
    }

    @Override
    public double evaluate(UpgradeToCityAction action, Player player) {
        return 1;
    }
}
