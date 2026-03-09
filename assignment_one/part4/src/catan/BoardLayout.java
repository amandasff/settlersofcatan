ackage catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public final class BoardLayout {
    public static final int TILE_COUNT = 19;
    public static final int NODE_COUNT = 54;
    public static final int EDGE_COUNT = 72;

    private final TileSpec[] tileDefinitions;
    private final NodeSpec[] nodeDefinitions;
    private final EdgeSpec[] edgeDefinitions;

    public BoardLayout(TileSpec[] tileDefinitions, NodeSpec[] nodeDefinitions, EdgeSpec[] edgeDefinitions) {
        if (tileDefinitions == null || tileDefinitions.length != TILE_COUNT) {
            throw new IllegalArgumentException("BoardLayout must contain exactly 19 tile definitions.");
        }
        if (nodeDefinitions == null || nodeDefinitions.length != NODE_COUNT) {
            throw new IllegalArgumentException("BoardLayout must contain exactly 54 node definitions.");
        }
        if (edgeDefinitions == null || edgeDefinitions.length != EDGE_COUNT) {
            throw new IllegalArgumentException("BoardLayout must contain exactly 72 edge definitions.");
        }

        this.tileDefinitions = tileDefinitions;
        this.nodeDefinitions = nodeDefinitions;
        this.edgeDefinitions = edgeDefinitions;
    }

    public TileSpec getTileSpec(int tileId) {
        return tileDefinitions[tileId];
    }

    public NodeSpec getNodeSpec(int nodeId) {
        return nodeDefinitions[nodeId];
    }

    public EdgeSpec getEdgeSpec(int edgeId) {
        return edgeDefinitions[edgeId];
    }

    public TileSpec[] getTileDefinitions() {
        return tileDefinitions;
    }

    public NodeSpec[] getNodeDefinitions() {
        return nodeDefinitions;
    }

    public EdgeSpec[] getEdgeDefinitions() {
        return edgeDefinitions;
    }

    public static BoardLayout createDefaultLayout() {
        TileSpec[] tiles = buildTileDefinitions();
        NodeSpec[] nodes = buildNodeDefinitions(tiles);
        EdgeSpec[] edges = buildEdgeDefinitions(tiles);

        return new BoardLayout(tiles, nodes, edges);
    }

    private static TileSpec[] buildTileDefinitions() {
        TileSpec[] tiles = new TileSpec[TILE_COUNT];

        tiles[0]  = new TileSpec(0,  TerrainType.FOREST,    10, new int[]{ 5,  0,  1,  2,  3,  4});
        tiles[1]  = new TileSpec(1,  TerrainType.FIELDS,    11, new int[]{ 1,  6,  7,  8,  9,  2});
        tiles[2]  = new TileSpec(2,  TerrainType.HILLS,      8, new int[]{ 3,  2,  9, 10, 11, 12});
        tiles[3]  = new TileSpec(3,  TerrainType.MOUNTAINS,  3, new int[]{15,  4,  3, 12, 13, 14});
        tiles[4]  = new TileSpec(4,  TerrainType.PASTURE,   11, new int[]{18, 16,  5,  4, 15, 17});
        tiles[5]  = new TileSpec(5,  TerrainType.PASTURE,    5, new int[]{21, 19, 20,  0,  5, 16});
        tiles[6]  = new TileSpec(6,  TerrainType.PASTURE,   12, new int[]{20, 22, 23,  6,  1,  0});
        tiles[7]  = new TileSpec(7,  TerrainType.FIELDS,     3, new int[]{ 7, 24, 25, 26, 27,  8});
        tiles[8]  = new TileSpec(8,  TerrainType.MOUNTAINS,  6, new int[]{ 9,  8, 27, 28, 29, 10});
        tiles[9]  = new TileSpec(9,  TerrainType.FOREST,     4, new int[]{11, 10, 29, 30, 31, 32});
        tiles[10] = new TileSpec(10, TerrainType.MOUNTAINS,  6, new int[]{13, 12, 11, 32, 33, 34});
        tiles[11] = new TileSpec(11, TerrainType.FIELDS,     9, new int[]{37, 14, 13, 34, 35, 36});
        tiles[12] = new TileSpec(12, TerrainType.FOREST,     5, new int[]{39, 17, 15, 14, 37, 38});
        tiles[13] = new TileSpec(13, TerrainType.HILLS,      9, new int[]{42, 40, 18, 17, 39, 41});
        tiles[14] = new TileSpec(14, TerrainType.HILLS,      8, new int[]{44, 43, 21, 16, 18, 40});
        tiles[15] = new TileSpec(15, TerrainType.FIELDS,     4, new int[]{45, 47, 46, 19, 21, 43});
        tiles[16] = new TileSpec(16, TerrainType.DESERT,  null, new int[]{46, 48, 49, 22, 20, 19});
        tiles[17] = new TileSpec(17, TerrainType.FOREST,     2, new int[]{49, 50, 51, 52, 23, 22});
        tiles[18] = new TileSpec(18, TerrainType.PASTURE,   10, new int[]{23, 52, 53, 24,  7,  6});

        return tiles;
    }

    private static NodeSpec[] buildNodeDefinitions(TileSpec[] tiles) {
        List<Set<Integer>> nodeToTiles = new ArrayList<>();
        List<Set<Integer>> nodeToNodes = new ArrayList<>();

        for (int i = 0; i < NODE_COUNT; i++) {
            nodeToTiles.add(new TreeSet<>());
            nodeToNodes.add(new TreeSet<>());
        }

        for (TileSpec tile : tiles) {
            int tileId = tile.getId();
            int[] corners = tile.getCornerNodeIds();

            for (int nodeId : corners) {
                nodeToTiles.get(nodeId).add(tileId);
            }

            for (int i = 0; i < corners.length; i++) {
                int a = corners[i];
                int b = corners[(i + 1) % corners.length];
                nodeToNodes.get(a).add(b);
                nodeToNodes.get(b).add(a);
            }
        }

        NodeSpec[] nodes = new NodeSpec[NODE_COUNT];
        for (int i = 0; i < NODE_COUNT; i++) {
            nodes[i] = new NodeSpec(
                    i,
                    toIntArray(nodeToTiles.get(i)),
                    toIntArray(nodeToNodes.get(i))
            );
        }
        return nodes;
    }

    private static EdgeSpec[] buildEdgeDefinitions(TileSpec[] tiles) {
        TreeMap<Long, int[]> uniqueEdges = new TreeMap<>();

        for (TileSpec tile : tiles) {
            int[] corners = tile.getCornerNodeIds();

            for (int i = 0; i < corners.length; i++) {
                int a = corners[i];
                int b = corners[(i + 1) % corners.length];

                int min = Math.min(a, b);
                int max = Math.max(a, b);

                uniqueEdges.put(edgeKey(min, max), new int[]{min, max});
            }
        }

        if (uniqueEdges.size() != EDGE_COUNT) {
            throw new IllegalStateException(
                    "Derived " + uniqueEdges.size() + " unique edges, expected 72."
            );
        }

        EdgeSpec[] edges = new EdgeSpec[EDGE_COUNT];
        int edgeId = 0;
        for (int[] endpoints : uniqueEdges.values()) {
            edges[edgeId] = new EdgeSpec(edgeId, endpoints[0], endpoints[1]);
            edgeId++;
        }

        return edges;
    }

    private static long edgeKey(int a, int b) {
        return (((long) a) << 32) | (b & 0xffffffffL);
    }

    private static int[] toIntArray(Set<Integer> values) {
        int[] result = new int[values.size()];
        int i = 0;
        for (int value : values) {
            result[i++] = value;
        }
        return result;
    }
}