package catan;

/*
 * Assignment 3 changes:
 * - PassAction now fully participates in the Command pattern
 * - added undo so it matches the Action command interface
 */
public final class PassAction implements Action {
    
    @Override
    public boolean isExecutable(GameState state, Player player) {
        return true;
    }

    @Override
    public void execute(GameState state, Player player) {
        // do nothing
    }

    @Override
    public void undo(GameState state, Player player) {
        // do nothing
    }
    
    @Override
    public String describe() {
        return "PASS";
    }
}