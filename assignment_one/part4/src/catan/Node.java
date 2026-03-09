package catan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Node {
    private final int id;
    private final List<Integer> adjacentTileIds;
    private final List<Integer> adjacentNodeIds;
    private final List<Integer> incidentEdgeIds;
    private Building building;

    public Node(NodeSpec spec) {
        this.id = spec.getId();
        this.adjacentTileIds = new ArrayList<>();
        this.adjacentNodeIds = new ArrayList<>();
        this.incidentEdgeIds = new ArrayList<>();

        for (int tileId : spec.getAdjacentTileIds()) {
            adjacentTileIds.add(tileId);
        }
        for (int nodeId : spec.getAdjacentNodeIds()) {
            adjacentNodeIds.add(nodeId);
        }
    }

    public int getId() {
        return id;
    }

    public List<Integer> getAdjacentTileIds() {
        return Collections.unmodifiableList(adjacentTileIds);
    }

    public List<Integer> getAdjacentNodeIds() {
        return Collections.unmodifiableList(adjacentNodeIds);
    }

    public List<Integer> getIncidentEdgeIds() {
        return incidentEdgeIds;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public boolean isOccupied() {
        return building != null;
    }
}