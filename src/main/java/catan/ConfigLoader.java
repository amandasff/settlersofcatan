package catan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConfigLoader {
    public static SimulationConfig load(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("turns:")) {
                    String value = line.substring("turns:".length()).trim();
                    int turns = Integer.parseInt(value);
                    if (turns < 1 || turns > 8192) {
                        throw new IllegalArgumentException("turns must be between 1 and 8192");
                    }
                    return new SimulationConfig(turns);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + e.getMessage());
        }
        throw new RuntimeException("Config file missing 'turns' field");
    }
}
