package catan;

/**
 * Handles resource production after a dice roll.
 *
 * Rules implemented:
 * - Roll 7 produces no resources in this simplified simulator.
 * - Settlements produce 1 resource.
 * - Cities produce 2 resources.
 * - Desert produces nothing.
 */
public final class ResourceProductionEngine {

    public void produce(GameState state, int roll) {
        if (roll == 7) {
            return;
        }

        for (Tile tile : state.getBoard().getTiles()) {
            Integer token = tile.getToken();
            if (token == null || token != roll) {
                continue;
            }

            ResourceType producedType = terrainToResource(tile.getTerrain());
            if (producedType == null) {
                continue;
            }

            for (int nodeId : tile.getCornerNodeIds()) {
                Node node = state.getBoard().getNode(nodeId);

                if (!node.isOccupied()) {
                    continue;
                }

                Building building = node.getBuilding();
                Player owner = state.getPlayers()[building.getOwnerId()];

                int amount = (building instanceof City) ? 2 : 1;
                state.getBank().payTo(owner, producedType, amount);
            }
        }
    }

    private ResourceType terrainToResource(TerrainType terrain) {
        switch (terrain) {
            case HILLS:
                return ResourceType.BRICK;
            case FOREST:
                return ResourceType.LUMBER;
            case PASTURE:
                return ResourceType.WOOL;
            case FIELDS:
                return ResourceType.GRAIN;
            case MOUNTAINS:
                return ResourceType.ORE;
            case DESERT:
            default:
                return null;
        }
    }
}