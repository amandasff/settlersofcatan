package catan;

public class RuleEngine {
    private final Board board;

    public RuleEngine(Board board) {
        this.board = board;
    }

    public boolean canBuildRoad(Player p, Edge e) {
        if (e.isOccupied()) return false;
        if (!p.getPieces().hasRoad()) return false;
        // Must connect to player's existing structure or road
        Node a = e.getA();
        Node b = e.getB();
        return hasConnection(a, p) || hasConnection(b, p);
    }

    public boolean canBuildSettlement(Player p, Node n) {
        if (n.isOccupied()) return false;
        if (!p.getPieces().hasSettlement()) return false;
        // Distance rule
        for (Edge e : n.getIncidentEdges()) {
            Node neighbor = e.getOtherNode(n);
            if (neighbor != null && neighbor.isOccupied()) return false;
        }
        // Must be connected to player's road
        for (Edge e : n.getIncidentEdges()) {
            if (e.isOccupied() && e.getRoad().getOwner() == p) return true;
        }
        return false;
    }

    public boolean canUpgradeToCity(Player p, Node n) {
        if (!n.isOccupied()) return false;
        if (n.getOwner() != p) return false;
        if (!(n.getBuilding() instanceof Settlement)) return false;
        if (!p.getPieces().hasCity()) return false;
        return true;
    }

    private boolean hasConnection(Node node, Player p) {
        // Player's own building on this node allows connection
        if (node.isOccupied() && node.getOwner() == p) return true;
        // An opponent's building on this node blocks road extension through it
        if (node.isOccupied() && node.getOwner() != p) return false;
        // Otherwise, check if player has an existing road touching this node
        for (Edge e : node.getIncidentEdges()) {
            if (e.isOccupied() && e.getRoad().getOwner() == p) return true;
        }
        return false;
    }
}
