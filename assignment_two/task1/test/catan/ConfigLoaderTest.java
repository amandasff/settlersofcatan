package catan;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

    @Test
    void loadsValidTurnsAndSeedFromFile() throws Exception {
        Path temp = Files.createTempFile("config-valid", ".txt");
        Files.writeString(temp, "turns: 25\nseed: 99\n");

        SimulationConfig config = new ConfigLoader().load(temp.toString());

        assertEquals(25, config.getTurns());
        assertEquals(99L, config.getSeed());
    }


    @Test
    void ignoresBlankLinesAndComments() throws Exception {
        Path temp = Files.createTempFile("config-comments", ".txt");
        Files.writeString(temp, "\n# comment line\nturns: 12\n\nseed: 7\n");

        SimulationConfig config = new ConfigLoader().load(temp.toString());

        assertEquals(12, config.getTurns());
        assertEquals(7L, config.getSeed());
    }

    @Test
    void acceptsMinimumBoundaryTurnsOne() throws Exception {
        Path temp = Files.createTempFile("config-min", ".txt");
        Files.writeString(temp, "turns: 1\nseed: 5\n");

        SimulationConfig config = new ConfigLoader().load(temp.toString());

        assertEquals(1, config.getTurns());
        assertEquals(5L, config.getSeed());
    }

    @Test
    void acceptsMaximumBoundaryTurns8192() throws Exception {
        Path temp = Files.createTempFile("config-max", ".txt");
        Files.writeString(temp, "turns: 8192\nseed: 5\n");

        SimulationConfig config = new ConfigLoader().load(temp.toString());

        assertEquals(8192, config.getTurns());
        assertEquals(5L, config.getSeed());
    }

    @Test
    void rejectsTurnsBelowMinimumBoundary() throws Exception {
        Path temp = Files.createTempFile("config-low", ".txt");
        Files.writeString(temp, "turns: 0\nseed: 3\n");

        assertThrows(IllegalArgumentException.class,
                () -> new ConfigLoader().load(temp.toString()));
    }

    @Test
    void rejectsTurnsAboveMaximumBoundary() throws Exception {
        Path temp = Files.createTempFile("config-high", ".txt");
        Files.writeString(temp, "turns: 8193\nseed: 3\n");

        assertThrows(IllegalArgumentException.class,
                () -> new ConfigLoader().load(temp.toString()));
    }
}