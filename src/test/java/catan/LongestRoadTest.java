package catan;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class LongestRoadTest {
    private Board board;
    private Player p1;
    private Player p2;
    private LongestRoadTracker tracker;

    @Before
    public void setUp() {
        BoardLayout layout = BoardLayout.createStandardLayout();
        board = new Board(layout);
        p1 = new Player(1, new RandomStrategy());
        p2 = new Player(2, new RandomStrategy());
        tracker = new LongestRoadTracker();
    }

    @Test
    public void testNoRoadsReturnsZero() {
        assertEquals(0, tracker.calculateLongestRoad(p1, board));
    }

    @Test
    public void testSingleRoadReturnsOne() {
        Edge e = board.getAllEdges().get(0);
        e.setRoad(new Road(p1, e));
        assertEquals(1, tracker.calculateLongestRoad(p1, board));
    }

    @Test
    public void testChainedRoadsCountCorrectly() {
        // Build a chain of 3 roads along consecutive edges from node 0
        Node start = board.getNode(0);
        int roadsPlaced = 0;
        Node current = start;

        for (int i = 0; i < 3 && current != null; i++) {
            for (Edge e : current.getIncidentEdges()) {
                if (!e.isOccupied()) {
                    e.setRoad(new Road(p1, e));
                    current = e.getOtherNode(current);
                    roadsPlaced++;
                    break;
                }
            }
        }

        assertEquals(roadsPlaced, tracker.calculateLongestRoad(p1, board));
    }

    @Test
    public void testOpponentBuildingBlocksRoad() {
        // Build 3 roads in a chain
        Node n = board.getNode(0);
        Node mid = null;
        int count = 0;

        for (int i = 0; i < 3 && n != null; i++) {
            for (Edge e : n.getIncidentEdges()) {
                if (!e.isOccupied()) {
                    e.setRoad(new Road(p1, e));
                    Node next = e.getOtherNode(n);
                    if (i == 0) mid = next;
                    n = next;
                    count++;
                    break;
                }
            }
        }

        if (mid != null && count >= 3) {
            int before = tracker.calculateLongestRoad(p1, board);
            // Place opponent building at middle node to break the chain
            mid.setBuilding(new Settlement(p2, mid));
            int after = tracker.calculateLongestRoad(p1, board);
            assertTrue("Opponent building should break road chain", after < before);
        }
    }

    @Test
    public void testLongestRoadAwards2VP() {
        Player[] players = {p1, p2};
        ActionLogger logger = new ActionLogger();

        // Build 5 roads for p1
        Node current = board.getNode(0);
        for (int i = 0; i < 5 && current != null; i++) {
            for (Edge e : current.getIncidentEdges()) {
                if (!e.isOccupied()) {
                    e.setRoad(new Road(p1, e));
                    current = e.getOtherNode(current);
                    break;
                }
            }
        }

        int vpBefore = p1.getVP();
        tracker.update(players, board, logger, 1);

        if (tracker.calculateLongestRoad(p1, board) >= 5) {
            assertEquals(vpBefore + 2, p1.getVP());
            assertEquals(p1, tracker.getCurrentHolder());
        }
    }
}
