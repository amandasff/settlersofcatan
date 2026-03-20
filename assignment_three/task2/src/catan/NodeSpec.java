package catan;

import java.util.Arrays;

public final class NodeSpec {
    private final int id;
    private final int[] adjacentTileIds;
    private final int[] adjacentNodeIds;

    public NodeSpec(int id, int[] adjacentTileIds, int[] adjacentNodeIds) {
        this.id = id;
        this.adjacentTileIds = Arrays.copyOf(adjacentTileIds, adjacentTileIds.length);
        this.adjacentNodeIds = Arrays.copyOf(adjacentNodeIds, adjacentNodeIds.length);
    }

    public int getId() {
        return id;
    }

    public int[] getAdjacentTileIds() {
        return Arrays.copyOf(adjacentTileIds, adjacentTileIds.length);
    }

    public int[] getAdjacentNodeIds() {
        return Arrays.copyOf(adjacentNodeIds, adjacentNodeIds.length);
    }
}