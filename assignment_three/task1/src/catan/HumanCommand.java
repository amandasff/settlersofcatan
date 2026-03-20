package catan;

/*
 * Assignment 3 changes:
 * - added undo and redo factory methods for command history interaction
 */
public final class HumanCommand {
    private final CommandType type;
    private final Integer nodeId;
    private final Integer fromNodeId;
    private final Integer toNodeId;

    private HumanCommand(CommandType type, Integer nodeId, Integer fromNodeId, Integer toNodeId) {
        this.type = type;
        this.nodeId = nodeId;
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
    }

    public static HumanCommand roll() {
        return new HumanCommand(CommandType.ROLL, null, null, null);
    }

    public static HumanCommand go() {
        return new HumanCommand(CommandType.GO, null, null, null);
    }

    public static HumanCommand list() {
        return new HumanCommand(CommandType.LIST, null, null, null);
    }

    public static HumanCommand undo() {
        return new HumanCommand(CommandType.UNDO, null, null, null);
    }

    public static HumanCommand redo() {
        return new HumanCommand(CommandType.REDO, null, null, null);
    }

    public static HumanCommand buildSettlement(int nodeId) {
        return new HumanCommand(CommandType.BUILD_SETTLEMENT, nodeId, null, null);
    }

    public static HumanCommand buildCity(int nodeId) {
        return new HumanCommand(CommandType.BUILD_CITY, nodeId, null, null);
    }

    public static HumanCommand buildRoad(int fromNodeId, int toNodeId) {
        return new HumanCommand(CommandType.BUILD_ROAD, null, fromNodeId, toNodeId);
    }

    public CommandType getType() {
        return type;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public Integer getFromNodeId() {
        return fromNodeId;
    }

    public Integer getToNodeId() {
        return toNodeId;
    }
}