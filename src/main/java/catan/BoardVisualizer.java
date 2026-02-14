package catan;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders the Catan board as ASCII art in the standard 3-4-5-4-3 hex layout.
 * Shows terrain types, number tokens, and player buildings at key game moments.
 */
public class BoardVisualizer {

    // Tile IDs arranged in display rows (3-4-5-4-3 hex grid, spiral per spec)
    private static final int[][] TILE_ROWS = {
        {17, 14, 15},
        {12, 4, 5, 16},
        {11, 3, 0, 6, 10},
        {13, 2, 1, 18},
        {9, 8, 7}
    };

    public void printBoard(Board board, String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
        System.out.println();

        // Print hex grid rows with indentation to create hex shape
        int maxRowSize = 5;
        for (int[] row : TILE_ROWS) {
            int indent = (maxRowSize - row.length) * 5;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indent; i++) sb.append(' ');
            for (int tileId : row) {
                Tile tile = board.getTile(tileId);
                sb.append(formatTileCell(tile));
            }
            System.out.println(sb.toString());
        }

        // Print building summary
        System.out.println();
        printBuildingSummary(board);
        System.out.println();
    }

    private String formatTileCell(Tile tile) {
        String terrain = terrainAbbrev(tile.getTerrain());
        if (tile.getTerrain() == TerrainType.DESERT) {
            return String.format("[ %s    ]", terrain);
        } else {
            return String.format("[ %s/%-2d ]", terrain, tile.getToken());
        }
    }

    private String terrainAbbrev(TerrainType t) {
        switch (t) {
            case FIELDS:    return "FLD";
            case FOREST:    return "FOR";
            case PASTURE:   return "PAS";
            case HILLS:     return "HIL";
            case MOUNTAINS: return "MTN";
            case DESERT:    return "DST";
            default:        return "???";
        }
    }

    private void printBuildingSummary(Board board) {
        // Collect buildings per player
        @SuppressWarnings("unchecked")
        List<String>[] playerBuildings = (List<String>[]) new List<?>[4];
        for (int i = 0; i < 4; i++) {
            playerBuildings[i] = new ArrayList<>();
        }

        for (Node node : board.getNodes()) {
            if (node != null && node.isOccupied()) {
                Building b = node.getBuilding();
                int playerId = b.getOwner().getId();
                String type = (b instanceof City) ? "City" : "Settlement";
                playerBuildings[playerId - 1].add(type + "@node" + node.getId());
            }
        }

        System.out.println("  Buildings:");
        for (int i = 0; i < 4; i++) {
            if (!playerBuildings[i].isEmpty()) {
                System.out.println("    " + (i + 1) + ": " + String.join(", ", playerBuildings[i]));
            } else {
                System.out.println("    " + (i + 1) + ": (none)");
            }
        }

        // Print VP summary
        System.out.println();
    }
}
