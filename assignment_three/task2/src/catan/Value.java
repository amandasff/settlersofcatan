package catan;

public interface Value {
    public double evaluate (BuildRoadAction action);
    public double evaluate (BuildSettlementAction action);
    public double evaluate (BuyCardAction action);
    public double evaluate (PassAction action);
    public double evaluate (UpgradeToCityAction action);
}