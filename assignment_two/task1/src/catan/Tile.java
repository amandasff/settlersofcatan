package catan;

public final class Tile {
    private final int id;
    private final TerrainType terrain;
    private final Integer token;
    private final int[] cornerNodeIds;

    public Tile(TileSpec spec) {
        this.id = spec.getId();
        this.terrain = spec.getTerrain();
        this.token = spec.getToken();
        this.cornerNodeIds = spec.getCornerNodeIds();
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
        return cornerNodeIds;
    }
}