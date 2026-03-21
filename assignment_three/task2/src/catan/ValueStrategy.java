package catan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public final class ValueStrategy implements AgentStrategy {
    private final List<Value> valueRules = List.of(new VPValue(), new BuildValue(), new SpendValue());
    private final Random rng = new Random();

    @Override
    public Action select(List<Action> options, Player player, GameState state) {
        if (options == null || options.isEmpty()) {
            return new PassAction();
        }

        // if holding more than 7 cards, player must spend first (R3.3)
        if (player.handSize() > 7) {
            List<Action> spending = filterNonPass(options);
            if (!spending.isEmpty()) {
                return pickBest(spending, player);
            }
        }

        // if two road chains are close enough to connect, prioritize roads (R3.3)
        if (hasNearbyRoadGap(player, state)) {
            List<Action> roads = filterRoads(options);
            if (!roads.isEmpty()) {
                return pickBest(roads, player);
            }
        }

        // if an opponent is close to taking longest road, buy a road to extend (R3.3)
        if (longestRoadThreat(player, state)) {
            List<Action> roads = filterRoads(options);
            if (!roads.isEmpty()) {
                return pickBest(roads, player);
            }
        }

        return pickBest(options, player);
    }

    private List<Action> filterNonPass(List<Action> options) {
        List<Action> result = new ArrayList<>();
        for (Action a : options) {
            if (!(a instanceof PassAction)) {
                result.add(a);
            }
        }
        return result;
    }

    private List<Action> filterRoads(List<Action> options) {
        List<Action> result = new ArrayList<>();
        for (Action a : options) {
            if (a instanceof BuildRoadAction) {
                result.add(a);
            }
        }
        return result;
    }

    private Action pickBest(List<Action> options, Player player) {
        double maxScore = -1;
        Action best = null;

        for (Action action : options) {
            double score = 0;
            for (Value rule : valueRules) {
                score += dispatch(rule, action, player);
            }
            if (score > maxScore) {
                maxScore = score;
                best = action;
            }
        }

        if (maxScore == 0) {
            return options.get(rng.nextInt(options.size()));
        }

        return best;
    }

    private double dispatch(Value rule, Action action, Player player) {
        if (action instanceof BuildRoadAction a)       return rule.evaluate(a, player);
        if (action instanceof BuildSettlementAction a) return rule.evaluate(a, player);
        if (action instanceof BuyCardAction a)         return rule.evaluate(a, player);
        if (action instanceof UpgradeToCityAction a)   return rule.evaluate(a, player);
        if (action instanceof PassAction a)            return rule.evaluate(a, player);
        return 0;
    }

    // checks if the player has two separate road chains within 2 edges of each other
    private boolean hasNearbyRoadGap(Player player, GameState state) {
        Board board = state.getBoard();
        List<Set<Integer>> components = findRoadComponents(player, board);
        if (components.size() < 2) {
            return false;
        }
        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                if (componentGap(components.get(i), components.get(j), board) <= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    // splits the player's road network into disconnected groups
    private List<Set<Integer>> findRoadComponents(Player player, Board board) {
        Set<Integer> visited = new HashSet<>();
        List<Set<Integer>> components = new ArrayList<>();

        for (Edge edge : board.getEdges()) {
            if (edge.getRoad() == null || edge.getRoad().getOwnerId() != player.getId()) {
                continue;
            }
            for (int nodeId : new int[]{edge.getNodeAId(), edge.getNodeBId()}) {
                if (!visited.contains(nodeId)) {
                    Set<Integer> component = new HashSet<>();
                    floodFillRoads(nodeId, player.getId(), board, visited, component);
                    components.add(component);
                }
            }
        }

        return components;
    }

    private void floodFillRoads(int nodeId, int playerId, Board board,
                                Set<Integer> visited, Set<Integer> component) {
        visited.add(nodeId);
        component.add(nodeId);
        for (int edgeId : board.getNode(nodeId).getIncidentEdgeIds()) {
            Edge edge = board.getEdge(edgeId);
            if (edge.getRoad() != null && edge.getRoad().getOwnerId() == playerId) {
                int other = edge.getNodeAId() == nodeId ? edge.getNodeBId() : edge.getNodeAId();
                if (!visited.contains(other)) {
                    floodFillRoads(other, playerId, board, visited, component);
                }
            }
        }
    }

    // finds the shortest gap between two road groups, counting only empty edges
    private int componentGap(Set<Integer> setA, Set<Integer> setB, Board board) {
        int minGap = Integer.MAX_VALUE;
        for (int nodeA : setA) {
            for (int nodeB : setB) {
                int gap = edgeDistance(nodeA, nodeB, board, 3);
                if (gap < minGap) {
                    minGap = gap;
                }
            }
        }
        return minGap;
    }

    // counts how many empty edges sit between two nodes, stops early past maxDepth
    private int edgeDistance(int fromId, int toId, Board board, int maxDepth) {
        if (fromId == toId) {
            return 0;
        }
        Set<Integer> visited = new HashSet<>();
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{fromId, 0});
        visited.add(fromId);

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int nodeId = curr[0];
            int depth = curr[1];
            if (depth >= maxDepth) {
                continue;
            }
            for (int edgeId : board.getNode(nodeId).getIncidentEdgeIds()) {
                Edge edge = board.getEdge(edgeId);
                if (edge.isOccupied()) {
                    continue;
                }
                int next = edge.getNodeAId() == nodeId ? edge.getNodeBId() : edge.getNodeAId();
                if (next == toId) {
                    return depth + 1;
                }
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(new int[]{next, depth + 1});
                }
            }
        }

        return Integer.MAX_VALUE;
    }

    // checks if any opponent is within 1 road of matching the player's longest road
    private boolean longestRoadThreat(Player player, GameState state) {
        int myRoad = longestRoad(player, state.getBoard());
        if (myRoad == 0) {
            return false;
        }
        for (Player other : state.getPlayers()) {
            if (other.getId() == player.getId()) {
                continue;
            }
            if (longestRoad(other, state.getBoard()) >= myRoad - 1) {
                return true;
            }
        }
        return false;
    }

    private int longestRoad(Player player, Board board) {
        int max = 0;
        for (Edge edge : board.getEdges()) {
            if (edge.getRoad() == null || edge.getRoad().getOwnerId() != player.getId()) {
                continue;
            }
            Set<Integer> visited = new HashSet<>();
            max = Math.max(max, dfsRoad(edge.getNodeAId(), -1, visited, player.getId(), board));
            visited.clear();
            max = Math.max(max, dfsRoad(edge.getNodeBId(), -1, visited, player.getId(), board));
        }
        return max;
    }

    private int dfsRoad(int nodeId, int fromEdgeId, Set<Integer> visitedEdges,
                        int playerId, Board board) {
        Node node = board.getNode(nodeId);
        if (node.getBuilding() != null && node.getBuilding().getOwnerId() != playerId) {
            return 0;
        }
        int max = 0;
        for (int edgeId : node.getIncidentEdgeIds()) {
            if (edgeId == fromEdgeId || visitedEdges.contains(edgeId)) {
                continue;
            }
            Edge edge = board.getEdge(edgeId);
            if (edge.getRoad() == null || edge.getRoad().getOwnerId() != playerId) {
                continue;
            }
            visitedEdges.add(edgeId);
            int other = edge.getNodeAId() == nodeId ? edge.getNodeBId() : edge.getNodeAId();
            int len = 1 + dfsRoad(other, edgeId, visitedEdges, playerId, board);
            max = Math.max(max, len);
            visitedEdges.remove(edgeId);
        }
        return max;
    }
}
