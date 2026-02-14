package catan;

import java.util.ArrayList;
import java.util.List;

public class ResourceProductionEngine {
    private final Board board;
    private final ResourceBank bank;
    private final ActionLogger logger;

    public ResourceProductionEngine(Board board, ResourceBank bank, ActionLogger logger) {
        this.board = board;
        this.bank = bank;
        this.logger = logger;
    }

    public void produce(int roll, Player[] players, int round) {
        if (roll == 7) return;

        for (Tile tile : board.getTiles()) {
            if (tile.producesOn(roll)) {
                ResourceType resource = tile.getProducedResource();
                if (resource == null) continue;

                // Calculate total demand for this resource from this tile
                List<Player> entitled = new ArrayList<>();
                List<Integer> amounts = new ArrayList<>();
                int totalNeeded = 0;

                for (Node node : tile.getCornerNodes()) {
                    if (node.isOccupied()) {
                        int amount = (node.getBuilding() instanceof City) ? 2 : 1;
                        entitled.add(node.getOwner());
                        amounts.add(amount);
                        totalNeeded += amount;
                    }
                }

                // Per Catan rules: if bank can't cover total demand, nobody gets any
                if (totalNeeded > 0 && bank.canPay(resource, totalNeeded)) {
                    for (int i = 0; i < entitled.size(); i++) {
                        bank.payTo(entitled.get(i), resource, amounts.get(i));
                        logger.logResourceGain(round, entitled.get(i).getId(), resource, amounts.get(i));
                    }
                }
            }
        }
    }
}
