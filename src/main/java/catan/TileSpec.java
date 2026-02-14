package catan;

public class TileSpec {
    private final int id;
    private final TerrainType terrain;
    private final int token;
    private final int[] cornerNodeIds;

    public TileSpec(int id, TerrainType terrain, int token, int[] cornerNodeIds) {
        this.id = id;
        this.terrain = terrain;
        this.token = token;
        this.cornerNodeIds = cornerNodeIds;
    }

    public int getId() { return id; }
    public TerrainType getTerrain() { return terrain; }
    public int getToken() { return token; }
    public int[] getCornerNodeIds() { return cornerNodeIds; }
}
