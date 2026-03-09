package catan;

public final class Demonstrator {

    public static void main(String[] args) {
        // Load the simulation configuration (number of rounds, optional seed, etc.)
        SimulationConfig config = new ConfigLoader().load("config.txt");

        // Create the fixed board layout and build the runtime board from it.
        // This demonstrates R1.1: a valid, hard-wired map using the specified IDs.
        BoardLayout layout = BoardLayout.createDefaultLayout();
        Board board = new Board(layout);

        // Create the shared bank and the four randomly acting agents required by R1.2.
        ResourceBank bank = new ResourceBank();
        Player[] players = new Player[] {
                Player.randomAgent(0),
                Player.randomAgent(1),
                Player.randomAgent(2),
                Player.randomAgent(3)
        };

        // Bundle the live simulation state so actions/rules can access the board, bank, and players.
        GameState state = new GameState(board, bank, players);

        // Create the main game controller that will run setup, turns, rule checks, logging, and termination.
        Game game = new Game(config, state, new ActionLogger());

        // Perform initial placement/setup before the main loop starts.
        game.setup();

        // Run the simulation until max rounds are reached or a player reaches 10 VP.
        game.run();
    }
}