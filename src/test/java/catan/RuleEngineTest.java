package catan;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class RuleEngineTest {
    private Board board;
    private RuleEngine rules;
    private Player player1;
    private Player player2;

    @Before
    public void setUp() {
        BoardLayout layout = BoardLayout.createStandardLayout();
        board = new Board(layout);
        rules = new RuleEngine(board);
        player1 = new Player(1, new RandomStrategy());
        player2 = new Player(2, new RandomStrategy());
    }

    @Test
    public void testCanBuildRoadOnEmptyEdge() {
        // Place a settlement for player1 at node 0
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player1, node0));
        player1.getPieces().takeSettlement();

        // Player should be able to build a road on an adjacent edge
        Edge adjEdge = node0.getIncidentEdges().get(0);
        assertTrue(rules.canBuildRoad(player1, adjEdge));
    }

    @Test
    public void testCannotBuildRoadOnOccupiedEdge() {
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player1, node0));

        Edge edge = node0.getIncidentEdges().get(0);
        edge.setRoad(new Road(player1, edge));
        player1.getPieces().takeRoad();

        assertFalse(rules.canBuildRoad(player1, edge));
    }

    @Test
    public void testCannotBuildRoadWithoutConnection() {
        // Pick an edge far from any player structure
        Edge edge = board.getEdge(30, 35);
        if (edge != null) {
            assertFalse(rules.canBuildRoad(player1, edge));
        }
    }

    @Test
    public void testCanBuildRoadExtendingFromRoad() {
        // Place settlement + road for player1
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player1, node0));
        Edge firstEdge = node0.getIncidentEdges().get(0);
        firstEdge.setRoad(new Road(player1, firstEdge));
        player1.getPieces().takeRoad();

        // Find an edge adjacent to the other end of the first road
        Node otherEnd = firstEdge.getOtherNode(node0);
        for (Edge nextEdge : otherEnd.getIncidentEdges()) {
            if (nextEdge != firstEdge && !nextEdge.isOccupied()) {
                assertTrue(rules.canBuildRoad(player1, nextEdge));
                return;
            }
        }
    }

    @Test
    public void testOpponentSettlementBlocksRoadExtension() {
        // Player 1 places settlement at node 0 and road
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player1, node0));
        Edge firstEdge = node0.getIncidentEdges().get(0);
        firstEdge.setRoad(new Road(player1, firstEdge));

        // Player 2 places settlement at the other end of player1's road
        Node otherEnd = firstEdge.getOtherNode(node0);
        otherEnd.setBuilding(new Settlement(player2, otherEnd));

        // Player 1 should NOT be able to extend road through player2's settlement
        for (Edge nextEdge : otherEnd.getIncidentEdges()) {
            if (nextEdge != firstEdge && !nextEdge.isOccupied()) {
                assertFalse("Road should not extend through opponent settlement",
                    rules.canBuildRoad(player1, nextEdge));
            }
        }
    }

    @Test
    public void testCanBuildSettlementWithRoadAndDistanceRule() {
        // Place a road from node 0 to node 1
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player1, node0));
        Edge edge01 = board.getEdge(0, 1);
        if (edge01 != null) {
            edge01.setRoad(new Road(player1, edge01));
        }

        // Extend road further to get far enough from node 0
        Node node1 = board.getNode(1);
        for (Edge e : node1.getIncidentEdges()) {
            Node other = e.getOtherNode(node1);
            if (other != null && other.getId() != 0 && !e.isOccupied()) {
                e.setRoad(new Road(player1, e));
                // Check if the far node satisfies distance rule
                boolean tooClose = false;
                for (Edge adj : other.getIncidentEdges()) {
                    Node neighbor = adj.getOtherNode(other);
                    if (neighbor != null && neighbor.isOccupied()) {
                        tooClose = true;
                        break;
                    }
                }
                if (!tooClose) {
                    // Give player resources to afford settlement
                    player1.addResources(ResourceType.BRICK, 1);
                    player1.addResources(ResourceType.LUMBER, 1);
                    player1.addResources(ResourceType.WOOL, 1);
                    player1.addResources(ResourceType.GRAIN, 1);
                    assertTrue(rules.canBuildSettlement(player1, other));
                }
                return;
            }
        }
    }

    @Test
    public void testCannotBuildSettlementOnOccupiedNode() {
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player1, node0));
        assertFalse(rules.canBuildSettlement(player2, node0));
    }

    @Test
    public void testDistanceRulePreventsAdjacentSettlement() {
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player1, node0));

        // Node 1 is adjacent to node 0 — should fail distance rule
        Node node1 = board.getNode(1);
        Edge edge01 = board.getEdge(0, 1);
        if (edge01 != null) {
            edge01.setRoad(new Road(player1, edge01));
            assertFalse(rules.canBuildSettlement(player1, node1));
        }
    }

    @Test
    public void testCanUpgradeOwnSettlementToCity() {
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player1, node0));
        assertTrue(rules.canUpgradeToCity(player1, node0));
    }

    @Test
    public void testCannotUpgradeOpponentSettlement() {
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player1, node0));
        assertFalse(rules.canUpgradeToCity(player2, node0));
    }

    @Test
    public void testCannotUpgradeCityAgain() {
        Node node0 = board.getNode(0);
        node0.setBuilding(new City(player1, node0));
        assertFalse(rules.canUpgradeToCity(player1, node0));
    }

    @Test
    public void testCannotUpgradeEmptyNode() {
        Node node0 = board.getNode(0);
        assertFalse(rules.canUpgradeToCity(player1, node0));
    }
}
