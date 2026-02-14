package catan;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Board {
    private final Tile[] tiles;
    private final Node[] nodes;
    private final Map<String, Edge> edgeMap;
    private final List<Edge> allEdges;
    private final BoardLayout layout;

    public Board(BoardLayout layout) {
        this.layout = layout;
        this.nodes = new Node[54];
        this.tiles = new Tile[19];
        this.edgeMap = new HashMap<>();
        this.allEdges = new ArrayList<>();

        // Create nodes
        for (NodeSpec ns : layout.getNodeDefinitions()) {
            nodes[ns.getId()] = new Node(ns.getId());
        }

        // Create tiles and link to corner nodes
        for (TileSpec ts : layout.getTileDefinitions()) {
            Tile tile = new Tile(ts.getId(), ts.getTerrain(), ts.getToken());
            tiles[ts.getId()] = tile;
            for (int nodeId : ts.getCornerNodeIds()) {
                Node node = nodes[nodeId];
                tile.addCornerNode(node);
                node.addAdjacentTile(tile);
            }
        }

        // Create edges and link to nodes
        for (EdgeSpec es : layout.getEdgeDefinitions()) {
            Node a = nodes[es.getNodeA()];
            Node b = nodes[es.getNodeB()];
            Edge edge = new Edge(a, b);
            String key = edgeKey(es.getNodeA(), es.getNodeB());
            edgeMap.put(key, edge);
            allEdges.add(edge);
            a.addIncidentEdge(edge);
            b.addIncidentEdge(edge);
        }
    }

    private String edgeKey(int a, int b) {
        return Math.min(a, b) + "-" + Math.max(a, b);
    }

    public Tile getTile(int id) { return tiles[id]; }
    public Node getNode(int id) { return nodes[id]; }

    public Edge getEdge(int a, int b) {
        return edgeMap.get(edgeKey(a, b));
    }

    public List<Node> getNodesOfTile(Tile t) {
        return t.getCornerNodes();
    }

    public List<Tile> getTilesOfNode(Node n) {
        return n.getAdjacentTiles();
    }

    public List<Node> getAdjacentNodes(Node n) {
        List<Node> neighbors = new ArrayList<>();
        for (Edge e : n.getIncidentEdges()) {
            neighbors.add(e.getOtherNode(n));
        }
        return neighbors;
    }

    public Tile[] getTiles() { return tiles; }
    public Node[] getNodes() { return nodes; }
    public List<Edge> getAllEdges() { return allEdges; }
}
