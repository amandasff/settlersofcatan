package catan;

public class Cost {
    private final int brick;
    private final int lumber;
    private final int wool;
    private final int grain;
    private final int ore;

    public Cost(int brick, int lumber, int wool, int grain, int ore) {
        this.brick = brick;
        this.lumber = lumber;
        this.wool = wool;
        this.grain = grain;
        this.ore = ore;
    }

    public int getBrick() { return brick; }
    public int getLumber() { return lumber; }
    public int getWool() { return wool; }
    public int getGrain() { return grain; }
    public int getOre() { return ore; }

    public static Cost roadCost() { return new Cost(1, 1, 0, 0, 0); }
    public static Cost settlementCost() { return new Cost(1, 1, 1, 1, 0); }
    public static Cost cityCost() { return new Cost(0, 0, 0, 2, 3); }
}
