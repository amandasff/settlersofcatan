package catan;

public final class Settlement extends Building {
    public Settlement(int ownerId, int nodeId) {
        super(ownerId, nodeId);
    }

    @Override
    public int getVPValue() {
        return 1;
    }
}