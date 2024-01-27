package de.officeryoda.fallingsand;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GridDrawer extends JFrame {

    public final static int TITLE_BAR_HEIGHT = 40;

    private final GridPanel gridPanel;

    public GridDrawer(Grid grid, int cellSize) {

        setTitle("Falling Sand");
        setSize(grid.getWidth() * cellSize + 16, // idk why but it was always 16px to narrow
                grid.getHeight() * cellSize + TITLE_BAR_HEIGHT - 1); // -1 to prevent one pixel row at the bottom
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gridPanel = new GridPanel(grid, cellSize);
        add(gridPanel);

        GridListener gridListener = new GridListener(grid, cellSize, this);
        addMouseListener(gridListener);
        addMouseWheelListener(gridListener);

//        setInvisibleCursor();

        setLocationRelativeTo(null); // center the frame on the screen
        setVisible(true);
    }

    private void setInvisibleCursor() {
        // Create a transparent cursor image
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "InvisibleCursor");

        // Set the cursor for the JFrame
        setCursor(invisibleCursor);
    }

    public void repaintGrid() {
        gridPanel.repaint();
    }

    public void repaintGrid(Rectangle rect) {
        gridPanel.setBoundsRect(rect);
        gridPanel.repaint(rect.x, rect.y, rect.width, rect.height);
    }
}

class GridPanel extends JPanel {

    private final Grid grid;
    private final int cellSize;
    private final int gridWidth;
    private final int gridHeight;

    private Rectangle boundsRect;

    public GridPanel(Grid grid, int cellSize) {
        this.grid = grid;
        this.cellSize = cellSize;
        this.gridWidth = grid.getWidth();
        this.gridHeight = grid.getHeight();

        this.boundsRect = new Rectangle(0, 0, getWidth(), getHeight());

        setBackground(Colors.BACKGROUND_COLOR);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

//        g.setColor(Colors.BACKGROUND_COLOR);
//        g.fillRect(0, 0, getWidth(), getHeight());
        paintParticles(g);
        paintCursor(g);
//        paintGrid(g);
//        paintFps(g);

        Grid.DRAW_FINISHED_LATCH.countDown();
    }

    private void paintGrid(Graphics g) {
        final int minX = 0;
        final int maxX = gridWidth * cellSize;
        final int minY = 0;
        final int maxY = gridHeight * cellSize;

        // Draw horizontal lines
        for(int y = 0; y <= gridHeight; y++) {
            g.drawLine(
                    minX, y * cellSize,
                    maxX, y * cellSize);
        }

        // Draw vertical lines
        for(int x = 0; x <= gridWidth; x++) {
            g.drawLine(
                    x * cellSize, minY,
                    x * cellSize, maxY);
        }
    }

    private void paintParticles(Graphics g) {
        if(grid.isCleared()) {
            clearPixels(g);
        } else if(!grid.getModifiedIndices().isEmpty()) {
            paintPixels(g);
        }
    }

    private void paintPixels(Graphics g) {
        int maxX = boundsRect.x + boundsRect.width;
        int maxY = boundsRect.y + boundsRect.height;

//        g.setColor(Colors.varyColor(Colors.SAND_COLOR));
//        g.fillRect(boundsRect.x, boundsRect.y, maxX, maxY);
        for(int x = 0; x < gridWidth; x++) {
            for(int y = 0; y < gridHeight; y++) {
                int index = x + y * gridWidth;

                g.setColor(grid.get(index).getColor());
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    }

    private void clearPixels(Graphics g) {
        g.setColor(Colors.BACKGROUND_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void paintCursor(Graphics g) {
        int[] cursorIndices = grid.getCursorIndices();
        Color[] cursorColors = grid.getCursorColors();

        for(int i = 0; i < cursorIndices.length; i++) {
            int index = cursorIndices[i];

            int x = index % gridWidth;
            int y = index / gridWidth;

            g.setColor(grid.isEmpty(index) ? cursorColors[i] : grid.get(index).getColor());
            g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
        }
    }

    private void paintFps(Graphics g) {
        long deltaTime = (System.currentTimeMillis() - grid.getLastUpdate());

        // Ensure that the last update time is not the same as the current time (divide by zero)
        if(deltaTime == 0) return;

        int fps = (int) (1000 / deltaTime);

        int screenWidth = getWidth();
        int screenHeight = getHeight();

        int fontSize = screenHeight / 30; // Adjust the divisor for the desired scaling

        Font font = new Font("Spline Sans", Font.PLAIN, fontSize);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + fps, 5, GridDrawer.TITLE_BAR_HEIGHT);
    }

    void setBoundsRect(Rectangle boundsRect) {
        this.boundsRect = boundsRect;
    }
}
