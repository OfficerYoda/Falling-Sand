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

        addMouseListener(new GridListener(grid, cellSize, this));

        setInvisibleCursor();

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
}

class GridPanel extends JPanel {

    private final Grid grid;
    private final int cellSize;

    public GridPanel(Grid grid, int cellSize) {
        this.grid = grid;
        this.cellSize = cellSize;
        setBackground(Color.decode("#010409"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        paintGrid(g);
        paintParticles(g);
        paintCursor(g);
    }

    private void paintGrid(Graphics g) {
        final int minX = 0;
        final int maxX = grid.getWidth() * cellSize;
        final int minY = 0;
        final int maxY = grid.getHeight() * cellSize;

        // Draw horizontal lines
        for(int y = 0; y <= grid.getHeight(); y++) {
            g.drawLine(
                    minX, y * cellSize,
                    maxX, y * cellSize);
        }

        // Draw vertical lines
        for(int x = 0; x <= grid.getWidth(); x++) {
            g.drawLine(
                    x * cellSize, minY,
                    x * cellSize, maxY);
        }
    }

    private void paintParticles(Graphics g) {
        for(int x = 0; x < grid.getWidth(); x++) {
            for(int y = 0; y < grid.getHeight(); y++) {
                if(grid.isEmpty(x, y)) continue;
                g.setColor(new Color(grid.get(x, y)));
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    }

    private void paintCursor(Graphics g) {
        int cursorIndex = grid.getCursorIndex();
//        System.out.println("cursor: " + cursorIndex);
        if(cursorIndex == -1) return;

        int x = cursorIndex % grid.getWidth();
        int y = cursorIndex / grid.getWidth();

        g.setColor(Grid.SAND_COLOR);
        g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
    }
}
