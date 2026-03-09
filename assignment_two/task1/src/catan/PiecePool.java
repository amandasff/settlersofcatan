package catan;

public final class PiecePool {
    private int roadsRemaining = 15;
    private int settlementsRemaining = 5;
    private int citiesRemaining = 4;

    public boolean hasRoad() {
        return roadsRemaining > 0;
    }

    public boolean hasSettlement() {
        return settlementsRemaining > 0;
    }

    public boolean hasCity() {
        return citiesRemaining > 0;
    }

    public void takeRoad() {
        if (!hasRoad()) throw new IllegalStateException("No roads remaining");
        roadsRemaining--;
    }

    public void takeSettlement() {
        if (!hasSettlement()) throw new IllegalStateException("No settlements remaining");
        settlementsRemaining--;
    }

    public void takeCity() {
        if (!hasCity()) throw new IllegalStateException("No cities remaining");
        citiesRemaining--;
    }

    public void returnSettlement() {
        settlementsRemaining++;
    }

    public int getRoadsRemaining() {
        return roadsRemaining;
    }

    public int getSettlementsRemaining() {
        return settlementsRemaining;
    }

    public int getCitiesRemaining() {
        return citiesRemaining;
    }
}