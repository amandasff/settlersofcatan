package catan;

public final class Demonstrator {

    private Demonstrator() {
    }

    public static void main(String[] args) {
        String configPath = "assignment_two/task3/config.txt";
        SimulationConfig config = new ConfigLoader().load(configPath);

        BoardLayout layout = BoardLayout.createDefaultLayout();
        Board board = new Board(layout);
        ResourceBank bank = new ResourceBank();

        Player[] players = new Player[] {
                Player.randomAgent(0, config.getSeed() + 0),
                Player.randomAgent(1, config.getSeed() + 1),
                Player.randomAgent(2, config.getSeed() + 2),
                Player.randomAgent(3, config.getSeed() + 3)
        };

        GameState state = new GameState(board, bank, players);

        // Player 0 is treated as the human player.
        // Step mode is enabled, so the simulator waits for "Go"
        // before advancing to the next player's turn.
        Game game = new Game(
                config,
                state,
                new ActionLogger(),
                0,
                true,
                "assignment_two/task3/visualizer/state.json"
        );

        // Initial setup places the starting settlements and roads.
        game.setup();

        // Give the players some extra resources so the human can
        // more easily demonstrate build commands during the run.
        grantDemoResources(players);

        System.out.println("=== Assignment 2 Demonstration ===");
        System.out.println("Player 0 is the human player.");
        System.out.println("Use Roll, List, Build ..., and Go in the console.");
        System.out.println("The visualizer reads state.json during the run.");
        System.out.println();

        game.run();
    }

    private static void grantDemoResources(Player[] players) {
        for (Player player : players) {
            player.getHand().add(ResourceType.BRICK, 2);
            player.getHand().add(ResourceType.LUMBER, 2);
            player.getHand().add(ResourceType.WOOL, 2);
            player.getHand().add(ResourceType.GRAIN, 2);
            player.getHand().add(ResourceType.ORE, 2);
        }
    }
}