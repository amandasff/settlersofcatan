package catan;

import java.util.List;

public class Player {
    private final int id;
    private final ResourceInventory hand;
    private final PiecePool pieces;
    private final AgentStrategy strategy;
    private int victoryPoints;

    public Player(int id, AgentStrategy strategy) {
        this.id = id;
        this.hand = new ResourceInventory();
        this.pieces = new PiecePool();
        this.strategy = strategy;
        this.victoryPoints = 0;
    }

    public int getId() { return id; }
    public int getVP() { return victoryPoints; }
    public void addVP(int amount) { victoryPoints += amount; }
    public void removeVP(int amount) { victoryPoints -= amount; }

    public int handSize() { return hand.size(); }

    public boolean canAfford(Cost cost) { return hand.contains(cost); }

    public void pay(Cost cost) { hand.pay(cost); }

    public void addResources(ResourceType type, int amount) {
        hand.add(type, amount);
    }

    public Action chooseAction(List<Action> options) {
        return strategy.select(options);
    }

    public ResourceInventory getHand() { return hand; }
    public PiecePool getPieces() { return pieces; }
}
