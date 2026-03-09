package catan;

public interface Action {
    void execute(GameState state, Player player);
    String describe();
    boolean spendsResources();
}