package catan;

public interface Value {
    double evaluate(BuildRoadAction action, Player player);
    double evaluate(BuildSettlementAction action, Player player);
    double evaluate(BuyCardAction action, Player player);
    double evaluate(PassAction action, Player player);
    double evaluate(UpgradeToCityAction action, Player player);
}
