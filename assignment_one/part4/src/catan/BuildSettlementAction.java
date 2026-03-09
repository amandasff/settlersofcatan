package catan;

public final class BuildSettlementAction implements Action {
    private static final RuleEngine RULES = new RuleEngine();

    private final Node target;

    public BuildSettlementAction(Node target) {
        this.target = target;
    }

    public Node getTarget() {
        return target;
    }

    @Override
    public boolean isExecutable(GameState state, Player player) {
        return RULES.canBuildSettlement(state, player, target);
    }

    @Override
    public void execute(GameState state, Player player) {
        if (!isExecutable(state, player)) {
            throw new IllegalStateException("BuildSettlementAction is not executable.");
        }

        // Spend settlement cost, consume one settlement piece,
        // place settlement on target node, and add 1 VP.
        state.getBank().takeFrom(player, Cost.settlementCost());
        player.getPieces().takeSettlement();

        Settlement settlement = new Settlement(player.getId(), target.getId());
        target.setBuilding(settlement);

        player.addVictoryPoints(settlement.getVPValue());
    }

    @Override
    public String describe() {
        return "BUILD_SETTLEMENT node=" + target.getId();
    }
}