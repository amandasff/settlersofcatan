package catan;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class ActionTest {
    private Board board;
    private ResourceBank bank;
    private Player player;
    private GameState state;

    @Before
    public void setUp() {
        BoardLayout layout = BoardLayout.createStandardLayout();
        board = new Board(layout);
        bank = new ResourceBank();
        Player[] players = { new Player(1, new RandomStrategy()) };
        player = players[0];
        state = new GameState(board, bank, players);
    }

    @Test
    public void testBuildRoadExecuteDeductsResources() {
        // Setup: place a settlement so road can connect
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player, node0));
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.LUMBER, 1);

        Edge edge = node0.getIncidentEdges().get(0);
        BuildRoadAction action = new BuildRoadAction(edge);
        assertTrue(action.isExecutable(state, player));

        action.execute(state, player);

        assertTrue(edge.isOccupied());
        assertEquals(player, edge.getRoad().getOwner());
        assertEquals(0, player.getHand().get(ResourceType.BRICK));
        assertEquals(0, player.getHand().get(ResourceType.LUMBER));
    }

    @Test
    public void testBuildRoadNotExecutableWithoutResources() {
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player, node0));

        Edge edge = node0.getIncidentEdges().get(0);
        BuildRoadAction action = new BuildRoadAction(edge);
        assertFalse(action.isExecutable(state, player));
    }

    @Test
    public void testBuildSettlementExecute() {
        // Place road to a far-enough node
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player, node0));
        player.getPieces().takeSettlement();
        player.addVP(1);

        Edge firstEdge = node0.getIncidentEdges().get(0);
        firstEdge.setRoad(new Road(player, firstEdge));
        player.getPieces().takeRoad();

        Node midNode = firstEdge.getOtherNode(node0);
        for (Edge nextEdge : midNode.getIncidentEdges()) {
            Node target = nextEdge.getOtherNode(midNode);
            if (target != null && target != node0 && !nextEdge.isOccupied()) {
                nextEdge.setRoad(new Road(player, nextEdge));
                player.getPieces().takeRoad();

                // Check distance rule
                boolean valid = true;
                for (Edge adj : target.getIncidentEdges()) {
                    Node neighbor = adj.getOtherNode(target);
                    if (neighbor != null && neighbor.isOccupied()) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    player.addResources(ResourceType.BRICK, 1);
                    player.addResources(ResourceType.LUMBER, 1);
                    player.addResources(ResourceType.WOOL, 1);
                    player.addResources(ResourceType.GRAIN, 1);

                    BuildSettlementAction action = new BuildSettlementAction(target);
                    assertTrue(action.isExecutable(state, player));

                    int vpBefore = player.getVP();
                    action.execute(state, player);

                    assertTrue(target.isOccupied());
                    assertEquals(player, target.getOwner());
                    assertEquals(vpBefore + 1, player.getVP());
                    return;
                }
            }
        }
    }

    @Test
    public void testUpgradeToCityExecute() {
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player, node0));
        player.getPieces().takeSettlement();

        player.addResources(ResourceType.ORE, 3);
        player.addResources(ResourceType.GRAIN, 2);

        UpgradeToCityAction action = new UpgradeToCityAction(node0);
        assertTrue(action.isExecutable(state, player));

        int vpBefore = player.getVP();
        action.execute(state, player);

        assertTrue(node0.getBuilding() instanceof City);
        assertEquals(vpBefore + 1, player.getVP());
        assertEquals(0, player.getHand().get(ResourceType.ORE));
        assertEquals(0, player.getHand().get(ResourceType.GRAIN));
    }

    @Test
    public void testUpgradeToCityReturnsSettlementPiece() {
        Node node0 = board.getNode(0);
        node0.setBuilding(new Settlement(player, node0));
        player.getPieces().takeSettlement();
        int settlementsBefore = player.getPieces().settlementsRemaining();

        player.addResources(ResourceType.ORE, 3);
        player.addResources(ResourceType.GRAIN, 2);

        new UpgradeToCityAction(node0).execute(state, player);

        assertEquals(settlementsBefore + 1, player.getPieces().settlementsRemaining());
    }

    @Test
    public void testCostValues() {
        Cost road = Cost.roadCost();
        assertEquals(1, road.getBrick());
        assertEquals(1, road.getLumber());
        assertEquals(0, road.getWool());
        assertEquals(0, road.getGrain());
        assertEquals(0, road.getOre());

        Cost settlement = Cost.settlementCost();
        assertEquals(1, settlement.getBrick());
        assertEquals(1, settlement.getLumber());
        assertEquals(1, settlement.getWool());
        assertEquals(1, settlement.getGrain());
        assertEquals(0, settlement.getOre());

        Cost city = Cost.cityCost();
        assertEquals(0, city.getBrick());
        assertEquals(0, city.getLumber());
        assertEquals(0, city.getWool());
        assertEquals(2, city.getGrain());
        assertEquals(3, city.getOre());
    }

    @Test
    public void testDiceRollRange() {
        Dice dice = new Dice();
        for (int i = 0; i < 100; i++) {
            int roll = dice.roll();
            assertTrue("Dice roll should be >= 2", roll >= 2);
            assertTrue("Dice roll should be <= 12", roll <= 12);
        }
    }
}
