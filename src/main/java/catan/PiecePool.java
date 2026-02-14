package catan;

public class PiecePool {
    private int roadsRemaining;
    private int settlementsRemaining;
    private int citiesRemaining;

    public PiecePool() {
        this.roadsRemaining = 15;
        this.settlementsRemaining = 5;
        this.citiesRemaining = 4;
    }

    public boolean hasRoad() { return roadsRemaining > 0; }
    public void takeRoad() { roadsRemaining--; }

    public boolean hasSettlement() { return settlementsRemaining > 0; }
    public void takeSettlement() { settlementsRemaining--; }

    public boolean hasCity() { return citiesRemaining > 0; }
    public void takeCity() { citiesRemaining--; }

    public void returnSettlement() { settlementsRemaining++; }

    public int roadsRemaining() { return roadsRemaining; }
    public int settlementsRemaining() { return settlementsRemaining; }
    public int citiesRemaining() { return citiesRemaining; }
}
