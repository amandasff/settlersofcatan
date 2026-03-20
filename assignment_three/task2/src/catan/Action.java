package catan;

/*
 * Assignment 3 changes:
 * - evolved Action into the Command abstraction for undo/redo
 * - each concrete action must now support undo in addition to execute
 */
public interface Action {
    boolean isExecutable(GameState state, Player player);
    void execute(GameState state, Player player);
    void undo(GameState state, Player player);
    String describe();
}