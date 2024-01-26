package de.officeryoda.fallingsand;

import javax.swing.*;
import java.awt.*;

public class GridDrawer extends JFrame {

    private final GridPanel gridPanel;

    public GridDrawer(Grid grid, int cellSize, int borderGap) {

        setTitle("Grid Drawer");
        setSize(grid.getWidth() * cellSize + 2 * borderGap, grid.getHeight() * cellSize + 3 * borderGap);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gridPanel = new GridPanel(grid, cellSize, borderGap);
        add(gridPanel);

        addMouseMotionListener(new GridListener(grid, cellSize, borderGap));

        setVisible(true);
    }

    public void repaintGrid() {
        gridPanel.repaint();
    }
}

class GridPanel extends JPanel {

    private final Grid grid;
    private final int cellSize;
    private final int borderGap;


    public GridPanel(Grid grid, int cellSize, int borderGap) {
        this.grid = grid;
        this.cellSize = cellSize;
        this.borderGap = borderGap;
        setBackground(Color.black);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

//        Color randomColor = new Color((int) (Math.random() * 0x1000000));
        g.setColor(Color.white);

        final int maxX = borderGap + grid.getWidth() * cellSize;
        final int maxY = borderGap + grid.getHeight() * cellSize;

        // Draw horizontal lines
        for(int y = 0; y <= grid.getHeight(); y++) {
            g.drawLine(
                    borderGap, borderGap + y * cellSize,
                    maxX, borderGap + y * cellSize);
        }

        // Draw vertical lines
        for(int x = 0; x <= grid.getWidth(); x++) {
            g.drawLine(
                    borderGap + x * cellSize, borderGap,
                    borderGap + x * cellSize, maxY);
        }

        for(int x = 0; x < grid.getWidth(); x++) {
            for(int y = 0; y < grid.getHeight(); y++) {
                if(grid.isEmpty(x, y)) continue;
                g.setColor(Color.white);
                g.fillRect(borderGap + x * cellSize , borderGap + y * cellSize, cellSize, cellSize);
            }
        }
    }
}
