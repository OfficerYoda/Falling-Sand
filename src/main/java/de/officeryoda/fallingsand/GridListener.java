package de.officeryoda.fallingsand;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GridListener extends MouseMotionAdapter {

    private final Grid grid;
    private final int cellSize;
    private final int borderGap;

    public GridListener(Grid grid, int cellSize, int borderGap) {
        this.grid = grid;
        this.cellSize = cellSize;
        this.borderGap = borderGap;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Handle mouse drag here
        System.out.println("Mouse dragged at: " + e.getX() + ", " + e.getY());
        int x = e.getX();
        int y = e.getY();

        x -= borderGap;
        x -= cellSize/2;
        y -= borderGap;
        y -= 30; // window bar

        if(x < 0 || x > grid.getWidth() * cellSize) return;
        if(y < 0 || y > grid.getHeight() * cellSize) return;

        grid.set(x / cellSize, y / cellSize, 1);
    }
}
