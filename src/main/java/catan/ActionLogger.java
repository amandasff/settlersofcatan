package catan;

public class ActionLogger {

    public void logBoardSetup(Board board) {
        System.out.println("=== BOARD SETUP ===");
        for (Tile tile : board.getTiles()) {
            String tokenStr = tile.getTerrain() == TerrainType.DESERT
                ? "no token" : "token " + tile.getToken();
            System.out.println("  Tile " + tile.getId() + ": " + tile.getTerrain()
                + " (" + tokenStr + ")");
        }
        System.out.println("===================");
        System.out.println();
    }

    public void logSetupPlacement(int playerId, String description) {
        System.out.println("Setup / " + playerId + ": " + description);
    }

    public void logDiceRoll(int round, int playerId, int roll) {
        System.out.println(round + " / " + playerId + ": rolls a " + roll);
    }

    public void logResourceGain(int round, int playerId, ResourceType type, int amount) {
        System.out.println(round + " / " + playerId + ": receives " + amount + " " + type);
    }

    public void logNoProduction(int round, int playerId) {
        System.out.println(round + " / " + playerId + ": no resources produced (rolled 7)");
    }

    public void logAction(int round, int playerId, String description) {
        System.out.println(round + " / " + playerId + ": " + description);
    }

    public void logDiscard(int round, int playerId, int discarded) {
        System.out.println(round + " / " + playerId + ": discards " + discarded + " cards (over 7)");
    }

    public void logEndOfRound(int round, Player[] players) {
        StringBuilder sb = new StringBuilder();
        sb.append(round).append(" / VP: ");
        for (int i = 0; i < players.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(players[i].getId()).append("=").append(players[i].getVP());
        }
        System.out.println(sb.toString());
    }

    public void logSectionHeader(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
