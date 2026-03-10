package catan;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

public final class ResourceInventory {
    private final EnumMap<ResourceType, Integer> counts;

    public ResourceInventory() {
        this.counts = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            counts.put(type, 0);
        }
    }

    public void add(ResourceType type, int amount) {
        counts.put(type, counts.get(type) + amount);
    }

    public void remove(ResourceType type, int amount) {
        int current = counts.get(type);
        if (current < amount) {
            throw new IllegalArgumentException("Not enough " + type);
        }
        counts.put(type, current - amount);
    }

    public boolean contains(Cost cost) {
        for (ResourceType type : ResourceType.values()) {
            if (counts.get(type) < cost.get(type)) {
                return false;
            }
        }
        return true;
    }

    public void pay(Cost cost) {
        if (!contains(cost)) {
            throw new IllegalArgumentException("Cannot afford cost");
        }
        for (ResourceType type : ResourceType.values()) {
            int amount = cost.get(type);
            if (amount > 0) {
                remove(type, amount);
            }
        }
    }

    public int get(ResourceType type) {
        return counts.get(type);
    }

    public int size() {
        int total = 0;
        for (int amount : counts.values()) {
            total += amount;
        }
        return total;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public List<ResourceType> getNonEmptyTypes() {
        List<ResourceType> result = new ArrayList<>();
        for (ResourceType type : ResourceType.values()) {
            if (counts.get(type) > 0) {
                result.add(type);
            }
        }
        return result;
    }

    public ResourceType removeRandom(Random rng) {
        List<ResourceType> candidates = getNonEmptyTypes();
        if (candidates.isEmpty()) {
            return null;
        }

        ResourceType chosen = candidates.get(rng.nextInt(candidates.size()));
        remove(chosen, 1);
        return chosen;
    }

    @Override
    public String toString() {
        return counts.toString();
    }
}