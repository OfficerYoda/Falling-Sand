package de.officeryoda.fallingsand;

import javax.swing.*;
import java.awt.*;

public class GridDrawer extends JFrame {

    public GridDrawer() {
        int cellSize = 10;
        int gridWidth = 600;
        int gridHeight = 450;
        int borderGap = 50;

        setTitle("Grid Drawer");
        setSize(gridWidth + 2 * borderGap, gridHeight + 3 * borderGap);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridPanel gridPanel = new GridPanel(cellSize, gridWidth, gridHeight, borderGap);
        add(gridPanel);

        setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GridDrawer::new);
    }
}

class GridPanel extends JPanel {

    private final int cellSize;
    private final int gridWidth;
    private final int gridHeight;
    private final int borderGap;

    GridPanel(int cellSize, int gridWidth, int gridHeight, int borderGap) {
        this.cellSize = cellSize;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.borderGap = borderGap;
        setBackground(Color.black);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);

        // Draw horizontal lines
        for(int y = 0; y <= gridHeight / cellSize; y++) {
            g.drawLine(borderGap, borderGap + y * cellSize, borderGap + gridWidth, borderGap + y * cellSize);
        }

        // Draw vertical lines
        for(int x = 0; x <= gridWidth / cellSize; x++) {
            g.drawLine(borderGap + x * cellSize, borderGap, borderGap + x * cellSize, borderGap + gridHeight);
        }

    }
}
