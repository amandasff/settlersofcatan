package catan;

/*
 * Assignment 3 changes:
 * - UpgradeToCityAction now acts as a concrete Command
 * - added undo support so city upgrades can be reversed through command history
 */
public final class UpgradeToCityAction implements Action {
    private static final RuleEngine RULES = new RuleEngine();

    private final Node target;

    public UpgradeToCityAction(Node target) {
        this.target = target;
    }

    public Node getTarget() {
        return target;
    }

    @Override
    public boolean isExecutable(GameState state, Player player) {
        return RULES.canUpgradeToCity(state, player, target);
    }

    @Override
    public void execute(GameState state, Player player) {
        if (!isExecutable(state, player)) {
            throw new IllegalStateException("UpgradeToCityAction is not executable.");
        }

        // Spend city cost, consume one city piece, return one settlement piece,
        // replace the settlement with a city, and add +1 VP (1 -> 2).
        state.getBank().takeFrom(player, Cost.cityCost());
        player.getPieces().takeCity();
        player.getPieces().returnSettlement();

        target.setBuilding(new City(player.getId(), target.getId()));
        player.addVictoryPoints(1);
    }

    @Override
    public void undo(GameState state, Player player) {
        if (!(target.getBuilding() instanceof City city)) {
            throw new IllegalStateException("No city exists on the target node.");
        }

        if (city.getOwnerId() != player.getId()) {
            throw new IllegalStateException("Cannot undo another player's city.");
        }

        target.setBuilding(new Settlement(player.getId(), target.getId()));
        player.getPieces().returnCity();
        player.getPieces().takeSettlement();
        state.getBank().giveTo(player, Cost.cityCost());
        player.removeVictoryPoints(1);
    }

    @Override
    public String describe() {
        return "UPGRADE_TO_CITY node=" + target.getId();
    }
}