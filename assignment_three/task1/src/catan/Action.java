package catan;

public interface Action {
    boolean isExecutable(GameState state, Player player);
    void execute(GameState state, Player player);
    void undo(GameState state, Player player);
    String describe();
}