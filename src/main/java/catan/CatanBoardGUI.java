package catan;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Graphical Swing GUI for the Catan board.
 * Renders a proper hex grid with terrain colors, number tokens,
 * player buildings, roads, and a scoreboard.
 * Includes playback speed controls so the user can watch the game unfold.
 */
public class CatanBoardGUI extends JFrame {

    // Hex geometry: pointy-top hexagons
    private static final int HEX_RADIUS = 50;
    private static final double HEX_WIDTH = Math.sqrt(3) * HEX_RADIUS;

    // Tile rows: 3-4-5-4-3 (spiral IDs per spec diagram)
    private static final int[][] TILE_ROWS = {
        {17, 14, 15},
        {12, 4, 5, 16},
        {11, 3, 0, 6, 10},
        {13, 2, 1, 18},
        {9, 8, 7}
    };

    // Row x-offsets to center the 3-4-5-4-3 pattern
    private static final double[] ROW_X_OFFSETS = {
        HEX_WIDTH,
        HEX_WIDTH / 2.0,
        0,
        HEX_WIDTH / 2.0,
        HEX_WIDTH
    };

    // Player colors (classic Catan: red, blue, white, orange)
    private static final Color[] PLAYER_COLORS = {
        new Color(220, 40, 40),    // Player 1: Red
        new Color(40, 80, 200),    // Player 2: Blue
        new Color(240, 240, 240),  // Player 3: White
        new Color(240, 160, 30)    // Player 4: Orange
    };

    private static final String[] PLAYER_COLOR_NAMES = {
        "Red", "Blue", "White", "Orange"
    };

    // Terrain colors
    private static final Map<TerrainType, Color> TERRAIN_COLORS = new HashMap<>();
    static {
        TERRAIN_COLORS.put(TerrainType.FIELDS,    new Color(255, 215, 0));
        TERRAIN_COLORS.put(TerrainType.FOREST,    new Color(34, 139, 34));
        TERRAIN_COLORS.put(TerrainType.PASTURE,   new Color(144, 238, 144));
        TERRAIN_COLORS.put(TerrainType.HILLS,     new Color(205, 92, 42));
        TERRAIN_COLORS.put(TerrainType.MOUNTAINS, new Color(139, 137, 137));
        TERRAIN_COLORS.put(TerrainType.DESERT,    new Color(237, 201, 136));
    }

    private static final Map<TerrainType, String> TERRAIN_NAMES = new HashMap<>();
    static {
        TERRAIN_NAMES.put(TerrainType.FIELDS,    "Fields");
        TERRAIN_NAMES.put(TerrainType.FOREST,    "Forest");
        TERRAIN_NAMES.put(TerrainType.PASTURE,   "Pasture");
        TERRAIN_NAMES.put(TerrainType.HILLS,     "Hills");
        TERRAIN_NAMES.put(TerrainType.MOUNTAINS, "Mountains");
        TERRAIN_NAMES.put(TerrainType.DESERT,    "Desert");
    }

    private static final Map<TerrainType, String> RESOURCE_NAMES = new HashMap<>();
    static {
        RESOURCE_NAMES.put(TerrainType.FIELDS,    "Grain");
        RESOURCE_NAMES.put(TerrainType.FOREST,    "Lumber");
        RESOURCE_NAMES.put(TerrainType.PASTURE,   "Wool");
        RESOURCE_NAMES.put(TerrainType.HILLS,     "Brick");
        RESOURCE_NAMES.put(TerrainType.MOUNTAINS, "Ore");
        RESOURCE_NAMES.put(TerrainType.DESERT,    "Nothing");
    }

    // Board padding
    private static final int BOARD_PADDING_X = 60;
    private static final int BOARD_PADDING_Y = 70;

    // State
    private Board board;
    private Player[] players;
    private int round;

    // Node pixel positions (computed once)
    private Map<Integer, double[]> nodePositions;

    // Panels
    private BoardPanel boardPanel;
    private ScorePanel scorePanel;
    private JLabel statusLabel;
    private JLabel titleLabel;

    // Speed control
    private volatile int delayMs = 300;   // milliseconds between GUI updates
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private JButton pauseButton;
    private JLabel speedLabel;

