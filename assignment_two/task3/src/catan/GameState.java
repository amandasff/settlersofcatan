package catan;

public final class GameState {
    private final Board board;
    private final ResourceBank bank;
    private final Player[] players;

    private int currentRoll;
    private int currentRound;
    private int currentPlayerIndex;
    private String lastAction;
    private int robberTileId;

    public GameState(Board board, ResourceBank bank, Player[] players) {
        this.board = board;
        this.bank = bank;
        this.players = players;
        this.currentRoll = 0;
        this.currentRound = 0;
        this.currentPlayerIndex = 0;
        this.lastAction = "";
        this.robberTileId = -1;
    }

    public Board getBoard() {
        return board;
    }

    public ResourceBank getBank() {
        return bank;
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getCurrentRoll() {
        return currentRoll;
    }

    public void setCurrentRoll(int currentRoll) {
        this.currentRoll = currentRoll;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public int getRobberTileId() {
        return robberTileId;
    }

    public void setRobberTileId(int robberTileId) {
        this.robberTileId = robberTileId;
    }
}