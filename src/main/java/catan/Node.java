package catan;

import java.util.List;
import java.util.ArrayList;

public class Node {
    private final int id;
    private Building building;
    private final List<Edge> incidentEdges;
    private final List<Tile> adjacentTiles;

    public Node(int id) {
        this.id = id;
        this.incidentEdges = new ArrayList<>();
        this.adjacentTiles = new ArrayList<>();
    }

    public int getId() { return id; }

    public Building getBuilding() { return building; }

    public void setBuilding(Building building) { this.building = building; }

    public boolean isOccupied() { return building != null; }

    public Player getOwner() {
        return building != null ? building.getOwner() : null;
    }

    public List<Edge> getIncidentEdges() { return incidentEdges; }

    public List<Tile> getAdjacentTiles() { return adjacentTiles; }

    public void addIncidentEdge(Edge edge) {
        if (!incidentEdges.contains(edge)) {
            incidentEdges.add(edge);
        }
    }

    public void addAdjacentTile(Tile tile) {
        if (!adjacentTiles.contains(tile)) {
            adjacentTiles.add(tile);
        }
    }
}
