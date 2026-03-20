package catan;

public final class BuyCardAction implements Action {
    private static final RuleEngine RULES = new RuleEngine();
    
    @Override
    public boolean isExecutable(Player player) {
        return RULES.canBuyDevelopmentCard(player);
    }

    @Override
    public void execute(GameState state, Player player) {
        if (!isExecutable(player)) {
            throw new IllegalStateException("BuyCardAction is not executable.");
        }

        state.getBank().takeFrom(player, Cost.developmentCardCost());
        drawCard(player);
    }

    @Override 
    public void undo(GameState state, Player player) {

    }

    @Override 
    public String describe() {
        return "Card purchased";
    }
}