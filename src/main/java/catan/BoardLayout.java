package catan;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class BoardLayout {
    private final List<TileSpec> tileDefinitions;
    private final List<NodeSpec> nodeDefinitions;
    private final List<EdgeSpec> edgeDefinitions;

    public BoardLayout(List<TileSpec> tileDefinitions, List<NodeSpec> nodeDefinitions, List<EdgeSpec> edgeDefinitions) {
        this.tileDefinitions = tileDefinitions;
        this.nodeDefinitions = nodeDefinitions;
        this.edgeDefinitions = edgeDefinitions;
    }

    public List<TileSpec> getTileDefinitions() { return tileDefinitions; }
    public List<NodeSpec> getNodeDefinitions() { return nodeDefinitions; }
    public List<EdgeSpec> getEdgeDefinitions() { return edgeDefinitions; }

    public TileSpec getTileSpec(int id) {
        for (TileSpec ts : tileDefinitions) {
            if (ts.getId() == id) return ts;
        }
        return null;
    }

    public static BoardLayout createStandardLayout() {
        // Standard Catan board: 19 tiles in rows of 3-4-5-4-3, 54 nodes, 72 edges
        // Tile corner nodes (6 corners per tile, clockwise from top-left)
        int[][] tileCornerNodes = {
            // Row 0 (top 3 tiles)
            {0, 4, 8, 12, 7, 3},
            {1, 5, 9, 13, 8, 4},
            {2, 6, 10, 14, 9, 5},
            // Row 1 (4 tiles)
            {7, 12, 17, 22, 16, 11},
            {8, 13, 18, 23, 17, 12},
            {9, 14, 19, 24, 18, 13},
            {10, 15, 20, 25, 19, 14},
            // Row 2 (middle 5 tiles)
            {16, 22, 28, 33, 27, 21},
            {17, 23, 29, 34, 28, 22},
            {18, 24, 30, 35, 29, 23},
            {19, 25, 31, 36, 30, 24},
            {20, 26, 32, 37, 31, 25},
            // Row 3 (4 tiles)
            {28, 34, 39, 43, 38, 33},
            {29, 35, 40, 44, 39, 34},
            {30, 36, 41, 45, 40, 35},
            {31, 37, 42, 46, 41, 36},
            // Row 4 (bottom 3 tiles)
            {39, 44, 48, 51, 47, 43},
            {40, 45, 49, 52, 48, 44},
            {41, 46, 50, 53, 49, 45},
        };

        // Standard terrain distribution: 4 Fields, 4 Forest, 4 Pasture, 3 Hills, 3 Mountains, 1 Desert
        List<TerrainType> terrains = new ArrayList<>(Arrays.asList(
            TerrainType.FIELDS, TerrainType.FIELDS, TerrainType.FIELDS, TerrainType.FIELDS,
            TerrainType.FOREST, TerrainType.FOREST, TerrainType.FOREST, TerrainType.FOREST,
            TerrainType.PASTURE, TerrainType.PASTURE, TerrainType.PASTURE, TerrainType.PASTURE,
            TerrainType.HILLS, TerrainType.HILLS, TerrainType.HILLS,
            TerrainType.MOUNTAINS, TerrainType.MOUNTAINS, TerrainType.MOUNTAINS,
            TerrainType.DESERT
        ));

        // Standard number tokens (placed on non-desert tiles)
        int[] standardTokens = {5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11};

        Random rng = new Random();
        Collections.shuffle(terrains, rng);

        // Spiral tile IDs per spec diagram (page 2):
        // center=0, inner ring 1-6 (counterclockwise from SE),
        // outer ring 7-18 (counterclockwise from bottom-right)
        int[] spiralIds = {
            17, 14, 15,          // Row 0: top 3 tiles
            12,  4,  5, 16,     // Row 1: 4 tiles
            11,  3,  0,  6, 10, // Row 2: middle 5 tiles (center=0)
            13,  2,  1, 18,     // Row 3: 4 tiles
             9,  8,  7          // Row 4: bottom 3 tiles
        };

        List<TileSpec> tileSpecs = new ArrayList<>();
        int tokenIndex = 0;
        for (int i = 0; i < 19; i++) {
            TerrainType t = terrains.get(i);
            int token = 0;
            if (t != TerrainType.DESERT) {
                token = standardTokens[tokenIndex++];
            }
            tileSpecs.add(new TileSpec(spiralIds[i], t, token, tileCornerNodes[i]));
        }

        List<NodeSpec> nodeSpecs = new ArrayList<>();
        for (int i = 0; i < 54; i++) {
            nodeSpecs.add(new NodeSpec(i));
        }

        // Derive edges from tile corners (each pair of adjacent corners forms an edge)
        Set<String> edgeSet = new HashSet<>();
        List<EdgeSpec> edgeSpecs = new ArrayList<>();
        for (int[] cn : tileCornerNodes) {
            for (int j = 0; j < 6; j++) {
                int a = cn[j];
                int b = cn[(j + 1) % 6];
                String key = Math.min(a, b) + "-" + Math.max(a, b);
                if (edgeSet.add(key)) {
                    edgeSpecs.add(new EdgeSpec(Math.min(a, b), Math.max(a, b)));
                }
            }
        }

        return new BoardLayout(tileSpecs, nodeSpecs, edgeSpecs);
    }
}
