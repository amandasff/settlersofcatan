package catan;

public final class Road {
    private final int ownerId;
    private final int edgeId;

    public Road(int ownerId, int edgeId) {
        this.ownerId = ownerId;
        this.edgeId = edgeId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getEdgeId() {
        return edgeId;
    }
}