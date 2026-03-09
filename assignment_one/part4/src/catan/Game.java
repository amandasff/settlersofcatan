package catan;

import java.util.List;

/**
 * Main simulation controller.
 *
 * Responsibilities:
 * - perform initial setup
 * - run rounds and turns
 * - roll dice
 * - trigger resource production
 * - generate legal actions
 * - allow each random agent to choose an action
 * - log actions and round-end victory points
 * - stop when a player reaches 10 VP or the configured round limit is reached
 */
public final class Game {
    private static final int PLAYER_COUNT = 4;
    private static final int TARGET_VICTORY_POINTS = 10;

    private final SimulationConfig config;
    private final GameState state;
    private final ActionLogger logger;

    private final Dice dice;
    private final ActionGenerator actionGenerator;
    private final ResourceProductionEngine productionEngine;
    private final SetupManager setupManager;

    public Game(SimulationConfig config, GameState state, ActionLogger logger) {
        this.config = config;
        this.state = state;
        this.logger = logger;

        this.dice = new Dice(config.getSeed());
        this.actionGenerator = new ActionGenerator();
        this.productionEngine = new ResourceProductionEngine();
        this.setupManager = new SetupManager(new RuleEngine(), config.getSeed());
    }

    public void setup() {
        setupManager.placeInitialPieces(state);
    }

    public void run() {
        for (int round = 1; round <= config.getRounds(); round++) {
            for (int i = 0; i < PLAYER_COUNT; i++) {
                Player player = state.getPlayers()[i];
                playTurn(round, player);

                if (checkTermination()) {
                    logger.logEndOfRound(round, state.getPlayers());
                    return;
                }
            }

            logger.logEndOfRound(round, state.getPlayers());

            if (checkTermination()) {
                return;
            }
        }
    }

    private void playTurn(int round, Player player) {
        int roll = dice.roll();
        state.setCurrentRoll(roll);

        // Resource production is skipped for roll 7 in this simplified simulator.
        productionEngine.produce(state, roll);

        // R1.8: if the player has more than 7 resources,
        // they must try to spend them by building something.
        boolean mustBuild = player.handSize() > 7;

        List<Action> legalActions = actionGenerator.getExecutableActions(state, player, mustBuild);
        Action chosen = player.getStrategy().select(legalActions);

        chosen.execute(state, player);
        logger.logAction(round, player.getId(), chosen.describe());
    }

    private boolean checkTermination() {
        for (Player player : state.getPlayers()) {
            if (player.getVictoryPoints() >= TARGET_VICTORY_POINTS) {
                return true;
            }
        }
        return false;
    }
}