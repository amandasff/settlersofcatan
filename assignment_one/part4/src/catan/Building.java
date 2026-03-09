package catan;

public abstract class Building {
    private final int ownerId;
    private final int nodeId;

    protected Building(int ownerId, int nodeId) {
        this.ownerId = ownerId;
        this.nodeId = nodeId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public abstract int getVPValue();
}