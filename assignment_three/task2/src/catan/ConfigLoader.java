package catan;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ConfigLoader {

    public SimulationConfig load(String pathString) {
        Path path = resolveConfigPath(pathString);

        if (path == null) {
            throw new IllegalArgumentException("Config file could not be found: " + pathString);
        }

        Integer turns = null;
        Long seed = 1L; // default seed if not provided

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(":", 2);
                if (parts.length != 2) {
                    throw new IllegalArgumentException(
                            "Invalid config line " + lineNumber + ": " + line
                    );
                }

                String key = parts[0].trim();
                String value = parts[1].trim();

                try {
                    if (key.equalsIgnoreCase("turns")) {
                        turns = Integer.parseInt(value);
                    } else if (key.equalsIgnoreCase("seed")) {
                        seed = Long.parseLong(value);
                    } else {
                        throw new IllegalArgumentException(
                                "Unknown config key on line " + lineNumber + ": " + key
                        );
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Invalid numeric value on line " + lineNumber + ": " + line
                    );
                }
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read config file: " + pathString, e);
        }

        if (turns == null) {
            throw new IllegalArgumentException("Missing required config field: turns");
        }

        return new SimulationConfig(turns, seed);
    }

    private Path resolveConfigPath(String pathString) {
        Path[] candidates = new Path[] {
                Paths.get(pathString),
                Paths.get("assignment_two", "config.txt"),
                Paths.get("assignment_one", "part4", "config.txt")
        };

        for (Path candidate : candidates) {
            if (Files.exists(candidate) && Files.isRegularFile(candidate)) {
                return candidate;
            }
        }

        return null;
    }
}