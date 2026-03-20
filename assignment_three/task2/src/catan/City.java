package catan;

public final class City extends Building {
    public City(int ownerId, int nodeId) {
        super(ownerId, nodeId);
    }

    @Override
    public int getVPValue() {
        return 2;
    }
}