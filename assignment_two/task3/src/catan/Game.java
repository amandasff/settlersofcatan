package catan;

import java.util.List;
import java.util.Scanner;

/**
 * Main simulation controller.
 *
 * Assignment 2 changes:
 * - one player may act as a human through console commands
 * - parser-driven commands for roll/go/list/build
 * - step-forward support using "go"
 * - state.json export for the instructor visualizer
 * - robber support on roll 7
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

    private final Scanner scanner;
    private final CommandParser parser;
    private final GameStateExporter stateExporter;
    private final RobberController robberController;

    private final int humanPlayerId;
    private final boolean stepMode;

    public Game(SimulationConfig config, GameState state, ActionLogger logger) {
        this(config, state, logger, 0, true, "assignment_two/task3/visualizer/state.json");
    }

    public Game(
            SimulationConfig config,
            GameState state,
            ActionLogger logger,
            int humanPlayerId,
            boolean stepMode,
            String stateJsonPath
    ) {
        this.config = config;
        this.state = state;
        this.logger = logger;

        this.dice = new Dice(config.getSeed());
        this.actionGenerator = new ActionGenerator();
        this.productionEngine = new ResourceProductionEngine();
        this.setupManager = new SetupManager(new RuleEngine(), config.getSeed());

        this.scanner = new Scanner(System.in);
        this.parser = new CommandParser();
        this.stateExporter = new GameStateExporter(stateJsonPath);
        this.robberController = new RobberController(config.getSeed());

        this.humanPlayerId = humanPlayerId;
        this.stepMode = stepMode;
    }

    public void setup() {
        setupManager.placeInitialPieces(state);
        stateExporter.export(state);
    }

    public void run() {
        boolean firstTurn = true;

        for (int round = 1; round <= config.getTurns(); round++) {
            state.setCurrentRound(round);

            for (int i = 0; i < PLAYER_COUNT; i++) {
                Player player = state.getPlayers()[i];
                state.setCurrentPlayerIndex(i);

                if (stepMode && !firstTurn) {
                    waitForStepForward();
                }
                firstTurn = false;

                if (player.getId() == humanPlayerId) {
                    playHumanTurn(round, player);
                } else {
                    playAgentTurn(round, player);
                }

                if (checkTermination()) {
                    logger.logEndOfRound(round, state.getPlayers());
                    stateExporter.export(state);
                    return;
                }
            }

            logger.logEndOfRound(round, state.getPlayers());
            stateExporter.export(state);

            if (checkTermination()) {
                return;
            }
        }
    }

    private void waitForStepForward() {
        System.out.println("Type 'Go' to proceed to the next player's turn.");
        while (true) {
            String input = scanner.nextLine();
            try {
                HumanCommand cmd = parser.parse(input);
                if (cmd.getType() == CommandType.GO) {
                    return;
                }
            } catch (IllegalArgumentException ignored) {
            }
            System.out.println("Please type 'Go'.");
        }
    }

    private void playAgentTurn(int round, Player player) {
        int roll = dice.roll();
        state.setCurrentRoll(roll);
        logger.logRoll(round, player.getId(), roll);

        if (roll == 7) {
            robberController.handleRollOfSeven(state, player);
            state.setLastAction("ROBBER");
        } else {
            productionEngine.produce(state, roll);
            state.setLastAction("ROLL " + roll);
        }

        List<Action> legalActions = actionGenerator.getExecutableActions(state, player, false);
        Action chosen = player.getStrategy().select(legalActions);
        if (chosen == null) {
            chosen = new PassAction();
        }

        chosen.execute(state, player);
        state.setLastAction(chosen.describe());
        logger.logAction(round, player.getId(), chosen.describe());

        stateExporter.export(state);
    }

    private void playHumanTurn(int round, Player player) {
        System.out.println("=== HUMAN TURN: P" + player.getId() + " ===");

        doHumanRoll(round, player);

        while (true) {
            System.out.println("Enter command: Roll | Go | List | Build settlement <nodeId> | Build city <nodeId> | Build road [from,to]");
            String input = scanner.nextLine();

            HumanCommand cmd;
            try {
                cmd = parser.parse(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid command.");
                continue;
            }

            switch (cmd.getType()) {
                case LIST -> {
                    System.out.println("Hand: " + player.getHand());
                }
                case GO -> {
                    state.setLastAction("PASS");
                    logger.logAction(round, player.getId(), "PASS");
                    stateExporter.export(state);
                    return;
                }
                case BUILD_SETTLEMENT -> {
                    if (tryExecuteSettlement(round, player, cmd.getNodeId())) {
                        stateExporter.export(state);
                    } else {
                        System.out.println("That settlement build is not currently legal.");
                    }
                }
                case BUILD_CITY -> {
                    if (tryExecuteCity(round, player, cmd.getNodeId())) {
                        stateExporter.export(state);
                    } else {
                        System.out.println("That city upgrade is not currently legal.");
                    }
                }
                case BUILD_ROAD -> {
                    if (tryExecuteRoad(round, player, cmd.getFromNodeId(), cmd.getToNodeId())) {
                        stateExporter.export(state);
                    } else {
                        System.out.println("That road build is not currently legal.");
                    }
                }
                case ROLL -> {
                    System.out.println("You have already rolled this turn.");
                }
            }
        }
    }

    private void doHumanRoll(int round, Player player) {
        while (true) {
            System.out.println("Type 'Roll' to roll the dice, or 'List' to inspect your hand.");
            String input = scanner.nextLine();

            HumanCommand cmd;
            try {
                cmd = parser.parse(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid command.");
                continue;
            }

            if (cmd.getType() == CommandType.LIST) {
                System.out.println("Hand: " + player.getHand());
                continue;
            }

            if (cmd.getType() != CommandType.ROLL) {
                System.out.println("You must roll before taking other actions.");
                continue;
            }

            int roll = dice.roll();
            state.setCurrentRoll(roll);
            logger.logRoll(round, player.getId(), roll);

            if (roll == 7) {
                robberController.handleRollOfSeven(state, player);
                state.setLastAction("ROBBER");
            } else {
                productionEngine.produce(state, roll);
                state.setLastAction("ROLL " + roll);
            }

            stateExporter.export(state);
            return;
        }
    }

    private boolean tryExecuteSettlement(int round, Player player, int nodeId) {
        List<Action> legalActions = actionGenerator.getExecutableActions(state, player, false);
        for (Action action : legalActions) {
            if (action instanceof BuildSettlementAction settlementAction) {
                if (settlementAction.getTarget().getId() == nodeId) {
                    settlementAction.execute(state, player);
                    state.setLastAction(settlementAction.describe());
                    logger.logAction(round, player.getId(), settlementAction.describe());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryExecuteCity(int round, Player player, int nodeId) {
        List<Action> legalActions = actionGenerator.getExecutableActions(state, player, false);
        for (Action action : legalActions) {
            if (action instanceof UpgradeToCityAction cityAction) {
                if (cityAction.getTarget().getId() == nodeId) {
                    cityAction.execute(state, player);
                    state.setLastAction(cityAction.describe());
                    logger.logAction(round, player.getId(), cityAction.describe());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryExecuteRoad(int round, Player player, int fromNodeId, int toNodeId) {
        List<Action> legalActions = actionGenerator.getExecutableActions(state, player, false);
        for (Action action : legalActions) {
            if (action instanceof BuildRoadAction roadAction) {
                Edge edge = roadAction.getTarget();
                boolean matchesForward = edge.getNodeAId() == fromNodeId && edge.getNodeBId() == toNodeId;
                boolean matchesReverse = edge.getNodeAId() == toNodeId && edge.getNodeBId() == fromNodeId;

                if (matchesForward || matchesReverse) {
                    roadAction.execute(state, player);
                    state.setLastAction(roadAction.describe());
                    logger.logAction(round, player.getId(), roadAction.describe());
                    return true;
                }
            }
        }
        return false;
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