package catan;

public final class ResourceBank {
    private final ResourceInventory supply;

    public ResourceBank() {
        this.supply = new ResourceInventory();


        for (ResourceType type : ResourceType.values()) {
            supply.add(type, 50);
        }
    }

    public boolean canPay(ResourceType type, int amount) {
        return supply.get(type) >= amount;
    }

    public void payTo(Player player, ResourceType type, int amount) {
        if (!canPay(type, amount)) {
            return;
        }
        supply.remove(type, amount);
        player.getHand().add(type, amount);
    }

    public void takeFrom(Player player, Cost cost) {
        player.getHand().pay(cost);
        for (ResourceType type : ResourceType.values()) {
            int amount = cost.get(type);
            if (amount > 0) {
                supply.add(type, amount);
            }
        }
    }

    public ResourceInventory getSupply() {
        return supply;
    }
}