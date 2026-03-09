package catan;

import java.util.Arrays;

public final class TileSpec {
    private final int id;
    private final TerrainType terrain;
    private final Integer token;
    private final int[] cornerNodeIds;

    public TileSpec(int id, TerrainType terrain, Integer token, int[] cornerNodeIds) {
        if (cornerNodeIds == null || cornerNodeIds.length != 6) {
            throw new IllegalArgumentException("Each tile must have exactly 6 corner node ids.");
        }
        this.id = id;
        this.terrain = terrain;
        this.token = token;
        this.cornerNodeIds = Arrays.copyOf(cornerNodeIds, cornerNodeIds.length);
    }

    public int getId() {
        return id;
    }

    public TerrainType getTerrain() {
        return terrain;
    }

    public Integer getToken() {
        return token;
    }

    public int[] getCornerNodeIds() {
        return Arrays.copyOf(cornerNodeIds, cornerNodeIds.length);
    }
}