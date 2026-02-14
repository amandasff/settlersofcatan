package catan;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class DiscardTest {
    private Player player;
    private ResourceBank bank;

    @Before
    public void setUp() {
        player = new Player(1, new RandomStrategy());
        bank = new ResourceBank();
    }

    @Test
    public void testPlayerWith7CardsDoesNotDiscard() {
        // Give player exactly 7 cards
        for (int i = 0; i < 7; i++) {
            player.addResources(ResourceType.BRICK, 1);
        }
        // 7 cards should NOT trigger discard (only >7)
        assertFalse(player.handSize() > 7);
    }

    @Test
    public void testPlayerWith8CardsMusDiscard() {
        for (int i = 0; i < 8; i++) {
            player.addResources(ResourceType.BRICK, 1);
        }
        assertTrue(player.handSize() > 7);
        int toDiscard = player.handSize() / 2; // 8/2 = 4
        assertEquals(4, toDiscard);
    }

    @Test
    public void testDiscardReturnsCardsToBank() {
        // Drain some brick from bank first
        bank.payTo(player, ResourceType.BRICK, 10);
        assertEquals(10, player.getHand().get(ResourceType.BRICK));

        // Simulate discard: remove from player, return to bank
        int toDiscard = player.handSize() / 2; // 5
        for (int i = 0; i < toDiscard; i++) {
            player.getHand().remove(ResourceType.BRICK, 1);
            bank.returnResources(ResourceType.BRICK, 1);
        }

        assertEquals(5, player.getHand().get(ResourceType.BRICK));
        // Bank started with 19, paid 10, got 5 back = 14
        assertTrue(bank.canPay(ResourceType.BRICK, 14));
    }

    @Test
    public void testDiscardHalvesOddHandSize() {
        // 9 cards -> discard 4 (integer division)
        for (int i = 0; i < 9; i++) {
            player.addResources(ResourceType.LUMBER, 1);
        }
        int toDiscard = player.handSize() / 2;
        assertEquals(4, toDiscard);
    }
}
