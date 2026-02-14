package catan;

import java.util.HashSet;
import java.util.Set;

/**
 * Tracks longest road for each player and awards/removes 2 VP accordingly.
 * A player needs at least 5 continuous road segments to claim longest road.
 * Roads are blocked by opponent buildings (settlements/cities) at intermediate nodes.
 */
public class LongestRoadTracker {
    private Player currentHolder;

    public Player getCurrentHolder() { return currentHolder; }

    /**
     * Recalculates longest road for all players and updates VP.
     * Call this after any road is built or settlement/city is placed.
     */
    public void update(Player[] players, Board board, ActionLogger logger, int round) {
        Player newHolder = null;
        int longestLength = 4; // Must be at least 5 to claim

        for (Player p : players) {
            int roadLength = calculateLongestRoad(p, board);
            if (roadLength > longestLength) {
                longestLength = roadLength;
                newHolder = p;
            } else if (roadLength == longestLength && currentHolder != null
                       && currentHolder == p) {
                // Tie goes to current holder
                newHolder = p;
            }
        }

        if (newHolder != currentHolder) {
            if (currentHolder != null) {
                currentHolder.removeVP(2);
                if (round > 0) {
                    logger.logAction(round, currentHolder.getId(),
                        "loses Longest Road (-2 VP)");
                }
            }
            if (newHolder != null) {
                newHolder.addVP(2);
                if (round > 0) {
                    logger.logAction(round, newHolder.getId(),
                        "claims Longest Road! (+2 VP, road length: " + longestLength + ")");
                }
            }
            currentHolder = newHolder;
        }
    }

    /**
     * Calculates the longest continuous road for a player using DFS.
     * Roads are broken at nodes occupied by other players' buildings.
     */
    public int calculateLongestRoad(Player player, Board board) {
        int maxLength = 0;

        for (Edge edge : board.getAllEdges()) {
            if (edge.isOccupied() && edge.getRoad().getOwner() == player) {
                // Try starting DFS from each end of this edge
                Set<Edge> visited = new HashSet<>();
                visited.add(edge);
                int fromA = dfs(player, edge.getA(), visited, board);
                int fromB = dfs(player, edge.getB(), visited, board);
                maxLength = Math.max(maxLength, 1 + fromA + fromB);
                // Also try just from one end
                visited.clear();
                visited.add(edge);
                maxLength = Math.max(maxLength, 1 + dfs(player, edge.getA(), visited, board));
                visited.clear();
                visited.add(edge);
                maxLength = Math.max(maxLength, 1 + dfs(player, edge.getB(), visited, board));
            }
        }

        return maxLength;
    }

    private int dfs(Player player, Node node, Set<Edge> visited, Board board) {
        // Can't pass through opponent buildings
        if (node.isOccupied() && node.getOwner() != player) {
            return 0;
        }

        int maxLength = 0;
        for (Edge e : node.getIncidentEdges()) {
            if (!visited.contains(e) && e.isOccupied() && e.getRoad().getOwner() == player) {
                visited.add(e);
                Node otherNode = e.getOtherNode(node);
                int length = 1 + dfs(player, otherNode, visited, board);
                maxLength = Math.max(maxLength, length);
                visited.remove(e);
            }
        }
        return maxLength;
    }
}
