package catan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GameStateExporter {
    private final Path outputPath;

    public GameStateExporter(String path) {
        this.outputPath = Path.of(path);
    }

    public void export(GameState state) {
        StringBuilder sb = new StringBuilder();

        sb.append("{\n");
        sb.append("  \"currentRound\": ").append(state.getCurrentRound()).append(",\n");
        sb.append("  \"currentPlayerIndex\": ").append(state.getCurrentPlayerIndex()).append(",\n");
        sb.append("  \"currentRoll\": ").append(state.getCurrentRoll()).append(",\n");
        sb.append("  \"robberTileId\": ").append(state.getRobberTileId()).append(",\n");
        sb.append("  \"lastAction\": \"").append(escape(state.getLastAction())).append("\",\n");

        sb.append("  \"roads\": [\n");
        boolean firstRoad = true;
        for (Edge edge : state.getBoard().getEdges()) {
            if (edge.getRoad() != null) {
                if (!firstRoad) {
                    sb.append(",\n");
                }
                sb.append("    { \"a\": ")
                        .append(edge.getNodeAId())
                        .append(", \"b\": ")
                        .append(edge.getNodeBId())
                        .append(", \"owner\": \"")
                        .append(ownerColor(edge.getRoad().getOwnerId()))
                        .append("\" }");
                firstRoad = false;
            }
        }
        sb.append("\n  ],\n");

        sb.append("  \"buildings\": [\n");
        boolean firstBuilding = true;
        for (Node node : state.getBoard().getNodes()) {
            if (node.getBuilding() != null) {
                if (!firstBuilding) {
                    sb.append(",\n");
                }
                String type = (node.getBuilding() instanceof City) ? "CITY" : "SETTLEMENT";

                sb.append("    { \"node\": ")
                        .append(node.getId())
                        .append(", \"owner\": \"")
                        .append(ownerColor(node.getBuilding().getOwnerId()))
                        .append("\", \"type\": \"")
                        .append(type)
                        .append("\" }");
                firstBuilding = false;
            }
        }
        sb.append("\n  ]\n");

        sb.append("}\n");

        try {
            Files.writeString(outputPath, sb.toString());
        } catch (IOException e) {
            throw new RuntimeException("Could not write state.json", e);
        }
    }

    private String ownerColor(int ownerId) {
        return switch (ownerId) {
            case 0 -> "RED";
            case 1 -> "BLUE";
            case 2 -> "ORANGE";
            case 3 -> "WHITE";
            default -> "RED";
        };
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}