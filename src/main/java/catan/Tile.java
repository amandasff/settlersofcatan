package catan;

import java.util.List;
import java.util.ArrayList;

public class Tile {
    private final int id;
    private final TerrainType terrain;
    private final int token;
    private final List<Node> cornerNodes;

    public Tile(int id, TerrainType terrain, int token) {
        this.id = id;
        this.terrain = terrain;
        this.token = token;
        this.cornerNodes = new ArrayList<>();
    }

    public int getId() { return id; }
    public TerrainType getTerrain() { return terrain; }
    public int getToken() { return token; }
    public List<Node> getCornerNodes() { return cornerNodes; }

    public void addCornerNode(Node node) {
        if (!cornerNodes.contains(node)) {
            cornerNodes.add(node);
        }
    }

    public boolean producesOn(int roll) {
        return terrain != TerrainType.DESERT && token == roll;
    }

    public ResourceType getProducedResource() {
        switch (terrain) {
            case HILLS: return ResourceType.BRICK;
            case FOREST: return ResourceType.LUMBER;
            case MOUNTAINS: return ResourceType.ORE;
            case FIELDS: return ResourceType.GRAIN;
            case PASTURE: return ResourceType.WOOL;
            default: return null;
        }
    }
}
