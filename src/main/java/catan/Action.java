package catan;

public interface Action {
    boolean isExecutable(GameState state, Player p);
    void execute(GameState state, Player p);
    String describe();
}
