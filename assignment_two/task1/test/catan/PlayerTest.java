package catan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    void constructorSetsIdCorrectly() {
        Player player = Player.randomAgent(7);
        assertEquals(7, player.getId());
    }

    @Test
    void newPlayerStartsWithZeroVictoryPoints() {
        Player player = Player.randomAgent(0);
        assertEquals(0, player.getVictoryPoints());
    }

    @Test
    void addVictoryPointsIncreasesTotal() {
        Player player = Player.randomAgent(1);
        player.addVictoryPoints(2);
        assertEquals(2, player.getVictoryPoints());
    }

    @Test
    void addVictoryPointsAccumulatesAcrossMultipleCalls() {
        Player player = Player.randomAgent(2);
        player.addVictoryPoints(1);
        player.addVictoryPoints(3);
        assertEquals(4, player.getVictoryPoints());
    }

    @Test
    void handSizeStartsAtZero() {
        Player player = Player.randomAgent(3);
        assertEquals(0, player.handSize());
    }

    @Test
    void canAffordRoadCostIsFalseForEmptyHand() {
        Player player = Player.randomAgent(4);
        assertFalse(player.canAfford(Cost.roadCost()));
    }

    @Test
    void canAffordSettlementCostIsFalseForEmptyHand() {
        Player player = Player.randomAgent(5);
        assertFalse(player.canAfford(Cost.settlementCost()));
    }

    @Test
    void randomAgentFactoryCreatesNonNullStrategy() {
        Player player = Player.randomAgent(6);
        assertNotNull(player.getStrategy());
    }

    @Test
    void randomAgentSeededFactoryCreatesNonNullStrategy() {
        Player player = Player.randomAgent(8, 12345L);
        assertNotNull(player.getStrategy());
    }

    @Test
    void playerStartsWithNonNullHandAndPieces() {
        Player player = Player.randomAgent(9);
        assertNotNull(player.getHand());
        assertNotNull(player.getPieces());
    }
}