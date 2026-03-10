package catan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CostTest {


    @Test
    void settlementCostHasCorrectResources() {
        Cost cost = Cost.settlementCost();

        assertEquals(1, cost.get(ResourceType.BRICK));
        assertEquals(1, cost.get(ResourceType.LUMBER));
        assertEquals(1, cost.get(ResourceType.WOOL));
        assertEquals(1, cost.get(ResourceType.GRAIN));
        assertEquals(0, cost.get(ResourceType.ORE));
    }

    @Test
    void cityCostHasCorrectResources() {
        Cost cost = Cost.cityCost();

        assertEquals(0, cost.get(ResourceType.BRICK));
        assertEquals(0, cost.get(ResourceType.LUMBER));
        assertEquals(0, cost.get(ResourceType.WOOL));
        assertEquals(2, cost.get(ResourceType.GRAIN));
        assertEquals(3, cost.get(ResourceType.ORE));
    }
    
}