package catan;

import java.util.Scanner;

// Entry point for the Catan simulator.
// Prompts user for settings, runs the game, and shows results.
public class Demonstrator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String configPath = (args.length > 0) ? args[0] : "config.txt";
        SimulationConfig defaultConfig = ConfigLoader.load(configPath);

        printWelcome();
        int maxRounds = promptForRounds(scanner, defaultConfig.getTurns());

        System.out.println();
        System.out.println("  Settings:");
        System.out.println("    Players:    4 (random AI agents)");
        System.out.println("    Max rounds: " + maxRounds);
        System.out.println("    Win target: 10 Victory Points");
        System.out.println();

        promptToStart(scanner);
        printLegend();

        Game game = new Game(maxRounds);
        game.run();

        Player winner = game.getWinner();
        System.out.println();
        System.out.println("============================================");
        if (winner != null) {
            System.out.println("  GAME OVER - Player " + winner.getId()
                + " wins with " + winner.getVP() + " VP!");
        } else {
            System.out.println("  GAME OVER - No winner after " + maxRounds + " rounds.");
        }
        System.out.println("============================================");
        System.out.println();
        System.out.println("Close the board window or press Ctrl+C to exit.");

        scanner.close();
    }

    private static void printWelcome() {
        System.out.println();
        System.out.println("============================================");
        System.out.println("    SETTLERS OF CATAN - Simulator");
        System.out.println("============================================");
        System.out.println();
        System.out.println("  Welcome! This simulator runs a game of");
        System.out.println("  Settlers of Catan with 4 AI players.");
        System.out.println("  A graphical board will open in a separate");
        System.out.println("  window with pause/speed controls.");
        System.out.println();
    }

    private static int promptForRounds(Scanner scanner, int defaultRounds) {
        System.out.print("  Enter desired rounds (1-8192): ");

        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("  -> Using default: " + defaultRounds + " rounds.");
            return defaultRounds;
        }

        try {
            int rounds = Integer.parseInt(input);
            if (rounds < 1 || rounds > 8192) {
                System.out.println("  -> Out of range. Using default: " + defaultRounds + " rounds.");
                return defaultRounds;
            }
            System.out.println("  -> Set to " + rounds + " rounds.");
            return rounds;
        } catch (NumberFormatException e) {
            System.out.println("  -> Invalid input. Using default: " + defaultRounds + " rounds.");
            return defaultRounds;
        }
    }

    private static void promptToStart(Scanner scanner) {
        System.out.print("  Press ENTER to start the simulation...");
        scanner.nextLine();
        System.out.println();
    }

    private static void printLegend() {
        System.out.println("============================================");
        System.out.println("  CONSOLE OUTPUT LEGEND:");
        System.out.println("--------------------------------------------");
        System.out.println("  Terrain: FLD=Fields(Grain)  FOR=Forest(Lumber)");
        System.out.println("           PAS=Pasture(Wool)  HIL=Hills(Brick)");
        System.out.println("           MTN=Mountains(Ore) DST=Desert");
        System.out.println();
        System.out.println("  Output:  [Round] / [ID]: [Action] [hand: ...]");
        System.out.println("  VP:      Settlement = 1 VP, City = 2 VP");
        System.out.println("           First to 10 VP wins!");
        System.out.println();
        System.out.println("  Costs:   Road       = 1 Brick + 1 Lumber");
        System.out.println("           Settlement = 1 Brick + 1 Lumber");
        System.out.println("                      + 1 Wool  + 1 Grain");
        System.out.println("           City       = 3 Ore   + 2 Grain");
        System.out.println();
        System.out.println("  Excluded: Harbours, Trading, Dev Cards, Robber");
        System.out.println("============================================");
        System.out.println();
    }
}
