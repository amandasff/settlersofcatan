package catan;

public class Edge {
    private final Node a;
    private final Node b;
    private Road road;

    public Edge(Node a, Node b) {
        this.a = a;
        this.b = b;
    }

    public Node getA() { return a; }
    public Node getB() { return b; }
    public Road getRoad() { return road; }

    public void setRoad(Road road) { this.road = road; }

    public boolean isOccupied() { return road != null; }

    public Node getOtherNode(Node node) {
        if (node == a) return b;
        if (node == b) return a;
        return null;
    }
}
