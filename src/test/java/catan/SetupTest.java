package catan;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class SetupTest {
    private Board board;
    private Player[] players;
    private ResourceBank bank;
    private RuleEngine rules;
    private ActionLogger logger;

    @Before
    public void setUp() {
        BoardLayout layout = BoardLayout.createStandardLayout();
        board = new Board(layout);
        bank = new ResourceBank();
        rules = new RuleEngine(board);
        logger = new ActionLogger();
        players = new Player[4];
        for (int i = 0; i < 4; i++) {
            players[i] = new Player(i + 1, new RandomStrategy());
        }
    }

    @Test
    public void testEachPlayerGets2Settlements() {
        SetupManager setup = new SetupManager();
        setup.placeInitialPieces(players, board, rules, bank, logger);

        for (Player p : players) {
            // Started with 5 settlements, should have used 2
            assertEquals(3, p.getPieces().settlementsRemaining());
        }
    }

    @Test
    public void testEachPlayerGets2Roads() {
        SetupManager setup = new SetupManager();
        setup.placeInitialPieces(players, board, rules, bank, logger);

        for (Player p : players) {
            // Started with 15 roads, should have used 2
            assertEquals(13, p.getPieces().roadsRemaining());
        }
    }

    @Test
    public void testEachPlayerStarts2VP() {
        SetupManager setup = new SetupManager();
        setup.placeInitialPieces(players, board, rules, bank, logger);

        for (Player p : players) {
            assertEquals(2, p.getVP());
        }
    }

    @Test
    public void testDistanceRuleEnforcedDuringSetup() {
        SetupManager setup = new SetupManager();
        setup.placeInitialPieces(players, board, rules, bank, logger);

        // Check that no two buildings are on adjacent nodes
        for (Node n : board.getNodes()) {
            if (n != null && n.isOccupied()) {
                for (Edge e : n.getIncidentEdges()) {
                    Node neighbor = e.getOtherNode(n);
                    if (neighbor != null) {
                        assertFalse("Adjacent nodes should not both have buildings",
                            neighbor.isOccupied());
                    }
                }
            }
        }
    }

    @Test
    public void testSecondPlacementGivesResources() {
        SetupManager setup = new SetupManager();
        setup.placeInitialPieces(players, board, rules, bank, logger);

        // At least some players should have received starting resources
        int totalCards = 0;
        for (Player p : players) {
            totalCards += p.handSize();
        }
        assertTrue("Players should have received some starting resources", totalCards > 0);
    }
}
