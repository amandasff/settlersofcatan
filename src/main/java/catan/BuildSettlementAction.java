package catan;

public class BuildSettlementAction implements Action {
    private final Node target;

    public BuildSettlementAction(Node target) {
        this.target = target;
    }

    @Override
    public boolean isExecutable(GameState state, Player p) {
        if (target.isOccupied()) return false;
        if (!p.canAfford(Cost.settlementCost())) return false;
        if (!p.getPieces().hasSettlement()) return false;
        // Distance rule: no adjacent node may have a building
        for (Edge e : target.getIncidentEdges()) {
            Node neighbor = e.getOtherNode(target);
            if (neighbor != null && neighbor.isOccupied()) return false;
        }
        // Must be adjacent to player's road
        boolean hasAdjacentRoad = false;
        for (Edge e : target.getIncidentEdges()) {
            if (e.isOccupied() && e.getRoad().getOwner() == p) {
                hasAdjacentRoad = true;
                break;
            }
        }
        return hasAdjacentRoad;
    }

    @Override
    public void execute(GameState state, Player p) {
        Cost cost = Cost.settlementCost();
        p.pay(cost);
        state.getBank().returnCost(cost);
        p.getPieces().takeSettlement();
        Settlement settlement = new Settlement(p, target);
        target.setBuilding(settlement);
        p.addVP(1);
    }

    @Override
    public String describe() {
        StringBuilder sb = new StringBuilder();
        sb.append("builds a settlement at node ").append(target.getId());
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
        sb.append(") [cost: 1 BRICK, 1 LUMBER, 1 WOOL, 1 GRAIN]");
        return sb.toString();
    }
}
