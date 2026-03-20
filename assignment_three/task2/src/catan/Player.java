package catan;

/*
 * Assignment 3 changes:
 * - added removeVictoryPoints to support reversing command effects during undo
 */
public final class Player {
    private final int id;
    private final ResourceInventory hand;
    private final PiecePool pieces;
    private final AgentStrategy strategy;
    private int victoryPoints;
    private final EnumMap<CardType, Integer> developmentHand;


    public Player(int id, AgentStrategy strategy) {
        this.id = id;
        this.strategy = strategy;
        this.hand = new ResourceInventory();
        this.pieces = new PiecePool();
        this.victoryPoints = 0;
        this.developmentHand = new EnumMap<>(CardType.class);
        for (CardType type : CardType.values()) {
            counts.put(type, 0);
        }

    }

    public static Player randomAgent(int id) {
        return new Player(id, new RandomStrategy());
    }

    public static Player randomAgent(int id, long seed) {
        return new Player(id, new RandomStrategy(seed));
    }

    public int getId() {
        return id;
    }

    public ResourceInventory getHand() {
        return hand;
    }

    public PiecePool getPieces() {
        return pieces;
    }

    public AgentStrategy getStrategy() {
        return strategy;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public EnumMap<CardType, Integer> getDevelopmentHand() {
        return developmentHand;
    }

    public void addVictoryPoints(int points) {
        this.victoryPoints += points;
    }

    public void removeVictoryPoints(int points) {
        this.victoryPoints -= points;
    }

    public boolean canAfford(Cost cost) {
        return hand.contains(cost);
    }

    public int handSize() {
        return hand.size();
    }

}