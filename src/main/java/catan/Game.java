package catan;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Game {
    private final Player[] players;
    private final Board board;
    private int currentRound;
    private final int maxRounds;
    private final int targetVictoryPoints;
    private final Dice dice;
    private final RuleEngine rules;
    private final ActionGenerator actionSelector;
    private final ActionLogger logger;
    private final ResourceProductionEngine productionEngine;
    private final SetupManager setupManager;
    private final GameState gameState;
    private final ResourceBank bank;
    private final BoardVisualizer visualizer;
    private final CatanBoardGUI gui;
    private final LongestRoadTracker longestRoadTracker;
    private int[] lastVPSnapshot;
    private Player winner;

    public Game(int maxRounds) {
        this.maxRounds = maxRounds;
        this.targetVictoryPoints = 10;
        this.currentRound = 0;

        // Create board
        BoardLayout layout = BoardLayout.createStandardLayout();
        this.board = new Board(layout);

        // Create bank
        this.bank = new ResourceBank();

        // Create 4 players with random strategies
        this.players = new Player[4];
        for (int i = 0; i < 4; i++) {
            players[i] = new Player(i + 1, new RandomStrategy());
        }

        // Create game components
        this.dice = new Dice();
        this.rules = new RuleEngine(board);
        this.actionSelector = new ActionGenerator(rules);
        this.logger = new ActionLogger();
        this.productionEngine = new ResourceProductionEngine(board, bank, logger);
        this.setupManager = new SetupManager();
        this.gameState = new GameState(board, bank, players);
        this.visualizer = new BoardVisualizer();
        this.gui = new CatanBoardGUI();
        this.longestRoadTracker = new LongestRoadTracker();
        this.lastVPSnapshot = new int[4];
    }

    public void setup() {
        // Print the board layout so the reviewer can see terrain and token placement
        logger.logBoardSetup(board);

        // Run the two-round setup phase where each player places settlements and roads
        setupManager.placeInitialPieces(players, board, rules, bank, logger);

        // Check longest road after setup
        longestRoadTracker.update(players, board, logger, 0);

        // Show the board after setup so the reviewer can see initial placements
        visualizer.printBoard(board, "BOARD AFTER SETUP");
        gui.update(board, players, 0, "Setup complete - game starting!");

        // Snapshot VP after setup for change detection
        for (int i = 0; i < players.length; i++) {
            lastVPSnapshot[i] = players[i].getVP();
        }
    }

    public void run() {
        setup();

        logger.logSectionHeader("GAME BEGINS");
        System.out.println();

        boolean done = false;
        while (!done) {
            currentRound++;
            for (Player p : players) {
                playTurn(p);
                if (hasWinner()) {
                    done = true;
                    break;
                }
            }
            logger.logEndOfRound(currentRound, players);

            // Show ASCII board in console if VP changed or at periodic checkpoints
            if (vpChanged()) {
                visualizer.printBoard(board, "BOARD (Round " + currentRound + " - VP changed)");
                snapshotVP();
            } else if (currentRound % 25 == 0) {
                visualizer.printBoard(board, "BOARD (Round " + currentRound + " checkpoint)");
            }

            if (currentRound >= maxRounds) {
                done = true;
            }
        }

        // Show the final board state
        visualizer.printBoard(board, "FINAL BOARD");
        String endMsg = winner != null
            ? "Game Over! Player " + winner.getId() + " wins with " + winner.getVP() + " VP!"
            : "Game Over! No winner after " + maxRounds + " rounds.";
        gui.update(board, players, currentRound, endMsg);
    }

    private boolean vpChanged() {
        for (int i = 0; i < players.length; i++) {
            if (players[i].getVP() != lastVPSnapshot[i]) return true;
        }
        return false;
    }

    private void snapshotVP() {
        for (int i = 0; i < players.length; i++) {
            lastVPSnapshot[i] = players[i].getVP();
        }
    }

    private boolean hasWinner() {
        for (Player p : players) {
            if (p.getVP() >= targetVictoryPoints) {
                winner = p;
                return true;
            }
        }
        return false;
    }

    private void playTurn(Player p) {
        // Roll dice
        int roll = dice.roll();
        gameState.setCurrentRoll(roll);

        // Log the dice roll
        logger.logDiceRoll(currentRound, p.getId(), roll);

        // When a 7 is rolled, all players with more than 7 cards must discard half
        if (roll == 7) {
            logger.logNoProduction(currentRound, p.getId());
            for (Player player : players) {
                if (player.handSize() > 7) {
                    int toDiscard = player.handSize() / 2;
                    discardRandomCards(player, toDiscard);
                    logger.logDiscard(currentRound, player.getId(), toDiscard);
                }
            }
        }
        productionEngine.produce(roll, players, currentRound);

        // Player can take multiple build actions per turn
        // Keep going until player passes or no actions remain
        String lastAction = "rolls dice: " + roll;
        while (true) {
            boolean mb = mustBuild(p);
            List<Action> actions = actionSelector.getExecutableActions(gameState, p, mb);

            if (actions.isEmpty()) break;

            Action chosen = p.chooseAction(actions);
            chosen.execute(gameState, p);
            lastAction = chosen.describe();
            logger.logAction(currentRound, p.getId(), lastAction
                + " [hand: " + p.getHand().toString() + "]");

            // Recalculate longest road after building roads/settlements/cities
            if (chosen instanceof BuildRoadAction || chosen instanceof BuildSettlementAction
                    || chosen instanceof UpgradeToCityAction) {
                longestRoadTracker.update(players, board, logger, currentRound);
            }

            if (chosen instanceof PassAction) break;
        }

        // Update GUI after every player turn
        gui.update(board, players, currentRound,
            "Round " + currentRound + " / Player " + p.getId() + ": " + lastAction);
    }

    private void discardRandomCards(Player player, int count) {
        // Build a list of all cards in hand, shuffle, and discard the first 'count'
        List<ResourceType> cards = new ArrayList<>();
        for (ResourceType r : ResourceType.values()) {
            int held = player.getHand().get(r);
            for (int i = 0; i < held; i++) {
                cards.add(r);
            }
        }
        Collections.shuffle(cards);
        for (int i = 0; i < count && i < cards.size(); i++) {
            player.getHand().remove(cards.get(i), 1);
            bank.returnResources(cards.get(i), 1);
        }
    }

    private boolean mustBuild(Player p) {
        return p.handSize() > 7;
    }

    public Player getWinner() { return winner; }
}
