package catan;

public final class ActionLogger {
    public void logAction(int round, int playerId, String actionText) {
        System.out.println("[Round " + round + "] P" + playerId + " " + actionText);
    }

    public void logEndOfRound(int round, Player[] players) {
        StringBuilder sb = new StringBuilder();
        sb.append("[EndRound ").append(round).append("] VP: ");
        for (Player player : players) {
            sb.append("P")
                    .append(player.getId())
                    .append("=")
                    .append(player.getVictoryPoints())
                    .append(" ");
        }
        System.out.println(sb.toString().trim());
    }
}