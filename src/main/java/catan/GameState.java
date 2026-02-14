package catan;

public class GameState {
    private final Board board;
    private final ResourceBank bank;
    private final Player[] players;
    private int currentRoll;

    public GameState(Board board, ResourceBank bank, Player[] players) {
        this.board = board;
        this.bank = bank;
        this.players = players;
    }

    public Board getBoard() { return board; }
    public ResourceBank getBank() { return bank; }
    public Player[] getPlayers() { return players; }
    public int getCurrentRoll() { return currentRoll; }
    public void setCurrentRoll(int roll) { this.currentRoll = roll; }
}
