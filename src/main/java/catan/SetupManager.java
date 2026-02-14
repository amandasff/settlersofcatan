package catan;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class SetupManager {

    public void placeInitialPieces(Player[] players, Board board, RuleEngine rules,
                                    ResourceBank bank, ActionLogger logger) {
        logger.logSectionHeader("SETUP PHASE");

        List<Node> availableNodes = getAvailableSetupNodes(board);

        // First round
        for (int i = 0; i < players.length; i++) {
            placeInitialSettlementAndRoad(players[i], board, bank, availableNodes, false, logger);
        }

        // Second round (reverse order)
        for (int i = players.length - 1; i >= 0; i--) {
            placeInitialSettlementAndRoad(players[i], board, bank, availableNodes, true, logger);
        }

        System.out.println();
    }

    private void placeInitialSettlementAndRoad(Player player, Board board, ResourceBank bank,
                                                List<Node> availableNodes,
                                                boolean giveResources, ActionLogger logger) {
        // Randomly pick a valid node (R1.2: randomly acting agents)
        Node chosenNode = pickRandomValidNode(availableNodes, board);

        if (chosenNode == null) return;

        // Place settlement (free during setup)
        Settlement settlement = new Settlement(player, chosenNode);
        chosenNode.setBuilding(settlement);
        player.getPieces().takeSettlement();
        player.addVP(1);

        // Log settlement placement with terrain context
        StringBuilder desc = new StringBuilder();
        desc.append("places settlement at node ").append(chosenNode.getId());
        desc.append(" (adjacent to ");
        boolean first = true;
        for (Tile t : chosenNode.getAdjacentTiles()) {
            if (!first) desc.append(", ");
            first = false;
            desc.append(t.getTerrain());
            if (t.getTerrain() != TerrainType.DESERT) {
                desc.append("/").append(t.getToken());
            }
        }
        desc.append(")");
        logger.logSetupPlacement(player.getId(), desc.toString());

        // Remove this node and adjacent nodes from available list (distance rule)
        availableNodes.remove(chosenNode);
        for (Edge e : chosenNode.getIncidentEdges()) {
            Node neighbor = e.getOtherNode(chosenNode);
            if (neighbor != null) {
                availableNodes.remove(neighbor);
            }
        }

        // Place a road on a random adjacent empty edge
        List<Edge> emptyEdges = new ArrayList<>();
        for (Edge e : chosenNode.getIncidentEdges()) {
            if (!e.isOccupied()) {
                emptyEdges.add(e);
            }
        }
        if (!emptyEdges.isEmpty()) {
            Collections.shuffle(emptyEdges);
            Edge chosenEdge = emptyEdges.get(0);
            Road road = new Road(player, chosenEdge);
            chosenEdge.setRoad(road);
            player.getPieces().takeRoad();
            logger.logSetupPlacement(player.getId(),
                "places road between node " + chosenEdge.getA().getId() + " and node " + chosenEdge.getB().getId());
        }

        // On second placement, give starting resources from adjacent tiles
        if (giveResources) {
            StringBuilder resDesc = new StringBuilder();
            boolean firstRes = true;
            for (Tile tile : chosenNode.getAdjacentTiles()) {
                ResourceType resource = tile.getProducedResource();
                if (resource != null) {
                    if (bank.canPay(resource, 1)) {
                        bank.payTo(player, resource, 1);
                    }
                    if (!firstRes) resDesc.append(", ");
                    firstRes = false;
                    resDesc.append("1 ").append(resource);
                }
            }
            if (resDesc.length() > 0) {
                logger.logSetupPlacement(player.getId(),
                    "receives starting resources: " + resDesc.toString());
            }
        }
    }

    private Node pickRandomValidNode(List<Node> availableNodes, Board board) {
        Collections.shuffle(availableNodes);
        for (Node node : availableNodes) {
            if (isValidSetupNode(node, board)) {
                return node;
            }
        }
        return null;
    }

    private boolean isValidSetupNode(Node node, Board board) {
        if (node.isOccupied()) return false;
        for (Edge e : node.getIncidentEdges()) {
            Node neighbor = e.getOtherNode(node);
            if (neighbor != null && neighbor.isOccupied()) return false;
        }
        return true;
    }

    private List<Node> getAvailableSetupNodes(Board board) {
        List<Node> nodes = new ArrayList<>();
        for (Node n : board.getNodes()) {
            if (n != null) nodes.add(n);
        }
        return nodes;
    }
}
