package catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Handles initial placement and starting resources.
 *
 * This implementation performs two settlement+road placements per player
 * in snake order:
 *   0,1,2,3,3,2,1,0
 *
 * The second settlement grants starting resources from adjacent non-desert tiles.
 */
public final class SetupManager {
    private final RuleEngine rules;
    private final Random rng;

    public SetupManager(RuleEngine rules) {
        this.rules = rules;
        this.rng = new Random();
    }

    public SetupManager(RuleEngine rules, long seed) {
        this.rules = rules;
        this.rng = new Random(seed);
    }

    public void placeInitialPieces(GameState state) {
        Player[] players = state.getPlayers();

        int[] order = {0, 1, 2, 3, 3, 2, 1, 0};

        for (int i = 0; i < order.length; i++) {
            Player player = players[order[i]];
            boolean giveStartingResources = (i >= 4);

            Node settlementNode = chooseInitialSettlementNode(state);
            placeFreeSettlement(player, settlementNode);

            Edge roadEdge = chooseInitialRoadEdge(state, settlementNode);
            placeFreeRoad(player, roadEdge);

            if (giveStartingResources) {
                grantStartingResources(state, player, settlementNode);
            }
        }
    }

    private Node chooseInitialSettlementNode(GameState state) {
        List<Node> legalNodes = new ArrayList<>();

        for (Node node : state.getBoard().getNodes()) {
            if (rules.canPlaceInitialSettlement(state.getBoard(), node)) {
                legalNodes.add(node);
            }
        }

        if (legalNodes.isEmpty()) {
            throw new IllegalStateException("No legal initial settlement positions available.");
        }

        return legalNodes.get(rng.nextInt(legalNodes.size()));
    }

    private Edge chooseInitialRoadEdge(GameState state, Node settlementNode) {
        List<Edge> legalEdges = new ArrayList<>();

        for (int edgeId : settlementNode.getIncidentEdgeIds()) {
            Edge edge = state.getBoard().getEdge(edgeId);
            if (!edge.isOccupied()) {
                legalEdges.add(edge);
            }
        }

        if (legalEdges.isEmpty()) {
            throw new IllegalStateException("No legal initial road positions available.");
        }

        return legalEdges.get(rng.nextInt(legalEdges.size()));
    }

    private void placeFreeSettlement(Player player, Node node) {
        player.getPieces().takeSettlement();
        Settlement settlement = new Settlement(player.getId(), node.getId());
        node.setBuilding(settlement);
        player.addVictoryPoints(1);
    }

    private void placeFreeRoad(Player player, Edge edge) {
        player.getPieces().takeRoad();
        edge.setRoad(new Road(player.getId(), edge.getId()));
    }

    private void grantStartingResources(GameState state, Player player, Node node) {
        for (int tileId : node.getAdjacentTileIds()) {
            Tile tile = state.getBoard().getTile(tileId);
            ResourceType type = terrainToResource(tile.getTerrain());
            if (type != null) {
                player.getHand().add(type, 1);
            }
        }
    }

    private ResourceType terrainToResource(TerrainType terrain) {
        switch (terrain) {
            case HILLS:
                return ResourceType.BRICK;
            case FOREST:
                return ResourceType.LUMBER;
            case PASTURE:
                return ResourceType.WOOL;
            case FIELDS:
                return ResourceType.GRAIN;
            case MOUNTAINS:
                return ResourceType.ORE;
            case DESERT:
            default:
                return null;
        }
    }
}