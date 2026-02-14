package catan;

public class ResourceBank {
    private final ResourceInventory supply;

    public ResourceBank() {
        this.supply = new ResourceInventory();
        // Standard Catan: 19 of each resource in the bank
        for (ResourceType r : ResourceType.values()) {
            supply.add(r, 19);
        }
    }

    public boolean canPay(ResourceType type, int amount) {
        return supply.get(type) >= amount;
    }

    public void payTo(Player player, ResourceType type, int amount) {
        if (canPay(type, amount)) {
            supply.remove(type, amount);
            player.addResources(type, amount);
        }
    }

    public void returnResources(ResourceType type, int amount) {
        supply.add(type, amount);
    }

    public void returnCost(Cost cost) {
        supply.add(ResourceType.BRICK, cost.getBrick());
        supply.add(ResourceType.LUMBER, cost.getLumber());
        supply.add(ResourceType.WOOL, cost.getWool());
        supply.add(ResourceType.GRAIN, cost.getGrain());
        supply.add(ResourceType.ORE, cost.getOre());
    }
}
