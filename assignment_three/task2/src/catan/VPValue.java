package catan;

public class VPValue implements Value {
    @Override
    public double evaluate (BuildRoadAction action) {
        return 0;
    }
    
    @Override
    public double evaluate (BuildSettlementAction action) {
        return 1;
    }
    
    @Override
    public double evaluate (BuyCardAction action) {
        return 0;
    }

    @Override
    public double evaluate (PassAction action) {
        return 0;
    }

    @Override
    public double evaluate (UpgradeToCityAction action) {
        return 1;
    }
}