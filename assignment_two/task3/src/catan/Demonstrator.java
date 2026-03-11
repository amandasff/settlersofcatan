package catan;

public final class Demonstrator {

    // Utility class only. No Demonstrator objects should ever be created.
    private Demonstrator() {
    }

    public static void main(String[] args) {
        // Loads the Task 3 config file.
        // This controls things like the turn limit and random seed used in the run.
        String configPath = "assignment_two/task3/config.txt";
        SimulationConfig config = new ConfigLoader().load(configPath);

        // Builds the fixed board used for the simulation.
        // The map is not randomly regenerated every time because using one consistent
        // setup makes testing, debugging, and demonstration easier.
        BoardLayout layout = BoardLayout.createDefaultLayout();
        Board board = new Board(layout);

        // Creates the shared resource bank for the game.
        ResourceBank bank = new ResourceBank();

        // Creates four players.
        // They are still created as random agents, but player 0 will be treated as the
        // human-controlled player by the Game object below.
        // Different seeds are used so the non-human players do not all behave identically.
        Player[] players = new Player[] {
                Player.randomAgent(0, config.getSeed() + 0),
                Player.randomAgent(1, config.getSeed() + 1),
                Player.randomAgent(2, config.getSeed() + 2),
                Player.randomAgent(3, config.getSeed() + 3)
        };

        // Bundles the board, bank, and players into one shared game state object.
        GameState state = new GameState(board, bank, players);

        // Creates the main game controller.
        // Player 0 is handled as the human player.
        // Step mode is turned on, so the simulator pauses and waits for "Go"
        // before advancing to the next player's turn.
        // The state file path is also provided here so the visualizer can keep reading
        // the updated game state while the simulation is running.
        Game game = new Game(
                config,
                state,
                new ActionLogger(),
                0,
                true,
                "assignment_two/task3/visualizer/state.json"
        );

        // Performs the initial placement phase.
        // This places the starting settlements and roads before the normal rounds begin.
        game.setup();

        // Gives every player a few extra resources.
        // This is only for the demonstration so that commands like building roads,
        // settlements, and cities are easier to try during the run.
        // It is not meant to represent the normal starting hand of the real game.
        grantDemoResources(players);

        // Prints a short explanation for the user before the simulation starts.
        System.out.println("=== Assignment 2 Demonstration ===");
        System.out.println("Player 0 is the human player.");
        System.out.println("Use Roll, List, Build ..., and Go in the console.");
        System.out.println("The visualizer reads state.json during the run.");
        System.out.println();

        // Starts the actual simulation loop.
        // From this point on, the human player can enter commands in the console
        // and the rest of the players continue taking their turns automatically.
        game.run();
    }

    private static void grantDemoResources(Player[] players) {
        // Adds a small amount of every resource to every player.
        // This makes the demonstration more useful because it increases the chance
        // that build actions can actually be shown during the run.
        for (Player player : players) {
            player.getHand().add(ResourceType.BRICK, 2);
            player.getHand().add(ResourceType.LUMBER, 2);
            player.getHand().add(ResourceType.WOOL, 2);
            player.getHand().add(ResourceType.GRAIN, 2);
            player.getHand().add(ResourceType.ORE, 2);
        }
    }
}