    public CatanBoardGUI() {
        setTitle("Settlers of Catan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 100, 180));

        // Title bar
        titleLabel = new JLabel("Settlers of Catan", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(20, 60, 120));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Board panel (center)
        boardPanel = new BoardPanel();
        boardPanel.setPreferredSize(new Dimension(580, 520));
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Score panel (right)
        scorePanel = new ScorePanel();
        scorePanel.setPreferredSize(new Dimension(220, 520));
        mainPanel.add(scorePanel, BorderLayout.EAST);

        // Bottom panel: status + controls
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(20, 60, 120));

        // Status bar
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(false);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 2, 10));
        bottomPanel.add(statusLabel, BorderLayout.NORTH);

        // Speed control bar
        JPanel controlPanel = createControlPanel();
        bottomPanel.add(controlPanel, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        panel.setBackground(new Color(20, 60, 120));
        panel.setBorder(BorderFactory.createEmptyBorder(2, 10, 6, 10));

        // Pause / Resume button
        pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        pauseButton.setPreferredSize(new Dimension(90, 28));
        pauseButton.addActionListener(e -> {
            paused = !paused;
            pauseButton.setText(paused ? "Resume" : "Pause");
            if (!paused) {
                synchronized (pauseLock) {
                    pauseLock.notifyAll();
                }
            }
        });
        panel.add(pauseButton);

        // Speed label
        JLabel lbl = new JLabel("Speed:");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(lbl);

        // Speed slider: 0ms (instant) to 1000ms (slow)
        JSlider speedSlider = new JSlider(0, 1000, delayMs);
        speedSlider.setInverted(true); // left = fast, right = slow visually inverted
        speedSlider.setPreferredSize(new Dimension(200, 30));
        speedSlider.setBackground(new Color(20, 60, 120));
        speedSlider.setForeground(Color.WHITE);

        // Labels on the slider
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        JLabel fastLabel = new JLabel("Fast");
        fastLabel.setForeground(Color.WHITE);
        fastLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        JLabel slowLabel = new JLabel("Slow");
        slowLabel.setForeground(Color.WHITE);
        slowLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        labels.put(0, fastLabel);
        labels.put(1000, slowLabel);
        speedSlider.setLabelTable(labels);
        speedSlider.setPaintLabels(true);

        speedSlider.addChangeListener(e -> {
            delayMs = speedSlider.getValue();
            updateSpeedLabel();
        });
        panel.add(speedSlider);

        // Current speed indicator
        speedLabel = new JLabel();
        speedLabel.setForeground(new Color(200, 200, 220));
        speedLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        updateSpeedLabel();
        panel.add(speedLabel);

        return panel;
    }

    private void updateSpeedLabel() {
        if (delayMs == 0) {
            speedLabel.setText("(instant)");
        } else {
            speedLabel.setText("(" + delayMs + "ms)");
        }
    }

    /**
     * Update the GUI with the current game state, then pause for the configured delay.
     * Called from Game after every turn.
     */
    public void update(Board board, Player[] players, int round, String status) {
        this.board = board;
        this.players = players;
        this.round = round;

        // Compute node positions from tile geometry
        computeNodePositions();

        // Update UI on the EDT
        SwingUtilities.invokeLater(() -> {
            if (round == 0) {
                titleLabel.setText("Settlers of Catan - Setup Phase");
            } else {
                titleLabel.setText("Settlers of Catan - Round " + round);
            }
            statusLabel.setText(status);
            boardPanel.repaint();
            scorePanel.repaint();

            if (!isVisible()) {
                setVisible(true);
            }
        });

        // Delay so the user can watch the game unfold
        sleepWithPause();
    }

    /**
     * Sleep for the configured delay, respecting pause state.
     */
    private void sleepWithPause() {
        // Check pause
        synchronized (pauseLock) {
            while (paused) {
                try {
                    pauseLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        // Delay
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Compute pixel positions for all 54 nodes based on hex geometry.
     */
    private void computeNodePositions() {
        nodePositions = new HashMap<>();

        for (int row = 0; row < TILE_ROWS.length; row++) {
            for (int col = 0; col < TILE_ROWS[row].length; col++) {
                int tileId = TILE_ROWS[row][col];
                Tile tile = board.getTile(tileId);

                double cx = BOARD_PADDING_X + ROW_X_OFFSETS[row] + col * HEX_WIDTH;
                double cy = BOARD_PADDING_Y + row * (HEX_RADIUS * 1.5);

                double[] angles = {
                    Math.toRadians(270), Math.toRadians(330), Math.toRadians(30),
                    Math.toRadians(90),  Math.toRadians(150), Math.toRadians(210)
                };

                java.util.List<Node> corners = tile.getCornerNodes();
                for (int i = 0; i < corners.size() && i < 6; i++) {
                    int nodeId = corners.get(i).getId();
                    if (!nodePositions.containsKey(nodeId)) {
                        double nx = cx + HEX_RADIUS * Math.cos(angles[i]);
                        double ny = cy + HEX_RADIUS * Math.sin(angles[i]);
                        nodePositions.put(nodeId, new double[]{nx, ny});
                    }
                }
            }
        }
    }

    private Polygon createHexagon(double cx, double cy) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            int px = (int) Math.round(cx + HEX_RADIUS * Math.cos(angle));
            int py = (int) Math.round(cy + HEX_RADIUS * Math.sin(angle));
            hex.addPoint(px, py);
        }
        return hex;
    }

    // ======================= Board Panel ==========================

    private class BoardPanel extends JPanel {

        BoardPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (board == null) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Ocean background
            g2.setColor(new Color(30, 100, 180));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Draw hex tiles first
            for (int row = 0; row < TILE_ROWS.length; row++) {
                for (int col = 0; col < TILE_ROWS[row].length; col++) {
                    int tileId = TILE_ROWS[row][col];
                    Tile tile = board.getTile(tileId);
                    double cx = BOARD_PADDING_X + ROW_X_OFFSETS[row] + col * HEX_WIDTH;
                    double cy = BOARD_PADDING_Y + row * (HEX_RADIUS * 1.5);
                    drawHex(g2, tile, cx, cy);
                }
            }

            // Draw roads on top of hexes (so they're clearly visible)
            drawRoads(g2);

            // Draw buildings on top of everything
            drawBuildings(g2);
        }

        private void drawHex(Graphics2D g2, Tile tile, double cx, double cy) {
            Polygon hex = createHexagon(cx, cy);

            Color terrainColor = TERRAIN_COLORS.getOrDefault(tile.getTerrain(), Color.GRAY);
            g2.setColor(terrainColor);
            g2.fillPolygon(hex);

            g2.setColor(new Color(80, 60, 40));
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawPolygon(hex);

            String terrainName = TERRAIN_NAMES.getOrDefault(tile.getTerrain(), "?");
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();

            if (tile.getTerrain() == TerrainType.FOREST || tile.getTerrain() == TerrainType.MOUNTAINS) {
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(new Color(40, 30, 20));
            }

            int textWidth = fm.stringWidth(terrainName);
            g2.drawString(terrainName, (int)(cx - textWidth / 2.0), (int)(cy - 8));

            if (tile.getTerrain() != TerrainType.DESERT) {
                drawToken(g2, tile.getToken(), cx, cy + 5);
            } else {
                g2.setFont(new Font("SansSerif", Font.ITALIC, 10));
                String sub = "(no resource)";
                textWidth = g2.getFontMetrics().stringWidth(sub);
                g2.drawString(sub, (int)(cx - textWidth / 2.0), (int)(cy + 12));
            }
        }

        private void drawToken(Graphics2D g2, int token, double cx, double cy) {
            int tokenRadius = 14;

            g2.setColor(new Color(255, 248, 220));
            g2.fillOval((int)(cx - tokenRadius), (int)(cy - tokenRadius),
                        tokenRadius * 2, tokenRadius * 2);
            g2.setColor(new Color(80, 60, 40));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval((int)(cx - tokenRadius), (int)(cy - tokenRadius),
                        tokenRadius * 2, tokenRadius * 2);

            String numStr = String.valueOf(token);
            if (token == 6 || token == 8) {
                g2.setColor(new Color(200, 0, 0));
                g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            } else {
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            }
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(numStr);
            g2.drawString(numStr, (int)(cx - tw / 2.0), (int)(cy + fm.getAscent() / 2.0 - 1));
        }

        private void drawRoads(Graphics2D g2) {
            if (nodePositions == null) return;

            for (Edge edge : board.getAllEdges()) {
                if (edge.isOccupied()) {
                    Road road = edge.getRoad();
                    int playerId = road.getOwner().getId();
                    Color color = PLAYER_COLORS[playerId - 1];

                    double[] posA = nodePositions.get(edge.getA().getId());
                    double[] posB = nodePositions.get(edge.getB().getId());
                    if (posA == null || posB == null) continue;

                    int x1 = (int) posA[0], y1 = (int) posA[1];
                    int x2 = (int) posB[0], y2 = (int) posB[1];

                    // Outer glow (wide, semi-transparent)
                    g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
                    g2.drawLine(x1, y1, x2, y2);

                    // Dark border
                    g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(new Color(20, 20, 20));
                    g2.drawLine(x1, y1, x2, y2);

                    // Main road color
                    g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(color);
                    g2.drawLine(x1, y1, x2, y2);

                    // Inner highlight for visibility
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    Color highlight = new Color(
                        Math.min(255, color.getRed() + 60),
                        Math.min(255, color.getGreen() + 60),
                        Math.min(255, color.getBlue() + 60)
                    );
                    g2.setColor(highlight);
                    g2.drawLine(x1, y1, x2, y2);
                }
            }
        }

        private void drawBuildings(Graphics2D g2) {
            if (nodePositions == null) return;

            for (Node node : board.getNodes()) {
                if (node == null || !node.isOccupied()) continue;

                Building b = node.getBuilding();
                int playerId = b.getOwner().getId();
                Color color = PLAYER_COLORS[playerId - 1];
                double[] pos = nodePositions.get(node.getId());
                if (pos == null) continue;

                int x = (int) pos[0];
                int y = (int) pos[1];

                if (b instanceof City) {
                    drawCity(g2, x, y, color);
                } else {
                    drawSettlement(g2, x, y, color);
                }
            }
        }

        private void drawSettlement(Graphics2D g2, int x, int y, Color color) {
            int size = 8;
            Polygon house = new Polygon();
            house.addPoint(x, y - size - 4);
            house.addPoint(x + size, y - 2);
            house.addPoint(x + size, y + size);
            house.addPoint(x - size, y + size);
            house.addPoint(x - size, y - 2);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawPolygon(house);
            g2.setColor(color);
            g2.fillPolygon(house);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawPolygon(house);
        }

        private void drawCity(Graphics2D g2, int x, int y, Color color) {
            int size = 10;
            Polygon city = new Polygon();
            city.addPoint(x - size + 3, y - size - 6);
            city.addPoint(x + 2, y - size - 6);
            city.addPoint(x + 2, y - 4);
            city.addPoint(x + size, y - 4);
            city.addPoint(x + size, y + size);
            city.addPoint(x - size, y + size);
            city.addPoint(x - size, y - size + 2);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawPolygon(city);
            g2.setColor(color);
            g2.fillPolygon(city);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawPolygon(city);
        }
    }

    // ======================= Score Panel ==========================

    private class ScorePanel extends JPanel {

        ScorePanel() {
            setBackground(new Color(20, 60, 120));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (players == null) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int y = 20;

            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.setColor(Color.WHITE);
            g2.drawString("SCOREBOARD", 30, y);
            y += 10;

            g2.setColor(new Color(100, 140, 200));
            g2.drawLine(15, y, 205, y);
            y += 20;

            for (int i = 0; i < players.length; i++) {
                drawPlayerStats(g2, players[i], PLAYER_COLORS[i], PLAYER_COLOR_NAMES[i], 15, y);
                y += 90;
            }

            g2.setColor(new Color(100, 140, 200));
            g2.drawLine(15, y, 205, y);
            y += 20;

            drawLegend(g2, 15, y);
        }

        private void drawPlayerStats(Graphics2D g2, Player player, Color color, String colorName, int x, int y) {
            g2.setColor(color);
            g2.fillRoundRect(x, y - 10, 14, 14, 3, 3);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(x, y - 10, 14, 14, 3, 3);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.drawString("Player " + player.getId() + " (" + colorName + ")", x + 20, y + 2);

            y += 18;
            int vp = player.getVP();
            int barWidth = 150;
            int barHeight = 14;
            int filled = (int)(barWidth * Math.min(vp, 10) / 10.0);

            g2.setColor(new Color(50, 50, 80));
            g2.fillRoundRect(x, y, barWidth, barHeight, 5, 5);

            if (vp >= 10) {
                g2.setColor(new Color(255, 215, 0));
            } else {
                g2.setColor(color);
            }
            g2.fillRoundRect(x, y, filled, barHeight, 5, 5);

            g2.setColor(new Color(150, 150, 180));
            g2.drawRoundRect(x, y, barWidth, barHeight, 5, 5);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.drawString(vp + " / 10 VP", x + barWidth + 6, y + 12);

            y += 22;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(new Color(200, 200, 220));
            int cards = player.handSize();
            int settlements = 5 - player.getPieces().settlementsRemaining();
            int cities = 4 - player.getPieces().citiesRemaining();
            int roads = 15 - player.getPieces().roadsRemaining();
            g2.drawString("Cards: " + cards + "  Roads: " + roads, x, y);
            y += 15;
            g2.drawString("Settlements: " + settlements + "  Cities: " + cities, x, y);
        }

        private void drawLegend(Graphics2D g2, int x, int y) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(Color.WHITE);
            g2.drawString("TERRAIN GUIDE", x + 15, y);
            y += 18;

            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            for (TerrainType t : TerrainType.values()) {
                Color c = TERRAIN_COLORS.getOrDefault(t, Color.GRAY);
                g2.setColor(c);
                g2.fillRoundRect(x, y - 9, 12, 12, 3, 3);
                g2.setColor(Color.BLACK);
                g2.drawRoundRect(x, y - 9, 12, 12, 3, 3);

                g2.setColor(new Color(200, 200, 220));
                String label = TERRAIN_NAMES.getOrDefault(t, "?");
                String resource = RESOURCE_NAMES.getOrDefault(t, "?");
                g2.drawString(label + " -> " + resource, x + 18, y + 1);
                y += 17;
            }
        }
    }
}
