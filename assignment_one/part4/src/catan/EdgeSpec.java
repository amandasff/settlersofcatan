package catan;

public final class EdgeSpec {
    private final int id;
    private final int nodeAId;
    private final int nodeBId;

    public EdgeSpec(int id, int nodeAId, int nodeBId) {
        if (nodeAId == nodeBId) {
            throw new IllegalArgumentException("Edge endpoints must be different.");
        }
        this.id = id;
        this.nodeAId = nodeAId;
        this.nodeBId = nodeBId;
    }

    public int getId() {
        return id;
    }

    public int getNodeAId() {
        return nodeAId;
    }

    public int getNodeBId() {
        return nodeBId;
    }

    public boolean connects(int nodeId) {
        return nodeAId == nodeId || nodeBId == nodeId;
    }
}