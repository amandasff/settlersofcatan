package catan;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class BoardTest {
    private Board board;

    @Before
    public void setUp() {
        BoardLayout layout = BoardLayout.createStandardLayout();
        board = new Board(layout);
    }

    @Test
    public void testBoardHas19Tiles() {
        int count = 0;
        for (Tile t : board.getTiles()) {
            if (t != null) count++;
        }
        assertEquals(19, count);
    }

    @Test
    public void testBoardHas54Nodes() {
        int count = 0;
        for (Node n : board.getNodes()) {
            if (n != null) count++;
        }
        assertEquals(54, count);
    }

    @Test
    public void testBoardHas72Edges() {
        assertEquals(72, board.getAllEdges().size());
    }

    @Test
    public void testTileHasSixCornerNodes() {
        Tile tile = board.getTile(0);
        assertEquals(6, tile.getCornerNodes().size());
    }

    @Test
    public void testEdgeLookupByNodeIds() {
        // Use the first edge from the board's edge list
        Edge known = board.getAllEdges().get(0);
        int idA = known.getA().getId();
        int idB = known.getB().getId();
        Edge found = board.getEdge(idA, idB);
        assertNotNull(found);
        assertSame(known, found);
    }

    @Test
    public void testEdgeLookupReversed() {
        // Looking up (b,a) should give the same edge as (a,b)
        Edge known = board.getAllEdges().get(0);
        int idA = known.getA().getId();
        int idB = known.getB().getId();
        Edge e1 = board.getEdge(idA, idB);
        Edge e2 = board.getEdge(idB, idA);
        assertSame(e1, e2);
    }

    @Test
    public void testNodesStartUnoccupied() {
        for (Node n : board.getNodes()) {
            if (n != null) {
                assertFalse(n.isOccupied());
                assertNull(n.getOwner());
            }
        }
    }

    @Test
    public void testEdgesStartUnoccupied() {
        for (Edge e : board.getAllEdges()) {
            assertFalse(e.isOccupied());
            assertNull(e.getRoad());
        }
    }

    @Test
    public void testDesertTileExists() {
        boolean found = false;
        for (Tile t : board.getTiles()) {
            if (t != null && t.getTerrain() == TerrainType.DESERT) {
                found = true;
                break;
            }
        }
        assertTrue("Board should contain a desert tile", found);
    }

    @Test
    public void testDesertProducesNothing() {
        for (Tile t : board.getTiles()) {
            if (t != null && t.getTerrain() == TerrainType.DESERT) {
                assertNull(t.getProducedResource());
                assertFalse(t.producesOn(7));
                assertFalse(t.producesOn(2));
            }
        }
    }

    @Test
    public void testTerrainResourceMapping() {
        assertEquals(ResourceType.BRICK, new Tile(0, TerrainType.HILLS, 5).getProducedResource());
        assertEquals(ResourceType.LUMBER, new Tile(0, TerrainType.FOREST, 5).getProducedResource());
        assertEquals(ResourceType.ORE, new Tile(0, TerrainType.MOUNTAINS, 5).getProducedResource());
        assertEquals(ResourceType.GRAIN, new Tile(0, TerrainType.FIELDS, 5).getProducedResource());
        assertEquals(ResourceType.WOOL, new Tile(0, TerrainType.PASTURE, 5).getProducedResource());
    }

    @Test
    public void testTileProducesOnCorrectRoll() {
        Tile tile = new Tile(0, TerrainType.FIELDS, 8);
        assertTrue(tile.producesOn(8));
        assertFalse(tile.producesOn(6));
    }
}
