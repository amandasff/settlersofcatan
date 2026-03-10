package catan;

public final class Edge {
    private final int id;
    private final int nodeAId;
    private final int nodeBId;
    private Road road;

    public Edge(EdgeSpec spec) {
        this.id = spec.getId();
        this.nodeAId = spec.getNodeAId();
        this.nodeBId = spec.getNodeBId();
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

    public Road getRoad() {
        return road;
    }

    public void setRoad(Road road) {
        this.road = road;
    }

    public boolean isOccupied() {
        return road != null;
    }

    public boolean connects(int nodeId) {
        return nodeAId == nodeId || nodeBId == nodeId;
    }
}