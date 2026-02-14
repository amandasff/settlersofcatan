package catan;

public class BuildRoadAction implements Action {
    private final Edge target;

    public BuildRoadAction(Edge target) {
        this.target = target;
    }

    @Override
    public boolean isExecutable(GameState state, Player p) {
        if (target.isOccupied()) return false;
        if (!p.canAfford(Cost.roadCost())) return false;
        if (!p.getPieces().hasRoad()) return false;
        // Must be adjacent to player's existing road, settlement, or city
        Node a = target.getA();
        Node b = target.getB();
        return playerHasAdjacentStructure(a, p) || playerHasAdjacentStructure(b, p);
    }

    private boolean playerHasAdjacentStructure(Node node, Player p) {
        // Player's own building on this node allows connection
        if (node.isOccupied() && node.getOwner() == p) return true;
        // An opponent's building on this node blocks road extension through it
        if (node.isOccupied() && node.getOwner() != p) return false;
        // Otherwise, check if player has a road on an adjacent edge
        for (Edge e : node.getIncidentEdges()) {
            if (e.isOccupied() && e.getRoad().getOwner() == p) return true;
        }
        return false;
    }

    @Override
    public void execute(GameState state, Player p) {
        Cost cost = Cost.roadCost();
        p.pay(cost);
        state.getBank().returnCost(cost);
        p.getPieces().takeRoad();
        Road road = new Road(p, target);
        target.setRoad(road);
    }

    @Override
    public String describe() {
        return "builds a road between node " + target.getA().getId()
            + " and node " + target.getB().getId()
            + " [cost: 1 BRICK, 1 LUMBER]";
    }
}
