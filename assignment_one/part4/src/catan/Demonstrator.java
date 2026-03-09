package catan;



public final class Demonstrator {

    public static void main(String[] args) {
        String configPath = "assignment_one/part4/config.txt";
        SimulationConfig config = new ConfigLoader().load(configPath);

        // Demonstration 1: standard simulator run from fixed board setup.
        System.out.println("=== DEMO 1: STANDARD SIMULATION ===");
        runSimulation(config, false);

        // Demonstration 2: richer run with extra resources so reviewers can observe
        // more action types such as settlement/city building instead of mostly PASS.
        System.out.println();
        System.out.println("=== DEMO 2: ENRICHED DEMONSTRATION RUN ===");
        runSimulation(config, true);
    }

    private static void runSimulation(SimulationConfig config, boolean enrichedDemo) {
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
        Game game = new Game(config, state, new ActionLogger());

        // Initial placement according to the simulator setup rules.
        game.setup();

        if (enrichedDemo) {
            grantDemoResources(players);
        }

        // Runs rounds until max rounds are reached or someone reaches 10 VP.
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