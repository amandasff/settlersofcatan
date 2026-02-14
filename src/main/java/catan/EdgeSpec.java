package catan;

public class EdgeSpec {
    private final int nodeA;
    private final int nodeB;

    public EdgeSpec(int nodeA, int nodeB) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }

    public int getNodeA() { return nodeA; }
    public int getNodeB() { return nodeB; }
}
