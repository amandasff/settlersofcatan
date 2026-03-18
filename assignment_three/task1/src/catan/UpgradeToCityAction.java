package catan;

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
    public String describe() {
        return "UPGRADE_TO_CITY node=" + target.getId();
    }
}