package catan;

import java.util.EnumMap;
import java.util.Map;

public final class Cost {
    private final EnumMap<ResourceType, Integer> amounts;

    public Cost() {
        this.amounts = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            amounts.put(type, 0);
        }
    }

    public Cost with(ResourceType type, int amount) {
        amounts.put(type, amount);
        return this;
    }

    public int get(ResourceType type) {
        return amounts.get(type);
    }

    public Map<ResourceType, Integer> getAmounts() {
        return amounts;
    }

    public static Cost roadCost() {
        return new Cost()
                .with(ResourceType.BRICK, 1)
                .with(ResourceType.LUMBER, 1);
    }

    public static Cost settlementCost() {
        return new Cost()
                .with(ResourceType.BRICK, 1)
                .with(ResourceType.LUMBER, 1)
                .with(ResourceType.WOOL, 1)
                .with(ResourceType.GRAIN, 1);
    }

    public static Cost cityCost() {
        return new Cost()
                .with(ResourceType.GRAIN, 2)
                .with(ResourceType.ORE, 3);
    }
}