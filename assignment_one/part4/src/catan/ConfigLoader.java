package catan;

import java.io.BufferedReader;
import java.io.FileReader;

public final class ConfigLoader {
    public SimulationConfig load(String path) {
        int rounds = 50;
        long seed = 1L;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=");
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
        } catch (Exception e) {
            System.out.println("Using default config because file could not be read.");
        }

        return new SimulationConfig(rounds, seed);
    }
}