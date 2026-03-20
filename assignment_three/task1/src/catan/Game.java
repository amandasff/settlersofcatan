package catan;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;

/*
 * Assignment 3 changes:
 * - introduced Command history management through undo and redo stacks
 * - centralized action execution so executed commands are recorded uniformly
 * - extended human turn handling to support undo and redo commands
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

    private final Deque<ExecutedAction> undoStack;
    private final Deque<ExecutedAction> redoStack;

    private static final class ExecutedAction {
        private final Action action;
        private final Player player;

        private ExecutedAction(Action action, Player player) {
            this.action = action;
            this.player = player;
        }
    }

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

        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
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
                clearTurnHistory();

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

        executeRecordedAction(round, player, chosen);

        stateExporter.export(state);
    }

    private void playHumanTurn(int round, Player player) {
        System.out.println("=== HUMAN TURN: P" + player.getId() + " ===");

        doHumanRoll(round, player);
        printLegalActions(player);

        while (true) {
            System.out.println("Enter command: Roll | Go | List | Undo | Redo | Build settlement <nodeId> | Build city <nodeId> | Build road [from,to]");
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
                case UNDO -> {
                    if (undoLastAction(round)) {
                        stateExporter.export(state);
                        printLegalActions(player);
                    } else {
                        System.out.println("Nothing to undo.");
                    }
                }
                case REDO -> {
                    if (redoLastAction(round)) {
                        stateExporter.export(state);
                        printLegalActions(player);
                    } else {
                        System.out.println("Nothing to redo.");
                    }
                }
                case GO -> {
                    executeRecordedAction(round, player, new PassAction());
                    stateExporter.export(state);
                    return;
                }
                case BUILD_SETTLEMENT -> {
                    if (tryExecuteSettlement(round, player, cmd.getNodeId())) {
                        stateExporter.export(state);
                        printLegalActions(player);
                    } else {
                        System.out.println("That settlement build is not currently legal.");
                    }
                }
                case BUILD_CITY -> {
                    if (tryExecuteCity(round, player, cmd.getNodeId())) {
                        stateExporter.export(state);
                        printLegalActions(player);
                    } else {
                        System.out.println("That city upgrade is not currently legal.");
                    }
                }
                case BUILD_ROAD -> {
                    if (tryExecuteRoad(round, player, cmd.getFromNodeId(), cmd.getToNodeId())) {
                        stateExporter.export(state);
                        printLegalActions(player);
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

    // added this for myself to see what course of actions I can take for quick testing
    private void printLegalActions(Player player) {
        List<Action> legalActions = actionGenerator.getExecutableActions(state, player, false);
        System.out.println("Legal actions right now:");
        for (Action action : legalActions) {
            if (action instanceof BuildRoadAction roadAction) {
                Edge edge = roadAction.getTarget();
                System.out.println("  BUILD_ROAD [" + edge.getNodeAId() + "," + edge.getNodeBId() + "]");
            } else if (action instanceof BuildSettlementAction settlementAction) {
                System.out.println("  BUILD_SETTLEMENT node=" + settlementAction.getTarget().getId());
            } else if (action instanceof UpgradeToCityAction cityAction) {
                System.out.println("  BUILD_CITY node=" + cityAction.getTarget().getId());
            } else {
                System.out.println("  " + action.describe());
            }
        }
    }

    private boolean tryExecuteSettlement(int round, Player player, int nodeId) {
        List<Action> legalActions = actionGenerator.getExecutableActions(state, player, false);
        for (Action action : legalActions) {
            if (action instanceof BuildSettlementAction settlementAction) {
                if (settlementAction.getTarget().getId() == nodeId) {
                    executeRecordedAction(round, player, settlementAction);
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
                    executeRecordedAction(round, player, cityAction);
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
                    executeRecordedAction(round, player, roadAction);
                    return true;
                }
            }
        }
        return false;
    }

    private void executeRecordedAction(int round, Player player, Action action) {
        action.execute(state, player);
        undoStack.push(new ExecutedAction(action, player));
        redoStack.clear();
        state.setLastAction(action.describe());
        logger.logAction(round, player.getId(), action.describe());
    }

    private boolean undoLastAction(int round) {
        if (undoStack.isEmpty()) {
            return false;
        }

        ExecutedAction executed = undoStack.pop();
        executed.action.undo(state, executed.player);
        redoStack.push(executed);

        state.setLastAction("UNDO " + executed.action.describe());
        logger.logAction(round, executed.player.getId(), "UNDO " + executed.action.describe());
        return true;
    }

    private boolean redoLastAction(int round) {
        if (redoStack.isEmpty()) {
            return false;
        }

        ExecutedAction executed = redoStack.pop();
        executed.action.execute(state, executed.player);
        undoStack.push(executed);

        state.setLastAction("REDO " + executed.action.describe());
        logger.logAction(round, executed.player.getId(), "REDO " + executed.action.describe());
        return true;
    }

    private void clearTurnHistory() {
        undoStack.clear();
        redoStack.clear();
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