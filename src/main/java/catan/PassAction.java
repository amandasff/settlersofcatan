package catan;

public class PassAction implements Action {
    @Override
    public boolean isExecutable(GameState state, Player p) {
        return true;
    }

    @Override
    public void execute(GameState state, Player p) {
        // Do nothing
    }

    @Override
    public String describe() {
        return "passes";
    }
}
