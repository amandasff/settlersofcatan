package catan;

/*
 * Assignment 3 changes:
 * - BuildSettlementAction now acts as a concrete Command
 * - added undo support so settlement placement can be reversed through command history
 */
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

        state.getBank().takeFrom(player, Cost.settlementCost());
        player.getPieces().takeSettlement();

        target.setBuilding(new Settlement(player.getId(), target.getId()));
        player.addVictoryPoints(1);
    }

    @Override
    public void undo(GameState state, Player player) {
        if (!(target.getBuilding() instanceof Settlement settlement)) {
            throw new IllegalStateException("No settlement exists on the target node.");
        }

        if (settlement.getOwnerId() != player.getId()) {
            throw new IllegalStateException("Cannot undo another player's settlement.");
        }

        target.setBuilding(null);
        player.getPieces().returnSettlement();
        state.getBank().giveTo(player, Cost.settlementCost());
        player.removeVictoryPoints(1);
    }

    @Override
    public String describe() {
        return "BUILD_SETTLEMENT node=" + target.getId();
    }
}