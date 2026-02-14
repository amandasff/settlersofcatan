package catan;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class ResourceBankTest {
    private ResourceBank bank;
    private Player player;

    @Before
    public void setUp() {
        bank = new ResourceBank();
        player = new Player(1, new RandomStrategy());
    }

    @Test
    public void testBankStartsWith19OfEach() {
        assertTrue(bank.canPay(ResourceType.BRICK, 19));
        assertFalse(bank.canPay(ResourceType.BRICK, 20));
    }

    @Test
    public void testPayToTransfersResource() {
        bank.payTo(player, ResourceType.LUMBER, 3);
        assertEquals(3, player.getHand().get(ResourceType.LUMBER));
        assertTrue(bank.canPay(ResourceType.LUMBER, 16));
        assertFalse(bank.canPay(ResourceType.LUMBER, 17));
    }

    @Test
    public void testPayToDoesNothingWhenBankEmpty() {
        // Drain the bank
        Player dummy = new Player(99, new RandomStrategy());
        bank.payTo(dummy, ResourceType.ORE, 19);
        // Now try to pay from empty bank
        bank.payTo(player, ResourceType.ORE, 1);
        assertEquals(0, player.getHand().get(ResourceType.ORE));
    }

    @Test
    public void testReturnResources() {
        bank.payTo(player, ResourceType.WOOL, 5);
        bank.returnResources(ResourceType.WOOL, 5);
        assertTrue(bank.canPay(ResourceType.WOOL, 19));
    }

    @Test
    public void testReturnCost() {
        Cost roadCost = Cost.roadCost();
        bank.returnCost(roadCost);
        // Bank should now have 20 brick and 20 lumber
        assertTrue(bank.canPay(ResourceType.BRICK, 20));
        assertTrue(bank.canPay(ResourceType.LUMBER, 20));
    }
}
