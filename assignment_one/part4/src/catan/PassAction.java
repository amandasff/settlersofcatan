package catan;

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
    public String describe() {
        return "PASS";
    }
}