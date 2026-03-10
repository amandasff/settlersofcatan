package catan;

public final class ActionLogger {

    public void logAction(int round, int playerId, String actionText) {
        System.out.println("[" + round + "] / [P" + playerId + "]: " + actionText);
    }

    public void logRoll(int round, int playerId, int roll) {
        System.out.println("[" + round + "] / [P" + playerId + "]: ROLL " + roll);
    }

    public void logEndOfRound(int round, Player[] players) {
        StringBuilder sb = new StringBuilder();
        sb.append("End of round ").append(round).append(" | VP: ");
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