package catan;

public final class BuyCardAction implements Action {
    private static final RuleEngine RULES = new RuleEngine();

    @Override
    public boolean isExecutable(GameState state, Player player) {
        return RULES.canBuyDevelopmentCard(player);
    }

    @Override
    public void execute(GameState state, Player player) {
        if (!isExecutable(state, player)) {
            throw new IllegalStateException("BuyCardAction is not executable.");
        }

        state.getBank().takeFrom(player, Cost.developmentCardCost());
        CardPool.drawCard(player);
    }

    @Override
    public void undo(GameState state, Player player) {
    }

    @Override
    public String describe() {
        return "BUY_CARD";
    }
}
