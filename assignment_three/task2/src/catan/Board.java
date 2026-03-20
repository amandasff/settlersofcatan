package catan;

public final class Board {
    private final Tile[] tiles;
    private final Node[] nodes;
    private final Edge[] edges;

    public Board(BoardLayout layout) {
        this.tiles = new Tile[BoardLayout.TILE_COUNT];
        for (int i = 0; i < BoardLayout.TILE_COUNT; i++) {
            tiles[i] = new Tile(layout.getTileSpec(i));
        }

        this.nodes = new Node[BoardLayout.NODE_COUNT];
        for (int i = 0; i < BoardLayout.NODE_COUNT; i++) {
            nodes[i] = new Node(layout.getNodeSpec(i));
        }

        this.edges = new Edge[BoardLayout.EDGE_COUNT];
        for (int i = 0; i < BoardLayout.EDGE_COUNT; i++) {
            edges[i] = new Edge(layout.getEdgeSpec(i));
        }

        linkIncidentEdges();
    }

    private void linkIncidentEdges() {
        for (Edge edge : edges) {
            nodes[edge.getNodeAId()].getIncidentEdgeIds().add(edge.getId());
            nodes[edge.getNodeBId()].getIncidentEdgeIds().add(edge.getId());
        }
    }

    public Tile getTile(int tileId) {
        return tiles[tileId];
    }

    public Node getNode(int nodeId) {
        return nodes[nodeId];
    }

    public Edge getEdge(int edgeId) {
        return edges[edgeId];
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public Edge[] getEdges() {
        return edges;
    }

    public int tileCount() {
        return tiles.length;
    }

    public int nodeCount() {
        return nodes.length;
    }

    public int edgeCount() {
        return edges.length;
    }
}