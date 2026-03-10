package catan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandParserTest {
    private final CommandParser parser = new CommandParser();

    @Test
    void parsesRollCommand() {
        HumanCommand cmd = parser.parse("Roll");
        assertEquals(CommandType.ROLL, cmd.getType());
    }

    @Test
    void parsesGoCommand() {
        HumanCommand cmd = parser.parse("Go");
        assertEquals(CommandType.GO, cmd.getType());
    }

    @Test
    void parsesListCommand() {
        HumanCommand cmd = parser.parse("List");
        assertEquals(CommandType.LIST, cmd.getType());
    }

    @Test
    void parsesBuildSettlementCommand() {
        HumanCommand cmd = parser.parse("Build settlement 12");
        assertEquals(CommandType.BUILD_SETTLEMENT, cmd.getType());
        assertEquals(12, cmd.getNodeId());
    }

    @Test
    void parsesBuildCityCommand() {
        HumanCommand cmd = parser.parse("Build city 7");
        assertEquals(CommandType.BUILD_CITY, cmd.getType());
        assertEquals(7, cmd.getNodeId());
    }

    @Test
    void parsesBuildRoadCommand() {
        HumanCommand cmd = parser.parse("Build road [3,8]");
        assertEquals(CommandType.BUILD_ROAD, cmd.getType());
        assertEquals(3, cmd.getFromNodeId());
        assertEquals(8, cmd.getToNodeId());
    }

    @Test
    void parsingIsCaseInsensitive() {
        HumanCommand cmd = parser.parse("bUiLd RoAd [10, 11]");
        assertEquals(CommandType.BUILD_ROAD, cmd.getType());
        assertEquals(10, cmd.getFromNodeId());
        assertEquals(11, cmd.getToNodeId());
    }

    @Test
    void rejectsInvalidCommand() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("dance"));
    }

    @Test
    void rejectsMalformedRoadCommand() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("Build road 3,8"));
    }
}