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
                Player.randomAgent(0, config.getSeed()),
                Player.randomAgent(1, config.getSeed() + 1),
                Player.randomAgent(2, config.getSeed() + 2),
                Player.randomAgent(3, config.getSeed() + 3)
        };

        GameState state = new GameState(board, bank, players);

        Game game = new Game(
                config,
                state,
                new ActionLogger(),
                0,
                true,
                "assignment_two/task3/visualizer/state.json"
        );

        game.setup();
        grantDemoResources(players);

        System.out.println("=== Assignment 2 Demonstration ===");
        System.out.println("Player 0 is the human player.");
        System.out.println("Try Roll, List, Build road [from,to], Build settlement <nodeId>, Build city <nodeId>, and Go.");
        System.out.println("The visualizer updates from state.json while the simulation runs.");
        System.out.println();

        game.run();
    }

    private static void grantDemoResources(Player[] players) {
        Player human = players[0];

        human.getHand().add(ResourceType.BRICK, 4);
        human.getHand().add(ResourceType.LUMBER, 4);
        human.getHand().add(ResourceType.WOOL, 2);
        human.getHand().add(ResourceType.GRAIN, 4);
        human.getHand().add(ResourceType.ORE, 3);

        for (int i = 1; i < players.length; i++) {
            players[i].getHand().add(ResourceType.BRICK, 1);
            players[i].getHand().add(ResourceType.LUMBER, 1);
            players[i].getHand().add(ResourceType.WOOL, 1);
            players[i].getHand().add(ResourceType.GRAIN, 1);
            players[i].getHand().add(ResourceType.ORE, 1);
        }
    }
}