package catan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Assignment 3 changes:
 * - extended parser support for undo and redo commands
 * - this allows the human player to trigger Command history operations
 */
public final class CommandParser {
    private static final Pattern ROLL_PATTERN =
            Pattern.compile("^\\s*roll\\s*$", Pattern.CASE_INSENSITIVE);

    private static final Pattern GO_PATTERN =
            Pattern.compile("^\\s*go\\s*$", Pattern.CASE_INSENSITIVE);

    private static final Pattern LIST_PATTERN =
            Pattern.compile("^\\s*list\\s*$", Pattern.CASE_INSENSITIVE);

    private static final Pattern UNDO_PATTERN =
            Pattern.compile("^\\s*undo\\s*$", Pattern.CASE_INSENSITIVE);

    private static final Pattern REDO_PATTERN =
            Pattern.compile("^\\s*redo\\s*$", Pattern.CASE_INSENSITIVE);

    private static final Pattern BUILD_SETTLEMENT_PATTERN =
            Pattern.compile("^\\s*build\\s+settlement\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);

    private static final Pattern BUILD_CITY_PATTERN =
            Pattern.compile("^\\s*build\\s+city\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);

    private static final Pattern BUILD_ROAD_PATTERN =
            Pattern.compile("^\\s*build\\s+road\\s*\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*]\\s*$",
                    Pattern.CASE_INSENSITIVE);

    public HumanCommand parse(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Command cannot be null.");
        }

        if (ROLL_PATTERN.matcher(input).matches()) {
            return HumanCommand.roll();
        }

        if (GO_PATTERN.matcher(input).matches()) {
            return HumanCommand.go();
        }

        if (LIST_PATTERN.matcher(input).matches()) {
            return HumanCommand.list();
        }

        if (UNDO_PATTERN.matcher(input).matches()) {
            return HumanCommand.undo();
        }

        if (REDO_PATTERN.matcher(input).matches()) {
            return HumanCommand.redo();
        }

        Matcher settlementMatcher = BUILD_SETTLEMENT_PATTERN.matcher(input);
        if (settlementMatcher.matches()) {
            int nodeId = Integer.parseInt(settlementMatcher.group(1));
            return HumanCommand.buildSettlement(nodeId);
        }

        Matcher cityMatcher = BUILD_CITY_PATTERN.matcher(input);
        if (cityMatcher.matches()) {
            int nodeId = Integer.parseInt(cityMatcher.group(1));
            return HumanCommand.buildCity(nodeId);
        }

        Matcher roadMatcher = BUILD_ROAD_PATTERN.matcher(input);
        if (roadMatcher.matches()) {
            int fromNodeId = Integer.parseInt(roadMatcher.group(1));
            int toNodeId = Integer.parseInt(roadMatcher.group(2));
            return HumanCommand.buildRoad(fromNodeId, toNodeId);
        }

        throw new IllegalArgumentException("Invalid command: " + input);
    }
}