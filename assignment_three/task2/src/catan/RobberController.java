package catan;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class RobberController {
    private final Random rng;

    public RobberController(long seed) {
        this.rng = new Random(seed + 99);
    }

    public void handleRollOfSeven(GameState state, Player roller) {
        discardForPlayersOverSeven(state);
        moveRobberRandomly(state);
        stealRandomResourceFromQualifyingPlayer(state, roller);
    }

    private void discardForPlayersOverSeven(GameState state) {
        for (Player player : state.getPlayers()) {
            int handSize = player.handSize();
            if (handSize > 7) {
                int discardCount = handSize / 2;
                for (int i = 0; i < discardCount; i++) {
                    ResourceType removed = player.getHand().removeRandom(rng);
                    if (removed != null) {
                        state.getBank().getSupply().add(removed, 1);
                    }
                }
            }
        }
    }

    private void moveRobberRandomly(GameState state) {
        int tileId = rng.nextInt(state.getBoard().tileCount());
        state.setRobberTileId(tileId);
    }

    private void stealRandomResourceFromQualifyingPlayer(GameState state, Player roller) {
        if (state.getRobberTileId() < 0) {
            return;
        }

        Tile robberTile = state.getBoard().getTile(state.getRobberTileId());
        Set<Integer> eligibleOwnerIds = new LinkedHashSet<>();

        for (int nodeId : robberTile.getCornerNodeIds()) {
            Node node = state.getBoard().getNode(nodeId);
            if (node.getBuilding() != null) {
                int ownerId = node.getBuilding().getOwnerId();
                if (ownerId != roller.getId()) {
                    Player owner = findPlayerById(state, ownerId);
                    if (owner != null && owner.handSize() > 0) {
                        eligibleOwnerIds.add(ownerId);
                    }
                }
            }
        }

        if (eligibleOwnerIds.isEmpty()) {
            return;
        }

        List<Integer> candidates = new ArrayList<>(eligibleOwnerIds);
        int victimId = candidates.get(rng.nextInt(candidates.size()));
        Player victim = findPlayerById(state, victimId);

        if (victim == null || victim.getHand().isEmpty()) {
            return;
        }

        ResourceType stolen = victim.getHand().removeRandom(rng);
        if (stolen != null) {
            roller.getHand().add(stolen, 1);
        }
    }

    private Player findPlayerById(GameState state, int playerId) {
        for (Player player : state.getPlayers()) {
            if (player.getId() == playerId) {
                return player;
            }
        }
        return null;
    }
}