package catan;

public class UpgradeToCityAction implements Action {
    private final Node target;

    public UpgradeToCityAction(Node target) {
        this.target = target;
    }

    @Override
    public boolean isExecutable(GameState state, Player p) {
        if (!target.isOccupied()) return false;
        if (target.getOwner() != p) return false;
        if (!(target.getBuilding() instanceof Settlement)) return false;
        if (!p.canAfford(Cost.cityCost())) return false;
        if (!p.getPieces().hasCity()) return false;
        return true;
    }

    @Override
    public void execute(GameState state, Player p) {
        Cost cost = Cost.cityCost();
        p.pay(cost);
        state.getBank().returnCost(cost);
        p.getPieces().takeCity();
        p.getPieces().returnSettlement();
        City city = new City(p, target);
        target.setBuilding(city);
        p.addVP(1);
    }

    @Override
    public String describe() {
        StringBuilder sb = new StringBuilder();
        sb.append("upgrades settlement to city at node ").append(target.getId());
        sb.append(" (adjacent to ");
        boolean first = true;
        for (Tile t : target.getAdjacentTiles()) {
            if (!first) sb.append(", ");
            first = false;
            sb.append(t.getTerrain());
            if (t.getTerrain() != TerrainType.DESERT) {
                sb.append("/").append(t.getToken());
            }
        }
        sb.append(") [cost: 3 ORE, 2 GRAIN]");
        return sb.toString();
    }
}
