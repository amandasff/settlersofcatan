package catan;

/*
 * Assignment 3 changes:
 * - BuildRoadAction now acts as a concrete Command
 * - added undo support so road placement can be reversed through command history
 */
public final class BuildRoadAction implements Action {
    private static final RuleEngine RULES = new RuleEngine();

    private final Edge target;

    public BuildRoadAction(Edge target) {
        this.target = target;
    }

    public Edge getTarget() {
        return target;
    }

    @Override
    public boolean isExecutable(GameState state, Player player) {
        return RULES.canBuildRoad(state, player, target);
    }

    @Override
    public void execute(GameState state, Player player) {
        if (!isExecutable(state, player)) {
            throw new IllegalStateException("BuildRoadAction is not executable.");
        }

        // Spend road cost, consume one road piece, and place road on target edge.
        state.getBank().takeFrom(player, Cost.roadCost());
        player.getPieces().takeRoad();

        target.setRoad(new Road(player.getId(), target.getId()));
    }

    @Override
    public void undo(GameState state, Player player) {
        if (target.getRoad() == null) {
            throw new IllegalStateException("No road exists on the target edge.");
        }

        if (target.getRoad().getOwnerId() != player.getId()) {
            throw new IllegalStateException("Cannot undo another player's road.");
        }

        target.setRoad(null);
        player.getPieces().returnRoad();
        state.getBank().giveTo(player, Cost.roadCost());
    }

    @Override
    public String describe() {
        return "BUILD_ROAD edge=" + target.getId();
    }
}