package catan;

public final class PassAction implements Action {
    @Override
    public void execute(GameState state, Player player) {
        // intentionally does nothing
    }

    @Override
    public String describe() {
        return "PASS";
    }

    @Override
    public boolean spendsResources() {
        return false;
    }
}