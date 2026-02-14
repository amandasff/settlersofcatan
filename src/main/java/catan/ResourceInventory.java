package catan;

import java.util.EnumMap;
import java.util.Map;

public class ResourceInventory {
    private final Map<ResourceType, Integer> counts;

    public ResourceInventory() {
        counts = new EnumMap<>(ResourceType.class);
        for (ResourceType r : ResourceType.values()) {
            counts.put(r, 0);
        }
    }

    public void add(ResourceType type, int amount) {
        counts.put(type, counts.get(type) + amount);
    }

    public void remove(ResourceType type, int amount) {
        counts.put(type, counts.get(type) - amount);
    }

    public int get(ResourceType type) {
        return counts.get(type);
    }

    public int size() {
        int total = 0;
        for (int v : counts.values()) {
            total += v;
        }
        return total;
    }

    public boolean contains(Cost cost) {
        return counts.get(ResourceType.BRICK) >= cost.getBrick()
            && counts.get(ResourceType.LUMBER) >= cost.getLumber()
            && counts.get(ResourceType.WOOL) >= cost.getWool()
            && counts.get(ResourceType.GRAIN) >= cost.getGrain()
            && counts.get(ResourceType.ORE) >= cost.getOre();
    }

    public void pay(Cost cost) {
        remove(ResourceType.BRICK, cost.getBrick());
        remove(ResourceType.LUMBER, cost.getLumber());
        remove(ResourceType.WOOL, cost.getWool());
        remove(ResourceType.GRAIN, cost.getGrain());
        remove(ResourceType.ORE, cost.getOre());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BRICK=").append(counts.get(ResourceType.BRICK));
        sb.append(", LUMBER=").append(counts.get(ResourceType.LUMBER));
        sb.append(", ORE=").append(counts.get(ResourceType.ORE));
        sb.append(", GRAIN=").append(counts.get(ResourceType.GRAIN));
        sb.append(", WOOL=").append(counts.get(ResourceType.WOOL));
        return sb.toString();
    }
}
