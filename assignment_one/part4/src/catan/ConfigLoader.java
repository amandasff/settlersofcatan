package catan;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ConfigLoader {

    public SimulationConfig load(String pathString) {
        int rounds = 50;
        long seed = 1L;

        Path path = resolveConfigPath(pathString);

        if (path == null) {
            System.out.println("Using default config because file could not be read.");
            return new SimulationConfig(rounds, seed);
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=", 2);
                if (parts.length != 2) {
                    continue;
                }

                String key = parts[0].trim();
                String value = parts[1].trim();

                if (key.equalsIgnoreCase("rounds")) {
                    rounds = Integer.parseInt(value);
                } else if (key.equalsIgnoreCase("seed")) {
                    seed = Long.parseLong(value);
                }
            }

            System.out.println("Loaded config from: " + path.toAbsolutePath());
            return new SimulationConfig(rounds, seed);

        } catch (IOException | NumberFormatException e) {
            System.out.println("Using default config because file could not be read.");
            return new SimulationConfig(rounds, seed);
        }
    }

    private Path resolveConfigPath(String pathString) {
        Path[] candidates = new Path[] {
                Paths.get(pathString),
                Paths.get("assignment_one/part4/config.txt"),
                Paths.get("assignment_one", "part4", "assignment_one/part4/config.txt"),
                Paths.get("part4", "assignment_one/part4/config.txt")
        };

        for (Path candidate : candidates) {
            if (Files.exists(candidate) && Files.isRegularFile(candidate)) {
                return candidate;
            }
        }

        return null;
    }
}