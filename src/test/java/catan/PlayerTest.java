package catan;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class PlayerTest {
    private Player player;

    @Before
    public void setUp() {
        player = new Player(1, new RandomStrategy());
    }

    @Test
    public void testPlayerStartsWithZeroVP() {
        assertEquals(0, player.getVP());
    }

    @Test
    public void testAddVP() {
        player.addVP(3);
        assertEquals(3, player.getVP());
    }

    @Test
    public void testRemoveVP() {
        player.addVP(5);
        player.removeVP(2);
        assertEquals(3, player.getVP());
    }

    @Test
    public void testPlayerStartsWithEmptyHand() {
        assertEquals(0, player.handSize());
    }

    @Test
    public void testAddResources() {
        player.addResources(ResourceType.BRICK, 3);
        assertEquals(3, player.getHand().get(ResourceType.BRICK));
        assertEquals(3, player.handSize());
    }

    @Test
    public void testCanAffordRoad() {
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.LUMBER, 1);
        assertTrue(player.canAfford(Cost.roadCost()));
    }

    @Test
    public void testCannotAffordRoad() {
        player.addResources(ResourceType.BRICK, 1);
        assertFalse(player.canAfford(Cost.roadCost()));
    }

    @Test
    public void testCanAffordSettlement() {
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.LUMBER, 1);
        player.addResources(ResourceType.WOOL, 1);
        player.addResources(ResourceType.GRAIN, 1);
        assertTrue(player.canAfford(Cost.settlementCost()));
    }

    @Test
    public void testCanAffordCity() {
        player.addResources(ResourceType.ORE, 3);
        player.addResources(ResourceType.GRAIN, 2);
        assertTrue(player.canAfford(Cost.cityCost()));
    }

    @Test
    public void testPayReducesHand() {
        player.addResources(ResourceType.BRICK, 2);
        player.addResources(ResourceType.LUMBER, 1);
        player.pay(Cost.roadCost());
        assertEquals(1, player.getHand().get(ResourceType.BRICK));
        assertEquals(0, player.getHand().get(ResourceType.LUMBER));
    }

    @Test
    public void testPiecePoolStartCounts() {
        assertEquals(15, player.getPieces().roadsRemaining());
        assertEquals(5, player.getPieces().settlementsRemaining());
        assertEquals(4, player.getPieces().citiesRemaining());
    }

    @Test
    public void testTakePiecesDecrementsCount() {
        player.getPieces().takeRoad();
        assertEquals(14, player.getPieces().roadsRemaining());
        player.getPieces().takeSettlement();
        assertEquals(4, player.getPieces().settlementsRemaining());
        player.getPieces().takeCity();
        assertEquals(3, player.getPieces().citiesRemaining());
    }
}
