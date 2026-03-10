package catan;

/**
 * Central legality checker for simulator actions.
 *
 * Implemented invariants (R1.6):
 * 1. A road may only be built on an unoccupied edge.
 * 2. A road must connect to the current player's existing road network or building.
 * 3. A settlement may only be built on an unoccupied node.
 * 4. Adjacent nodes to a new settlement must be unoccupied (distance rule).
 * 5. A normal settlement build must connect to one of the current player's roads.
 * 6. A city upgrade may only replace an existing settlement owned by the same player.
 * 7. Players must be able to afford the action cost and still have the required remaining pieces.
 */
public final class RuleEngine {

    public boolean canPlaceInitialSettlement(Board board, Node target) {
        if (target == null || target.isOccupied()) {
            return false;
        }

        for (int adjacentNodeId : target.getAdjacentNodeIds()) {
            if (board.getNode(adjacentNodeId).isOccupied()) {
                return false;
            }
        }

        return true;
    }

    public boolean canBuildSettlement(GameState state, Player player, Node target) {
        if (state == null || player == null || target == null) {
            return false;
        }

        if (target.isOccupied()) {
            return false;
        }

        if (!player.getPieces().hasSettlement()) {
            return false;
        }

        if (!player.canAfford(Cost.settlementCost())) {
            return false;
        }

        // Distance rule
        for (int adjacentNodeId : target.getAdjacentNodeIds()) {
            if (state.getBoard().getNode(adjacentNodeId).isOccupied()) {
                return false;
            }
        }

        // Normal settlement must connect to player's existing road
        for (int edgeId : target.getIncidentEdgeIds()) {
            Edge edge = state.getBoard().getEdge(edgeId);
            if (edge.getRoad() != null && edge.getRoad().getOwnerId() == player.getId()) {
                return true;
            }
        }

        return false;
    }

    public boolean canBuildRoad(GameState state, Player player, Edge target) {
        if (state == null || player == null || target == null) {
            return false;
        }

        if (target.isOccupied()) {
            return false;
        }

        if (!player.getPieces().hasRoad()) {
            return false;
        }

        if (!player.canAfford(Cost.roadCost())) {
            return false;
        }

        return touchesPlayerNetwork(state, player, target);
    }

    public boolean canUpgradeToCity(GameState state, Player player, Node target) {
        if (state == null || player == null || target == null) {
            return false;
        }

        if (!player.getPieces().hasCity()) {
            return false;
        }

        if (!player.canAfford(Cost.cityCost())) {
            return false;
        }

        if (!target.isOccupied()) {
            return false;
        }

        if (!(target.getBuilding() instanceof Settlement)) {
            return false;
        }

        return target.getBuilding().getOwnerId() == player.getId();
    }

    private boolean touchesPlayerNetwork(GameState state, Player player, Edge target) {
        Node a = state.getBoard().getNode(target.getNodeAId());
        Node b = state.getBoard().getNode(target.getNodeBId());

        // Road may connect to player's building
        if (a.getBuilding() != null && a.getBuilding().getOwnerId() == player.getId()) {
            return true;
        }
        if (b.getBuilding() != null && b.getBuilding().getOwnerId() == player.getId()) {
            return true;
        }

        // Road may connect to player's existing road network
        for (int edgeId : a.getIncidentEdgeIds()) {
            Edge edge = state.getBoard().getEdge(edgeId);
            if (edge.getRoad() != null && edge.getRoad().getOwnerId() == player.getId()) {
                return true;
            }
        }

        for (int edgeId : b.getIncidentEdgeIds()) {
            Edge edge = state.getBoard().getEdge(edgeId);
            if (edge.getRoad() != null && edge.getRoad().getOwnerId() == player.getId()) {
                return true;
            }
        }

        return false;
    }
}